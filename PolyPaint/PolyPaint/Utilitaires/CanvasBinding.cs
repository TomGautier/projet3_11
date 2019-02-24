using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Interactivity;

/*namespace PolyPaint.Utilitaires
{
    public class CanvasBehavior : Behavior<InkCanvas>
    {
        public InkCanvas Canvas
        {
            get { return (InkCanvas)GetValue(CanvasProperty); }
            set { SetValue(CanvasProperty, value); }
        }

        public static readonly DependencyProperty CanvasProperty =
            DependencyProperty.Register("Canvas",
                typeof(InkCanvas),
                typeof(CanvasBehavior),
                new PropertyMetadata(null));



        protected override void OnAttached()
        {
            base.OnAttached();
            var btnDelete = this.AssociatedObject as Button;
            if (btnDelete != null)
            {
                btnDelete.Click += BtnDelete_Click;
            }
        }

        /*private void BtnDelete_Click(object sender, RoutedEventArgs e)
        {
            if (this.Canvas != null)
            {
                var stokeCollection = this.Canvas.InkPresenter.StrokeContainer.GetStrokes();
                foreach (var stroke in stokeCollection)
                {
                    stroke.Selected = true;
                }
                this.Canvas.InkPresenter.StrokeContainer.DeleteSelected();
            }
        }
    }
}*/
