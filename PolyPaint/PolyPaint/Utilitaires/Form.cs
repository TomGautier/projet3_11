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
        public double Height { get; set; }
        public double Width { get; set; }

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
            SetRotation(this.CurrentRotation + angleInc);     
            this.CurrentRotation += angleInc;
            
        }
        public void SetRotation(int degrees)
        {
            Matrix rotatingMatrix = new Matrix();
            rotatingMatrix.RotateAt(360 -this.CurrentRotation, Center.X, Center.Y); //reset angle
            rotatingMatrix.RotateAt(degrees, Center.X, Center.Y);                   //apply rotation
            Stroke copy = this.Clone();
            copy.Transform(rotatingMatrix, false);
            this.StylusPoints = copy.StylusPoints;
            this.CurrentRotation = degrees;
        }
        public void SetToShape(Shape shape)
        {
            this.Width = shape.width;
            this.Height = shape.height;
            this.DrawingAttributes.Color = (Color)System.Windows.Media.ColorConverter.ConvertFromString(shape.borderColor);
            this.Remplissage = (Color)System.Windows.Media.ColorConverter.ConvertFromString(shape.fillingColor);
            this.SetRotation(shape.rotation);
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
