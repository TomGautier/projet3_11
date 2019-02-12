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

        private ChatWindow chatWindow;
        private int windowCount;

        public ChatControl()
        {

            InitializeComponent();
            var textBox = (TextBox)FindName("chatBox");
            textBox.TextChanged += (sender, e) =>
            {
                textBox.ScrollToEnd();
            };
            windowCount = Application.Current.Windows.Count;
            chatWindow = null;
        }

        private void SendMessageButton_Click(object sender, RoutedEventArgs e)
        {
            var textBox = (TextBox)FindName("messageText");
            textBox.Focus();
        }

        private void ToggleButton_Click(object sender, RoutedEventArgs e)
        {
            if (windowCount == 1)
            {
                ((FrameworkElement)((DockPanel)FindName("chatDockPanel")).Parent).Visibility = Visibility.Collapsed;
                chatWindow = new ChatWindow
                {
                    Name = "chatWindow"
                };
                chatWindow.Show();
                chatWindow.Closed += (object _sender, EventArgs _e) =>
                {
                    ((FrameworkElement)((DockPanel)FindName("chatDockPanel")).Parent).Visibility = Visibility.Visible;
                    chatWindow = null;
                };
            }
            else
            {
                foreach (Window window in Application.Current.Windows)
                {
                    if(window.Name == "chatWindow")
                    {
                        window.Close();
                    }
                }
            }
        }
    }
}
