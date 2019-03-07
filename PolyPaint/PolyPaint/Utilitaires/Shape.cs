using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
//{ id: string, drawingSessionId: string, author: string, properties: { type: string, fillingColor: string, borderColor: string, middlePointCoord: int[2], height: int, width: int, rotation: int }

namespace PolyPaint.Utilitaires
{
    class Shape
    {
        public string id { get; set; }
        public string drawingSessionId { get; set; }
        public string author { get; set; }
        public Object[] properties { get; set; }

        public Shape(string id, string drawingSessionId, string author, Object[] properties)
        {
            this.id = id;
            this.drawingSessionId = drawingSessionId;
            this.author = author;
            this.properties = properties;
        }
    }
}
