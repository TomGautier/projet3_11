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
        public string type { get; set; }
        public string fillingColor { get; set; }
        public string borderColor { get; set; }
        public int[] middlePointCoord { get; set; }
        public int height { get; set; }
        public int width { get; set; }
        public int rotation { get; set; }

        public Shape(string type, string fillingColor, string borderColor, int[] middlePointCoord, int height, int width, int rotation)
        {
            this.type = type;
            this.fillingColor = fillingColor;
            this.borderColor = borderColor;
            this.middlePointCoord = middlePointCoord;
            this.height = height;
            this.width = width;
            this.rotation = rotation;
        }
    }
}
