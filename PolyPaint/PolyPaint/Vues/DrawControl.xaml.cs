using Microsoft.Win32;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using PolyPaint.Utilitaires;
using PolyPaint.VueModeles;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Controls.Primitives;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using Shape = PolyPaint.Utilitaires.Shape;

namespace PolyPaint.Vues
{
    /// <summary>
    /// Interaction logic for DrawControl.xaml
    /// </summary>
    public partial class DrawControl : UserControl
    {
        public string LastDrag { get; set; }
        public DrawControl()
        {
            InitializeComponent();
            DataContext = new VueModele();
            
            this.surfaceDessin.AllowSelection = false;
            this.surfaceDessin.IsDraging = false;
            this.surfaceDessin.LastSelection = new MemoryStream();
            this.AllowDrop = true;
            (DataContext as VueModele).SendCanvas(this.surfaceDessin);

        }

        // Pour gérer les points de contrôles.
        private void GlisserCommence(object sender, DragStartedEventArgs e)
        {
            //this.surfaceDessin.IsDraging = true;
            
            (sender as Thumb).Background = Brushes.Black;
        }
        private void GlisserTermine(object sender, DragCompletedEventArgs e)
        {
            //this.surfaceDessin.IsDraging = false;
            (sender as Thumb).Background = Brushes.White;
            (DataContext as VueModele).HandleCanvasResize();
            //this.surfaceDessin.Height = 5;
            
        }

        void surfaceDessin_SelectionMoving(object sender, InkCanvasSelectionEditingEventArgs e)
        {
            // this.surfaceDessin.IsDraging = true;
        }
        private void GlisserMouvementRecu(object sender, DragDeltaEventArgs e)
        {
            //String nom = (sender as Thumb).Name;
            //LastDrag = (sender as Thumb).Name;
            LastDrag = "diagonal";            //if (nom == "horizontal" || nom == "diagonal") colonne.Width = new GridLength(Math.Max(32, colonne.Width.Value + e.HorizontalChange));
            //if (nom == "vertical" || nom == "diagonal") ligne.Height = new GridLength(Math.Max(32, ligne.Height.Value + e.VerticalChange));
            this.surfaceDessin.Width += e.HorizontalChange;
            this.surfaceDessin.Height += e.VerticalChange;
            
        }
        

        // Pour la gestion de l'affichage de position du pointeur.
        private void surfaceDessin_MouseLeave(object sender, MouseEventArgs e) => textBlockPosition.Text = "";
        private void surfaceDessin_MouseMove(object sender, MouseEventArgs e)
        {
            Point p = e.GetPosition(surfaceDessin);
            textBlockPosition.Text = Math.Round(p.X) + ", " + Math.Round(p.Y) + "px";
            (DataContext as VueModele).HandleMouseMove(e.GetPosition(surfaceDessin));
        }
        
        private void surfaceDessin_SelectionChanging(object sender, InkCanvasSelectionChangingEventArgs e)
        {
            if (!this.surfaceDessin.AllowSelection) //if the change is from the view
            {
                (DataContext as VueModele).HandleSelection(e.GetSelectedStrokes());
                e.Cancel = true;
            }

        }
        private void surfaceDessin_SizeChanged(object sender, SizeChangedEventArgs e)
        {
            // if (LastDrag == "horizontal" || LastDrag == "diagonal") colonne.Width = new GridLength(Math.Max(32, colonne.Width.Value + e.NewSize.Width - e.PreviousSize.Width));
            if (!(e.PreviousSize.Height == 0 && e.PreviousSize.Width == 0))
            {
                colonne.Width = new GridLength(Math.Max(32, colonne.Width.Value + e.NewSize.Width - e.PreviousSize.Width));
                // if (LastDrag == "vertical" || LastDrag == "diagonal") ligne.Height = new GridLength(Math.Max(32, ligne.Height.Value + e.NewSize.Height - e.PreviousSize.Height
                ligne.Height = new GridLength(Math.Max(32, ligne.Height.Value + e.NewSize.Height - e.PreviousSize.Height));
            }
    

            this.surfaceDessin.Width = e.NewSize.Width;
            this.surfaceDessin.Height = e.NewSize.Height;
            
           // (DataContext as VueModele).HandleCanvasResize(e.NewSize);
        }

