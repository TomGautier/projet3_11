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
    /// Interaction logic for Tutorial.xaml
    /// </summary>
    public partial class Tutorial : UserControl
    {
        private int index;
        private string[] images;

        public Tutorial()
        {
            InitializeComponent();

            this.Loaded += new RoutedEventHandler((s,e) =>
            {
                index = 0;
                images = System.IO.Directory.GetFiles(System.IO.Directory.GetParent(Environment.CurrentDirectory).Parent.FullName + "/Resources/Tutorials/" + (DataContext as VueModele).Localization, "*.jpg");
                ((Image)FindName("Slideshow")).Source = new BitmapImage(new Uri(images[index], UriKind.Absolute));
            });
        }

        private void Previous_Click(object sender, RoutedEventArgs e)
        {
            ((Image)FindName("Slideshow")).Source = new BitmapImage(new Uri(images[--index], UriKind.Absolute));
            if(index == 0)
                ((Button)FindName("Previous")).IsEnabled = false;
            ((Button)FindName("Next")).IsEnabled = true;
        }

        private void Next_Click(object sender, RoutedEventArgs e)
        {
            ((Image)FindName("Slideshow")).Source = new BitmapImage(new Uri(images[++index], UriKind.Absolute));
            if (index == images.Length - 1)
                ((Button)FindName("Next")).IsEnabled = false;
            ((Button)FindName("Previous")).IsEnabled = true;
        }
    }
}
