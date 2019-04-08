using System;
using System.Collections.Generic;
using System.IO;
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
        public Boolean IsDraging { get; set; }
        public MemoryStream LastSelection { get; set; }

    }
}


