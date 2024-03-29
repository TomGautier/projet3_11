﻿using PolyPaint.VueModeles;
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

namespace PolyPaint.Vues
{
    /// <summary>
    /// Interaction logic for NewSessionControl.xaml
    /// </summary>
    public partial class NewSessionControl : UserControl
    {
        public NewSessionControl()
        {
            InitializeComponent();
        }

        private void Confirm_Click(object sender, RoutedEventArgs e)
        {
            string visibility = ((RadioButton)FindName("public")).IsChecked.Value ? "public" : "private";
            string protection = ((TextBox)FindName("protection")).Text;
            (DataContext as VueModele).CreateNewSession(visibility, protection);
        }
    }
}
