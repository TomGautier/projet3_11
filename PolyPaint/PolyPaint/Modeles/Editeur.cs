using System.ComponentModel;
//using System.Drawing;
using System.Windows;
using System.Linq;
using System.Runtime.CompilerServices;
using System.Windows.Ink;
using System.Windows.Input;
using PolyPaint.Utilitaires;
using System.Windows.Media;
using PolyPaint.Managers;
using Newtonsoft.Json.Linq;
using System;

//using System.Drawing;

namespace PolyPaint.Modeles
{
    /// <summary>
    /// Modélisation de l'éditeur.
    /// Contient ses différents états et propriétés ainsi que la logique
    /// qui régis son fonctionnement.
    /// </summary>
    class Editeur : INotifyPropertyChanged
    {
        
        public event PropertyChangedEventHandler PropertyChanged;
        public StrokeCollection traits = new StrokeCollection();
        public StrokeCollection selectedStrokes = new StrokeCollection();
        private StrokeCollection traitsRetires = new StrokeCollection();
        
        public SocketManager SocketManager { get; set; }
        // Outil actif dans l'éditeur
        private string outilSelectionne = "lasso";
        public string OutilSelectionne
        {
            get { return outilSelectionne; }
            set { outilSelectionne = value; ProprieteModifiee(); }
        }
        public StrokeCollection LastCut { get; set; }
        // Forme de la pointe du crayon
        private string pointeSelectionnee = "ronde";
        public string PointeSelectionnee
        {
            get { return pointeSelectionnee; }
            set
            {
                pointeSelectionnee = value;                                
                ProprieteModifiee();
            }
        }
        

        // Couleur des traits tracés par le crayon.
        private string couleurSelectionnee = "Black";
        public string CouleurSelectionnee
        {
            get { return couleurSelectionnee; }
            // Lorsqu'on sélectionne une couleur c'est généralement pour ensuite dessiner un trait.
            // C'est pourquoi lorsque la couleur est changée, l'outil est automatiquement changé pour le crayon.
            set
            {
                couleurSelectionnee = value;
                
                if (selectedStrokes != null)
                {
                    foreach (Stroke form in selectedStrokes)
                    {
                        form.DrawingAttributes.Color = (Color)System.Windows.Media.ColorConverter.ConvertFromString(couleurSelectionnee);
                    }
                }
                ProprieteModifiee();                   
            }
        }
        private string remplissageSelectionne = "White";
        public string RemplissageSelectionne
        {
            get { return remplissageSelectionne; }

            set
            {
                remplissageSelectionne = value;

                if (selectedStrokes != null)
                {


                    foreach (Form form in selectedStrokes)
                    {                   
                        form.Remplissage = (Color)System.Windows.Media.ColorConverter.ConvertFromString(remplissageSelectionne);
                        
                    }
                }
                ProprieteModifiee();
            }
        }


