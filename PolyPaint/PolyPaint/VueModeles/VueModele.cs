﻿using System.ComponentModel;
using System.Runtime.CompilerServices;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using PolyPaint.Modeles;
using PolyPaint.Utilitaires;
using PolyPaint.Managers;
using PolyPaint.Vues;
using System.Collections.Generic;
using System.Linq;
using Newtonsoft.Json;

namespace PolyPaint.VueModeles
{

    /// <summary>
    /// Sert d'intermédiaire entre la vue et le modèle.
    /// Expose des commandes et propriétés connectées au modèle aux des éléments de la vue peuvent se lier.
    /// Reçoit des avis de changement du modèle et envoie des avis de changements à la vue.
    /// </summary>
    class VueModele : INotifyPropertyChanged
    {
        public event PropertyChangedEventHandler PropertyChanged;
        private Editeur editeur = new Editeur();
        private NetworkManager networkManager = new NetworkManager();

        private string sessionId;
        public string SessionId
        {
            get
            {
                return sessionId;
            }
            set
            {
                sessionId = value;
                ChatManager.SessionID = value;
                ProprieteModifiee();
            }
        }

        private int switchView = 0;
        private int previousView;
        public int SwitchView
        {
            get { return switchView; }
            set { previousView = switchView; switchView = value; ProprieteModifiee(); }
        }
        private CustomInkCanvas Canvas { get; set; }
        public string CouleurSelectionnee
        {
            get { return editeur.CouleurSelectionnee; }
            set {
                editeur.CouleurSelectionnee = value;
            }
        }
        public StrokeCollection LastCut
        {
            get { return editeur.LastCut; }
            set { editeur.LastCut = value; }
        }
        public string RemplissageSelectionne
        {
            get { return editeur.RemplissageSelectionne; }
            set
            {
                editeur.RemplissageSelectionne = value;
            }
        }

        private ChatManager chatManager = new ChatManager();
        public ChatManager ChatManager
        {
            get { return chatManager; }
            set { ProprieteModifiee(); }

        }
        public SocketManager SocketManager {
            get { return editeur.SocketManager; }
            set { editeur.SocketManager = value; }
        }
        public FormConnectorManager FormConnectorManager
        {
            get { return editeur.FormConnectorManager; }
            set { editeur.FormConnectorManager = value; }
        }

        private string username;
        public string Username
        {
            get { return username; }
            set
            {
                username = value;
                ChatManager.Username = value;
                this.SocketManager.UserName = username;
                ProprieteModifiee();
            }
        }

        private List<GalleryControl.GalleryItem> galleryItems;
        public List<GalleryControl.GalleryItem> GalleryItems
        {
            get { return galleryItems; }
            set { galleryItems = value; ProprieteModifiee(); }
        }

        // Ensemble d'attributs qui définissent l'apparence d'un trait.
        public DrawingAttributes AttributsDessin { get; set; } = new DrawingAttributes();

        public string OutilSelectionne
        {
            get { return editeur.OutilSelectionne; }            
            set { ProprieteModifiee(); }
        }
        

        public int TailleTrait
        {
            get { return editeur.TailleTrait; }
            set { editeur.TailleTrait = value; }
        }
        public string ConnectorLabel
        {
            get { return editeur.ConnectorLabel; }
            set { editeur.ConnectorLabel = value; }
        }
        public string ConnectorType
        {
            get { return editeur.ConnectorType; }
            set { editeur.ConnectorType = value; }
        }
        public int ConnectorSize
        {
            get { return editeur.ConnectorSize; }
            set { editeur.ConnectorSize = value; }
        }
        public string ConnectorColor
        {
            get { return editeur.ConnectorColor; }
            set { editeur.ConnectorColor = value; }
        }    
        
        public StrokeCollection SelectedStrokes
        {
            get { return editeur.selectedStrokes; }
            set { editeur.selectedStrokes = value; }
        }
        public int CurrentRotation
        {
            get { return (editeur.selectedStrokes[0] as Form).CurrentRotation; }
            set { (editeur.selectedStrokes[0] as Form).CurrentRotation = value; }
        }

        public StrokeCollection Traits { get; set; }
        // Commandes sur lesquels la vue pourra se connecter.
        public RelayCommand<object> Empiler { get; set; }
        public RelayCommand<object> Depiler { get; set; }
        public RelayCommand<string> ChoisirOutil { get; set; }
        public RelayCommand<object> Reinitialiser { get; set; }
        public RelayCommand<object> HandleDuplicate { get; set; }

