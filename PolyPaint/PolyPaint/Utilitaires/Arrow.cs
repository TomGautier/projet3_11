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
        public Form Shape2;
        public Arrow(StylusPointCollection pts)
            :base(pts)
        {
            this.DrawingAttributes.Color = Colors.Red;
 

        }
        public void ShapeMoved(string shapeID, StylusPoint point)
        {
            if (shapeID == Shape1.Id)
            {
                this.StylusPoints[0] = point;
            }
            else
            {
                this.StylusPoints[this.StylusPoints.Count - 1] = point;
            }
        }
    }
}
