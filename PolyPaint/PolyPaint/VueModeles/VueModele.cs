using System.ComponentModel;
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
using System;
using System.Web.Script.Serialization;
using Newtonsoft.Json;
using System.Net.Http;
using System.IO;

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
        public bool IsOffline
        {
            get
            {
                return editeur.IsOffline;
            }
            set
            {
                editeur.IsOffline = value;
                if (!editeur.IsOffline)
                {
                    //SendLocalCanvas();
                }
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
        public PlayerManager PlayerManager
        {
            get { return editeur.PlayerManager; }
            set { editeur.PlayerManager = value; }
        }
        public FormConnectorManager FormConnectorManager
        {
            get { return editeur.FormConnectorManager; }
            set { editeur.FormConnectorManager = value; }
        }

        private string username = RandomString(5);
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
        public Stroke StrokeBeingDragged { get; set; }
        public Stroke StrokeBeingRotated { get; set; }
        public bool isDragging { get; set; }
        public int IndexBeingDragged { get; set; }

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
        public string ConnectorQ1
        {
            get { return editeur.ConnectorQ1; }
            set { editeur.ConnectorQ1 = value; }
        }
        public string ConnectorQ2
        {
            get { return editeur.ConnectorQ2; }
            set { editeur.ConnectorQ2 = value; }
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
        public string ConnectorBorderStyle
        {
            get { return editeur.ConnectorBorderStyle; }
            set { editeur.ConnectorBorderStyle = value; }
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
        public ICommand NavigateNewSession { get { return new RelayCommand(OnNavigateNewSession, () => { return true; }); } }
        public ICommand NavigateForgotPWD { get { return new RelayCommand(OnNavigateForgotPwd, () => { return true; }); } }
        public ICommand NavigateHome { get { return new RelayCommand(OnNavigateHome, () => { return true; }); } }

        private void OnNavigateHome()
        {
            SwitchView = 0;
        }

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
            LoadGallery("public");
            SwitchView = 4;
        }

        private void OnNavigateNewSession()
        {
            string newDrawingId = Username + System.DateTimeOffset.UtcNow.ToUnixTimeSeconds().ToString();

            networkManager.PostImage(Username, SessionId, newDrawingId, "public", "");
            
            JoinDrawSession(newDrawingId);
        }

        private void OnNavigateForgotPwd()
        {
            SwitchView = 6;
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
        public async void RequestPwd(string email)
        {
            await networkManager.RequestPwdAsync(Username, email);
        }

        public async void NewPassword(string oldPassword, string newPassword)
        {
            {
                await networkManager.ForgotPWDAsync(Username, oldPassword, newPassword);
                Login(newPassword);
            }
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

        public bool JoinDrawSession(string joinningSessionID)
        {
            GalleryControl.GalleryItem info = GalleryItems.Find(x => x.id == joinningSessionID);
            if (info.protection != "")
                return false;

            var format = new
            {
                sessionId = SessionId,
                username = Username,
                imageId = joinningSessionID
            };

            SocketManager.Socket.Emit("JoinDrawingSession", JsonConvert.SerializeObject(format));

            SocketManager.Socket.On("JoinedDrawingSession", () => {
                if(info != null)
                {
                    //TODO : Load canvas from GalleryItems 
                }
                else
                {
                    //TODO : Load default canvas
                }
            });
            SwitchView = 5;
            return true;
        }

        public void JoinSecuredDrawSession(string joinningSessionID, string pwd)
        {
            GalleryControl.GalleryItem info = GalleryItems.Find(x => x.id == joinningSessionID);
            if (info.protection != pwd)
            {
                MessageBox.Show("Wrong password", "Error");
                return;
            }

            var format = new
            {
                sessionId = SessionId,
                username = Username,
                imageId = joinningSessionID
            };

            SocketManager.Socket.Emit("JoinDrawingSession", JsonConvert.SerializeObject(format));

            SocketManager.Socket.On("JoinedDrawingSession", () => {
                    //TODO : Load canvas from GalleryItems 
            });
            SwitchView = 5;
        }

        public async void LoadGallery(string galleryType)
        {
            string gallery;
            if (galleryType == "private")
            {
                gallery = await networkManager.LoadUserImageAsync(Username, SessionId);
            }
            else
            {
                gallery = await networkManager.LoadAllImageAsync(Username, SessionId);
            }
            if(GalleryItems != null)
                GalleryItems.Clear();
            GalleryItems = JsonConvert.DeserializeObject<List<GalleryControl.GalleryItem>>(gallery, new JsonSerializerSettings { NullValueHandling = NullValueHandling.Ignore });
        }

        public async System.Threading.Tasks.Task<List<ChatControl.UserItem>> LoadUsersAsync()
        {
            string userList = await networkManager.LoadUsersAsync(Username, SessionId);

            List<ChatControl.UserItem> userItems = new List<ChatControl.UserItem>();
            
            var users = JsonConvert.DeserializeObject<List<ChatControl.UserItemTemplate>>(userList);

            foreach (var user in users)
            {
                if (user.Username == Username)
                    continue;
                userItems.Add(new ChatControl.UserItem() { Username = user.Username, ConnectionStatus = user.ConnectionStatus ? 1 : 0 });
            }

            return userItems.OrderByDescending(x => x.ConnectionStatus).ToList(); ;
        }

        private class channelTemplate
        {
            public Object participants;
            public string name;
        }

        public async void LoadChannelAsync()
        {
            string channelList = await networkManager.LoadChannelAsync(Username, SessionId);

            List<channelTemplate> channels = JsonConvert.DeserializeObject<List<channelTemplate>>(channelList);

            ChatManager.RoomsID.Clear();
            foreach (var channel in channels)
            {
                ChatManager.RoomsID.Add(channel.name);
            }
        }

        public void addChannelAsync()
        {
            try
            {
                networkManager.CreateChannelAsync(Username, SessionId, ChatManager.NewRoomID);
            }
            catch (Exception)
            {
                MessageBox.Show("Chanel already exists", "Error");
                return;
            }
            ChatManager.RoomsID.Add(ChatManager.NewRoomID);
            ChatManager.RoomID = ChatManager.NewRoomID;
        }

        /// <summary>
        /// Constructeur de VueModele
        /// On récupère certaines données initiales du modèle et on construit les commandes
        /// sur lesquelles la vue se connectera.
        /// </summary>
        public VueModele()
        {
            this.IsOffline = false;
            
            this.Canvas = new CustomInkCanvas();

            FormConnectorManager = new FormConnectorManager();
            SocketManager = new SocketManager(this.IsOffline);
            PlayerManager = new PlayerManager();

            //SocketManager.JoinDrawingSession("MockSessionID");
            ChatManager.socket = SocketManager.Socket;
            //SocketManager.UserName = "Olivier";
            if (!this.IsOffline)
            {
                SocketManager.UserName = this.Username;
                this.SessionId = "MockSessionId";
                editeur.initializeSocketEvents();
                SocketManager.JoinDrawingSession("MockSessionID");
                //this.SendLocalCanvas();
            }
            else
            {
                this.PlayerManager.AddPlayer(this.SocketManager.UserName);
            }
            // On écoute pour des changements sur le modèle. Lorsqu'il y en a, EditeurProprieteModifiee est appelée.
            editeur.PropertyChanged += new PropertyChangedEventHandler(EditeurProprieteModifiee);

            // On initialise les attributs de dessin avec les valeurs de départ du modèle.
            AttributsDessin = new DrawingAttributes();
            AttributsDessin.Color = (Color)ColorConverter.ConvertFromString(editeur.CouleurSelectionnee);
            AjusterPointe();

            Traits = editeur.traits;
            StylusPointCollection pts = new StylusPointCollection();

            //editeur.traits.Add(new Stroke(pts));
            SelectedStrokes = editeur.selectedStrokes;
            this.IndexBeingDragged = -1;



            // Pour chaque commande, on effectue la liaison avec des méthodes du modèle.            
            Empiler = new RelayCommand<object>(editeur.Empiler);//, editeur.PeutEmpiler);
            Depiler = new RelayCommand<object>(editeur.Depiler);//, editeur.PeutDepiler);
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
        public void SendLocalCanvas()
        {
            int compteur = 0;
            foreach (string file in Directory.EnumerateFiles(Directory.GetCurrentDirectory() + "/Backup/", "*.txt"))
            {
                string contents = File.ReadAllText(file);

                var parameters = new
                {
                    author = this.Username,
                    imageId = this.Username + "_" + compteur,
                    visibility = "protected",
                    protection = "public"

                    
                };
                compteur++;
                this.networkManager.CreateImage(parameters,this.SessionId,this.Username);

                string canvas = new JavaScriptSerializer().Serialize(new
                {
                    shapes = contents

                });
                this.networkManager.SendLocalCanvas(this.SocketManager.UserName, this.SessionId, canvas);
               

            }
            string[] filePaths = Directory.GetFiles(Directory.GetCurrentDirectory() + "/Backup");
            foreach (string filePath in filePaths)
                File.Delete(filePath);

            //HttpClient client = new HttpClient();
            //client.PostAsync("http://127.0.0.1:3000/api/images/" +this.SessionId + "/"+ this.Username  , null);
        }
        public void HandleCanvasResize()//double width, double height)
        {
            if (this.IsOffline) { this.editeur.ResizedCanvas(new double[2] { this.Canvas.Width, this.Canvas.Height }); }
            else
            {
                this.SocketManager.ResizeCanvas((int)this.Canvas.Width, (int)this.Canvas.Height);
            }
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
            else if (e.PropertyName == "CanvasSize")
            {
                this.Canvas.Height = editeur.CanvasHeight;
                this.Canvas.Width = editeur.CanvasWidth;
            }
            else if (e.PropertyName == "RemplissageSelectionne")
            {
                ProprieteModifiee(e.PropertyName);
            }
            else if (e.PropertyName == "OutilSelectionne")
            {
                OutilSelectionne = editeur.OutilSelectionne;
                editeur.HandleChangeSelection(new StrokeCollection());
                //ProprieteModifiee(e.PropertyName);
                switch (OutilSelectionne)
                {
                    case "lasso":
                        this.Canvas.EditingMode = InkCanvasEditingMode.Select;
                        break;
                    case "efface_trait":
                        this.Canvas.EditingMode = InkCanvasEditingMode.EraseByStroke;
                        break;
                    default:
                        this.Canvas.EditingMode = InkCanvasEditingMode.None;
                        break;
                }

                //InkCanvasEditingMode current = this.Canvas.EditingMode;

                // this.Canvas.EditingMode = current;

                //StrokeCollection S = this.Canvas.GetSelectedStrokes();
                // editeur.HandleChangeSelection(this.Canvas.GetSelectedStrokes());
                //    this.HandleSelection(this.Canvas.GetSelectedStrokes());            
            }
            else if (e.PropertyName == "Selection")
            {
                this.Canvas.AllowSelection = true;

                this.Canvas.Select(editeur.selectedStrokes);
                this.Canvas.AllowSelection = false;
                this.Canvas.ResizeEnabled = true;
                this.Canvas.MoveEnabled = true;
                switch (OutilSelectionne)
                {
                    case "lasso":
                        this.Canvas.EditingMode = InkCanvasEditingMode.Select;
                        break;
                    case "efface_trait":
                        this.Canvas.EditingMode = InkCanvasEditingMode.EraseByStroke;
                        break;
                    default:
                        this.Canvas.EditingMode = InkCanvasEditingMode.None;
                        break;
                }
                //this.Canvas.EditingMode = InkCanvasEditingMode.None;

                foreach (Stroke s in this.SelectedStrokes)
                {
                    if ((s as Form).Type == "Text")
                    {
                        this.Canvas.ResizeEnabled = false;
                    }
                    else if ((s as Form).Type == "Arrow")
                    {
                        this.Canvas.ResizeEnabled = false;
                        this.Canvas.MoveEnabled = false;
                    }
                }

            }

        }
        public void HandleSelection(StrokeCollection strokes)
        {
            if (strokes.Count > 1)
            {
                editeur.HandleChangeSelection(strokes);
            }
            else if (strokes.Count == 1 && this.isDragging)
            {
                editeur.HandleChangeSelection(strokes);
                
                //this.StrokeBeingLassoed = strokes[0];
                  //this.HandlePreviewMouseDown((strokes[0] as Form).Center);
            }
            this.isDragging = false;
            //TODO : Send socket -> selection was changed
        }
        public void SetConnectorSettings(string label, string type, string border, int size, string color, string q1, string q2)
        {
            this.ConnectorLabel = label;
            this.ConnectorType = type;
            this.ConnectorBorderStyle = border;
            this.ConnectorSize = size;
            this.ConnectorColor = color;
            this.ConnectorQ1 = q1;
            this.ConnectorQ2 = q2;
        }
        public void HandleLabelChange(string label, string border)
        {
            editeur.HandleLabelChange(label, border);
        }
        public void HandleUmlTextChange(string name, string border, List<string> methods, List<string> attributes)
        {
            editeur.HandleUmlTextChange(name, border, methods, attributes);
            editeur.HandleSelectionModification();
        }
        public void HandleSelectionSuppression()
        {
            editeur.HandleDeleteSelection();
        }
        public void HandleDrag()
        {
            editeur.HandleSelectionModification();
            // TODO : Send socket -> selection has moved
            /* foreach (Stroke s in this.SelectedStrokes)
             {
                 if ((s as Form).Center.X > this.Canvas.Width || (s as Form).Center.X < 0)
                 {
                     (s as Form).Center = new Point(this.Canvas.Width / 2, (s as Form).Center.Y);
                     (s as Form).MakeShape();
                 }
                 if ((s as Form).Center.Y > this.Canvas.Height || (s as Form).Center.Y < 0)
                 {
                     (s as Form).Center = new Point((s as Form).Center.X, this.Canvas.Height / 2);
                     (s as Form).MakeShape();
                 }
             }*/

        }
        public void HandleResize()
        {
            editeur.HandleSelectionModification();
            // TODO : Send socket -> selection was resized
        }
        public void HandleErasing(Stroke stroke)
        {
            editeur.HandleErasing(stroke);
        }
        public void RestoreLastTrait()
        {
            editeur.Depiler(null);
        }
        public string ConvertCanvasToString()
        {
            List<Shape> shapes_ = new List<Shape>();
            foreach (Stroke s in Traits)
            {
                shapes_.Add((s as Form).ConvertToShape(this.SocketManager.SessionID));
            }
            /* string parameters = new JavaScriptSerializer().Serialize(new
             {            
                 shapes = shapes_
             });*/
            var serializer = new JavaScriptSerializer();
            string parameters = serializer.Serialize(shapes_);
            return parameters;
        }
        public void LoadLocally(string json) {
            this.editeur.LoadLocally(json);
        }

        public void HandleMouseDown(Point mousePos)
        {

            /*         else
                     {
                         editeur.HandleMouseDown(mousePos);
                     }*/
        }
        public void HandleMouseMove(Point mousePos)
        {
            if (this.StrokeBeingDragged != null)
            {
                this.StrokeBeingDragged.StylusPoints[this.IndexBeingDragged] = new StylusPoint(mousePos.X, mousePos.Y);
            }
            else if (this.StrokeBeingRotated != null)
            {
                CalculateAngle(mousePos);
            }
        }
        private void CalculateAngle(Point mousePos)
        {
            Vector direction = (this.StrokeBeingRotated as Form).Center - mousePos;
            Vector origin = new Vector(0, 1);//(this.StrokeBeingRotated as Form).HeightDirection;
            //direction.Normalize();
            double angle = Vector.AngleBetween(origin, direction);
            (this.StrokeBeingRotated as Form).SetRotation((int)angle);
            if (angle != 0)
            {

            }
        }
        public void HandlePreviewMouseDown(Point mousePos)
        {
            this.isDragging = true;
            Rect selectionZone = this.Canvas.GetSelectionBounds();
            if (selectionZone.Size != Size.Empty)
            {

                selectionZone.Inflate(new Size(15, 15)); //To cover the resizing bounds
            }
            //if (this.OutilSelectionne == "lasso" && this.StrokeBeingDragged == null)
            if (this.OutilSelectionne == "lasso" && this.StrokeBeingRotated == null && this.StrokeBeingDragged == null)
            {
                foreach (Stroke s in SelectedStrokes.Where(x => (x as Form).Type != "Arrow"))
                {
                    Point pts = (s as Form).RotatePoint;
                    if (Math.Abs(Point.Subtract(pts, mousePos).Length) < 10)
                    {
                        this.Canvas.MoveEnabled = false;
                        this.StrokeBeingRotated = s;
                        this.isDragging = false;
                        //this.Canvas.EditingMode = InkCanvasEditingMode.None;
                    }
                }
            }
            if (this.OutilSelectionne == "lasso" && this.StrokeBeingDragged == null && this.StrokeBeingRotated == null)
            {
                foreach (Stroke s in SelectedStrokes.Where(x => (x as Form).Type == "Arrow"))
                {
                    for (int i = 0; i < s.StylusPoints.Count; i++)
                    {
                        Point pts = s.StylusPoints[i].ToPoint();
                        if (Math.Abs(Point.Subtract(pts, mousePos).Length) < 10 && this.IndexBeingDragged == -1)
                        {
                            this.StrokeBeingDragged = s;
                            this.IndexBeingDragged = i;
                            this.isDragging = false;
                            editeur.ShowEncrage = true;
                            //this.Canvas.EditingMode = InkCanvasEditingMode.None;
                        }
                    }
                }
            }
            if (this.OutilSelectionne == "lasso" && !selectionZone.Contains(mousePos) && this.StrokeBeingDragged == null && this.StrokeBeingRotated == null)
            {
                StrokeCollection selection = new StrokeCollection();
                for (int i = Traits.Count - 1; i >= 0; i--)
                {
                    if (Traits[i].GetBounds().Contains(mousePos) && selection.Count == 0) //&& (Traits[i] as Form).Type != "Arrow")
                    {
                        if ((Traits[i] as Form).Type == "Arrow")
                        {
                            if (Traits[i].HitTest(mousePos)) { selection.Add(Traits[i]); }

                        }
                        else
                        {
                            selection.Add(Traits[i]);
                        }
                    }

                }
                editeur.HandleChangeSelection(selection);


            }
            else
            {
                this.isDragging = false;
                editeur.HandleMouseDown(mousePos);
            }
        }
            
        
        public void HandlePreviewMouseUp(Point mousePos)
        {
            //this.isDragging = false;
            if (this.StrokeBeingDragged != null)
            {
                editeur.ShowEncrage = false;
                this.Canvas.EditingMode = InkCanvasEditingMode.Select;
                editeur.UpdateArrow(this.StrokeBeingDragged, this.IndexBeingDragged,mousePos);
                //this.StrokeBeingDragged.StylusPoints[this.IndexBeingDragged] = new StylusPoint(mousePos.X, mousePos.Y);
                Shape[] shapes = new Shape[1];
                shapes[0] = (this.StrokeBeingDragged as Form).ConvertToShape(this.SocketManager.SessionID);
                if (IsOffline) { this.editeur.ModifiedElements(shapes); }
                else
                {
                    this.SocketManager.HandleModification(shapes);
                }
                this.StrokeBeingDragged = null;
                this.IndexBeingDragged = -1;
            }
            else if (this.StrokeBeingRotated != null)
            {               
                //this.Canvas.EditingMode = InkCanvasEditingMode.Select;
                this.Canvas.MoveEnabled = true;
                Shape[] shapes = new Shape[1];
                shapes[0] = (this.StrokeBeingRotated as Form).ConvertToShape(this.SocketManager.SessionID);
                if (IsOffline) { this.editeur.ModifiedElements(shapes); }
                else
                {
                    this.SocketManager.HandleModification(shapes);
                }
                this.StrokeBeingRotated = null;
            }
            
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
        private static Random random = new Random();
        public static string RandomString(int length)
        {
            const string chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            return new string(Enumerable.Repeat(chars, length)
              .Select(s => s[random.Next(s.Length)]).ToArray());
        }
    }
}
