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
        public string Name { get; set; }
        public int Height { get; set; }
        public int Width { get; set; }


        public Artefact(StylusPointCollection pts) : base(MakeShape(pts))

        {
            this.StylusPoints = pts;
            this.Name = "";
            this.Height = (int)(pts[6].Y - pts[0].Y);
            this.Width = (int)(pts[6].X - pts[0].X);
            this.CurrentRotation = 0;
            this.BorderColor = Colors.Black;
            this.Remplissage = Colors.White;
            updateCenter();


        }
        private static StylusPointCollection MakeShape(StylusPointCollection pts)
        {
            pts.Add(new StylusPoint(pts[0].X + 35, pts[0].Y));
            pts.Add(new StylusPoint(pts[0].X + 35 + 15, pts[0].Y + 20));
            pts.Add(new StylusPoint(pts[0].X +35, pts[0].Y + 20));
            pts.Add(new StylusPoint(pts[0].X + 35, pts[0].Y));
            pts.Add(new StylusPoint(pts[0].X + 35 + 15, pts[0].Y + 20));
            pts.Add(new StylusPoint(pts[0].X + 35 + 15, pts[0].Y + 65));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y + 65));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y));
            return pts;
        }
        private void updateCenter()
        {
            double x = this.StylusPoints[0].X + (this.StylusPoints[6].X - this.StylusPoints[0].X) / 2;
            double y = this.StylusPoints[0].Y + (this.StylusPoints[6].Y - this.StylusPoints[0].Y) / 2;
            this.Center = new Point((int)x, (int)y);
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
            base.DrawCore(drawingContext, drawingAttributes);

            updateCenter();
        }
    }
}
