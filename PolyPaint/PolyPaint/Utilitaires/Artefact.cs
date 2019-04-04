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
    class Artefact : Form
    {
        public const int DEFAULT_HEIGHT = 65;
        public const int DEFAULT_WIDTH = 50;
        public const string TYPE = "Artefact";

        public string Name { get; set; }



        public Artefact(StylusPointCollection pts) : base(pts)

        {
            this.Center = new Point(pts[0].X, pts[0].Y);
            this.Height = 65;
            this.Width = 50;
            MakeShape();
            this.CurrentRotation = 0;
            this.BorderColor = Colors.Black;
            this.Remplissage = Colors.White;
            this.Type = TYPE;
            this.updatePoints();
        }
        public override void MakeShape()
        {
            StylusPointCollection pts = new StylusPointCollection();
            pts.Add(new StylusPoint(this.Center.X - this.Width / 2, this.Center.Y - this.Height / 2));

            pts.Add(new StylusPoint(pts[0].X + 0.7*this.Width, pts[0].Y));
            pts.Add(new StylusPoint(pts[0].X + this.Width, pts[0].Y + 0.307*this.Height));
            pts.Add(new StylusPoint(pts[0].X +0.7 *this.Width, pts[0].Y + 0.307*this.Height));
            pts.Add(new StylusPoint(pts[0].X + 0.7*this.Width, pts[0].Y));
            pts.Add(new StylusPoint(pts[0].X + this.Width, pts[0].Y + 0.307*this.Height));
            pts.Add(new StylusPoint(pts[0].X + this.Width, pts[0].Y + this.Height));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y + this.Height));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y));
            this.StylusPoints = pts;
        }
        private void updatePoints()
        {
            this.WidthDirection = Point.Subtract(this.StylusPoints[6].ToPoint(), this.StylusPoints[7].ToPoint());
            this.WidthDirection /= this.WidthDirection.Length;
            this.HeightDirection = Point.Subtract(this.StylusPoints[7].ToPoint(), this.StylusPoints[0].ToPoint());
            this.HeightDirection /= this.HeightDirection.Length;
            double x = this.StylusPoints[0].X + (this.StylusPoints[6].X - this.StylusPoints[0].X) / 2;
            double y = this.StylusPoints[0].Y + (this.StylusPoints[6].Y - this.StylusPoints[0].Y) / 2;
            this.Center = new Point((int)x, (int)y);        
            this.Width = Point.Subtract(this.StylusPoints[7].ToPoint(), this.StylusPoints[6].ToPoint()).Length;
            this.Height = Point.Subtract(this.StylusPoints[7].ToPoint(), this.StylusPoints[0].ToPoint()).Length;
            this.UpdateEncPoints();

            if (this.Arrow != null)
            {
                this.Arrow.ShapeMoved(this.Id);
            }
        }
        private void Fill(DrawingContext drawingContext)
        {
            LineSegment[] segments = new LineSegment[4];
            for (int i = 0; i < 2; i++)
            {
                segments[i] = new LineSegment(this.StylusPoints[i+1].ToPoint(), true);
            }
            for (int i = 6; i < 8; i++)
            {
                segments[i-4] = new LineSegment(this.StylusPoints[i].ToPoint(), true);
            }
            var figure = new PathFigure(this.StylusPoints[0].ToPoint(), segments, true);
            var geo = new PathGeometry(new[] { figure });

            SolidColorBrush brush = new SolidColorBrush(this.Remplissage);
            drawingContext.DrawGeometry(brush, null, geo);
        }
        protected override void DrawCore(DrawingContext drawingContext, DrawingAttributes drawingAttributes)

        {
            Fill(drawingContext);
            SetSelection(drawingContext);
            OnDrawCore(drawingContext, drawingAttributes);
            //          base.DrawCore(drawingContext, drawingAttributes);
            updatePoints();
            if (this.Label != null)
            {
                DrawName(drawingContext);
            }
            DrawEncrage(drawingContext);
        }
        private void DrawName(DrawingContext drawingContext)
        {
            Point origin = new Point(this.Center.X, this.Center.Y + this.Height / 2 + 20);
            SolidColorBrush brush = new SolidColorBrush(Colors.Red);
            Typeface typeFace = new Typeface(new FontFamily("Segoe UI"), FontStyles.Normal, FontWeights.Normal, FontStretches.Normal);
            FormattedText text = new FormattedText(this.Label, CultureInfo.CurrentUICulture, FlowDirection.LeftToRight, typeFace, 12, brush);
            origin.X -= text.Width / 2;
            drawingContext.DrawText(text, origin);
        }
    }
}
