using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using Newtonsoft.Json.Linq;
using PolyPaint.Utilitaires;
using Quobject.SocketIoClientDotNet.Client;
using System.Web.Script.Serialization;
using PolyPaint.Modeles;
using Newtonsoft.Json;
using System.Windows.Threading;

namespace PolyPaint.Managers
{
    class SocketManager
    {
        private const string SERVER_ADDRESS = "127.0.0.1";
        private const string SERVER_PORT = "3000";
        public Socket Socket;

        private string SessionID { get; set; }
        public string UserName { get; set; }
        public SocketManager()
        {
            IO.Options op = new IO.Options
            {
                Reconnection = false
            };
            Socket = IO.Socket("http://" + SERVER_ADDRESS + ":" + SERVER_PORT, op);
            //InitializeOns();
        }
        public void JoinDrawingSession(string sessionID)
        {
            this.SessionID = sessionID;
            Socket.Emit("JoinDrawingSession", SessionID);

        }
        public void AddElement(string type_, string filling_, string borderColor_, Point center, int height_, int width_, int rotation_)
        {
            //Object[] properties = new Object[] { type, filling, borderColor, center, height, width, rotation };
            //string id = "mockID";
            //var shape = Newtonsoft.Json.JsonConvert.SerializeObject(new Shape(id, SessionID, UserName, properties));

            //Object[] parameters = new object[] { SessionID, UserName, shape };

            //outilSelectionne, RemplissageSelectionne, CouleurSelectionnee, center,height,width,0,type
            /* string properties_ = new JavaScriptSerializer().Serialize(new
             {
                 type =  type_ ,
                 fillingColor = filling_ ,
                 borderColor =borderColor_ ,
                 middlePointCoord = new [] { (int)center.X, (int)center.Y },
                 height = height_ ,
                 width = width_ ,
                 rotation = rotation_
             });
             string shape_ = new JavaScriptSerializer().Serialize(new
             {
                 id =  "mockID" ,
                 author = this.UserName ,
                 properties = new [] { properties_ }
             });*/
            string parameters = new JavaScriptSerializer().Serialize(new
            {
                sessionId = "MockID",
                username = this.UserName,
                shape = new
                {
                    drawingSessionId = this.SessionID,
                    author = this.UserName,
                    properties = new
                    {
                        type = type_,
                        fillingColor = filling_,
                        borderColor = borderColor_,
                        middlePointCoord = new[] { (int)center.X, (int)center.Y },
                        height = height_,
                        width = width_,
                        rotation = rotation_
                    }
                }
            });
            //Object[] parameters = new Object[] { this.SessionID, this.UserName, shape_ };
            this.Socket.Emit("AddElement", parameters);

        }
    }
}
