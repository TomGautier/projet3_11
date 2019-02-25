using System;
using System.Collections.Generic;
using System.Globalization;
//using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;

namespace PolyPaint.Utilitaires
{
    class Form : Stroke
    {
        public Color BorderColor { get; set; }
        public int CurrentRotation { get; set; }
        protected Point Center { get; set; }
        public  void rotate()
        {
            int angleInc = 10;
            CurrentRotation = (CurrentRotation + angleInc) % 360;
            Matrix rotatingMatrix = new Matrix();         
            rotatingMatrix.RotateAt(angleInc, Center.X, Center.Y);

            Stroke copy = this.Clone();
            copy.Transform(rotatingMatrix, false);
            this.StylusPoints = copy.StylusPoints;
        }
        public void update()
        {
            Stroke copy = this.Clone();
            this.StylusPoints = copy.StylusPoints;
        }
        public Form(StylusPointCollection pts)
            : base(pts) { }


            
    }
}
