using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;

namespace PolyPaint.Utilitaires
{
    
    class Arrow: Form
    {
        public const string TYPE = "Arrow";
        public Form Shape1;
        public int Index1;
        public int Index2;
        public Form Shape2;
        public Arrow(StylusPointCollection pts)
            :base(pts)
        {
            this.DrawingAttributes.Color = Colors.Red;
            
 

        }
        public void ShapeMoved(string shapeID)
        {
            if (shapeID == Shape1.Id)
            {
                this.StylusPoints[0] = new StylusPoint(this.Shape1.EncPoints[Index1].X,this.Shape1.EncPoints[Index1].Y);
            }
            else
            {
                this.StylusPoints[this.StylusPoints.Count - 1] = new StylusPoint(this.Shape2.EncPoints[Index2].X, this.Shape2.EncPoints[Index2].Y);
            }
        }
    }
}
