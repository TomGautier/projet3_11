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
        private string roomID = "";
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
        private string _history;
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

        public Socket socket;

        public event PropertyChangedEventHandler PropertyChanged;

        protected virtual void NotifyPropertyChanged([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
        #endregion

        #region Constructors
        public ChatManager()
        {
            RoomsID = new ObservableCollection<string>();
        }
        #endregion

        #region Methods
        private void OnSendMessage()
        {
            var messageFormat = new
            {
                date = DateTime.Now.ToString("hh:mm:ss tt"),
                username = Username,
                message = Message.Trim(),
                conversationId = RoomID
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
            if (!RoomsID.Contains(NewRoomID))
            {
                var createFormat = new
                {
                    sessionId = SessionID,
                    username = Username,
                    conversationId = NewRoomID
                };
                socket.Emit("CreateConverstation", JsonConvert.SerializeObject(createFormat));
                // TEST ONLY
                RoomsID.Add(NewRoomID);

                socket.On("CreatedCoversation", (data) => {
                    var _roomIDformat = new
                    {
                        conversationId = ""
                    };
                    var _roomID = JsonConvert.DeserializeAnonymousType(data.ToString(), _roomIDformat);
                    RoomsID.Add(_roomID.conversationId);
                    RoomID = _roomID.conversationId;
                });
            }
            RoomID = NewRoomID;
        }

        internal void JoinChannel()
        {
            var joinFormat = new
            {
                sessionId = SessionID,
                username = Username,
                conversationId = RoomID
            };
            socket.Emit("UserJoinedConversation", JsonConvert.SerializeObject(joinFormat));
            // TEST ONLY
            History = "Bienvenue dans la conversation " + RoomID + "!";
            
            //l'event est mis en commentaire dans le serveur
            //socket.On("UserJoinedConversation", (data) => {
            //    var _roomIDformat = new
            //    {
            //        conversationId = ""
            //    };
            //    var _roomID = JsonConvert.DeserializeAnonymousType(data.ToString(), _roomIDformat);
            //    RoomID = _roomID.conversationId;
            //    History = "Bienvenue dans la conversation " + RoomID + "!";
            //});
            //socket.On("ChannelAlreadyJoined", (data) => {
            //    Console.WriteLine("ChannelAlreadyJoined : " + data);
            //});
            //socket.On("ChannelCouldntBeJoined", (data) => {
            //    Console.WriteLine("ChannelCouldntBeJoined : " + data);
            //});
        }

        public void Connect()
        {
            socket.On("MessageSent", (data) =>
            {
                var messageFormat = new
                {
                    date = "",
                    username = "",
                    message = "",
                    conversationId = ""
                };
                Console.WriteLine("Message recieved : " + data.ToString());
                var message = JsonConvert.DeserializeAnonymousType(data.ToString(), messageFormat);
                ReceiveMessage(message.date, message.username, message.message);
            });
        }
        #endregion

        #region Commands 
        public ICommand SendMessage { get { return new RelayCommand(OnSendMessage, MessageValid); } }
        public ICommand CreateChannel { get { return new RelayCommand(OnCreateChannel, RoomIdValid); } }

        private bool AlwaysTrue() { return true; }
        private bool AlwaysFalse() { return false; }
        private bool MessageValid() { return !string.IsNullOrWhiteSpace(Message); }
        private bool RoomIdValid() { return !string.IsNullOrWhiteSpace(RoomID); }
        #endregion

    }
}
