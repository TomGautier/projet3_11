using PolyPaint.Utilitaires;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Input;
using System.Windows.Media;

namespace PolyPaint.Managers
{
    class FormConnectorManager
    {
        public StylusPointCollection Points;
        public Form Shape1;
        public Form Shape2;
        private int Index1;
        private int Index2;
        public List<Arrow> Arrows;

        private string Label { get; set; }
        private string Type { get; set; }
        private string BorderStyle { get; set; }
        private int Size { get; set; }
        private string Color { get; set; }
        private string Q1 { get; set; }
        private string Q2 { get; set; }
        public bool IsDrawingArrow { get; set; }

        public FormConnectorManager(StylusPointCollection pts, Form shape1, Form shape2)
        {
            Points = pts;
            Shape1 = shape1;
            Shape2 = shape2;
            IsDrawingArrow = false;
        }
        public FormConnectorManager()
        {
            Points = new StylusPointCollection();
            this.Arrows = new List<Arrow>();
            IsDrawingArrow = false;
        }
        public void SetParameters(string label, string type,string border, int size, string color,string q1, string q2)
        {
            this.Label = label;
            this.Type = type;
            this.BorderStyle = border;
            this.Size = size;
            this.Color = color;
            this.Q1 = q1;
            this.Q2 = q2;
        }
        public void reset()
        {
            this.Arrows.RemoveAt(this.Arrows.Count - 1);
            this.IsDrawingArrow = false;
        }
        
            public bool update(StylusPoint p, bool isOnEncrage, Form shape, int index) //returns true if a new arrow was created
        {
           if (!IsDrawingArrow) //&& isOnEncrage) //Premier point
            {
                if (isOnEncrage)
                {
                    this.Shape1 = shape;
                    this.Index1 = index;
                    
                }
                this.Arrows.Add(new Arrow(new StylusPointCollection { p }));
                this.Arrows[this.Arrows.Count - 1].Label = this.Label;
                this.Arrows[this.Arrows.Count - 1].BorderStyle = this.BorderStyle;
                this.Arrows[this.Arrows.Count - 1].DrawingAttributes.Color = (Color)ColorConverter.ConvertFromString(this.Color);
                this.Arrows[this.Arrows.Count - 1].DrawingAttributes.Width = this.Size;
                this.Arrows[this.Arrows.Count - 1].DrawingAttributes.Height = this.Size;
                this.Arrows[this.Arrows.Count - 1].Category = this.Type;
                this.Arrows[this.Arrows.Count - 1].Q1 = this.Q1;
                this.Arrows[this.Arrows.Count - 1].Q2 = this.Q2;
                this.IsDrawingArrow = true;
                return true;
            }
           else if(IsDrawingArrow && isOnEncrage) //&& shape.Id != Shape1.Id)
            {
                if (Shape1 == null || shape.Id != Shape1.Id)
                {
                    this.Arrows[this.Arrows.Count - 1].StylusPoints.Add(p);
                    this.Shape2 = shape;
                    this.Index2 = index;

                    this.Shape1.Arrow = this.Arrows[this.Arrows.Count - 1];
                    this.Shape2.Arrow = this.Arrows[this.Arrows.Count - 1];
                    this.Arrows[this.Arrows.Count - 1].Shape1 = this.Shape1;
                    this.Arrows[this.Arrows.Count - 1].Shape2 = this.Shape2;
                    this.Arrows[this.Arrows.Count - 1].Index1 = this.Index1;
                    this.Arrows[this.Arrows.Count - 1].Index2 = this.Index2;
                    this.Arrows[this.Arrows.Count - 1].update();
                    this.IsDrawingArrow = false; //reset
                }             

                //this.IsDrawingArrow = false; //reset
            }
           else if(IsDrawingArrow && !isOnEncrage)
            {
                this.Arrows[this.Arrows.Count-1].StylusPoints.Add(p);
            }
           
            return false;
        }
       
    }
}