        private void surfaceDessin_PreviewMouseDown(object sender, MouseButtonEventArgs e)
        {

            (DataContext as VueModele).HandlePreviewMouseDown(e.GetPosition(surfaceDessin));
            this.surfaceDessin.IsDraging = true;
        }
        private void surfaceDessin_PreviewMouseUp(object sender, MouseButtonEventArgs e)
        {
            (DataContext as VueModele).HandlePreviewMouseUp(e.GetPosition(surfaceDessin));
            this.surfaceDessin.IsDraging = false;
        }
        private void surfaceDessin_Drop(object sender, DragEventArgs e)
        {

            // (DataContext as VueModele).HandleDrop(e.GetPosition(surfaceDessin));
        }
        private void surfaceDessin_MouseDown(object sender, MouseButtonEventArgs e)
        {
            (DataContext as VueModele).HandleMouseDown(e.GetPosition(surfaceDessin));

        }


        private void DupliquerSelection(object sender, RoutedEventArgs e)
        {

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
        private void SaveLocally(object sender, RoutedEventArgs e)
        {
            StreamWriter myStream;
            SaveFileDialog save = new SaveFileDialog();

            save.Filter = "txt files (*.txt)|*.txt";
            save.FilterIndex = 2;
            save.RestoreDirectory = true;
            Nullable<bool> result = save.ShowDialog();
            if (result == true)
            {
                myStream = new StreamWriter(save.OpenFile());
                myStream.Write((DataContext as VueModele).ConvertCanvasToString());
                myStream.Dispose();

                myStream.Close();
            }
        }
        private void LoadLocally(object sender, RoutedEventArgs e)
        {
            StreamReader myStream;
            OpenFileDialog open = new OpenFileDialog();

            open.Filter = "txt files (*.txt)|*.txt";
            open.FilterIndex = 2;
            open.RestoreDirectory = true;
            Nullable<bool> result = open.ShowDialog();
            if (result == true)
            {
                myStream = new StreamReader(open.OpenFile());
                string json = myStream.ReadToEnd();
                (DataContext as VueModele).LoadLocally(json);
                //List<Shape> datalist = JsonConvert.DeserializeObject<List<Shape>>(json);
                // List<Shape> datalist = JsonConvert.DeserializeObject<List<Shape>>(json);
            }
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
            string q1 = (sender as ConnectorSetter).Quantification1List.Text;
            string q2 = (sender as ConnectorSetter).Quantification2List.Text;
            //Console.WriteLine(type);
            surfaceDessin.Visibility = Visibility.Visible;

            (DataContext as VueModele).SetConnectorSettings(label, type, border, size, color,q1,q2);
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

                UmlClassSetter menu = new UmlClassSetter(name, border, methods, attributes);
                surfaceDessin.Visibility = Visibility.Hidden;
                menu.Show();
                menu.Closing += new CancelEventHandler(UMLSetterClosingHandler);

            }
            else
            {
                string label = (surfaceDessin.GetSelectedStrokes()[0] as Form).Label;
                string border = (surfaceDessin.GetSelectedStrokes()[0] as Form).BorderStyle;
                GenericSetter menu = new GenericSetter(label, border);
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

            (DataContext as VueModele).HandleUmlTextChange(name, border, methods, attributes);
        }
        private void GenericSetterClosingHandler(object sender, CancelEventArgs e)
        {
            surfaceDessin.Visibility = Visibility.Visible;
            string label = (sender as GenericSetter).Label;
            string border = (sender as GenericSetter).borderList.Text;
            (DataContext as VueModele).HandleLabelChange(label, border);
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
        private void surfaceDessin_SelectionResized(object sender, EventArgs e)//object sender, InkCanvasSelectionEditingEventArgs e)
        {
            this.surfaceDessin.IsDraging = false;
            (DataContext as VueModele).HandleResize();
        }
        private void surfaceDessin_SelectionResizing(object sender, InkCanvasSelectionEditingEventArgs e)//object sender, InkCanvasSelectionEditingEventArgs e)
        {
            this.surfaceDessin.Select(surfaceDessin.GetSelectedStrokes());
            this.surfaceDessin.IsDraging = true;
            //(DataContext as VueModele).HandleResize();
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
            //this.surfaceDessin.IsDraging = false;
            (DataContext as VueModele).HandleDrag();
        }
        
    }
}