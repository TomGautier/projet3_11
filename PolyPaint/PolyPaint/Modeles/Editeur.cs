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
using System.Collections.Generic;
using Newtonsoft.Json;

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
        public const double HEIGHT_PROPORTION = 1.00d;
        public const double WIDTH_PROPORTION = 1.00d;

        public event PropertyChangedEventHandler PropertyChanged;
        public StrokeCollection traits = new StrokeCollection();
        public StrokeCollection selectedStrokes = new StrokeCollection();
        private StrokeCollection traitsRetires = new StrokeCollection();
        public StrokeCollection LastCut = new StrokeCollection();

        public string ConnectorLabel { get; set; }
        public string ConnectorQ1 { get; set; }
        public string ConnectorQ2 { get; set; }
        public string ConnectorType { get; set; }
        public int ConnectorSize { get; set; }
        public string ConnectorColor { get; set; }
        public string ConnectorBorderStyle { get; set; }
        public double CanvasWidth { get; set; }
        public double CanvasHeight { get; set; }


        private bool showEncrage = false;
        public bool ShowEncrage
        {
            get{ return showEncrage; }
            set
            {
                showEncrage = value;                             
                foreach (Form form in this.traits) { form.ShowEncrage = showEncrage; }
                
            }

        }

        public SocketManager SocketManager { get; set; }
        public PlayerManager PlayerManager { get; set; }
        public FormConnectorManager FormConnectorManager { get; set; }
        // Outil actif dans l'éditeur
        private string outilSelectionne = "lasso";
        public string OutilSelectionne
        {
            get { return outilSelectionne; }
            set
            {
                if (outilSelectionne == "connexion" && value != outilSelectionne)
                {
                    this.ShowEncrage = false;
                    if (this.FormConnectorManager.IsDrawingArrow && this.FormConnectorManager.Arrows.Last().StylusPoints.Count <2)
                    {
                        this.traits.Remove(this.FormConnectorManager.Arrows.Last());
                        this.FormConnectorManager.reset();
                        
                    }
                }
                else if (value == "connexion") { this.ShowEncrage = true; }
                outilSelectionne = value;
                ProprieteModifiee();
            }
        }      
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
                
                if (selectedStrokes.Count > 0)
                {
                    Shape[] shapes = new Shape[selectedStrokes.Count];

                    for (int i = 0; i < selectedStrokes.Count; i++)
                    {
                        (selectedStrokes[i] as Form).DrawingAttributes.Color = (Color)System.Windows.Media.ColorConverter.ConvertFromString(couleurSelectionnee);
                        shapes[i] = (selectedStrokes[i] as Form).ConvertToShape(this.SocketManager.SessionID);
                    }
                    this.SocketManager.HandleModification(shapes);
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

                if (selectedStrokes.Count >0)
                {
                    Shape[] shapes = new Shape[selectedStrokes.Count];

                    for (int i = 0; i< selectedStrokes.Count; i++)
                    {                      
                        (selectedStrokes[i] as Form).Remplissage = (Color)System.Windows.Media.ColorConverter.ConvertFromString(remplissageSelectionne);
                        shapes[i] = (selectedStrokes[i] as Form).ConvertToShape(this.SocketManager.SessionID);
                    }
                    this.SocketManager.HandleModification(shapes);
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
                //selectedStrokes = strokes;
                CouleurSelectionnee = strokes[0].DrawingAttributes.Color.ToString();
                RemplissageSelectionne = (strokes[0] as Form).Remplissage.ToString();
               // ProprieteModifiee("TEST");
            }
            else
            {
                //selectedStrokes.Clear(); //= null;
                CouleurSelectionnee = "Black";
                RemplissageSelectionne = "White";
            }
           
        }
        private bool SameCollection(StrokeCollection a, StrokeCollection b)
        {
            if (a.Count != b.Count) { return false; }
            List<string> ids = new List<string>();
            foreach (Stroke s in a)
            {
                ids.Add((s as Form).Id);
            }
            foreach (Stroke s in b)
            {
                if (!ids.Contains((s as Form).Id))
                {
                    return false;
                }
            }
            return true;
        }
        public void LoadLocally(string json)
        {
            this.ResetCanvas();
            List<Shape> datalist = JsonConvert.DeserializeObject<List<Shape>>(json);
            foreach (Shape shape in datalist)
            {
                this.AddForm(shape,false);
            }
        }
        public void HandleChangeSelection(StrokeCollection strokes)
        {
            //ProprieteModifiee("Test");
            // bool validRequest = true;

            for (int i = 0; i < strokes.Count; i++)
            {
                if ((strokes[i] as Form).IsSelectedByOther) { strokes.RemoveAt(i); }

            }
            if (!SameCollection(selectedStrokes, strokes))
            {


                String[] toBeSelected = new String[strokes.Count];

                for (int i = 0; i < toBeSelected.Length; i++)
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
              this.SocketManager.CutElements(idList);
            }
           
        }
        public void HandleSelectionModification()
        {
            Shape[] modifiedShapes = new Shape[this.selectedStrokes.Count];

            for (int i = 0; i< modifiedShapes.Length; i++)
            {
                modifiedShapes[i] = (this.selectedStrokes[i] as Form).ConvertToShape(this.SocketManager.SessionID);
            }
            this.SocketManager.HandleModification(modifiedShapes);
        }
        public void HandleLabelChange(string label,string border)
        {
            (this.selectedStrokes[0] as Form).Label = label;
            (this.selectedStrokes[0] as Form).BorderStyle = border;

        }
        public void HandleUmlTextChange(string name,string border, List<string> methods, List<string> attributes)
        {
            (this.selectedStrokes[0] as UMLClass).Label = name;
            (this.selectedStrokes[0] as UMLClass).Methods = methods;
            (this.selectedStrokes[0] as UMLClass).Attributes = attributes;
            (this.selectedStrokes[0] as UMLClass).BorderStyle = border;

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
                if (traits.Count > 0)
                {
                    Stroke toEmpile = traits.Where(x => (x as Form).Type != "Arrow").Last();
                    SocketManager.StackElement((toEmpile as Form).Id);
                }
              /*  Stroke trait = traits.Last();
                traitsRetires.Add(trait);
                traits.Remove(trait);   */            
            }
            catch { }

        }
        public void UpdateArrow(Stroke arrow, int index, Point pts)
        {
            bool isOnEncrage = false;
            foreach (Form form in this.traits)
            {
                for (int i = 0; i < form.EncPoints.Length; i++)
                {
                    if (Math.Abs(Point.Subtract(form.EncPoints[i], pts).Length) < 20)
                    {
                        
                        if (index == 0)
                        {
                            if (form != (arrow as Arrow).Shape1)
                            {
                                isOnEncrage = true;
                                form.Arrow = (arrow as Arrow);
                                if ((arrow as Arrow).Shape1 != null)
                                {
                                    (arrow as Arrow).Shape1.Arrow = null;
                                }
                                (arrow as Arrow).Shape1 = form;
                                (arrow as Arrow).Index1 = i;

                                arrow.StylusPoints[index] = new StylusPoint(form.EncPoints[i].X, form.EncPoints[i].Y);
                            }
                        }
                        else
                        {
                            if (form != (arrow as Arrow).Shape1)
                            {
                                isOnEncrage = true;
                                form.Arrow = (arrow as Arrow);
                                if ((arrow as Arrow).Shape2 != null)
                                {
                                    (arrow as Arrow).Shape2.Arrow = null;
                                }
                                (arrow as Arrow).Shape2 = form;
                                (arrow as Arrow).Index2 = i;

                                arrow.StylusPoints[index] = new StylusPoint(form.EncPoints[i].X, form.EncPoints[i].Y);
                            }
                        }
                    }
                }
                if (!isOnEncrage)
                {
                    arrow.StylusPoints[index] = new StylusPoint(pts.X, pts.Y);
                    if (index == 0 && (arrow as Arrow).Shape1 != null)
                    {
                        (arrow as Arrow).Shape1.Arrow = null;
                        (arrow as Arrow).Shape1 = null;
                    }
                    else if (index == arrow.StylusPoints.Count - 1 && (arrow as Arrow).Shape2 != null)
                    {
                        (arrow as Arrow).Shape2.Arrow = null;
                        (arrow as Arrow).Shape2 = null;
                    }

                }
            }
        }
        private void HandleConnector(Point p)
        {

            bool isOnEncrage = false;
            bool newArrowCreated = false;
            foreach (Form form in this.traits)
            {
                for (int i = 0; i < form.EncPoints.Length; i++)
                {
                    if (Math.Abs(Point.Subtract(form.EncPoints[i], p).Length) < 20)
                    {
                        this.FormConnectorManager.SetParameters(this.ConnectorLabel, this.ConnectorType,this.ConnectorBorderStyle, this.ConnectorSize, this.ConnectorColor,this.ConnectorQ1,this.ConnectorQ2);
                        newArrowCreated = this.FormConnectorManager.update(new StylusPoint(form.EncPoints[i].X, form.EncPoints[i].Y), true, form, i);
                        
                        isOnEncrage = true;
                    }
                    
                }
            }
            if (!isOnEncrage)
            {
                if (!this.FormConnectorManager.IsDrawingArrow) { this.FormConnectorManager.SetParameters(this.ConnectorLabel, this.ConnectorType, this.ConnectorBorderStyle, this.ConnectorSize, this.ConnectorColor,this.ConnectorQ1,this.ConnectorQ2); }
                newArrowCreated = this.FormConnectorManager.update(new StylusPoint(p.X, p.Y), false, null, 0);
            }
            if (newArrowCreated)
            {
                this.traits.Add(this.FormConnectorManager.Arrows.Last());
                this.SocketManager.AddElement(this.FormConnectorManager.Arrows.Last().ConvertToShape(this.SocketManager.SessionID));
            }
        }
               /* if (Math.Abs(Point.Subtract(form.Center,p).Length) < 20)
                {
                    newArrowCreated = this.FormConnectorManager.update(new StylusPoint(form.Center.X,form.Center.Y), true, form);
                    isOnEncrage = true;
                }*/
      
        public void HandleMouseDown(Point p)
        {
            if (OutilSelectionne == "connexion")
            {
                HandleConnector(p);
            }
            
            if (OutilSelectionne.Contains("form"))
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
                    case "form_Texte":
                        form = new FloatingText(pts);
                        break;
                    case "form_Phase":
                        form = new Phase(pts);
                        break;
                    case "form_Comment":
                        form = new Comment(pts);
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
               /* Stroke trait = traitsRetires.Last();
                traits.Add(trait);
                traitsRetires.Remove(trait);*/
                if (traitsRetires.Count > 0)
                {
                    Stroke toUnstack = traitsRetires.Last();
                    SocketManager.UnStackElement((toUnstack as Form).ConvertToShape(this.SocketManager.SessionID));
                }
            }
            catch { }         
        }
        public void RotateForm(object sender)
        {
            if (selectedStrokes != null)
            {
                Shape[] shapes = new Shape[selectedStrokes.Count];
                for (int i = 0; i< selectedStrokes.Count; i++)
                {
                    (selectedStrokes[i] as Form).rotate();
                    shapes[i] = (selectedStrokes[i] as Form).ConvertToShape(this.SocketManager.SessionID);
                }
                this.SocketManager.HandleModification(shapes);
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
        public void AddForm(Shape shape, bool setSelection)
        {
            StylusPointCollection pts = new StylusPointCollection();
          /*  FormProperties properties = new FormProperties();
            if (shape.properties.type == "Arrow")
            {
                 properties = (shape.properties as ArrowProperties);
            }
            else
            {
                 properties = (shape.properties as ShapeProperties);
            }*/
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
                case "Text":
                    FloatingText text = new FloatingText(pts);
                    text.SetToShape(shape);
                    traits.Add(text);
                    break;
                case "Phase":
                    Phase phase = new Phase(pts);
                    phase.SetToShape(shape);
                    traits.Add(phase);
                    break;
                case "Comment":
                    Comment comment = new Comment(pts);
                    comment.SetToShape(shape);
                    traits.Add(comment);
                    break;
                case "Arrow":
                    Arrow arrow = new Arrow(pts);
                    arrow.SetToShape(shape);
                    traits.Add(arrow);
                    break;

            }
            if (setSelection)
            {
                if (shape.author == this.SocketManager.UserName)
                {
                    this.OutilSelectionne = "lasso";
                    ProprieteModifiee("OutilSelectionne");
                    this.selectedStrokes = new StrokeCollection { traits.Last() }; //select the new shape created               
                    ProprieteModifiee("Selection");

                }
                else
                {
                    (traits.Last() as Form).SelectionColor = this.PlayerManager.GetPlayer(shape.author).Color;
                    (traits.Last() as Form).IsSelectedByOther = true;
                }
            }
        }
        private void deleteElements(string[] list)
        {

            StrokeCollection toBeDeleted = new StrokeCollection(traits.Where(s => list.Contains((s as Form).Id)));
            foreach (Stroke s in toBeDeleted)
            {
                if ((s as Form).Arrow != null)
                {
                    traits.Remove((s as Form).Arrow);
                }
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
                        (s as Form).SelectionColor = this.PlayerManager.GetPlayer(username).Color;
                        (s as Form).IsSelectedByOther = true;
                        
                    }
                }
            }
            this.ChangeSelection(selectedStrokes);
            ProprieteModifiee("Selection");
        }
      
        
        // On assigne une nouvelle forme de pointe passée en paramètre.
        public void ChoisirPointe(string pointe) => PointeSelectionnee = pointe;

        // L'outil actif devient celui passé en paramètre.
        public void ChoisirOutil(string outil) => OutilSelectionne = outil;

        // On vide la surface de dessin de tous ses traits.
        public void Reinitialiser(object o)
        {
            this.SocketManager.Reinitialiser();
        }
        private void ResetCanvas()
        {
            this.traits.Clear();
            this.traitsRetires.Clear();
            this.selectedStrokes.Clear();
            this.LastCut.Clear();
        }

        public void HandleDuplicate(object o)
        {
            if (this.selectedStrokes.Count > 0)
            {
                
            }
        }
        public void HandleErasing(Stroke stroke)
        {           
                String[] idList = new string[1];
                idList[0] = (stroke as Form).Id;

            /* Shape[] shapes = new Shape[this.selectedStrokes.Count];
             for (int i = 0; i < shapes.Length; i++)
             {
                 shapes[i] = (this.selectedStrokes[i] as Form).ConvertToShape(this.SocketManager.SessionID);

             }
             this.SocketManager.DeleteElement(shapes);*/
            if (!(stroke as Form).IsSelectedByOther)
            {
                this.SocketManager.DeleteElement(idList);
            }
            
        }


        public void initializeSocketEvents()
        {
            this.SocketManager.Socket.On("CanvasReset", (data) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    this.ResetCanvas();
                });
            });
            this.SocketManager.Socket.On("NewUserJoined", (data) =>
            {
                //JObject result = (data as JObject);
                String[] users = new string[(data as JArray).Count];
                for (int i = 0; i <users.Length; i++)
                {
                    users[i] = (data as JArray)[i].ToObject<String>();
                    if (!this.PlayerManager.Contain(users[i]))
                    { 
                        this.PlayerManager.AddPlayer(users[i]);
                    }
                    
                }
            });
            this.SocketManager.Socket.On("ResizedCanvas", (data) =>
            {
                JObject result = (data as JObject);
                double[] dimensions = new double[2];
                dimensions = result["newCanvasDimensions"].ToObject<double[]>();

                Application.Current.Dispatcher.Invoke(() =>
                {
                    this.CanvasWidth = dimensions[0];
                    this.CanvasHeight = dimensions[1];
                    ProprieteModifiee("CanvasSize");
                    
                });
                
            });
            this.SocketManager.Socket.On("AddedElement", (data) =>
                {

                    //  string author = (data as JObject)["author"].ToObject<String>();
                    //   string id = (data as JObject)["id"].ToObject<String>();
                    JObject result = (data as JObject);                
                    Shape shape = result["shape"].ToObject<Shape>();
                    //Shape shape = (data as JObject).ToObject<Shape>();//["properties"].ToObject<Shape>();
                    Application.Current.Dispatcher.Invoke(() =>
                     {
                         this.AddForm(shape,true);
                         // Code causing the exception or requires UI thread access
                     });

                });
            this.SocketManager.Socket.On("DeletedElements", (data) =>
            {

                //  string author = (data as JObject)["author"].ToObject<String>();
                //   string id = (data as JObject)["id"].ToObject<String>();
                // Shape shape = (data as JObject).ToObject<Shape>();//["properties"].ToObject<Shape>();
                JObject result = (data as JObject);
                String[] idList = new string[(result["elementIds"] as JArray).Count];

                for (int i = 0; i < idList.Length; i++)
                {
                    idList[i] = (result["elementIds"] as JArray)[i].ToObject<String>();
                }
                //string list = (data as JArray)[0].ToObject<String>();//(data as JObject).ToObject<String[]>();
                Application.Current.Dispatcher.Invoke(() =>
                {
                    this.deleteElements(idList);
                    // this.AddForm(shape);
                    // Code causing the exception or requires UI thread access
                });

            });
            this.SocketManager.Socket.On("CutedElements", (data) =>
            {
                JObject result = (data as JObject);
                String[] idList = new string[(result["elementIds"] as JArray).Count];
                string username = result["username"].ToObject<String>();
                for (int i = 0; i < idList.Length; i++)
                {
                    idList[i] = (result["elementIds"] as JArray)[i].ToObject<String>();
                }
                //string list = (data as JArray)[0].ToObject<String>();//(data as JObject).ToObject<String[]>();
                Application.Current.Dispatcher.Invoke(() =>
                {              

                    if (username == this.SocketManager.UserName)
                    {
                        StrokeCollection toBeDeleted = new StrokeCollection(traits.Where(s => idList.Contains((s as Form).Id)));
                        LastCut = toBeDeleted;
                    }
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
                });              
            });
            this.SocketManager.Socket.On("StackedElement", (data) =>
            {
                JObject result = (data as JObject);
                String username = result["username"].ToObject<String>();
                String id = result["elementId"].ToObject<String>();
                //Shape shape = (data as JObject).ToObject<Shape>();
                Stroke toStack = (traits.Where(x => (x as Form).Id == id).First());
                Application.Current.Dispatcher.Invoke(() =>
                {
                    if (this.SocketManager.UserName == username)
                    {
                        this.traitsRetires.Add(toStack as Form);
                    }
                    this.deleteElements(new string[] { (toStack as Form).Id });
                });
            });
            this.SocketManager.Socket.On("UnstackedElement", (data) =>
            {
                JObject result = (data as JObject);
                String username = result["username"].ToObject<String>();
                Shape shape = result["shape"].ToObject<Shape>();

                StylusPointCollection pts = new StylusPointCollection();
                pts.Add(new StylusPoint(0, 0));
                Form toUnstack = new Form(pts);
                toUnstack.SetToShape(shape);

                Application.Current.Dispatcher.Invoke(() =>
                {
                    this.AddForm(shape,true);

                    if (this.SocketManager.UserName == username)
                    {
                      
                        Stroke toRemove = this.traitsRetires.Where(x=> (x as Form).Id == shape.id).Last();
                        this.traitsRetires.Remove(toRemove);
                    }
                });
            });
            this.SocketManager.Socket.On("ModifiedElement", (data) =>
            {
                JObject result = (data as JObject);
                String username = result["username"].ToObject<String>();
                Shape[] shapes = result["shapes"].ToObject<Shape[]>();
                //int rotation = shapes[0].properties.rotation;
                Application.Current.Dispatcher.Invoke(() =>
                {                 
                    if (username != this.SocketManager.UserName)
                    {
                        foreach (Shape s in shapes)
                        {
                            for (int i = 0; i < traits.Count; i++)
                            {
                                if ((traits[i] as Form).Id == s.id)
                                {
                                    (traits[i] as Form).SetToShape(s);
                                }
                            }
                            //StrokeCollection toBeModified = new StrokeCollection(traits.Where(t => (t as Form).Id == s.id));
                            //(toBeModified[0] as Form).SetToShape(s);
                        }
                    }
                });
                
                     
            });
            


            //drawingSessionId = this.SessionID,
           // username = this.UserName,
           //     oldElementIds = oldSelection,
           //     newElementIds = newSelection
        }
    }
}
