using System;
using System.Windows;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Controls.Primitives;
using PolyPaint.VueModeles;
using System.Windows.Ink;
using PolyPaint.Utilitaires;
using System.Windows.Controls;

namespace PolyPaint
{
    /// <summary>
    /// Logique d'interaction pour FenetreDessin.xaml
    /// </summary>
    public partial class FenetreDessin : Window
    {
        public FenetreDessin()
        {
            InitializeComponent();
            DataContext = new VueModele();
            (DataContext as VueModele).SendCanvas(this.surfaceDessin);
            this.surfaceDessin.AllowSelection = false;
        }
        
        // Pour gérer les points de contrôles.
        private void GlisserCommence(object sender, DragStartedEventArgs e) => (sender as Thumb).Background = Brushes.Black;
        private void GlisserTermine(object sender, DragCompletedEventArgs e) => (sender as Thumb).Background = Brushes.White;
        private void GlisserMouvementRecu(object sender, DragDeltaEventArgs e)
        {
            String nom = (sender as Thumb).Name;
            if (nom == "horizontal" || nom == "diagonal") colonne.Width = new GridLength(Math.Max(32, colonne.Width.Value + e.HorizontalChange));
            if (nom == "vertical" || nom == "diagonal") ligne.Height = new GridLength(Math.Max(32, ligne.Height.Value + e.VerticalChange));
        }

        // Pour la gestion de l'affichage de position du pointeur.
        private void surfaceDessin_MouseLeave(object sender, MouseEventArgs e) => textBlockPosition.Text = "";
        private void surfaceDessin_MouseMove(object sender, MouseEventArgs e)
        {
            Point p = e.GetPosition(surfaceDessin);
            textBlockPosition.Text = Math.Round(p.X) + ", " + Math.Round(p.Y) + "px";
        }
        private void surfaceDessin_SelectionChanging(object sender, InkCanvasSelectionChangingEventArgs e)
        {
            if (!this.surfaceDessin.AllowSelection) //if the change is from the view
            {
                (DataContext as VueModele).HandleSelection(e.GetSelectedStrokes());
                e.Cancel = true;
            }

        }
        private void surfaceDessin_PreviewMouseDown(object sender, MouseButtonEventArgs e)
        {
            (DataContext as VueModele).HandleMouseDown(e.GetPosition(surfaceDessin));
        }

        private void DupliquerSelection(object sender, RoutedEventArgs e)
        {
            
            StrokeCollection selection = surfaceDessin.GetSelectedStrokes();
            if (selection.Count == 0 && (DataContext as VueModele).LastCut != null)
            {
                surfaceDessin.Strokes.Add((DataContext as VueModele).LastCut);
                (DataContext as VueModele).LastCut = null;
            }
            foreach (Stroke form in selection)
            {
                Form duplicate = (Form)(form.Clone());
                duplicate.translate(30, 30);
                surfaceDessin.Strokes.Add(duplicate);
               
            }
        }
        

        private void SupprimerSelection(object sender, RoutedEventArgs e)
        {
            (DataContext as VueModele).HandleSelectionSuppression();
           // (DataContext as VueModele).LastCut = surfaceDessin.GetSelectedStrokes();
           // surfaceDessin.CutSelection();        
        }
        
        private void TextBox_TextChanged(object sender, EventArgs e)
        {

        }

        private void PointeRonde_Click(object sender, RoutedEventArgs e)
        {

        }

        private void TextBox_TextChanged(object sender, System.Windows.Controls.TextChangedEventArgs e)
        {

        }

      /*  private void surfaceDessin_SelectionMoved(object sender, EventArgs e)
        {
            
        }*/
        private void surfaceDessin_SelectionResizing(object sender, EventArgs e)//object sender, InkCanvasSelectionEditingEventArgs e)
        {
            (DataContext as VueModele).HandleResize();
        }
        private void surfaceDessin_StrokeErasing(object sender, InkCanvasStrokeErasingEventArgs e)
        {
            e.Cancel = true; //Prevent deletion
        }
        void surfaceDessin_HandleKey(object sender, KeyEventArgs e)
        {
            if (e.Key == Key.Delete && e.OriginalSource is InkCanvas) //Redirect to Cut handler if Key.delete is pressed
            {
                e.Handled = true;
                SupprimerSelection(null, null);
            }
        }
        private void ChatControl_Loaded(object sender, RoutedEventArgs e) { }

        private void surfaceDessin_SelectionMoved(object sender, EventArgs e)
        {
            (DataContext as VueModele).HandleDrag();
        }
    }
}
