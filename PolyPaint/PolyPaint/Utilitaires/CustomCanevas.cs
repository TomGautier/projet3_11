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
using System.Windows.Controls;

  namespace PolyPaint.Utilitaire
{
    class CustomCanevas : InkCanvas
    {
        public  StrokeCollection SelectedStroke {get;set;}
    }

}