        public RelayCommand<string> ChoisirForme { get; set; }
        public RelayCommand<string> AddForm { get; set; }
        public RelayCommand<object> RotateForm { get; set; }
        // public RelayCommand<MouseButtonEventArgs> HandleMouseDown { get; set; }

        public ICommand NavigateLogin { get { return new RelayCommand(OnNavigateLogin, () => { return true; }); } }
        public ICommand NavigateSignup { get { return new RelayCommand(OnNavigateSignup, () => { return true; }); } }
        public ICommand NavigateBack { get { return new RelayCommand(OnNavigateBack, () => { return true; }); } }
        public ICommand NavigateGallery { get { return new RelayCommand(OnNavigateGallery, () => { return true; }); } }
        public ICommand NavigateDrawSession { get { return new RelayCommand(OnNavigateDrawSession, () => { return true; }); } }

        private void OnNavigateLogin()
        {
            SwitchView = 1;
        }

        private void OnNavigateSignup()
        {
            SwitchView = 2;
        }

        private void OnNavigateGallery()
        {
            SwitchView = 4;
        }

        private void OnNavigateDrawSession()
        {
            SwitchView = 5;
        }

        private void OnNavigateBack()
        {
            SwitchView = previousView;
        }

        public async void Login(string password)
        {
            SessionId = await networkManager.LoginAsync(Username, password);
            if (SessionId == "")
            {
                MessageBox.Show("Wrong login informations", "Error");
                return;
            }
            ChatManager.Connect();
            SwitchView = 3;
        }

        public async void Signup(string password)
        {
            SessionId = await networkManager.SignupAsync(Username, password);
            if (SessionId == "")
            {
                MessageBox.Show("Wrong signup informations", "Error");
                return;
            }
            SwitchView = 3;
        }

        public void JoinDrawSession(string joinningSessionID)
        {
            //TODO : JOIN
        }

        public void LoadGallery()
        {
            //TODO : LOAD INTO GalleryItems
            // NOTE : CALL DURING NAVIGATE_TO_GALLERY
        }

        public List<ChatControl.UserItem> LoadUsers()
        {
            string userList = networkManager.LoadUsersAsync(Username, SessionId).Result;

            List<ChatControl.UserItem> userItems = new List<ChatControl.UserItem>();

            var users = JsonConvert.DeserializeObject<Dictionary<string,bool>>(userList);

            foreach (var user in users)
            {
                userItems.Add(new ChatControl.UserItem() { Username = user.Key, ConnectionStatus = user.Value ? 1 : 0 });
            }

            return userItems.OrderByDescending(x => x.ConnectionStatus).ToList(); ;
        }

        /// <summary>
        /// Constructeur de VueModele
        /// On récupère certaines données initiales du modèle et on construit les commandes
        /// sur lesquelles la vue se connectera.
        /// </summary>
        public VueModele()
        {
            this.Canvas = new CustomInkCanvas();
            FormConnectorManager = new FormConnectorManager();
            SocketManager = new SocketManager();
            //SocketManager.JoinDrawingSession("MockSessionID");
            ChatManager.socket = SocketManager.Socket;
            //SocketManager.UserName = "Olivier";
            editeur.initializeSocketEvents();
            // On écoute pour des changements sur le modèle. Lorsqu'il y en a, EditeurProprieteModifiee est appelée.
            editeur.PropertyChanged += new PropertyChangedEventHandler(EditeurProprieteModifiee);

            // On initialise les attributs de dessin avec les valeurs de départ du modèle.
            AttributsDessin = new DrawingAttributes();
            AttributsDessin.Color = (Color)ColorConverter.ConvertFromString(editeur.CouleurSelectionnee);
            AjusterPointe();
            
            Traits = editeur.traits;
            SelectedStrokes = editeur.selectedStrokes;
            

            // Pour chaque commande, on effectue la liaison avec des méthodes du modèle.            
            Empiler = new RelayCommand<object>(editeur.Empiler, editeur.PeutEmpiler);
            Depiler = new RelayCommand<object>(editeur.Depiler, editeur.PeutDepiler);
            //AddForm = new RelayCommand<string>(editeur.AddForm);
            RotateForm = new RelayCommand<object>(editeur.RotateForm);
            // Pour les commandes suivantes, il est toujours possible des les activer.
            // Donc, aucune vérification de type Peut"Action" à faire.
            ChoisirOutil = new RelayCommand<string>(editeur.ChoisirOutil);
            Reinitialiser = new RelayCommand<object>(editeur.Reinitialiser);
            HandleDuplicate = new RelayCommand<object>(editeur.HandleDuplicate);
        }
        public void SendCanvas(CustomInkCanvas canvas)
        {
            this.Canvas = canvas;
        }

