/*
 Computer graphics Asignment  
______________________________

 by:
    Piyush kumar
    pxk152030
   
 */
package tetris;

import java.awt.Button;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Set;
import java.util.Timer;
import javax.swing.JFrame;



public class Tetris {
    int n= 20 ; // no of rows 
    float s = (float) 0.1 ; // speed
    int m = 1 ; // scoring factor .

    public Tetris(int a, float b , int c)
    {
        n = a ;
        s = b ;
        m = c ;
        
     }
   // public static void main(String[] args) {
   public void almostMain (){    
        // creating a new Jframe.
        JFrame jf = new JFrame("Tetris Game");
      
       jf.setSize(750,750);
       jf.setVisible(true);
       jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
       
       draw d = new draw(n,s,m);
     
        d.ini();  // to initialize the array. 
       jf.add(d);
        
       d.setBackground(Color.white);
       
       d.updateGraphics();
       d.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
               // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

     
        @Override
       public void mouseMoved (MouseEvent e)
       {
           d.mouseX= e.getX();
           d.mouseY= e.getY();
           d.updateGraphics();
           //System.out.println("mousemoved  !!");
       }
        });
       
       
       d.addMouseListener(new MouseAdapter() {
           
                 @Override
       public void mousePressed(MouseEvent e)
       {   d.mouseX= e.getX();
           d.mouseY= e.getY();
           d.updateGraphics();
           d.pressed= true;
           d.moveX=0;
           
           if (e.getButton() == MouseEvent.BUTTON3) 
           {   System.out.println("Right Button Pressed");
               d.moveX=1;
           }
           else 
           {   d.moveX = -1;
           }
          
       }
});
       
       d.addMouseWheelListener(new MouseWheelListener() {
           
              public void mouseWheelMoved(MouseWheelEvent e) 
              {
                    if (e.getWheelRotation() < 0)  // want to roatate clockwise.
                       {
                                System.out.println("Rotated Up... " + e.getWheelRotation());  
                                d.old_orient_index = d.orient_index;
                                d.orient_index = (d.orient_index-1) %4;
                                if( d.orient_index <0)
                                     d.orient_index = 3;
                                System.out.println("orientation value " + d.orientation[d.orient_index] + "and index "+d.orient_index ) ;  
                        }
                    else if(e.getWheelRotation() > 0)  //want to roatate anti-clockwise.
                        {
                                System.out.println("Rotated Down... " + e.getWheelRotation());
                                d.old_orient_index = d.orient_index;
                                d.orient_index = (d.orient_index+1) %4;
                                System.out.println("orientation value " + d.orientation[d.orient_index]);  
                         }

             }
       });
               
              
       
      
       
    } // main ends
    
}
