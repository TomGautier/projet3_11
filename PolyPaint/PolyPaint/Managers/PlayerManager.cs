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
    class PlayerManager
    {
        int Compteur = 0;
        List<Player> Players;
        Color[] Colors = new Color[10] { Color.Green, Color.Red, Color.Blue, Color.Brown, Color.Pink, Color.Orange, Color.Purple, Color.Black, Color.Yellow, Color.Turquoise };
        public PlayerManager()
        {
            Players = new List<Player>();
        }
        public void AddPlayer(string username)
        {
            this.Players.Add(new Player(username, this.Colors[Compteur % 10]));
            Compteur++;
        }
        public void RemovePlayer(string username)
        {
            this.Players.Remove(this.Players.FindLast(x => x.Username == username));
            Compteur--;
        }
        public bool Contain(string username)
        {
            foreach (Player p in this.Players)
            {
                if (p.Username == username) { return true; }
            }
            return false;
        }
        public Player GetPlayer(string username)
        {
            return this.Players.FindLast(x => x.Username == username);
        }
    }
}

