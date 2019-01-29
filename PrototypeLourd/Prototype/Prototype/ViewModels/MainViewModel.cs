﻿using MvvmDialogs;
using log4net;
using MvvmDialogs.FrameworkDialogs.OpenFile;
using MvvmDialogs.FrameworkDialogs.SaveFile;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Reflection;
using System.Windows.Input;
using System.Xml.Linq;
using Prototype.Views;
using Prototype.Utils;
using Quobject.SocketIoClientDotNet.Client;
using System.Text.RegularExpressions;
using Newtonsoft.Json;

namespace Prototype.ViewModels
{
    class MainViewModel : ViewModelBase
    {
        #region Constant
        // TODO : Set to true value
        private const string SERVER_PORT = "8000";
        private const string MESSAGE_ID = "message";
        #endregion

        #region Parameters
        private readonly IDialogService DialogService;

        /// <summary>
        /// Title of the application, as displayed in the top bar of the window
        /// </summary>
        public string Title
        {
            get { return "Prototype"; }
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
                NotifyPropertyChanged("Message");
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
                NotifyPropertyChanged("History");
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
                NotifyPropertyChanged("Username");
            }
        }
        private string _serverAdress;
        public string ServerAdress
        {
            get
            {
                return _serverAdress;
            }
            set
            {
                _serverAdress = value;
                NotifyPropertyChanged("ServerAdress");
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
                NotifyPropertyChanged("ConnectionStatus");
            }
        }
        private Socket socket;
        #endregion

        #region Constructors
        public MainViewModel()
        {
            // DialogService is used to handle dialogs
            this.DialogService = new MvvmDialogs.DialogService();
        }
        #endregion

        #region Methods
        private void OnExitApp()
        {
            System.Windows.Application.Current.MainWindow.Close();
        }
        private void OnSendMessage()
        {
            var messageFormat = new
            {
                username = Username,
                time = System.DateTime.Now.ToString("HH:mm:ss"),
                message = Message
            };
            socket.Emit(MESSAGE_ID, JsonConvert.SerializeObject(messageFormat));
            Message = string.Empty;
        }
        private void ReceiveMessage(string username, string time, string message)
        {
            History += Environment.NewLine + username + " - " + time + " - " + message;
        }
        private void OnConnect()
        {
            Regex rgx = new Regex("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
            if (!rgx.IsMatch(ServerAdress))
            {
                System.Windows.MessageBox.Show("Wrong Adress format", "Error");
                return;
            }

            IO.Options op = new IO.Options
            {
                Reconnection = false
            };

            socket = IO.Socket("http://" + ServerAdress + ":" + SERVER_PORT, op);

            socket.On(Socket.EVENT_CONNECT_ERROR, EventConnectError);

            socket.On(Socket.EVENT_CONNECT, EventConnect);
        }
        #endregion

        #region Commands 
        public ICommand ExitCmd { get { return new RelayCommand(OnExitApp, AlwaysTrue); } }
        public ICommand SendMessage { get { return new RelayCommand(OnSendMessage, MessageValid); } }
        public ICommand Connect { get { return new RelayCommand(OnConnect, InfosValid); } }

        private bool AlwaysTrue() { return true; }
        private bool AlwaysFalse() { return false; }
        private bool MessageValid() { return !string.IsNullOrWhiteSpace(Message) && ConnectionStatus != "disconnected"; }
        private bool InfosValid() { return !string.IsNullOrWhiteSpace(ServerAdress) && !string.IsNullOrWhiteSpace(Username); } // TODO : regex for server adress
        #endregion

        #region Events
        private void EventConnectError(object error)
        {
            // if error == username already exists
            System.Windows.MessageBox.Show("Username already exists", "Error");
            socket.Disconnect();
            // else
            // socket.Connect();
        }

        private void EventConnect()
        {
            ConnectionStatus = "connected";
            socket.On(MESSAGE_ID, (data) =>
            {
                var messageFormat = new
                {
                    username = "",
                    time = "",
                    message = ""
                };
                var message = JsonConvert.DeserializeAnonymousType((string)data, messageFormat);
                ReceiveMessage(message.username, message.time, message.message);
            });
        }
        #endregion
    }
}
