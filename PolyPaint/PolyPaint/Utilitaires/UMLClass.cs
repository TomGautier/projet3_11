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
    class UMLClass : Stroke
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
        }
        public void AddMethod(string method)
        {
            this.Methods.Add(method);
        }
        public void AddAttribute(string attribute)
        {
            this.Attributes.Add(attribute);
        }
        protected override void DrawCore(DrawingContext drawingContext, DrawingAttributes drawingAttributes)
         
        {
            SolidColorBrush brush = new SolidColorBrush(Color.FromRgb(255,0,0));
            brush.Freeze();
            //FILL
           // drawingContext.DrawRectangle(brush, null, new Rect(this.StylusPoints[0].ToPoint(),this.StylusPoints[2].ToPoint()));
            DrawName(drawingContext);
            DrawAttributes(drawingContext);
            DrawMethods(drawingContext);
            base.DrawCore(drawingContext, drawingAttributes);
        }
        private void DrawName(DrawingContext drawingContext)
        {
            if (this.Name != null && this.Name.Length != 0)
            {
                Point origin = new Point(this.StylusPoints[0].ToPoint().X + 2, this.StylusPoints[0].ToPoint().Y + 4);
                Typeface typeFace = new Typeface(new FontFamily("Segoe UI"), FontStyles.Normal, FontWeights.Normal, FontStretches.Normal);
                drawingContext.DrawText(new FormattedText(this.Name, CultureInfo.CurrentUICulture, FlowDirection.LeftToRight, typeFace, 12, Brushes.Black), origin);
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
                    if (Math.Abs(this.StylusPoints[2].Y - origin.Y) < 10)
                    {
                        this.StylusPoints[2] = new StylusPoint(this.StylusPoints[2].X, this.StylusPoints[2].Y + increment);
                        this.StylusPoints[3] = new StylusPoint(this.StylusPoints[3].X, this.StylusPoints[3].Y + increment);
                    }
                }
            }
        }
    }
}
