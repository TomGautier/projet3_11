using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;
using PolyPaint.Utilitaires;
using Quobject.SocketIoClientDotNet.Client;
using System.Text.RegularExpressions;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using System.Collections.ObjectModel;

namespace PolyPaint.Managers
{
    class ChatManager : INotifyPropertyChanged
    {
        #region Constant
        private const string SERVER_ADDRESS = "40.122.119.160";
        private const string SERVER_PORT = "3000";
        #endregion

        #region Parameters
        private string conversationID = "General";
        public string ConversationID
        {
            get
            {
                return conversationID;
            }
            set
            {
                conversationID = value;
                NotifyPropertyChanged();
            }
        }
        private ObservableCollection<string> conversationsID;
        public ObservableCollection<string> ConversationsID
        {
            get
            {
                return conversationsID;
            }
            set
            {
                conversationsID = value;
                NotifyPropertyChanged();
            }
        }
        private string _message = string.Empty;
        public string Message
        {
            get
            {
                return _message;
            }
            set
            {
                _message = value;
                NotifyPropertyChanged();
            }
        }
        private string _history = "Welcome!";
        public string History
        {
            get
            {
                return _history;
            }
            set
            {
                _history = value;
                NotifyPropertyChanged();
            }
        }
        private string _username;
        public string Username
        {
            get
            {
                return _username;
            }
            set
            {
                _username = value;
                NotifyPropertyChanged();
            }
        }
        private string _serverAddress = SERVER_ADDRESS;
        public string ServerAddress
        {
            get
            {
                return _serverAddress;
            }
            set
            {
                _serverAddress = value;
                NotifyPropertyChanged();
            }
        }
        private string _connectionStatus = "disconnected";
        public string ConnectionStatus
        {
            get
            {
                return _connectionStatus;
            }
            set
            {
                _connectionStatus = value;
                NotifyPropertyChanged();
            }
        }
        private Socket socket;

        public event PropertyChangedEventHandler PropertyChanged;

        protected virtual void NotifyPropertyChanged([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
        #endregion

        #region Constructors
        public ChatManager()
        {
            System.Windows.Application.Current.MainWindow.Closing += OnExitApp;
            ConversationsID = new ObservableCollection<string>();
            ConversationsID.Add("General"); // TODO : Load from server
        }
        #endregion

        #region Methods
        public void OnExitApp(object sender, CancelEventArgs e)
        {
            if (ConnectionStatus == "connected")
                socket.Emit("UserLeft", Username);
            if (ConnectionStatus != "disconnected")
                socket.Emit("disconnect");
        }
        private void OnExitApp()
        {
            System.Windows.Application.Current.MainWindow.Close();
        }
        private void OnSendMessage()
        {
            var messageFormat = new
            {
                date = DateTime.Now.ToString("hh:mm:ss tt"),
                username = Username,
                message = Message.Trim()
            };
            Console.WriteLine(JsonConvert.SerializeObject(messageFormat));
            socket.Emit("MessageSent", JsonConvert.SerializeObject(messageFormat));
            Message = string.Empty;
        }
        private void ReceiveMessage(string date, string username, string message)
        {
            History += Environment.NewLine + date + "  [" + username + "] : " + message;
        }
        private void OnConnect()
        {
            ConnectionStatus = "connectAttempt";
            Regex rgx = new Regex("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
            if (!rgx.IsMatch(ServerAddress))
            {
                System.Windows.MessageBox.Show("Wrong Address format", "Error");
                ConnectionStatus = "disconnected";
                return;
            }
            IO.Options op = new IO.Options
            {
                Reconnection = false
            };

            socket = IO.Socket("http://" + ServerAddress + ":" + SERVER_PORT, op);

            socket.On(Socket.EVENT_CONNECT, () =>
            {
                ConnectionStatus = "connecting";
            });
            socket.On(Socket.EVENT_CONNECT_TIMEOUT, () =>
            {
                System.Windows.MessageBox.Show("Connection timeout", "Error");
                ConnectionStatus = "disconnected";
            });
            socket.On(Socket.EVENT_CONNECT_ERROR, () =>
            {
                System.Windows.MessageBox.Show("Connection error", "Error");
                ConnectionStatus = "disconnected";
            });
        }

        private void OnLogin()
        {
            Username = Username.Trim();
            socket.Emit("LoginAttempt", Username);
            socket.On("UsernameAlreadyExists", () =>
            {
                System.Windows.MessageBox.Show("Username already exists.", "Error");
            });
            socket.On("UserLogged", () =>
            {
                ConnectionStatus = "connected";
                socket.On("MessageSent", (data) =>
                {
                    var messageFormat = new
                    {
                        date = "",
                        username = "",
                        message = ""
                    };
                    Console.WriteLine(data);
                    var message = JsonConvert.DeserializeAnonymousType(data.ToString(), messageFormat);
                    ReceiveMessage(message.date, message.username, message.message);
                });
            });
        }

        private void OnDisconnect()
        {
            socket.Emit("UserLeft", Username);
            socket.Disconnect();
            ConnectionStatus = "disconnected";
            History = "Welcome!";
            Username = string.Empty;
        }
        private void OnCreateConversation()
        {
            //TODO : POP UP TO CREATE
            //!ConversationsID.Contains(newElelemet); // TODO : Refresh list before checking
            ConversationsID.Add("Test");
        }
        #endregion

        #region Commands 
        public ICommand ExitCmd { get { return new RelayCommand(OnExitApp, AlwaysTrue); } }
        public ICommand SendMessage { get { return new RelayCommand(OnSendMessage, MessageValid); } }
        public ICommand Connect { get { return new RelayCommand(OnConnect, AddressValid); } }
        public ICommand Disconnect { get { return new RelayCommand(OnDisconnect, AlwaysTrue); } }
        public ICommand Login { get { return new RelayCommand(OnLogin, UsernameValid); } }
        public ICommand CreateConversation { get { return new RelayCommand(OnCreateConversation, AlwaysTrue); } }

        private bool AlwaysTrue() { return true; }
        private bool AlwaysFalse() { return false; }
        private bool MessageValid() { return !string.IsNullOrWhiteSpace(Message); }
        private bool AddressValid() { return !string.IsNullOrWhiteSpace(ServerAddress) && ConnectionStatus == "disconnected"; }
        private bool UsernameValid() { return !string.IsNullOrWhiteSpace(Username); }
        #endregion

    }
}
