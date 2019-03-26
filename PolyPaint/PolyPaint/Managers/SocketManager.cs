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
        private int Compteur { get; set; }

        public string SessionID { get; set; }
        public string UserName { get; set; }
        public SocketManager()
        {
            IO.Options op = new IO.Options
            {
                Reconnection = false
            };
            Socket = IO.Socket("http://" + SERVER_ADDRESS + ":" + SERVER_PORT, op);
            //InitializeOns();
            this.Compteur = 0;
        }
        public void JoinDrawingSession(string sessionID)
        {
            this.SessionID = sessionID;
            string parameters = new JavaScriptSerializer().Serialize(new
            {
                drawingSessionId = this.SessionID,
                username = this.UserName,              
            });
            Socket.Emit("JoinDrawingSession", parameters);

        }
        public void UnStackElement(Shape shape_)
        {
            string parameters = new JavaScriptSerializer().Serialize(new
            {
                sessionId = this.SessionID,
                username = this.UserName,
                shape = shape_
            });
            this.Socket.Emit("UnstackElement",parameters);
        }
        public void StackElement(String id_)
        {
            string parameters = new JavaScriptSerializer().Serialize(new
            {
                sessionId = this.SessionID,
                username = this.UserName,
                drawingSessionId = this.SessionID,
                elementId = id_
            });
            this.Socket.Emit("StackElement",parameters);
        }
        public void Select(String[] oldSelection, String[] newSelection)
        {
            string parameters = new JavaScriptSerializer().Serialize(new
            {
                drawingSessionId = this.SessionID,
                username = this.UserName,
                oldElementIds = oldSelection,
                newElementIds = newSelection
            });
            this.Socket.Emit("SelectElements", parameters);
        }
        public void AddElement(Shape shape_)
        {
            shape_.id = this.UserName + "_" + this.Compteur.ToString();
            shape_.author = this.UserName;

            string parameters = new JavaScriptSerializer().Serialize(new
            {
                sessionId = this.SessionID,
                username = this.UserName,
                shape = shape_
              /*  shape = new
                {
                    id = this.UserName + "_" + this.Compteur.ToString(),
                    drawingSessionId = this.SessionID,
                    author = this.UserName,
                    properties = new
                    {
                        type = shape.properties.type,
                        fillingColor = shape.properties.fillingColor,
                        borderColor = shape.properties.borderColor,
                        middlePointCoord = shape.properties.middlePointCoord,
                        height = shape.properties.height,
                        width = shape.properties.width,
                        rotation = shape.properties.rotation
                    }
                }*/
            });
            Compteur++;
            //Object[] parameters = new Object[] { this.SessionID, this.UserName, shape_ };
            this.Socket.Emit("AddElement", parameters);

        }
        public void DeleteElement(String[] idList)
        {
            //string[] elementIds



            string parameters = new JavaScriptSerializer().Serialize(new
            {
                drawingSessionId = this.SessionID,
                elementIds = idList,
                username = this.UserName
            });
            this.Socket.Emit("DeleteElements", parameters);


        }
        public void CutElements(String[] idList)
        {
            string parameters = new JavaScriptSerializer().Serialize(new
            {
                drawingSessionId = this.SessionID,
                elementIds = idList,
                username = this.UserName
            });
            this.Socket.Emit("CutElements", parameters);

        }
        public void HandleModification(Shape[] shapes_)
        {
            string parameters = new JavaScriptSerializer().Serialize(new
            {
                drawingSessionId = this.SessionID,
                username = this.UserName,
                shapes = shapes_
            });
            this.Socket.Emit("ModifyElement", parameters);
        }
    }
}
