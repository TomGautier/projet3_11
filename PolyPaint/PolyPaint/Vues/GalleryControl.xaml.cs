﻿using System;
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

        private void JoinDrawSession_Click(object sender, RoutedEventArgs e)
        {
            string sessionID = ((Grid)((Button)sender).Parent).Children.OfType<Label>().AsEnumerable().Single(x => x.Name == "SessionID").Content.ToString();
            if (!((VueModele)DataContext).JoinDrawSession(sessionID))
            {
                ((Grid)((Button)sender).Parent).Children.OfType<Grid>().AsEnumerable().First().Visibility = Visibility.Visible;
            }
        }

        private void SecuredBtn_Click(object sender, RoutedEventArgs e)
        {
            string password = ((Grid)((Button)sender).Parent).Children.OfType<TextBox>().AsEnumerable().Single(x => x.Name == "PwdTextBox").Text;
            string sessionID = ((Grid)((Grid)((Button)sender).Parent).Parent).Children.OfType<Label>().AsEnumerable().Single(x => x.Name == "SessionID").Content.ToString();
            ((VueModele)DataContext).JoinSecuredDrawSession(sessionID, password);
        }

        public class ShapeProperties
        {
            public string type { get; set; }
            public string fillingColor { get; set; }
            public string borderColor { get; set; }
            public float[] middlePointCoord { get; set; }
            public float height { get; set; }
            public float width { get; set; }
            public float rotation { get; set; }
        }

        public class Shape
        {
            public string id { get; set; }
            public string imageId { get; set; }
            public string author { get; set; }
            public ShapeProperties properties { get; set; }
        }

        public class ShapeConnectionsProperties
        {
            public string type { get; set; }
            public string color { get; set; }
            public float[] points { get; set; }
            public string shape1id { get; set; }
            public string shape2id { get; set; }
        }

        public class ShapeConnections
        {
            public string id { get; set; }
            public string imageId { get; set; }
            public string author { get; set; }
            public ShapeConnectionsProperties properties { get; set; }
        }

        public class GalleryItem
        {
            public string id { get; set; }
            public string author { get; set; }
            public string visibility { get; set; }
            public string protection { get; set; }
            public string thumbnail { get; set; }
            public Int64 thumbnailTimestamp { get; set; }
            public Shape[] shapes { get; set; }
            public ShapeConnections[] shapeConnections { get; set; }
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
    }
}
