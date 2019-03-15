using PolyPaint.VueModeles;
using System.Windows;
using System.Windows.Controls;

namespace PolyPaint.Vues
{
    /// <summary>
    /// Interaction logic for LoginControl.xaml
    /// </summary>
    public partial class LoginControl : UserControl
    {
        public LoginControl()
        {
            InitializeComponent();
        }

        private void LoginButton_Click(object sender, RoutedEventArgs e)
        {
            ((VueModele)DataContext).Login(((PasswordBox)FindName("passwordBox")).Password);
        }
    }
}