        // Grosseur des traits tracés par le crayon.
        private int tailleTrait = 11;
        public int TailleTrait
        {
            get { return tailleTrait; }
            // Lorsqu'on sélectionne une taille de trait c'est généralement pour ensuite dessiner un trait.
            // C'est pourquoi lorsque la taille est changée, l'outil est automatiquement changé pour le crayon.
            set
            {
                tailleTrait = value;
                ProprieteModifiee();
            }
        }
        public void ChangeSelection(StrokeCollection strokes)
        {         
            if (strokes.Count > 0)
            {
                selectedStrokes = strokes;
                CouleurSelectionnee = strokes[0].DrawingAttributes.Color.ToString();
                RemplissageSelectionne = (strokes[0] as Form).Remplissage.ToString();
               // ProprieteModifiee("TEST");
            }
            else
            {
                selectedStrokes = null;
                CouleurSelectionnee = "Black";
                RemplissageSelectionne = "White";
            }
           
        }
        public void HandleChangeSelection(StrokeCollection strokes)
        {
            //ProprieteModifiee("Test");
           // bool validRequest = true;
          
            for (int i = 0; i< strokes.Count; i++)
            {
               if((strokes[i] as Form).IsSelectedByOther) { strokes.RemoveAt(i); }
                
            }
            String[] toBeSelected = new String[strokes.Count];
            
            for (int i = 0; i< toBeSelected.Length; i++)
            {
                toBeSelected[i] = (strokes[i] as Form).Id;
            }
            String[] oldSelection = new string[selectedStrokes.Count];
            for (int i = 0; i < oldSelection.Length; i++)
            {
                oldSelection[i] = (selectedStrokes[i] as Form).Id;
            }
            this.SocketManager.Select(oldSelection, toBeSelected);
            //this.SocketManager
        }
        public void HandleDeleteSelection()
        {
            if (selectedStrokes != null && selectedStrokes.Count > 0)
            {
                String[] idList = new string[this.selectedStrokes.Count];
                for (int i = 0; i < idList.Length; i++)
                {
                    idList[i] = (this.selectedStrokes[i] as Form).Id;
                }
               /* Shape[] shapes = new Shape[this.selectedStrokes.Count];
                for (int i = 0; i < shapes.Length; i++)
                {
                    shapes[i] = (this.selectedStrokes[i] as Form).ConvertToShape(this.SocketManager.SessionID);
                  
                }
                this.SocketManager.DeleteElement(shapes);*/
              this.SocketManager.DeleteElement(idList);
            }
           
        }
        /// <summary>
        /// Appelee lorsqu'une propriété d'Editeur est modifiée.
        /// Un évènement indiquant qu'une propriété a été modifiée est alors émis à partir d'Editeur.
        /// L'évènement qui contient le nom de la propriété modifiée sera attrapé par VueModele qui pourra
        /// alors prendre action en conséquence.
        /// </summary>
        /// <param name="propertyName">Nom de la propriété modifiée.</param>
        protected void ProprieteModifiee([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }

        // S'il y a au moins 1 trait sur la surface, il est possible d'exécuter Empiler.
        public bool PeutEmpiler(object o) => (traits.Count > 0); 
        // On retire le trait le plus récent de la surface de dessin et on le place sur une pile.
        public void Empiler(object o)
        {
            try
            {
                Stroke trait = traits.Last();
                traitsRetires.Add(trait);
                traits.Remove(trait);               
            }
            catch { }

        }
        public void HandleMouseDown(Point p)
        {
            if (outilSelectionne.Contains("form"))
            {
                //Point center = new Point((int)position.X, (int)position.Y);
                StylusPointCollection pts = new StylusPointCollection();
                pts.Add(new StylusPoint(p.X, p.Y));
                Form form = new Form(pts);
       
                switch (outilSelectionne)
                {
                    case "form_UmlClass":
                        form = new UMLClass(pts);
                        //height = UMLClass.DEFAULT_HEIGHT;
                        // = UMLClass.DEFAULT_WIDTH;
                        //type = UMLClass.TYPE;
                        break;
                    case "form_Artefact":
                        form = new Artefact(pts);
                        //height = Artefact.DEFAULT_HEIGHT;
                        //width = Artefact.DEFAULT_WIDTH;
                        //type = Artefact.TYPE;
                        break;

                    case "form_Activity":
                        form = new Activity(pts);
                        //height = Activity.DEFAULT_HEIGHT;
                        // width = Activity.DEFAULT_WIDTH;
                        //type = Activity.TYPE;

                        break;
                    case "form_Role":
                        form = new Role(pts);
                        //height = Role.DEFAULT_HEIGHT;
                        // width = Role.DEFAULT_WIDTH;
                        //type = Role.TYPE;

                        break;
                }
                //SocketManager.AddElement(type, RemplissageSelectionne, CouleurSelectionnee, center,height,width,0);
                form.DrawingAttributes.Color = (Color)System.Windows.Media.ColorConverter.ConvertFromString(CouleurSelectionnee);
                form.Remplissage = (Color)System.Windows.Media.ColorConverter.ConvertFromString(RemplissageSelectionne);
                SocketManager.AddElement(form.ConvertToShape(this.SocketManager.SessionID));
                //AddForm(new Point((int)position.X, (int)position.Y),outilSelectionne);
            }
        }

        // S'il y a au moins 1 trait sur la pile de traits retirés, il est possible d'exécuter Depiler.
        public bool PeutDepiler(object o) => (traitsRetires.Count > 0);
        public void Depiler(object o)
        {
            try
            {
                Stroke trait = traitsRetires.Last();
                traits.Add(trait);
                traitsRetires.Remove(trait);
            }
            catch { }         
        }
        public void RotateForm(object sender)
        {
            if (selectedStrokes != null)
            {
                foreach (Form form in selectedStrokes)
                {
                    form.rotate();
                }
            }
        }
        public void AddForm(Point p, string forme)
        {
            StylusPointCollection pts = new StylusPointCollection();                   
            pts.Add(new StylusPoint(p.X, p.Y));

            switch (forme)
            {
                case "form_UmlClass":                       
                    UMLClass umlClass = new UMLClass(pts);
                    umlClass.DrawingAttributes.Color = (Color)System.Windows.Media.ColorConverter.ConvertFromString(CouleurSelectionnee);
                    umlClass.Remplissage = (Color)System.Windows.Media.ColorConverter.ConvertFromString(RemplissageSelectionne);
                    traits.Add(umlClass);
                   

                    break;
                case "form_Artefact":
                    Artefact artefact = new Artefact(pts);
                    artefact.DrawingAttributes.Color = (Color)System.Windows.Media.ColorConverter.ConvertFromString(CouleurSelectionnee);
                    artefact.Remplissage = (Color)System.Windows.Media.ColorConverter.ConvertFromString(RemplissageSelectionne);
                    traits.Add(artefact);
                    break;
                case "form_Activity":
                    Activity activity = new Activity(pts);
                    activity.DrawingAttributes.Color = (Color)System.Windows.Media.ColorConverter.ConvertFromString(CouleurSelectionnee);
                    activity.Remplissage = (Color)System.Windows.Media.ColorConverter.ConvertFromString(RemplissageSelectionne);
                    traits.Add(activity);
                    break;
                case "form_Role":
                    Role role = new Role(pts);
                    role.DrawingAttributes.Color = (Color)System.Windows.Media.ColorConverter.ConvertFromString(CouleurSelectionnee);
                    role.Remplissage = (Color)System.Windows.Media.ColorConverter.ConvertFromString(RemplissageSelectionne);
                    traits.Add(role);
                    break;
            }
            
        }
        public void AddForm(Shape shape)
        {
            StylusPointCollection pts = new StylusPointCollection();
            pts.Add(new StylusPoint(shape.properties.middlePointCoord[0], shape.properties.middlePointCoord[1]));
            switch (shape.properties.type)
            {
                case "UmlClass":
                    UMLClass umlClass = new UMLClass(pts);
                    umlClass.SetToShape(shape);

                    traits.Add(umlClass);


                    break;
                case "Artefact":
                    Artefact artefact = new Artefact(pts);
                    artefact.SetToShape(shape);
                    traits.Add(artefact);
                    break;

                case "Activity":
                    Activity activity = new Activity(pts);
                    activity.SetToShape(shape);
                    traits.Add(activity);
                    break;

                case "Role":
                    Role role = new Role(pts);
                    role.SetToShape(shape);
                    traits.Add(role);
                    break;
            }
        }
        private void deleteElements(string[] list)
        {

            StrokeCollection toBeDeleted = new StrokeCollection(traits.Where(s => list.Contains((s as Form).Id)));
            foreach (Stroke s in toBeDeleted)
            {
                traits.Remove(s);
            }
        }
        private void selectElements(string username, string[] oldIds, string[] newIds)
        {

            foreach (Stroke s in traits)
            {
                if (oldIds.Contains((s as Form).Id))
                {
                    if (username == this.SocketManager.UserName)
                    {
                        selectedStrokes.Remove(s);
                    }
                    else
                    {
                        (s as Form).IsSelectedByOther = false; ;
                    }
                }
                else if (newIds.Contains((s as Form).Id))
                {
                    if (username == this.SocketManager.UserName)
                    {
                        selectedStrokes.Add(s);
                    }
                    else
                    {
                        (s as Form).IsSelectedByOther = true;
                    }
                }
            }
            ProprieteModifiee("Selection");
        }
      
        
        // On assigne une nouvelle forme de pointe passée en paramètre.
        public void ChoisirPointe(string pointe) => PointeSelectionnee = pointe;

        // L'outil actif devient celui passé en paramètre.
        public void ChoisirOutil(string outil) => OutilSelectionne = outil;

        // On vide la surface de dessin de tous ses traits.
        public void Reinitialiser(object o) => traits.Clear();

        public void initializeSocketEvents()
        {

            this.SocketManager.Socket.On("AddedElement", (data) =>
                {

                  //  string author = (data as JObject)["author"].ToObject<String>();
                 //   string id = (data as JObject)["id"].ToObject<String>();
                    Shape shape = (data as JObject).ToObject<Shape>();//["properties"].ToObject<Shape>();
                    Application.Current.Dispatcher.Invoke(() =>
                     {
                         this.AddForm(shape);
                         // Code causing the exception or requires UI thread access
                     });
                
                });
            this.SocketManager.Socket.On("DeletedElements", (data) =>
            {

                //  string author = (data as JObject)["author"].ToObject<String>();
                //   string id = (data as JObject)["id"].ToObject<String>();
                // Shape shape = (data as JObject).ToObject<Shape>();//["properties"].ToObject<Shape>();
                String[] idList = new string[(data as JArray).Count];
                for (int i = 0; i< idList.Length; i++)
                {
                    idList[i] = (data as JArray)[i].ToObject<String>();
                }
                //string list = (data as JArray)[0].ToObject<String>();//(data as JObject).ToObject<String[]>();
                Application.Current.Dispatcher.Invoke(() =>
                {
                    this.deleteElements(idList);
                   // this.AddForm(shape);
                    // Code causing the exception or requires UI thread access
                });

            });
            this.SocketManager.Socket.On("SelectedElements", (data) =>
            {
                JObject result = (data as JObject);
                string username = result["username"].ToObject<String>();
                string[] oldIds = result["oldElementIds"].ToObject<String[]>();
                string[] newIds = result["newElementIds"].ToObject<String[]>();
                // string username = (data as JObject)["username"].ToObject<string>();
                // String[] oldIds = new string[(data as JObject)["oldElementIds"].Count];
                //  string author = (data as JObject)["author"].ToObject<String>();
                //   string id = (data as JObject)["id"].ToObject<String>();
                //  Shape shape = (data as JObject).ToObject<Shape>();//["properties"].ToObject<Shape>();
                Application.Current.Dispatcher.Invoke(() =>
                {
                    this.selectElements(username, oldIds, newIds);
                    // Code causing the exception or requires UI thread access
                });

            });

            //drawingSessionId = this.SessionID,
           // username = this.UserName,
           //     oldElementIds = oldSelection,
           //     newElementIds = newSelection
        }
    }
}
