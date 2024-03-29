﻿using System;
using System.Windows;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Controls.Primitives;
using PolyPaint.VueModeles;
using System.Windows.Ink;
using PolyPaint.Utilitaires;
using System.Windows.Controls;
using PolyPaint.Vues;
using System.ComponentModel;
using System.Collections.Generic;

namespace PolyPaint
{
    /// <summary>
    /// Logique d'interaction pour FenetreDessin.xaml
    /// </summary
    
   
    public partial class FenetreDessin : Window
    {
        
        public FenetreDessin()
        {
            InitializeComponent();
            DataContext = new VueModele();
            (DataContext as VueModele).SendCanvas(this.surfaceDessin);
            this.surfaceDessin.AllowSelection = false;
            this.surfaceDessin.IsDraging = false;
        }

        // Pour gérer les points de contrôles.
        private void GlisserCommence(object sender, DragStartedEventArgs e) {
             //this.surfaceDessin.IsDraging = true;
             (sender as Thumb).Background = Brushes.Black;
        }
        private void GlisserTermine(object sender, DragCompletedEventArgs e)
        {
            //this.surfaceDessin.IsDraging = false;
            (sender as Thumb).Background = Brushes.White;
        }

        void surfaceDessin_SelectionMoving(object sender, InkCanvasSelectionEditingEventArgs e)
        {
            this.surfaceDessin.IsDraging = true;
        }
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
           // (DataContext as VueModele).HandleDuplicate();
            
            /*StrokeCollection selection = surfaceDessin.GetSelectedStrokes();
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
               
            }*/
        }
        

        private void SupprimerSelection(object sender, RoutedEventArgs e)
        {
            (DataContext as VueModele).HandleSelectionSuppression();
           // (DataContext as VueModele).LastCut = surfaceDessin.GetSelectedStrokes();
           // surfaceDessin.CutSelection();        
        }
        private void surfaceDessin_OpenConnectorSettings(object sender, RoutedEventArgs e)
        {
            ConnectorSetter menu = new ConnectorSetter();
            surfaceDessin.Visibility = Visibility.Hidden;
            menu.Show();
            menu.Closing += new CancelEventHandler(ConnectorSetterClosingHandler);
            
        }

        private void ConnectorSetterClosingHandler(object sender, CancelEventArgs e)
        {
            string label = (sender as ConnectorSetter).txtLabel.Text;
            string type = (sender as ConnectorSetter).typeList.Text;
            string border = (sender as ConnectorSetter).borderList.Text;
            int size = Convert.ToInt32((sender as ConnectorSetter).sizeList.SelectedItem.ToString().Trim(new char[] { 'p', 'x' }));
            string color = (sender as ConnectorSetter).selecteurCouleur.SelectedColor.ToString();
            Console.WriteLine(type);
            surfaceDessin.Visibility = Visibility.Visible;

      //     (DataContext as VueModele).SetConnectorSettings(label, type, border, size, color);
        }

        private void surfaceDessin_SetSelectionText(object sender, RoutedEventArgs e)
        {
            if (surfaceDessin.GetSelectedStrokes().Count > 1)
            {
                MessageBox.Show("Error : Too many elements selected");
            }
            else if (surfaceDessin.GetSelectedStrokes().Count == 0)
            {
                MessageBox.Show("Error : No element selected");
            }
            else if ((surfaceDessin.GetSelectedStrokes()[0] as Form).Type == "UmlClass")
            {
                string name = (surfaceDessin.GetSelectedStrokes()[0] as UMLClass).Label;
                string border = (surfaceDessin.GetSelectedStrokes()[0] as UMLClass).BorderStyle;
                List<string> methods = (surfaceDessin.GetSelectedStrokes()[0] as UMLClass).Methods;
                List<string> attributes = (surfaceDessin.GetSelectedStrokes()[0] as UMLClass).Attributes;

                UmlClassSetter menu = new UmlClassSetter(name,border,methods,attributes);
                surfaceDessin.Visibility = Visibility.Hidden;
                menu.Show();
                menu.Closing+= new CancelEventHandler(UMLSetterClosingHandler);

            }
            else
            {
                string label = (surfaceDessin.GetSelectedStrokes()[0] as Form).Label;
                string border = (surfaceDessin.GetSelectedStrokes()[0] as Form).BorderStyle;
                GenericSetter menu = new GenericSetter(label,border);
                surfaceDessin.Visibility = Visibility.Hidden;
                menu.Show();
                menu.Closing += new CancelEventHandler(GenericSetterClosingHandler);
            }
           
        }

        private void UMLSetterClosingHandler(object sender, CancelEventArgs e)
        {
            surfaceDessin.Visibility = Visibility.Visible;
            string name = (sender as UmlClassSetter).Name;
            string border = (sender as UmlClassSetter).borderList.Text;
            List<string> methods = (sender as UmlClassSetter).Methods;
            List<string> attributes = (sender as UmlClassSetter).Attributes;
            
            (DataContext as VueModele).HandleUmlTextChange(name,border,methods, attributes);
        }
        private void GenericSetterClosingHandler(object sender, CancelEventArgs e)
        {
            surfaceDessin.Visibility = Visibility.Visible;
            string label = (sender as GenericSetter).Label;
            string border = (sender as GenericSetter).borderList.Text;
            (DataContext as VueModele).HandleLabelChange(label,border);
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
            (DataContext as VueModele).HandleErasing(e.Stroke);
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
            this.surfaceDessin.IsDraging = false;
            (DataContext as VueModele).HandleDrag();
        }
    }
}
