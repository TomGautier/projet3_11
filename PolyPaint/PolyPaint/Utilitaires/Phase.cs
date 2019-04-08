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
    class Phase : Form
    {
        public const int DEFAULT_HEIGHT = 155;//310;
        public const int DEFAULT_WIDTH = 200;//400;
        public const string TYPE = "Phase";

        public string Name { get; set; }



        public Phase(StylusPointCollection pts) : base(pts)

        {
            this.Center = new Point(pts[0].X, pts[0].Y);
            this.Height = 155;
            this.Width = 200;
            MakeShape();
            this.CurrentRotation = 0;
            this.BorderColor = Colors.Black;
            this.Remplissage = Colors.White;
            this.Type = TYPE;
            this.updatePoints();
           

            //this.StrokeDashArray = new DoubleCollection() { 2 };

            /* this.HeightDirection = Point.Subtract(this.StylusPoints[4].ToPoint(), this.StylusPoints[0].ToPoint());
             this.HeightDirection.Normalize();
             this.WidthDirection = Point.Subtract(this.StylusPoints[1].ToPoint(), this.StylusPoints[0].ToPoint());
             this.WidthDirection.Normalize();*/
        }

        public override void MakeShape()
        {
            StylusPointCollection pts = new StylusPointCollection();
            pts.Add(new StylusPoint(this.Center.X - this.Width / 2, this.Center.Y - this.Height / 2));
            pts.Add(new StylusPoint(pts[0].X + this.Width, pts[0].Y));
            pts.Add(new StylusPoint(pts[0].X + this.Width, pts[0].Y + this.Height / 10));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y + this.Height / 10));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y + this.Height));
            pts.Add(new StylusPoint(pts[0].X + this.Width, pts[0].Y + this.Height));
            pts.Add(new StylusPoint(pts[0].X +this.Width, pts[0].Y));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y));


            this.StylusPoints = pts;

            if (this.CurrentRotation != 0)
            {
                int rotation = this.CurrentRotation;
                this.CurrentRotation = 0;
                this.SetRotation(rotation);
            }
        }
        private void updatePoints()
        {
            this.HeightDirection = Point.Subtract(this.StylusPoints[5].ToPoint(), this.StylusPoints[0].ToPoint());
            Point startWidth = new Point(this.StylusPoints[0].X + this.HeightDirection.X / 2, this.StylusPoints[0].Y + this.HeightDirection.Y / 2);
            this.HeightDirection /= this.HeightDirection.Length;
            this.WidthDirection = Point.Subtract(this.StylusPoints[1].ToPoint(), this.StylusPoints[0].ToPoint());
            this.WidthDirection /= this.WidthDirection.Length;

            this.Width = Point.Subtract(this.StylusPoints[1].ToPoint(), this.StylusPoints[0].ToPoint()).Length;
            this.Height = Point.Subtract(this.StylusPoints[5].ToPoint(), this.StylusPoints[0].ToPoint()).Length;
            //Vector heightDirection = Point.Subtract(this.StylusPoints[4].ToPoint(), this.StylusPoints[0].ToPoint());

            //  double x = startWidth.X + (this.StylusPoints[2].X - startWidth.X) / 2;
            //  double y = this.StylusPoints[0].Y + (this.StylusPoints[4].Y - this.StylusPoints[0].Y) / 2;
            //  this.Center = new Point((int)x, (int)y); 

            this.Center = this.StylusPoints[0].ToPoint() + this.WidthDirection * this.Width/2;
            this.Center += this.HeightDirection * this.Height / 2;

           
            this.UpdateEncPoints();

            if (this.Arrow.Count > 0)
            {
                foreach (Arrow a in this.Arrow)
                    a.ShapeMoved(this.Id);
            }

        }
        private void Fill(DrawingContext drawingContext)
        {
            LineSegment[] segments = new LineSegment[4];           
            segments[0] = new LineSegment(this.StylusPoints[6].ToPoint(), true);
            segments[1] = new LineSegment(this.StylusPoints[5].ToPoint(), true);
            segments[2] = new LineSegment(this.StylusPoints[3].ToPoint(), true);
            segments[3] = new LineSegment(this.StylusPoints[2].ToPoint(), true);

            var figure = new PathFigure(this.StylusPoints[2].ToPoint(), segments, true);
            var geo = new PathGeometry(new[] { figure });
            SolidColorBrush brush = new SolidColorBrush(this.Remplissage);
            drawingContext.DrawGeometry(brush, null, geo);
        }
        protected override void DrawCore(DrawingContext drawingContext, DrawingAttributes drawingAttributes)

        {
            Fill(drawingContext);
            SetSelection(drawingContext);
            OnDrawCore(drawingContext, drawingAttributes);
            // base.DrawCore(drawingContext, drawingAttributes);
            updatePoints();
            if (this.Label != null)
            {
                DrawName(drawingContext);
            }
            DrawEncrage(drawingContext);
            DrawRotator(drawingContext);
        }
        private void DrawName(DrawingContext drawingContext)
        {
            Point origin = this.Center - this.HeightDirection * (this.Height / 2 - this.Height / 20);
            SolidColorBrush brush = new SolidColorBrush(Colors.Black);
            Typeface typeFace = new Typeface(new FontFamily("Segoe UI"), FontStyles.Normal, FontWeights.Normal, FontStretches.Normal);
            FormattedText text = new FormattedText(this.Label, CultureInfo.CurrentUICulture, FlowDirection.LeftToRight, typeFace, 12, brush);
            RotateTransform RT = new RotateTransform(this.CurrentRotation, origin.X, origin.Y);
            drawingContext.PushTransform(RT);
            origin.X -= text.Width / 2;
            origin.Y -= text.Height / 2;
            drawingContext.DrawText(text, origin);
            drawingContext.Pop();
        }
    }
}