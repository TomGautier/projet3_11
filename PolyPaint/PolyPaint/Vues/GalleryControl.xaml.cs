using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Interop;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using Newtonsoft.Json;
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

        private async void JoinDrawSession_Click(object sender, RoutedEventArgs e)
        {
            string sessionID = ((Grid)((Button)sender).Parent).Children.OfType<Label>().AsEnumerable().Single(x => x.Name == "SessionID").Content.ToString();
            if (!await ((VueModele)DataContext).JoinDrawSession(sessionID))
            {
                ((StackPanel)((Grid)((Button)sender).Parent).Parent).Children.OfType<Grid>().AsEnumerable().Last().Visibility = (((StackPanel)((Grid)((Button)sender).Parent).Parent).Children.OfType<Grid>().AsEnumerable().Last().Visibility == Visibility.Visible ? Visibility.Collapsed : Visibility.Visible);
            }
        }

        private void SecuredBtn_Click(object sender, RoutedEventArgs e)
        {
            string password = ((Grid)((Button)sender).Parent).Children.OfType<TextBox>().AsEnumerable().Single(x => x.Name == "PwdTextBox").Text;
            string sessionID = ((StackPanel)((Grid)((Button)sender).Parent).Parent).Children.OfType<Grid>().First().Children.OfType<Label>().AsEnumerable().Single(x => x.Name == "SessionID").Content.ToString();
            ((VueModele)DataContext).JoinSecuredDrawSession(sessionID, password);
        }

        public class GalleryItem
        {
            public string id { get; set; }
            public string author { get; set; }
            public string visibility { get; set; }
            public string protection { get; set; }
            public string thumbnail { get; set; }
            public Int64 thumbnailTimestamp { get; set; }
            public ImageSource image { get; set; }
        }

        private void PublicBtn_Click(object sender, RoutedEventArgs e)
        {
            ((VueModele)DataContext).LoadGallery("public");
        }

        private void PrivateBtn_Click(object sender, RoutedEventArgs e)
        {
            ((VueModele)DataContext).LoadGallery("private");
        }

        private void TryEditProtection_Click(object sender, RoutedEventArgs e)
        {
            string author = ((StackPanel)((Grid)((Button)sender).Parent).Parent).Children.OfType<Grid>().First().Children.OfType<Label>().AsEnumerable().Single(x => x.Name == "Author").Content.ToString();
            if((DataContext as VueModele).canChangeProtection(author))
            {
                ((StackPanel)((Grid)((Button)sender).Parent).Parent).Children.OfType<Grid>().AsEnumerable().ElementAt(1).Visibility = (((StackPanel)((Grid)((Button)sender).Parent).Parent).Children.OfType<Grid>().AsEnumerable().ElementAt(1).Visibility == Visibility.Visible ? Visibility.Collapsed : Visibility.Visible);
            }
        }

        private void ChangeBtn_Click(object sender, RoutedEventArgs e)
        {
            string NewCode = ((Grid)((Button)sender).Parent).Children.OfType<TextBox>().AsEnumerable().Single(x => x.Name == "NewCode").Text;
            ((Grid)((Button)sender).Parent).Children.OfType<TextBox>().AsEnumerable().Single(x => x.Name == "NewCode").Clear();
            string sessionID = ((StackPanel)((Grid)((Button)sender).Parent).Parent).Children.OfType<Grid>().First().Children.OfType<Label>().AsEnumerable().Single(x => x.Name == "SessionID").Content.ToString();
            (DataContext as VueModele).ChangeProtection(sessionID, NewCode);
            ((Grid)((Button)sender).Parent).Visibility = Visibility.Collapsed;
        }
    }
}
