using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;


namespace PolyPaint.Utilitaires
{
    class Shape
    {
        public string id { get; set; }
        public string imageId { get; set; }
        public string author { get; set; }
        public ShapeProperties properties { get; set; }
        
        public Shape(string id, string imageId, string author, ShapeProperties properties)
        {
            this.id = id;
            this.imageId = imageId;
            this.author = author;
            this.properties = properties;
        }

    }
}
