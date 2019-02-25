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
    class Role : Form
    {
        public string Name { get; set; }
        public int Height { get; set; }
        public int Width { get; set; }


        public Role(StylusPointCollection pts) : base(MakeShape(pts))

        {
            this.StylusPoints = pts;
            this.Name = "";
            this.Height = (int)(pts[5].Y - pts[0].Y -10);
            this.Width = (int)(pts[5].X - pts[0].X);
            this.CurrentRotation = 0;
            this.BorderColor = Colors.Black;
            this.Remplissage = Colors.White;
            updateCenter();


        }
        private static StylusPointCollection MakeShape(StylusPointCollection pts)
        {
            
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y + 10));
            pts.Add(new StylusPoint(pts[0].X - 20, pts[0].Y + 10));
            pts.Add(new StylusPoint(pts[0].X +20, pts[0].Y + 10));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y + 10));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y + 50));
            pts.Add(new StylusPoint(pts[0].X -20, pts[0].Y + 75));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y + 50));
            pts.Add(new StylusPoint(pts[0].X +20, pts[0].Y + 75));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y + 50));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y));

            float radius = 10;
            for (float i = (float)Math.PI/2; i <= 2 * Math.PI + (float)Math.PI / 2; i += 0.1f)
            {

                var x = (pts[0].X) + radius * Math.Cos(i);
                var y = (pts[0].Y - radius) + radius * Math.Sin(i);
                pts.Add(new StylusPoint(x, y));
            }
            // draw dot here at x,y
            return pts;
        }
        private void updateCenter()
        {
            double x = this.StylusPoints[0].X + (this.StylusPoints[5].X - this.StylusPoints[0].X) / 2;
            double y = (this.StylusPoints[0].Y) + (this.StylusPoints[5].Y - this.StylusPoints[0].Y) / 2;
            this.Center = new Point((int)x, (int)y);
        }
        private void Fill(DrawingContext drawingContext)
        {
            LineSegment[] segments = new LineSegment[this.StylusPoints.Count-12];
            for (int i = 11; i < this.StylusPoints.Count - 1; i++)
            {
                segments[i-11] = new LineSegment(this.StylusPoints[i].ToPoint(), true);
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
            updateCenter();
        }
    }
}
