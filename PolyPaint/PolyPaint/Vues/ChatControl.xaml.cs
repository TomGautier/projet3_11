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
    /// Interaction logic for ChatControl.xaml
    /// </summary>
    public partial class ChatControl : UserControl
    {
        public ChatControl()
        {
            InitializeComponent();
            var textBox = (TextBox)FindName("chatBox");
            textBox.TextChanged += (sender, e) =>
            {
                textBox.ScrollToEnd();
            };
        }

        private void SendMessageButton_Click(object sender, RoutedEventArgs e)
        {
            var textBox = (TextBox)FindName("messageText");
            textBox.Focus();
        }
    }
}
