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
    /// Logique d'interaction pour HomeWindow.xaml
    /// --The view "HomeWindow" represents the home page (or "Page d'accueil")
    ///     of the application. Basically, you are supposed to see this
    ///     window first.
    /// </summary>
 
    ///<Note> MP: 2019-02-26
    ///--Button "login" must redirect to the authentification page.
    ///--Button "Sign up" must lead to the page who allows the creation of a new account.
    ///--We must display the 4 most recent images created in the app.
    ///--Switch langage option [facultatif]
    ///</Note>
    public partial class HomeWindow : Window
    {
        public HomeWindow()
        {
            InitializeComponent();
        }

        private void Login_Click(object sender, RoutedEventArgs e) {
          /*  MainWindow mainWindow = new MainWindow();
            mainWindow.Show();
            this.Close();*/
        }
    }
}
