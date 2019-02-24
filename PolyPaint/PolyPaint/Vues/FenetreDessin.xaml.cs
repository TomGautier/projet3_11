using System;
using System.Windows;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Controls.Primitives;
using PolyPaint.VueModeles;
using System.Windows.Ink;
using PolyPaint.Utilitaires;

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
            DataContext = new VueModele(surfaceDessin);
            
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
        private void surfaceDessin_SelectionChanged(object sender, EventArgs e)
        {
            (DataContext as VueModele).HandleSelection(surfaceDessin.GetSelectedStrokes());
            
        }


        private void DupliquerSelection(object sender, RoutedEventArgs e)
        {          
            surfaceDessin.CopySelection();
            surfaceDessin.Paste();
        }

        private void SupprimerSelection(object sender, RoutedEventArgs e) => surfaceDessin.CutSelection();
        
        private void TextBox_TextChanged(object sender, EventArgs e)
        {

        }

        private void PointeRonde_Click(object sender, RoutedEventArgs e)
        {

        }

        private void TextBox_TextChanged(object sender, System.Windows.Controls.TextChangedEventArgs e)
        {

        }
        /*private void rotate_Selection(object sender, RoutedEventArgs e)
        {    
            //Matrix rotatingMatrix = new Matrix();
            
            Point rotatePoint = new Point(surfaceDessin.ActualWidth / 2, surfaceDessin.ActualHeight / 2);
            //(DataContext as VueModele);
            StrokeCollection selection = surfaceDessin.GetSelectedStrokes();
           // rotatingMatrix.RotateAt(currentRotation+10, rotatePoint.X, rotatePoint.Y);
           
        }*/
    }
}
