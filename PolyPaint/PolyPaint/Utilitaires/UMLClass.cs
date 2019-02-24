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
    class UMLClass : Form
    {
        public string Name { get; set; }
        private List<String> Methods { get; set; }
        private List<String> Attributes { get; set; }
        
       
        public UMLClass(StylusPointCollection pts)
            : base(pts)
        {
            this.StylusPoints = pts;
            this.Name = "";
            this.Methods = new List<String>();
            this.Attributes = new List<String>();
            this.CurrentRotation = 0;
            this.BorderColor = Colors.Black;
            updateCenter();
            
            
        }
        private void updateCenter()
        {
            double x = this.StylusPoints[0].X + (this.StylusPoints[2].X - this.StylusPoints[0].X) / 2;
            double y = this.StylusPoints[0].Y + (this.StylusPoints[2].Y - this.StylusPoints[0].Y) / 2;
            this.Center = new Point((int)x, (int)y);
        }
        
       /* private void updatePivot()
        {
            this.Pivot = new StylusPoint(this.StylusPoints[0].X + ((this.StylusPoints[1].X - this.StylusPoints[0].X) / 2), this.StylusPoints[0].Y + ((this.StylusPoints[3].Y - this.StylusPoints[0].Y) / 2));
        }*/
        public void AddMethod(string method)
        {
            this.Methods.Add(method);
        }
       //private ApplyRotation(StylusPoint point,
        protected override void OnStylusPointsChanged(EventArgs e)
        {
            base.OnStylusPointsChanged(e);
        }
        public void AddAttribute(string attribute)
        {
            this.Attributes.Add(attribute);
        }
        private StylusPoint ApplyRotation(double angle, StylusPoint pivot, StylusPoint point)
        {
            double rad = angle * Math.PI / 180;
            double cos = Math.Cos(rad);
            double sin = Math.Sin(rad);
            double dx = (point.X - pivot.X);
            double dy = (point.Y - pivot.Y);
            double x = cos * dx - sin * dy + pivot.X;
            double y = sin * dx + cos * dy + pivot.Y;
            return new StylusPoint((int)Math.Round(x), (int)Math.Round(y));
        }
        protected override void DrawCore(DrawingContext drawingContext, DrawingAttributes drawingAttributes)
         
        {
           
            SolidColorBrush brush = new SolidColorBrush(Color.FromRgb(255,0,0));
            //brush.Freeze();
            //FILL
             //drawingContext.DrawRectangle(brush, null, new Rect(this.StylusPoints[0].ToPoint(),this.StylusPoints[2].ToPoint()));         
            //DrawName(drawingContext);
            // DrawAttributes(drawingContext);
            //DrawMethods(drawingContext);
           //  updatePivot();
            base.DrawCore(drawingContext, drawingAttributes);
            updateCenter();
        }
        private void DrawName(DrawingContext drawingContext)
        {
            if (this.Name != null && this.Name.Length != 0)
            {

                Point origin = new Point(this.StylusPoints[0].ToPoint().X + 2, this.StylusPoints[0].ToPoint().Y + 4);
                //StylusPoint pivot = this.StylusPoints.Last();
                //StylusPoint pivot = new StylusPoint(0, 0);
                // origin = ApplyRotation(this.CurrentRotation, pivot, origin);
                //  origin = ApplyRotation(this.CurrentRotation, new StylusPoint(0,0), origin);
                //RotateTransform RT = new RotateTransform(this.CurrentRotation);
                // drawingContext.PushTransform(RT);

                //Point location = new Point(origin.X, origin.Y);
                SolidColorBrush brush = new SolidColorBrush(this.BorderColor);
                Typeface typeFace = new Typeface(new FontFamily("Segoe UI"), FontStyles.Normal, FontWeights.Normal, FontStretches.Normal);
                drawingContext.DrawText(new FormattedText(this.Name, CultureInfo.CurrentUICulture, FlowDirection.LeftToRight, typeFace, 12, brush), origin);
               // drawingContext.Pop();    
            }
        }
        private void DrawAttributes(DrawingContext drawingContext)
        {
            if (this.Attributes.Count != 0)
            {
                Point origin = new Point(this.StylusPoints[5].ToPoint().X + 2, this.StylusPoints[5].ToPoint().Y + 4);
                int increment = 15;
                Typeface typeFace = new Typeface(new FontFamily("Segoe UI"), FontStyles.Normal, FontWeights.Normal, FontStretches.Normal);
                for (int i = 0; i < this.Attributes.Count; i++)
                {
                    drawingContext.DrawText(new FormattedText("+ " + this.Attributes[i], CultureInfo.CurrentUICulture, FlowDirection.LeftToRight, typeFace, 12, Brushes.Black), origin);
                    origin.Y += increment;
                    if (Math.Abs(this.StylusPoints[8].Y - origin.Y) < 15)
                    {
                        this.StylusPoints[7] = new StylusPoint(this.StylusPoints[7].X, this.StylusPoints[7].Y + increment);
                        this.StylusPoints[8] = new StylusPoint(this.StylusPoints[8].X, this.StylusPoints[8].Y + increment);
                    }
                }
            }
        }
        private void DrawMethods(DrawingContext drawingContext)
        {
            if (this.Methods.Count != 0)
            {
                Point origin = new Point(this.StylusPoints[8].ToPoint().X + 2, this.StylusPoints[8].ToPoint().Y + 4);
                int increment = 15;
                Typeface typeFace = new Typeface(new FontFamily("Segoe UI"), FontStyles.Normal, FontWeights.Normal, FontStretches.Normal);
                for (int i = 0; i < this.Methods.Count; i++)
                {
                    drawingContext.DrawText(new FormattedText("+ " + this.Methods[i], CultureInfo.CurrentUICulture, FlowDirection.LeftToRight, typeFace, 12, Brushes.Black), origin);

                    origin.Y += increment;
                    if (Math.Abs(this.StylusPoints[2].Y - origin.Y) < 15)
                    {
                        this.StylusPoints[2] = new StylusPoint(this.StylusPoints[2].X, this.StylusPoints[2].Y + increment);
                        this.StylusPoints[3] = new StylusPoint(this.StylusPoints[3].X, this.StylusPoints[3].Y + increment);
                    }
                }
            }
        }
    }
}
