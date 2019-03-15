using PolyPaint.Utilitaires;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Input;

namespace PolyPaint.Managers
{
    class FormConnectorManager
    {
        public StylusPointCollection Points;
        public Form Shape1;
        public Form Shape2;
        public List<Arrow> Arrows;
        private bool IsDrawingArrow { get; set; }

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
        public bool update(StylusPoint p, bool isOnEncrage, Form shape) //returns true if a new arrow was created
        {
           if (!IsDrawingArrow && isOnEncrage) //Premier point
            {
                Shape1 = shape;
                this.Arrows.Add(new Arrow(new StylusPointCollection { p }));
                this.IsDrawingArrow = true;
                return true;
            }
           else if(IsDrawingArrow && isOnEncrage && shape.Id != Shape1.Id)
            {
                this.Arrows[this.Arrows.Count-1].StylusPoints.Add(p);
                this.Shape2 = shape;

                this.Shape1.Arrow = this.Arrows[this.Arrows.Count-1];
                this.Shape2.Arrow = this.Arrows[this.Arrows.Count-1];
                this.Arrows[this.Arrows.Count - 1].Shape1 = this.Shape1;
                this.Arrows[this.Arrows.Count - 1].Shape2 = this.Shape2;
                this.IsDrawingArrow = false; //reset
            }
           else if(IsDrawingArrow && !isOnEncrage)
            {
                this.Arrows[this.Arrows.Count-1].StylusPoints.Add(p);
            }
            return false;
        }
       
    }
}