        /// <summary>
        /// Appelee lorsqu'une propriété de VueModele est modifiée.
        /// Un évènement indiquant qu'une propriété a été modifiée est alors émis à partir de VueModèle.
        /// L'évènement qui contient le nom de la propriété modifiée sera attrapé par la vue qui pourra
        /// alors mettre à jour les composants concernés.
        /// </summary>
        /// <param name="propertyName">Nom de la propriété modifiée.</param>
        protected virtual void ProprieteModifiee([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }

        /// <summary>
        /// Traite les évènements de modifications de propriétés qui ont été lancés à partir
        /// du modèle.
        /// </summary>
        /// <param name="sender">L'émetteur de l'évènement (le modèle)</param>
        /// <param name="e">Les paramètres de l'évènement. PropertyName est celui qui nous intéresse. 
        /// Il indique quelle propriété a été modifiée dans le modèle.</param>
        private void EditeurProprieteModifiee(object sender, PropertyChangedEventArgs e)
        {     
            if (e.PropertyName == "CouleurSelectionnee")
            {
                ProprieteModifiee(e.PropertyName);
            }
            else if (e.PropertyName == "RemplissageSelectionne")
            {
                ProprieteModifiee(e.PropertyName);
            }
            else if (e.PropertyName == "OutilSelectionne")
            {
                OutilSelectionne = editeur.OutilSelectionne;
            }
            else if (e.PropertyName == "Selection")
            {
                this.Canvas.AllowSelection = true;
                this.Canvas.Select(editeur.selectedStrokes);
                this.Canvas.AllowSelection = false;
            }
            
        }
        public void HandleSelection(StrokeCollection strokes)
        {
            editeur.HandleChangeSelection(strokes);
            //TODO : Send socket -> selection was changed
        }
        public void SetConnectorSettings(string label, string type, int size,string color)
        {
            this.ConnectorLabel = label;
            this.ConnectorType = type;
            this.ConnectorSize = size;
            this.ConnectorColor = color;
        }
        public void HandleLabelChange(string label)
        {
            editeur.HandleLabelChange(label);
        }
        public void HandleUmlTextChange(string name, List<string> methods, List<string> attributes)
        {
            editeur.HandleUmlTextChange(name,methods,attributes);
        }
        public void HandleSelectionSuppression()
        {
            editeur.HandleDeleteSelection();
        }
        public void HandleDrag()
        {
            // TODO : Send socket -> selection has moved
            editeur.HandleSelectionModification();
        }
        public void HandleResize()
        {
            editeur.HandleSelectionModification();
            // TODO : Send socket -> selection was resized
        }
        public void RestoreLastTrait()
        {
            editeur.Depiler(null);
        }
        public void HandleMouseDown(Point mousePos)
        {
            editeur.HandleMouseDown(mousePos);
        }
        public void HandleRotation(Point rotatePoint)
        {
            editeur.RotateForm(rotatePoint);

            //TODO : Send socket -> selection was rotated
        }

        /// <summary>
        /// C'est ici qu'est défini la forme de la pointe, mais aussi sa taille (TailleTrait).
        /// Pourquoi deux caractéristiques se retrouvent définies dans une même méthode? Parce que pour créer une pointe 
        /// horizontale ou verticale, on utilise une pointe carrée et on joue avec les tailles pour avoir l'effet désiré.
        /// </summary>
        private void AjusterPointe()
        {
            AttributsDessin.StylusTip = (editeur.PointeSelectionnee == "ronde") ? StylusTip.Ellipse : StylusTip.Rectangle;
            AttributsDessin.Width = (editeur.PointeSelectionnee == "verticale") ? 1 : editeur.TailleTrait;
            AttributsDessin.Height = (editeur.PointeSelectionnee == "horizontale") ? 1 : editeur.TailleTrait;
        }
    }
}
