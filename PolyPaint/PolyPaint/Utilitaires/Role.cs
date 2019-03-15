﻿using System;
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
        public const int DEFAULT_HEIGHT = 75;
        public const int DEFAULT_WIDTH = 40;
        public const string TYPE = "Role";

        public string Name { get; set; }

        private double Radius { get; set; }


        public Role(StylusPointCollection pts) : base(pts)

        {
            // this.StylusPoints = pts;
            this.Center = new Point(pts[0].X, pts[0].Y);
            this.Name = "";
            this.Height = 120;
            this.Width = 60;
            MakeShape();
            this.CurrentRotation = 0;
            this.BorderColor = Colors.Black;
            this.Remplissage = Colors.White;
            this.Type = TYPE;
        }
        protected override void MakeShape()
        {
            StylusPointCollection pts = new StylusPointCollection();
            pts.Add(new StylusPoint(this.Center.X, this.Center.Y - 0.4*Height));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y + 0.1333 * this.Height));
            pts.Add(new StylusPoint(pts[0].X - 0.5 * this.Width, pts[0].Y + 0.1333 * this.Height));
            pts.Add(new StylusPoint(pts[0].X + 0.5 * this.Width, pts[0].Y + 0.1333 * this.Height));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y + 0.13333 * this.Height));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y + 0.50666 * this.Height));
            pts.Add(new StylusPoint(pts[0].X - 0.5 * this.Width, pts[0].Y + 0.866*this.Height));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y + 0.50666 * this.Height));
            pts.Add(new StylusPoint(pts[0].X + 0.5 * this.Width, pts[0].Y + 0.866*this.Height));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y + 0.50666 * this.Height));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y));
            /*pts.Add(new StylusPoint(this.Center.X, this.Center.Y - 0.382 * this.Height));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y + 0.1333*this.Height));
            pts.Add(new StylusPoint(pts[0].X - 0.5*this.Width, pts[0].Y + 0.1333*this.Height));
            pts.Add(new StylusPoint(pts[0].X +0.5*this.Width, pts[0].Y + 0.1333*this.Height));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y + 0.1333*this.Height));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y + 0.666*this.Height));
            pts.Add(new StylusPoint(pts[0].X -0.5*this.Width, pts[0].Y + this.Height));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y + 0.666*this.Height));
            pts.Add(new StylusPoint(pts[0].X +0.5*this.Width, pts[0].Y + this.Height));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y + 0.666*this.Height));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y));*/

            this.Radius = (float)this.Height * 0.1;
            for (float i = (float)Math.PI/2; i <= 2 * Math.PI + (float)Math.PI / 2; i += 0.1f)
            {

                var x = pts[0].X + this.Radius * Math.Cos(i);
                var y = (pts[0].Y - this.Radius) + this.Radius * Math.Sin(i);
                pts.Add(new StylusPoint(x, y));
            }
            this.StylusPoints = pts;
        }
        private void updatePoints()
        {
            //double x = this.StylusPoints[0].X + (this.StylusPoints[5].X - this.StylusPoints[0].X) / 2;
            //double y = this.StylusPoints[0].Y - this.Radius + (this.StylusPoints[6].Y - this.StylusPoints[0].Y - this.Radius) / 2;
            //this.Center = new Point(x, y);
            Vector heightDirection = Point.Subtract(this.StylusPoints[5].ToPoint(), this.StylusPoints[0].ToPoint());
            heightDirection.Normalize();
            this.Height = Point.Subtract(this.StylusPoints[6].ToPoint(),this.StylusPoints[0].ToPoint()).Length + this.Radius; 
            this.Width = Point.Subtract(this.StylusPoints[8].ToPoint(), this.StylusPoints[6].ToPoint()).Length;
            Point startHeight = Point.Subtract(this.StylusPoints[0].ToPoint(), this.Radius * heightDirection);
            Point endHeight = startHeight + this.Height*heightDirection;
            this.Center = startHeight + Point.Subtract(endHeight, startHeight) / 2;

            if (this.Arrow != null)
            {
                this.Arrow.ShapeMoved(this.Id,new StylusPoint(this.Center.X,this.Center.Y));
            }
            
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
            
            base.DrawCore(drawingContext, drawingAttributes);
            updatePoints();
            Fill(drawingContext);
            SetSelection(drawingContext);
        }
    }
}