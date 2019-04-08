using PolyPaint.VueModeles;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace PolyPaint.Vues
{
    /// <summary>
    /// Interaction logic for ConnectorSetter.xaml
    /// </summary>
    public partial class ConnectorSetter : Window
    {
        public ConnectorSetter()
        {
            InitializeComponent();
            DataContext = new VueModele();
            for (int i = 1; i <= 20; i++)
            {
                sizeList.Items.Add(i + " px");
            }
            sizeList.SelectedIndex = 1;
            typeList.SelectedIndex = 0;
            borderList.SelectedIndex = 0;
            selecteurCouleur.SelectedColor = Colors.Black;




        }
        private void connectorSetter_LabelChanged(object sender, TextChangedEventArgs args)
        {

        }
        private void textSetter_ApplyChanges(object sender, RoutedEventArgs e)
        {
            this.Close();
        }
    }
}
