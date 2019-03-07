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
    class Activity : Form
    {
        public const int DEFAULT_HEIGHT = 70;
        public const int DEFAULT_WIDTH = 130;
        public const string TYPE = "Activity";

        public string Name { get; set; }
        public double Height { get; set; }
        public double Width { get; set; }


        public Activity(StylusPointCollection pts) : base(pts)

        {
            this.Center = new Point(pts[0].X, pts[0].Y);
            this.Height = 70;
            this.Width = 130;
            MakeShape();
            this.CurrentRotation = 0;
            this.BorderColor = Colors.Black;
            this.Remplissage = Colors.White;
        }
        private void MakeShape()
        {
            StylusPointCollection pts = new StylusPointCollection();
            pts.Add(new StylusPoint(this.Center.X - this.Width / 2, this.Center.Y - this.Height / 2));
            pts.Add(new StylusPoint(pts[0].X + 0.769*this.Width, pts[0].Y));
            pts.Add(new StylusPoint(pts[0].X + this.Width, pts[0].Y + 0.5*this.Height));
            pts.Add(new StylusPoint(pts[0].X + 0.769*this.Width, pts[0].Y + this.Height));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y + this.Height));
            pts.Add(new StylusPoint(pts[0].X + 0.23*this.Width, pts[0].Y + 0.5*this.Height));
            pts.Add(new StylusPoint(pts[0].X , pts[0].Y));
            this.StylusPoints = pts;
        }
        private void updatePoints()
        {
            double x = this.StylusPoints[0].X + (this.StylusPoints[3].X - this.StylusPoints[0].X) / 2;
            double y = this.StylusPoints[0].Y + (this.StylusPoints[3].Y - this.StylusPoints[0].Y) / 2;
            this.Center = new Point((int)x, (int)y);
            Vector heightDirection = Point.Subtract(this.StylusPoints[4].ToPoint(), this.StylusPoints[0].ToPoint());
            Point startWidth = new Point(this.StylusPoints[0].X + heightDirection.X / 2, this.StylusPoints[0].Y + heightDirection.Y / 2);
            this.Width = Point.Subtract(this.StylusPoints[2].ToPoint(), startWidth).Length;
            this.Height = Point.Subtract(this.StylusPoints[4].ToPoint(), this.StylusPoints[0].ToPoint()).Length;
        }
        private void Fill(DrawingContext drawingContext)
        {
           LineSegment[] segments = new LineSegment[6];
            for (int i = 0; i < this.StylusPoints.Count-1; i++)
            {
                segments[i] = new LineSegment(this.StylusPoints[i + 1].ToPoint(), true);
            }       
            var figure = new PathFigure(this.StylusPoints[0].ToPoint(), segments, true);
            var geo = new PathGeometry(new[] { figure });
            SolidColorBrush brush = new SolidColorBrush(this.Remplissage);
            drawingContext.DrawGeometry(brush, null, geo);
        }
        protected override void DrawCore(DrawingContext drawingContext, DrawingAttributes drawingAttributes)

        {
            Fill(drawingContext);
            base.DrawCore(drawingContext, drawingAttributes);
            updatePoints();
        }
    }
}
