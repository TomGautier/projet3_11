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
        private const string SERVER_ADDRESS = "127.0.0.1"; //"40.122.119.160";
        private const string SERVER_PORT = "3000";
        #endregion

        #region Parameters
        private string roomID = "GeneralRoom";
        public string RoomID
        {
            get
            {
                return roomID;
            }
            set
            {
                roomID = value;
                NotifyPropertyChanged();
            }
        }
        private ObservableCollection<string> roomsID;
        public ObservableCollection<string> RoomsID
        {
            get
            {
                return roomsID;
            }
            set
            {
                roomsID = value;
                NotifyPropertyChanged();
            }
        }
        private string newRoomID;
        public string NewRoomID
        {
            get
            {
                return newRoomID;
            }
            set
            {
                newRoomID = value;
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
        private string sessionID;
        public string SessionID
        {
            get
            {
                return sessionID;
            }
            set
            {
                sessionID = value;
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

            RoomsID = new ObservableCollection<string>();
            RoomsID.Add("GeneralRoom"); // TODO : Load from server
        }
        #endregion

        #region Methods
        public void OnExitApp(object sender, CancelEventArgs e)
        {
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
                message = Message.Trim(),
                roomId = RoomID
            };
            Console.WriteLine("Message Sent : " + JsonConvert.SerializeObject(messageFormat));
            socket.Emit("MessageSent", JsonConvert.SerializeObject(messageFormat));
            Message = string.Empty;
        }
        private void ReceiveMessage(string date, string username, string message)
        {
            History += Environment.NewLine + date + "  [" + username + "] : " + message;
        }
        private void OnCreateChannel()
        {
            //!RoomsID.Contains(newElemet); // TODO : Refresh list before checking
            var createFormat = new
            {
                sessionId = SessionID,
                username = Username,
                channelId = NewRoomID
            };
            socket.Emit("CreateChannel", JsonConvert.SerializeObject(createFormat));

            socket.On("CreatedChannel", (data) => {
                var _roomIDformat = new
                {
                    channelId = ""
                };
                var _roomID = JsonConvert.DeserializeAnonymousType(data.ToString(), _roomIDformat);
                RoomsID.Add(_roomID.channelId);
                RoomID = _roomID.channelId;
            });
        }

        internal void JoinChannel()
        {
            var joinFormat = new
            {
                sessionId = SessionID,
                username = Username,
                channelId = RoomID
            };
            socket.Emit("JoinChannel", JsonConvert.SerializeObject(joinFormat));

            socket.On("JoinedChannel", (data) => {
                var _roomIDformat = new
                {
                    channelId = ""
                };
                var _roomID = JsonConvert.DeserializeAnonymousType(data.ToString(), _roomIDformat);
                RoomID = _roomID.channelId;
            });
            socket.On("ChannelAlreadyJoined", (data) => {
                Console.WriteLine("ChannelAlreadyJoined : " + data);
            });
            socket.On("ChannelCouldntBeJoined", (data) => {
                Console.WriteLine("ChannelCouldntBeJoined : " + data);
            });
        }

        public void Connect()
        {
            IO.Options op = new IO.Options
            {
                Reconnection = false
            };

            socket = IO.Socket("http://" + SERVER_ADDRESS + ":" + SERVER_PORT, op);

            socket.On(Socket.EVENT_CONNECT, () =>
            {
                JoinChannel();

                socket.On("MessageReceived", (data) =>
                {
                    var messageFormat = new
                    {
                        date = "",
                        username = "",
                        message = "",
                        roomId = ""
                    };
                    var message = JsonConvert.DeserializeAnonymousType(data.ToString(), messageFormat);
                    ReceiveMessage(message.date, message.username, message.message);
                });
            });
            socket.On(Socket.EVENT_CONNECT_TIMEOUT, () =>
            {
                System.Windows.MessageBox.Show("Connection timeout", "Error");
            });
            socket.On(Socket.EVENT_CONNECT_ERROR, () =>
            {
                System.Windows.MessageBox.Show("Connection error", "Error");
            });
        }
        #endregion

        #region Commands 
        public ICommand ExitCmd { get { return new RelayCommand(OnExitApp, AlwaysTrue); } }
        public ICommand SendMessage { get { return new RelayCommand(OnSendMessage, MessageValid); } }
        public ICommand CreateChannel { get { return new RelayCommand(OnCreateChannel, RoomIdValid); } }

        private bool AlwaysTrue() { return true; }
        private bool AlwaysFalse() { return false; }
        private bool MessageValid() { return !string.IsNullOrWhiteSpace(Message); }
        private bool RoomIdValid() { return !string.IsNullOrWhiteSpace(RoomID); }
        #endregion

    }
}
