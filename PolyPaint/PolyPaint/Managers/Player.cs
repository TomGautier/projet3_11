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
using System.Drawing;

namespace PolyPaint.Managers
{
    class Player
    {
        public string Username { get; set; }
        public Color Color { get; set; }
        public Player(string username, Color color)
        {
            this.Username = username;
            this.Color = color;
        }
    }
}

