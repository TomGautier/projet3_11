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
        private const string SERVER_ADDRESS = "127.0.0.1";//"10.200.18.25";
        private const string SERVER_PORT = "3000";
        public const double S_PROP = 0.5d;
        public Socket Socket;
        public int Compteur { get; set; }

        public string SessionID { get; set; }
        public string UserName { get; set; }
        public bool IsOffline { get; set; }
        public SocketManager(bool isOffline)
        {
            this.IsOffline = isOffline;
            if (!this.IsOffline)
            {
                IO.Options op = new IO.Options
                {
                    Reconnection = false
                };
                Socket = IO.Socket("http://" + SERVER_ADDRESS + ":" + SERVER_PORT, op);
                //InitializeOns();
            }
            else
            {
                this.UserName = "offlinePlayer";
                this.SessionID = "offline";
            }
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
        public void Reinitialiser()
        {
            string parameters = new JavaScriptSerializer().Serialize(new
            {
                drawingSessionId = this.SessionID,
            });
            this.Socket.Emit("ResetCanvas", parameters);
        }
        public void ResizeCanvas(double width, double height)
        {
            double[] size = new double[2] { width*S_PROP, height*S_PROP };

            string parameters = new JavaScriptSerializer().Serialize(new
            {
                username = this.UserName,
                drawingSessionId = this.SessionID,
                newCanvasDimensions = size
            });
            this.Socket.Emit("ResizeCanvas", parameters);
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
            ConvertToMobile(shape_);
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
            foreach (Shape s in shapes_) { ConvertToMobile(s); }
            string parameters = new JavaScriptSerializer().Serialize(new
            {
                drawingSessionId = this.SessionID,
                username = this.UserName,
                shapes = shapes_
            });
            this.Socket.Emit("ModifyElement", parameters); 
            
        }
        public void DuplicateElements(Shape[] shapes_)
        {
            foreach(Shape s in shapes_) { ConvertToMobile(s); }
            string parameters = new JavaScriptSerializer().Serialize(new
            {
                drawingSessionId = this.SessionID,
                username = this.UserName,
                shapes = shapes_
            });
            this.Socket.Emit("DuplicateElements", parameters);
        }
        public void DuplicateCutElements(Shape[] shapes_)
        {
            foreach (Shape s in shapes_) { ConvertToMobile(s); }
            string parameters = new JavaScriptSerializer().Serialize(new
            {
                drawingSessionId = this.SessionID,
                username = this.UserName,
                shapes = shapes_
            });
            this.Socket.Emit("DuplicateCutElements", parameters);
        }
        public void ConvertToMobile(Shape shape)
        {
            if (shape.properties.type == "Arrow")
            {
                shape.properties.height = (int)(shape.properties.height * S_PROP);
                shape.properties.width = (int)(shape.properties.width * S_PROP);
                for(int i = 0; i< shape.properties.pointsX.Length; i++)
                {
                    shape.properties.pointsX[i] = (int)(shape.properties.pointsX[i] * S_PROP);
                    shape.properties.pointsY[i] = (int)(shape.properties.pointsY[i] * S_PROP);
                }

            }
            else
            {
                shape.properties.height = (int)(shape.properties.height * S_PROP);
                shape.properties.width = (int)(shape.properties.width * S_PROP);
                shape.properties.middlePointCoord[0] = (int)(shape.properties.middlePointCoord[0] * S_PROP);
                shape.properties.middlePointCoord[1] = (int)(shape.properties.middlePointCoord[1] * S_PROP);
            }
        }
    }
}
