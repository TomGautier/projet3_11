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
    class Form : Stroke
    {
        public Color BorderColor { get; set; }
        public double Height { get; set; }
        public double Width { get; set; }
        public string Id { get; set; }
        public string Author { get; set; }
        public string Type { get; set; }
        public int CurrentRotation { get; set; }
        public Point Center { get; set; }
        public Arrow Arrow { get; set; }
        public Point[] EncPoints { get; set; }
        protected Vector WidthDirection { get; set; }
        protected Vector HeightDirection { get; set; }

        private string borderStyle;
        public string BorderStyle
        {
            get { return borderStyle; }
            set
            {
                borderStyle = value;
                this.update();
            }
        }

        private bool showEncrage;
        public bool ShowEncrage
        {
            get { return showEncrage; }
            set
            {
                showEncrage = value;
                this.update();
            }
        }

        private Color remplissage;
        public Color Remplissage
        {
            get { return remplissage; }
            set
            {
                remplissage = value;
                this.update();
            }
        }
        private string label;
        public string Label
        {
            get { return label; }
            set
            {
                label = value;
                this.update();
                if (this.Type != "Arrow") {

                    this.MakeShape();                       
                    int rotation = this.CurrentRotation;
                    this.CurrentRotation = 0;
                    this.SetRotation(rotation);
                }
            }
        }

        private Boolean isSelectedByOther;
        public Boolean IsSelectedByOther
        {
            get { return isSelectedByOther; }
            set
            {
                isSelectedByOther = value;
                this.update();
            }
        }

        public void rotate()
        {
            int angleInc = 10;
            SetRotation(this.CurrentRotation + angleInc);
            // this.CurrentRotation += angleInc;

        }
        public virtual void MakeShape() { }
        public void SetRotation(int degrees)
        {
            
                Matrix rotatingMatrix = new Matrix();
                rotatingMatrix.RotateAt(360 - this.CurrentRotation, Center.X, Center.Y); //reset angle
                rotatingMatrix.RotateAt(degrees, Center.X, Center.Y);                   //apply rotation
                this.CurrentRotation = degrees;
                Stroke copy = this.Clone();
                copy.Transform(rotatingMatrix, false);
                this.StylusPoints = copy.StylusPoints;
            


        }
        public void SetToShape(Shape shape)
        {
            this.Id = shape.id;
            this.Author = shape.author;
            this.Width = shape.properties.width;
            this.Height = shape.properties.height;
            this.Center = new Point(shape.properties.middlePointCoord[0], shape.properties.middlePointCoord[1]);

            this.MakeShape();
            this.DrawingAttributes.Color = (Color)System.Windows.Media.ColorConverter.ConvertFromString(shape.properties.borderColor);
            this.BorderColor = (Color)System.Windows.Media.ColorConverter.ConvertFromString(shape.properties.borderColor);
            this.Remplissage = (Color)System.Windows.Media.ColorConverter.ConvertFromString(shape.properties.fillingColor);
            //this.CurrentRotation = shape.properties.rotation;
            //this.SetRotation(this.CurrentRotation);
            this.CurrentRotation = 0;
            this.SetRotation(shape.properties.rotation);

        }
        public Shape ConvertToShape(string drawingSessionID)
        {
            int[] middlePoint = new int[2] { (int)this.Center.X, (int)this.Center.Y };
            ShapeProperties properties = new ShapeProperties(this.Type, this.Remplissage.ToString(), this.DrawingAttributes.Color.ToString(), middlePoint,
                (int)this.Height, (int)this.Width, this.CurrentRotation);
            return new Shape(this.Id, drawingSessionID, this.Author, properties);
        }
        protected Pen GetPen()
        {
            var pen = new Pen(new SolidColorBrush(this.DrawingAttributes.Color), 2.5);
            pen.Thickness = this.DrawingAttributes.Width;
            //pen.Thickness = 3;
           switch (this.BorderStyle)
            {
                case "Dash":
                    pen.DashStyle = DashStyles.Dash;
                    break;

                case "Dot":
                    pen.DashStyle = DashStyles.Dot;
                    break;

                case "DashDot":
                    pen.DashStyle = DashStyles.DashDot;
                    break;

                case "DashDotDot":
                    pen.DashStyle = DashStyles.DashDotDot;
                    break;

                default:
                    pen.DashStyle = DashStyles.Solid;
                    break;
            }
            return pen;
        }
        protected void OnDrawCore(DrawingContext drawingContext,DrawingAttributes drawingAttributes)
        {
            if (this.BorderStyle == "Solid")
            {
                base.DrawCore(drawingContext, drawingAttributes);
            }
            else
            {
                Pen pen = this.GetPen();
                for (int i = 0; i < this.StylusPoints.Count - 1; i++)
                {
                    drawingContext.DrawLine(pen, this.StylusPoints[i].ToPoint(), this.StylusPoints[i + 1].ToPoint());
                }
            }
        }
        protected void DrawEncrage(DrawingContext drawingContext)
        {
            this.UpdateEncPoints();
            // Point[] encPoints = new Point[4]; 
            if (this.ShowEncrage)
            {
                for (int i = 0; i < this.EncPoints.Length; i++)
                {
                    LineSegment[] segments = new LineSegment[4];

                    segments[0] = new LineSegment(new Point(this.EncPoints[i].X + 4, this.EncPoints[i].Y - 4), true);
                    segments[1] = new LineSegment(new Point(this.EncPoints[i].X + 4, this.EncPoints[i].Y + 4), true);
                    segments[2] = new LineSegment(new Point(this.EncPoints[i].X - 4, this.EncPoints[i].Y + 4), true);
                    segments[3] = new LineSegment(new Point(this.EncPoints[i].X - 4, this.EncPoints[i].Y - 4), true);
                    var figure = new PathFigure(new Point(this.EncPoints[i].X - 4, this.EncPoints[i].Y - 4), segments, true);
                    var geo = new PathGeometry(new[] { figure });

                    SolidColorBrush brush = new SolidColorBrush(Colors.Brown);
                    drawingContext.DrawGeometry(brush, null, geo);
                }
            }

        /*    LineSegment[] segments = new LineSegment[4];

            segments[0] = new LineSegment(new Point(this.Center.X + 5, this.Center.Y - 5), true);
            segments[1] = new LineSegment(new Point(this.Center.X + 5, this.Center.Y + 5), true);
            segments[2] = new LineSegment(new Point(this.Center.X - 5, this.Center.Y + 5), true);
            segments[3] = new LineSegment(new Point(this.Center.X - 5, this.Center.Y - 5), true);
            var figure = new PathFigure(new Point(this.Center.X - 5, this.Center.Y - 5), segments, true);
            var geo = new PathGeometry(new[] { figure });

            SolidColorBrush brush = new SolidColorBrush(Colors.Green);
            drawingContext.DrawGeometry(brush, null, geo);*/
        }
        protected void UpdateEncPoints()
        {
            this.EncPoints[0] = this.Center - (this.HeightDirection * this.Height / 2);

            this.EncPoints[1] = this.Center + (this.WidthDirection * this.Width / 2);

            this.EncPoints[2] = this.Center + (this.HeightDirection * this.Height / 2);

            this.EncPoints[3] = this.Center - (this.WidthDirection * this.Width / 2);
        }
        protected void SetSelection(DrawingContext drawingContext)
        {
            if (this.IsSelectedByOther)
            {
                LineSegment[] segments = new LineSegment[4];
                
                segments[0] = new LineSegment(new Point(this.Center.X+5, this.Center.Y-5), true);
                segments[1] = new LineSegment(new Point(this.Center.X+5, this.Center.Y+5), true);
                segments[2] = new LineSegment(new Point(this.Center.X-5, this.Center.Y +5), true);
                segments[3] = new LineSegment(new Point(this.Center.X -5, this.Center.Y-5), true);
                var figure = new PathFigure(new Point(this.Center.X -5, this.Center.Y -5), segments, true);
                var geo = new PathGeometry(new[] { figure });

                SolidColorBrush brush = new SolidColorBrush(Colors.Green);
                drawingContext.DrawGeometry(brush, null, geo);
            }
        }
        public void update()
        {
            Stroke copy = this.Clone();
            this.StylusPoints = copy.StylusPoints;
        }

        public void translate(int x, int y)
        {
            for (int i = 0; i < this.StylusPoints.Count; i++)
            {
                this.StylusPoints[i] = new StylusPoint(this.StylusPoints[i].X + x, this.StylusPoints[i].Y + y);
            }
        }
        public Form(StylusPointCollection pts)
            : base(pts) {
            this.IsSelectedByOther = false;
            this.EncPoints = new Point[4];
            this.Label = "";
            this.showEncrage = false;
            this.BorderStyle = "Solid";
        }   
        
    }
}
