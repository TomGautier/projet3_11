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
    class Comment : Form
    {
        public const int DEFAULT_HEIGHT = 25;// 50;
        public const int DEFAULT_WIDTH = 50;//100;
        public const string TYPE = "Comment";

        public string Name { get; set; }



        public Comment(StylusPointCollection pts) : base(pts)

        {
            this.Center = new Point(pts[0].X, pts[0].Y);
            this.Height = 25;
            this.Width = 50;
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
            pts.Add(new StylusPoint(pts[0].X + this.Width, pts[0].Y + this.Height));
            pts.Add(new StylusPoint(pts[0].X, pts[0].Y + this.Height));
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
            this.HeightDirection = Point.Subtract(this.StylusPoints[3].ToPoint(), this.StylusPoints[0].ToPoint());           
            this.HeightDirection /= this.HeightDirection.Length;
            this.WidthDirection = Point.Subtract(this.StylusPoints[1].ToPoint(), this.StylusPoints[0].ToPoint());
            this.WidthDirection /= this.WidthDirection.Length;

            this.Width = Point.Subtract(this.StylusPoints[1].ToPoint(), this.StylusPoints[0].ToPoint() ).Length;
            this.Height = Point.Subtract(this.StylusPoints[3].ToPoint(), this.StylusPoints[0].ToPoint()).Length;
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
            Point origin = new Point(this.Center.X, this.Center.Y);
            SolidColorBrush brush = new SolidColorBrush(Colors.Black);
            Typeface typeFace = new Typeface(new FontFamily("Segoe UI"), FontStyles.Normal, FontWeights.Normal, FontStretches.Normal);
            FormattedText text = new FormattedText(this.Label, CultureInfo.CurrentUICulture, FlowDirection.LeftToRight, typeFace, 12, brush);
            origin.X -= text.Width / 2;
            origin.Y -= text.Height / 2;

            RotateTransform RT = new RotateTransform(this.CurrentRotation, this.Center.X, this.Center.Y);
            drawingContext.PushTransform(RT);

            while (text.Width > this.Width) { this.Width += 5; }
            while (text.Height > this.Height) { this.Height += 5; }
            drawingContext.DrawText(text, origin);

            drawingContext.Pop();
        }
    }
}