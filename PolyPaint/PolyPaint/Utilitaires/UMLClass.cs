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
        public const int DEFAULT_HEIGHT = 220;
        public const int DEFAULT_WIDTH = 110;
        public const string TYPE = "UmlClass";

        public string Name { get; set; }

        
        private List<String> Methods { get; set; }
        private List<String> Attributes { get; set; }
        
       
        public UMLClass(StylusPointCollection pts) : base((pts))
            
        {
            this.Center = new Point(pts[0].X,pts[0].Y);
            this.Height = 220;
            this.Width = 110;
            MakeShape();
            this.Name = "";
            this.Methods = new List<String>();
            this.Attributes = new List<String>();
            this.CurrentRotation = 0;
            this.BorderColor = Colors.Black;
            this.Remplissage = Colors.White;
            this.Type = TYPE;

        }
        public void MakeShape()
        {
            StylusPointCollection pts = new StylusPointCollection();
            pts.Add(new StylusPoint(this.Center.X - this.Width / 2, this.Center.Y - this.Height / 2));
            pts.Add(new StylusPoint(pts[0].X + this.Width, pts[0].Y));
            pts.Add(new StylusPoint(pts[0].X + this.Width, pts[0].Y + this.Height));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y + this.Height));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y + this.Height/7));
            pts.Add(new StylusPoint(pts[0].X + this.Width, pts[0].Y + this.Height/7));
            pts.Add(new StylusPoint(pts[0].X + this.Width, pts[0].Y + this.Height/2.3));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y + this.Height/2.3));

            this.StylusPoints = pts;
        }
        private void updatePoints()
        {
            double x = this.StylusPoints[0].X + (this.StylusPoints[2].X - this.StylusPoints[0].X) / 2;
            double y = this.StylusPoints[0].Y + (this.StylusPoints[2].Y - this.StylusPoints[0].Y) / 2;
            this.Center = new Point(x,y);

            this.Width = Point.Subtract(this.StylusPoints[1].ToPoint(), this.StylusPoints[0].ToPoint()).Length;
            this.Height = Point.Subtract(this.StylusPoints[3].ToPoint(), this.StylusPoints[0].ToPoint()).Length;
        }
        private void Fill(DrawingContext drawingContext)
        {
            LineSegment[] segments = new LineSegment[4];          
            for (int i = 0; i< 4; i++)
            {
                segments[i] = new LineSegment(this.StylusPoints[i+1].ToPoint(), true);
            }
            var figure = new PathFigure(this.StylusPoints[0].ToPoint(), segments, true);
            var geo = new PathGeometry(new[] { figure });

            SolidColorBrush brush = new SolidColorBrush(this.Remplissage);
            drawingContext.DrawGeometry(brush, null, geo);
        }
        public void AddMethod(string method)
        {
            this.StylusPoints[0] = new StylusPoint(this.StylusPoints[0].X + 50, this.StylusPoints[0].Y);
        }
        protected override void OnStylusPointsChanged(EventArgs e)
        {
            base.OnStylusPointsChanged(e);
        }
        public void AddAttribute(string attribute)
        {
            this.Attributes.Add(attribute);
        }
        protected override void DrawCore(DrawingContext drawingContext, DrawingAttributes drawingAttributes)
         
        {       
            Fill(drawingContext);
            base.DrawCore(drawingContext, drawingAttributes);
                      
            SetSelection(drawingContext);
            updatePoints(); 
        }
        private void DrawName(DrawingContext drawingContext)
        {
                Point origin = new Point(this.StylusPoints[0].ToPoint().X + 2, this.StylusPoints[0].ToPoint().Y + 4);
                SolidColorBrush brush = new SolidColorBrush(this.BorderColor);
                Typeface typeFace = new Typeface(new FontFamily("Segoe UI"), FontStyles.Normal, FontWeights.Normal, FontStretches.Normal);
                drawingContext.DrawText(new FormattedText(this.Height.ToString(), CultureInfo.CurrentUICulture, FlowDirection.LeftToRight, typeFace, 12, brush), origin);
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
