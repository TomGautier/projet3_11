using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
//{ id: string, drawingSessionId: string, author: string, properties: { type: string, fillingColor: string, borderColor: string, middlePointCoord: int[2], height: int, width: int, rotation: int }

namespace PolyPaint.Utilitaires
{
    class ShapeProperties
    {
        public string type { get; set; }
        public string fillingColor { get; set; }
        public string borderColor { get; set; }
        public int[] middlePointCoord { get; set; }
        public int height { get; set; }
        public int width { get; set; }
        public int rotation { get; set; }
        public string borderType { get; set; }
        public string label { get; set; }
        public String[] methods { get; set;}
        public String[] attributes { get; set; }
        public string idShape1 { get; set; }
        public string idShape2 { get; set; }
        public int index1 { get; set; }
        public int index2 { get; set; }
        public string q1 { get; set; }
        public string q2 { get; set; }
        public int[] pointsX { get; set; }
        public int[] pointsY { get; set; }
        public string category { get; set; }

        public ShapeProperties(string type, string fillingColor, string borderColor, int[] middlePointCoord, int height, int width, int rotation,string borderType, string label, 
            String[] methods, String[] attributes, string idShape1, string idShape2, int index1, int index2, string q1, string q2, int[] pointsX,int[] pointsY,string category)
        {
            this.type = type;
            this.fillingColor = fillingColor;
            this.borderColor = borderColor;
            this.middlePointCoord = middlePointCoord;
            this.height = height;
            this.width = width;
            this.rotation = rotation;
            this.borderType = borderType;
            this.label = label;
            this.methods = methods;
            this.attributes = attributes;
            this.idShape1 = idShape1;
            this.idShape2 = idShape2;
            this.index1 = index1;
            this.index2 = index2;
            this.q1 = q1;
            this.q2 = q2;
            this.pointsX = pointsX;
            this.pointsY = pointsY;
            this.category = category;
            
        }
    }
}
