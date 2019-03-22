using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
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
        protected override void DrawCore(DrawingContext drawingContext, DrawingAttributes drawingAttributes)
        {
            OnDrawCore(drawingContext, drawingAttributes);            
            DrawLabel(drawingContext);
        }
        private Point GetCenter()
        {
            double distance = GetLength() / 2;
            double curr = 0;
            for (int i = 0; i< this.StylusPoints.Count-1; i++)
            {
                curr += Point.Subtract(this.StylusPoints[i + 1].ToPoint(), this.StylusPoints[i].ToPoint()).Length;

                if (curr > distance)
                {
                    double toCenter = curr - distance;
                    Vector direction = Point.Subtract(this.StylusPoints[i].ToPoint(), this.StylusPoints[i+1].ToPoint());
                    direction.Normalize();
                    Point center = (this.StylusPoints[i + 1].ToPoint() + direction * toCenter);
                    center += (new Vector(-direction.Y,direction.X) * 15);
                    return center;
                    
                }
            }
            


            return new Point(0, 0);
        }
        private double GetLength()
        {
            double length = 0;
            for (int i = 0; i < this.StylusPoints.Count - 1; i++)
            {
                length += Point.Subtract(this.StylusPoints[i + 1].ToPoint(), this.StylusPoints[i].ToPoint()).Length;
            }
            return length;
        }
        private void DrawLabel(DrawingContext drawingContext)
        {
            if (this.StylusPoints.Count > 1)
            {
                Point origin = GetCenter();
                //origin.Y += this.DrawingAttributes.Width;
                SolidColorBrush brush = new SolidColorBrush(Colors.Black);
                Typeface typeFace = new Typeface(new FontFamily("Segoe UI"), FontStyles.Normal, FontWeights.Bold, FontStretches.Normal);
                drawingContext.DrawText(new FormattedText(this.Label, CultureInfo.CurrentUICulture, FlowDirection.LeftToRight, typeFace, 12, brush), origin);
            }
        }
    }
}
