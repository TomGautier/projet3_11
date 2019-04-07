using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;

namespace PolyPaint.Utilitaires
{

    class Arrow : Form
    {
        public const string TYPE = "Arrow";
        public string Category { get; set; }
        public Form Shape1;
        public int Index1;
        public int Index2;
        public Form Shape2;
        public string Q1 { get; set; }
        public string Q2 { get; set; }
        public Arrow(StylusPointCollection pts)
            : base(pts)
        {
            this.DrawingAttributes.Color = Colors.Red;
            this.Type = TYPE;


        }
        public void ShapeMoved(string shapeID)
        {
            if (Shape1 != null && shapeID == Shape1.Id)
            {
                this.StylusPoints[0] = new StylusPoint(this.Shape1.EncPoints[Index1].X, this.Shape1.EncPoints[Index1].Y);
            }
            else
            {
                this.StylusPoints[this.StylusPoints.Count - 1] = new StylusPoint(this.Shape2.EncPoints[Index2].X, this.Shape2.EncPoints[Index2].Y);
            }
        }
        protected override void DrawCore(DrawingContext drawingContext, DrawingAttributes drawingAttributes)
        {
            SetSelection(drawingContext);
            OnDrawCore(drawingContext, drawingAttributes);
            
            DrawLabel(drawingContext);
            if (this.StylusPoints.Count > 1)
            {
                DrawCategory(drawingContext);

            }
            if (this.Shape1 != null && this.Shape2 != null)
            {

                DrawQuantity(drawingContext);
            }

        }
        private void DrawQuantity(DrawingContext dc)
        {
            Vector dir1 = Point.Subtract(this.StylusPoints[1].ToPoint(), this.StylusPoints[0].ToPoint());
            dir1.Normalize();
            Point origin1 = this.StylusPoints[0].ToPoint() + dir1*30;
            origin1 += (new Vector(-dir1.Y, dir1.X) * 15);

            Vector dir2 = Point.Subtract(this.StylusPoints[this.StylusPoints.Count-1].ToPoint(), this.StylusPoints[this.StylusPoints.Count-2].ToPoint());
            dir2.Normalize();
            Point origin2 = this.StylusPoints[this.StylusPoints.Count-1].ToPoint() - dir2 * 30;
            origin2 += (new Vector(-dir2.Y, dir2.X) * 15);
            //origin.Y += this.DrawingAttributes.Width;
            SolidColorBrush brush = new SolidColorBrush(Colors.Black);
            Typeface typeFace = new Typeface(new FontFamily("Segoe UI"), FontStyles.Normal, FontWeights.Bold, FontStretches.Normal);
            if (this.Q1 != null)
            {
                dc.DrawText(new FormattedText(this.Q1, CultureInfo.CurrentUICulture, FlowDirection.LeftToRight, typeFace, 12, brush), origin1);
            }
            if (this.Q2 != null)
            {
                dc.DrawText(new FormattedText(this.Q2, CultureInfo.CurrentUICulture, FlowDirection.LeftToRight, typeFace, 12, brush), origin2);
            }

        }
        public override void SetToShape(Shape shape)
        {
            this.Id = shape.id;
            this.Author = shape.author;
            if (shape.properties.height > 0)
            {
                this.DrawingAttributes.Width = shape.properties.height;//shape.properties.width;
                this.DrawingAttributes.Height = shape.properties.height;//shape.properties.width;
            }
            StylusPointCollection points = new StylusPointCollection();
            for (int i = 0; i < shape.properties.pointsX.Length; i++)
            {
                
                points.Add(new StylusPoint(shape.properties.pointsX[i], shape.properties.pointsY[i]));
            }
            this.StylusPoints = points;
            //this.Center = new Point(shape.properties.middlePointCoord[0], shape.properties.middlePointCoord[1]);
           // this.MakeShape();

            this.DrawingAttributes.Color = (Color)System.Windows.Media.ColorConverter.ConvertFromString(shape.properties.borderColor);
            this.BorderColor = (Color)System.Windows.Media.ColorConverter.ConvertFromString(shape.properties.borderColor);
            this.Remplissage = (Color)System.Windows.Media.ColorConverter.ConvertFromString(shape.properties.fillingColor);
            //  this.Index1 = shape.properties.index1;
            //  this.Index2 = shape.properties.index2;
            this.Q1 = shape.properties.q1;
            this.Q2 = shape.properties.q2;
            this.Category = shape.properties.category;
            this.Label = shape.properties.label;
            this.BorderStyle = shape.properties.borderType;
        }

