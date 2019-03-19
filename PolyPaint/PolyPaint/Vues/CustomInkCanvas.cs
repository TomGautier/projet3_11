using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Controls;
using System.Windows.Ink;

namespace PolyPaint.Vues
{
    class CustomInkCanvas : InkCanvas
    {
        public CustomInkCanvas() : base() { }
        public Boolean AllowSelection { get; set; }

    }
}


