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
    /// Interaction logic for GenericSetter.xaml
    /// </summary>
    public partial class GenericSetter : Window
    {
        public string Label { get; set; }
        public GenericSetter(string label,string border)
        {
            this.Label = label;
            InitializeComponent();
            DataContext = new VueModele();
            borderList.SelectedIndex = 0;
            //borderList.Text = border;
            txtLabel.Text = this.Label;
        }
        private void genericSetter_LabelChanged(object sender, TextChangedEventArgs args)
        {
            Label = txtLabel.Text;
        }
        private void genericSetter_ApplyChanges(object sender, RoutedEventArgs e)
        {
            this.Close();
        }
    }
}
