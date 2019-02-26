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
        private Color remplissage;
        public Color Remplissage
        {
            get { return remplissage; }
            set
            {
                remplissage = value;
                this.update();
            }
        }

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
        public void translate(int x, int y) 
        {
           for (int i = 0; i < this.StylusPoints.Count; i++)
            {
                this.StylusPoints[i] = new StylusPoint(this.StylusPoints[i].X + x, this.StylusPoints[i].Y + y);
            }
        }
        public Form(StylusPointCollection pts)
            : base(pts) { }


            
    }
}
