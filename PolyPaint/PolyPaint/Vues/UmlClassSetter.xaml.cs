using PolyPaint.VueModeles;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace PolyPaint.Vues
{
    /// <summary>
    /// Interaction logic for TextSetter.xaml
    /// </summary>
    public partial class UmlClassSetter : Window
    {
        public new string Name { get; set; }
        public List<string> Methods { get; set; }
        public List<string> Attributes { get; set; }
        public UmlClassSetter(string name,string border, List<string> methods, List<string> attributes)
        {
            Name = name;
            Methods = methods;
            Attributes = attributes;      
            InitializeComponent();
            DataContext = new VueModele();
            txtName.Text = Name;
            borderList.Text = border;
            SetMethodList();
            SetAttributeList();
        }
        private void textSetter_NameChanged(object sender, TextChangedEventArgs args){
            Name = txtName.Text;             
        }
        private void textSetter_ApplyMethodAdding(object sender, RoutedEventArgs e)
        {
            this.Methods.Add(txtMethods.Text);
            SetMethodList();
            txtMethods.Text = "";
            //this.Close();
        }
        private void textSetter_RemoveSelectedMethod(object sender, RoutedEventArgs e)
        {
            if (this.Methods.Count > 0)
            {
                this.Methods.RemoveAt(methodList.Items.IndexOf(methodList.SelectedItem));
                SetMethodList();
            }

        }
        private void textSetter_ApplyAttributeAdding(object sender, RoutedEventArgs e)
        {
            this.Attributes.Add(txtAttributes.Text);
            SetAttributeList();
            txtAttributes.Text = "";
        }
        private void textSetter_RemoveSelectedAttribute(object sender,RoutedEventArgs e)
        {
            if (this.Attributes.Count > 0)
            {
                this.Attributes.RemoveAt(attributeList.Items.IndexOf(attributeList.SelectedItem));
                SetAttributeList();
            }
            
        }
        private void textSetter_ApplyChanges(object sender, RoutedEventArgs e)
        {
            this.Close();
        }
        private void SetMethodList()
        {
            methodList.Items.Clear();
            foreach (string method in this.Methods)
            {
                methodList.Items.Add(method);
            }
            methodList.SelectedIndex = 0;
        }
        private void SetAttributeList()
        {
            attributeList.Items.Clear();
            foreach (string attribute in this.Attributes)
            {
                attributeList.Items.Add(attribute);
            }
            attributeList.SelectedIndex = 0;
        }
    }
}
