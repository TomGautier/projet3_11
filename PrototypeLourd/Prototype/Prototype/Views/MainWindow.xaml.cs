using System.Windows;
using System.Windows.Controls;
using System.Reflection;
using log4net;


namespace Prototype.Views
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private static readonly ILog Log = log4net.LogManager.GetLogger(MethodBase.GetCurrentMethod().DeclaringType);

        public MainWindow()
        {
            InitializeComponent();
            this.Closing += MainView_Closing;
            var textBox = (TextBox)FindName("chatBox");
            textBox.TextChanged += (sender, e) =>
            {
                textBox.ScrollToEnd();
            };
        }

        private void MainView_Closing(object sender, System.ComponentModel.CancelEventArgs e)
        {
            /*
                if (((MainViewModel)(this.DataContext)).Data.IsModified)
                if (!((MainViewModel)(this.DataContext)).PromptSaveBeforeExit())
                {
                    e.Cancel = true;
                    return;
                }
            */
            Log.Info("Closing App");
        }

        private void SendMessageButton_Click(object sender, RoutedEventArgs e)
        {
            var textBox = (TextBox)FindName("messageText");
            textBox.Focus();
        }
    }
}
