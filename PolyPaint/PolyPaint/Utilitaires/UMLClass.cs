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
    class UMLClass : Stroke
    {
        public UMLClass(StylusPointCollection pts)
            : base(pts)
        {
            this.StylusPoints = pts;
        }
        protected override void DrawCore(DrawingContext drawingContext, DrawingAttributes drawingAttributes)
         
        {
            base.DrawCore(drawingContext, drawingAttributes);
            SolidColorBrush brush = new SolidColorBrush(Color.FromRgb(255,0,0));
            brush.Freeze();
            //FILL
            drawingContext.DrawRectangle(brush, null, new Rect(this.StylusPoints[0].ToPoint(),this.StylusPoints[2].ToPoint()));
            Point origin = new Point(this.StylusPoints[0].ToPoint().X + 2, this.StylusPoints[0].ToPoint().Y + 4);
            Typeface typeFace = new Typeface(new FontFamily("Segoe UI"), FontStyles.Normal, FontWeights.Normal, FontStretches.Normal);
            drawingContext.DrawText(new FormattedText("wtfIsGoingOn()", CultureInfo.CurrentUICulture, FlowDirection.LeftToRight, typeFace, 16, Brushes.Black),origin);
        }
    }
}
