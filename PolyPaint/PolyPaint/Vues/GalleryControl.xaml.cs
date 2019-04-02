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
using System.Windows.Navigation;
using System.Windows.Shapes;
using PolyPaint.VueModeles;

namespace PolyPaint.Vues
{
    /// <summary>
    /// Interaction logic for GalleryControl.xaml
    /// </summary>
    public partial class GalleryControl : UserControl
    {
        public GalleryControl()
        {
            InitializeComponent();
        }

        private void JoinDrawSession_Click(object sender, RoutedEventArgs e)
        {
            string sessionID = ((Grid)((Button)sender).Parent).Children.OfType<Label>().AsEnumerable().Single(x => x.Name == "SessionID").Content.ToString();
            ((VueModele)DataContext).JoinDrawSession(sessionID);
        }

        public class GalleryItem
        {
            public string Author { get; set; }
            public string SessionID { get; set; }
            //Image ???
        }
    }
}
