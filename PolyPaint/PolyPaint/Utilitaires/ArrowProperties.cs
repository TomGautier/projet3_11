using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;

namespace PolyPaint.Utilitaires
{
    class ArrowProperties:FormProperties
    {
        public string type { get; set; }      
        public string borderColor { get; set; }
        public int[] middlePointCoord { get; set; }
        public int height { get; set; }
        public int width { get; set; }

        public ArrowProperties(string type, string borderColor, Point[] pts, int thickness, string label, string q1, string q2)
        {
            
        }
    }
}
