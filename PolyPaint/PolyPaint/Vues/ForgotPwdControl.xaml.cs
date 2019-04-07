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
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace PolyPaint.Vues
{
    /// <summary>
    /// Interaction logic for ForgotPwdControl.xaml
    /// </summary>
    public partial class ForgotPwdControl : UserControl
    {
        public ForgotPwdControl()
        {
            InitializeComponent();
        }

        private void NewPWDbtn_Click(object sender, RoutedEventArgs e)
        {
            ((VueModele)DataContext).RequestPwd(((TextBox)FindName("email")).Text);
            ((Grid)FindName("NewPwdForm")).Visibility = Visibility.Visible;
        }

        private void LoginButton_Click(object sender, RoutedEventArgs e)
        {
            ((VueModele)DataContext).NewPassword(((TextBox)FindName("passcodeBox")).Text,((PasswordBox)FindName("passwordBox")).Password);
        }
    }
}
