using PolyPaint.VueModeles;
using System.Windows;
using System.Windows.Controls;

namespace PolyPaint.Vues
{
    /// <summary>
    /// Interaction logic for SignupControl.xaml
    /// </summary>
    public partial class SignupControl : UserControl
    {
        public SignupControl()
        {
            InitializeComponent();
        }

        private void SignupButton_Click(object sender, RoutedEventArgs e)
        {
            ((VueModele)DataContext).Signup(((PasswordBox)FindName("passwordBox")).Password);
        }
    }
}
