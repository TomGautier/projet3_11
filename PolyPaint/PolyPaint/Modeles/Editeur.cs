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
            }
            else
            {
                selectedStrokes = null;
                CouleurSelectionnee = "Black";
                RemplissageSelectionne = "White";
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
        public void HandleMouseDown(Point position)
        {
            if (outilSelectionne.Contains("form"))
            {
                Point center = new Point((int)position.X, (int)position.Y);
                int height = 0;
                int width = 0;
                string type = "";
                switch (outilSelectionne)
                {
                    case "form_UmlClass":
                        height = UMLClass.DEFAULT_HEIGHT;
                        width = UMLClass.DEFAULT_WIDTH;
                        type = UMLClass.TYPE;
                        break;
                    case "form_Artefact":
                        height = Artefact.DEFAULT_HEIGHT;
                        width = Artefact.DEFAULT_WIDTH;
                        type = Artefact.TYPE;
                        break;

                    case "form_Activity":

                        height = Activity.DEFAULT_HEIGHT;
                        width = Activity.DEFAULT_WIDTH;
                        type = Activity.TYPE;

                        break;
                    case "form_Role":

                        height = Role.DEFAULT_HEIGHT;
                        width = Role.DEFAULT_WIDTH;
                        type = Role.TYPE;

                        break;
                }
                SocketManager.AddElement(type, RemplissageSelectionne, CouleurSelectionnee, center,height,width,0);
                AddForm(new Point((int)position.X, (int)position.Y),outilSelectionne);
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
        
        // On assigne une nouvelle forme de pointe passée en paramètre.
        public void ChoisirPointe(string pointe) => PointeSelectionnee = pointe;

        // L'outil actif devient celui passé en paramètre.
        public void ChoisirOutil(string outil) => OutilSelectionne = outil;

        // On vide la surface de dessin de tous ses traits.
        public void Reinitialiser(object o) => traits.Clear();
    }
}
