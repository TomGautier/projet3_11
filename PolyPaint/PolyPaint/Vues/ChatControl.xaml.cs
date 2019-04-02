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
    /// Interaction logic for ChatControl.xaml
    /// </summary>
    public partial class ChatControl : UserControl
    {
        private ChatWindow chatWindow;

        public ChatControl()
        {
            InitializeComponent();
            var textBox = (TextBox)FindName("chatBox");
            textBox.TextChanged += (sender, e) =>
            {
                textBox.ScrollToEnd();
            };
            chatWindow = null;
            //UpdateUserList(); TODO : FIND WHY DATA CONTEXT IS NULL
        }

        private void SendMessageButton_Click(object sender, RoutedEventArgs e)
        {
            var textBox = (TextBox)FindName("messageText");
            textBox.Focus();
        }

        private void ToggleButton_Click(object sender, RoutedEventArgs e)
        {
            Window _chatWindow = null;
            foreach (Window window in Application.Current.Windows)
            {
                if (window.Name == "chatWindow")
                {
                    _chatWindow = window;
                }
            }

            if (_chatWindow == null)
            {
                ((FrameworkElement)((DockPanel)FindName("chatDockPanel")).Parent).Visibility = Visibility.Collapsed;
                chatWindow = new ChatWindow();
                chatWindow.Name = "chatWindow";
                chatWindow.DataContext = DataContext;
                chatWindow.Show();
                chatWindow.Closed += (object _sender, EventArgs _e) =>
                {
                    ((FrameworkElement)((DockPanel)FindName("chatDockPanel")).Parent).Visibility = Visibility.Visible;
                    chatWindow = null;
                };
            }
            else
            {
                _chatWindow.Close();
            }
        }

        private void ComboBox_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            ((VueModele)DataContext).ChatManager.JoinChannel();
        }

        private void ShowAddChannelForm_Click(object sender, RoutedEventArgs e)
        {
            ((StackPanel)FindName("AddChannelForm")).Visibility = Visibility.Visible;
        }

        private void AddChannel_Click(object sender, RoutedEventArgs e)
        {
            ((StackPanel)FindName("AddChannelForm")).Visibility = Visibility.Hidden;
        }

        private async void UpdateUserList()
        {
            UsersList.ItemsSource = await ((VueModele)DataContext).LoadUsersAsync();
        }
        
        public class UserItem
        {
            public string Username { get; set; }
            public int ConnectionStatus { get; set; }
        }

        public class UserItemTemplate
        {
            public string Username { get; set; }
            public bool ConnectionStatus { get; set; }
        }

        private void UpdateUsersList_Click(object sender, RoutedEventArgs e)
        {
            UpdateUserList();
        }

        private void InviteUser_Click(object sender, RoutedEventArgs e)
        {
            string invited = ((Grid)((Button)sender).Parent).Children.OfType<TextBlock>().Single().Text;

            //TODO : INVITE
        }
    }
}