        public override Shape ConvertToShape(string drawingSessionID)
        {
            string id1 = "";//null;
            string id2 = "";//null;
            if (this.Shape1 != null)
            {
                id1 = this.Shape1.Id;
            }
            if (this.Shape2 != null)
            {
                id2 = this.Shape2.Id;
            }
            int[] ptsX = new int[this.StylusPoints.Count];
            int[] ptsY = new int[this.StylusPoints.Count];
            for (int i = 0; i < this.StylusPoints.Count; i++)
            {
                ptsX[i] = (int)this.StylusPoints[i].X;
                ptsY[i] = (int)this.StylusPoints[i].Y;
                // pts[i] = this.StylusPoints[i].ToPoint();
            }
            ShapeProperties properties = new ShapeProperties(this.Type, this.Remplissage.ToString(), this.DrawingAttributes.Color.ToString(), null,
                (int)this.DrawingAttributes.Height, (int)this.DrawingAttributes.Height, -1, this.BorderStyle, this.Label,null, null, id1, id2, this.Index1, this.Index2, this.Q1, this.Q2, ptsX,ptsY, this.Category);
            return new Shape(this.Id, drawingSessionID, this.Author, properties);
        }
        private  void DrawCategory(DrawingContext drawingContext)
        {
            switch (this.Category)
            {
                case "One Way":
                    DrawOneWay(drawingContext);
                    break;
                case "Bidirectional":
                    DrawTwoWays(drawingContext);
                    break;
                case "Inheritance":
                    DrawInheritance(drawingContext);
                    break;
                case "Aggregation":
                    DrawAggregation(drawingContext);
                    break;
                case "Composition":
                    DrawComposition(drawingContext);
                    break;
            }
        }
        private void DrawOneWay(DrawingContext dc)
        {
            Vector direction = Point.Subtract(this.StylusPoints[this.StylusPoints.Count - 2].ToPoint(), this.StylusPoints[this.StylusPoints.Count - 1].ToPoint());
            direction.Normalize();
            Vector heightDirection = new Vector(-direction.Y, direction.X);
            heightDirection.Normalize();
            int width = (int)this.DrawingAttributes.Width * 3;
            LineSegment[] segments = new LineSegment[3];

            Point end = this.StylusPoints[this.StylusPoints.Count - 1].ToPoint();

            //segments[0] = new LineSegment(end,true);
            segments[0] = new LineSegment(end + width * direction + width*heightDirection,true);
            segments[1] = new LineSegment(end + width * direction - width * heightDirection, true);
            segments[2] = new LineSegment(end -5 * direction, true);

            var figure = new PathFigure(end -5 * direction, segments, true);
            var geo = new PathGeometry(new[] { figure });
            SolidColorBrush brush = new SolidColorBrush(Colors.Black);
            dc.DrawGeometry(brush, null, geo);
        }
        private void DrawTwoWays(DrawingContext dc)
        {
            DrawOneWay(dc);

            Vector direction = Point.Subtract(this.StylusPoints[1].ToPoint(), this.StylusPoints[0].ToPoint());
            direction.Normalize();
            Vector heightDirection = new Vector(-direction.Y, direction.X);
            heightDirection.Normalize();
            int width = (int)this.DrawingAttributes.Width * 3;
            LineSegment[] segments = new LineSegment[3];

            Point end = this.StylusPoints[0].ToPoint();

            //segments[0] = new LineSegment(end,true);
            segments[0] = new LineSegment(end + width * direction + width * heightDirection, true);
            segments[1] = new LineSegment(end + width * direction - width * heightDirection, true);
            segments[2] = new LineSegment(end - 5 * direction, true);

            var figure = new PathFigure(end - 5 * direction, segments, true);
            var geo = new PathGeometry(new[] { figure });
            SolidColorBrush brush = new SolidColorBrush(Colors.Black);
            dc.DrawGeometry(brush, null, geo);
        }
        private void DrawInheritance(DrawingContext dc)
        {
            Vector direction = Point.Subtract(this.StylusPoints[this.StylusPoints.Count - 2].ToPoint(), this.StylusPoints[this.StylusPoints.Count - 1].ToPoint());
            direction.Normalize();
            Vector heightDirection = new Vector(-direction.Y, direction.X);
            heightDirection.Normalize();
            int width = (int)this.DrawingAttributes.Width * 3;
            LineSegment[] segments = new LineSegment[3];

            Point end = this.StylusPoints[this.StylusPoints.Count - 1].ToPoint();

            //segments[0] = new LineSegment(end,true);
            segments[0] = new LineSegment(end + width * direction + width * heightDirection, true);
            segments[1] = new LineSegment(end + width * direction - width * heightDirection, true);
            segments[2] = new LineSegment(end - 5 * direction, true);

            var figure = new PathFigure(end - 5 * direction, segments, true);
            var geo = new PathGeometry(new[] { figure });
            SolidColorBrush brush = new SolidColorBrush(Colors.White);
            dc.DrawGeometry(brush, null, geo);
            SolidColorBrush brushBorder = new SolidColorBrush(Colors.Black);
            dc.DrawLine(new Pen(brushBorder, 1), end,segments[0].Point);
            dc.DrawLine(new Pen(brushBorder, 1), end, segments[1].Point);
            dc.DrawLine(new Pen(brushBorder, 1), segments[0].Point, segments[1].Point);
        }
        private void DrawAggregation(DrawingContext dc)
        {
            Vector direction = Point.Subtract(this.StylusPoints[this.StylusPoints.Count - 2].ToPoint(), this.StylusPoints[this.StylusPoints.Count - 1].ToPoint());
            direction.Normalize();
            Vector heightDirection = new Vector(-direction.Y, direction.X);
            heightDirection.Normalize();
            int width = (int)this.DrawingAttributes.Width * 3;
            LineSegment[] segments = new LineSegment[4];

            Point end = this.StylusPoints[this.StylusPoints.Count - 1].ToPoint();

            //segments[0] = new LineSegment(end,true);
            segments[0] = new LineSegment(end + width * direction + width * heightDirection, true);
            segments[1] = new LineSegment(end + (2*width + 5) * direction, true);
            segments[2] = new LineSegment(end + width * direction - width * heightDirection, true);
            segments[3] = new LineSegment(end - 5 * direction, true);


            var figure = new PathFigure(end - 5 * direction, segments, true);
            var geo = new PathGeometry(new[] { figure });
            SolidColorBrush brush = new SolidColorBrush(Colors.White);
            dc.DrawGeometry(brush, null, geo);
            SolidColorBrush brushBorder = new SolidColorBrush(Colors.Black);
            dc.DrawLine(new Pen(brushBorder, 1), end -5*direction, segments[0].Point);
            dc.DrawLine(new Pen(brushBorder, 1), end-5*direction, segments[2].Point);
            dc.DrawLine(new Pen(brushBorder, 1), segments[1].Point, segments[0].Point);
            dc.DrawLine(new Pen(brushBorder, 1), segments[1].Point, segments[2].Point);
        }
        private void DrawComposition(DrawingContext dc)
        {
            Vector direction = Point.Subtract(this.StylusPoints[this.StylusPoints.Count - 2].ToPoint(), this.StylusPoints[this.StylusPoints.Count - 1].ToPoint());
            direction.Normalize();
            Vector heightDirection = new Vector(-direction.Y, direction.X);
            heightDirection.Normalize();
            int width = (int)this.DrawingAttributes.Width * 3;
            LineSegment[] segments = new LineSegment[4];

            Point end = this.StylusPoints[this.StylusPoints.Count - 1].ToPoint();

            //segments[0] = new LineSegment(end,true);
            segments[0] = new LineSegment(end + width * direction + width * heightDirection, true);
            segments[1] = new LineSegment(end + (2 * width + 5) * direction, true);
            segments[2] = new LineSegment(end + width * direction - width * heightDirection, true);
            segments[3] = new LineSegment(end - 5 * direction, true);


            var figure = new PathFigure(end - 5 * direction, segments, true);
            var geo = new PathGeometry(new[] { figure });
            SolidColorBrush brush = new SolidColorBrush(Colors.Black);
            dc.DrawGeometry(brush, null, geo);
        }
        private Point GetCenter()
        {
            double distance = GetLength() / 2;
            double curr = 0;
            for (int i = 0; i< this.StylusPoints.Count-1; i++)
            {
                curr += Point.Subtract(this.StylusPoints[i + 1].ToPoint(), this.StylusPoints[i].ToPoint()).Length;

                if (curr > distance)
                {
                    double toCenter = curr - distance;
                    Vector direction = Point.Subtract(this.StylusPoints[i].ToPoint(), this.StylusPoints[i+1].ToPoint());
                    direction.Normalize();
                    Point center = (this.StylusPoints[i + 1].ToPoint() + direction * toCenter);
                    center += (new Vector(-direction.Y,direction.X) * 15);
                    return center;
                    
                }
            }
            


            return new Point(0, 0);
        }
        private double GetLength()
        {
            double length = 0;
            for (int i = 0; i < this.StylusPoints.Count - 1; i++)
            {
                length += Point.Subtract(this.StylusPoints[i + 1].ToPoint(), this.StylusPoints[i].ToPoint()).Length;
            }
            return length;
        }
        private void DrawLabel(DrawingContext drawingContext)
        {
            if (this.StylusPoints.Count > 1)
            {
                Point origin = GetCenter();
                //origin.Y += this.DrawingAttributes.Width;
                SolidColorBrush brush = new SolidColorBrush(Colors.Black);
                Typeface typeFace = new Typeface(new FontFamily("Segoe UI"), FontStyles.Normal, FontWeights.Bold, FontStretches.Normal);
                if (this.Label != null)
                {
                    drawingContext.DrawText(new FormattedText(this.Label, CultureInfo.CurrentUICulture, FlowDirection.LeftToRight, typeFace, 12, brush), origin);
                }
            }
        }
    }
}
