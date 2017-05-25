/*
for pause mode : 
when the mouse pointer is brought inside the  main menu it pauses the game .
and when you touch the falling shape the shape is converted into the shape seen in the 'next shape' window

when game ends :
screen is "Game Over" message is displayed .

 */
package tetris;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class draw extends Canvas {
    
     int rows = 20;
     int cols = 10 ;
     float s = (float) 0.1 ; // speed
     int m = 1 ; // scoring factor .
     int unit = 20;  
     
     int score = 0;
    int level =0;
    float falling_speed ;
    int orient_index =0 ;
    int old_orient_index = 0 ;
    String [] orientation = {"up", "right", "down", "left"};
    int[][] next = new int[4][2];    // next [3][0] will tell about the last square's i th position and next [3][1]  last square's jth position 
    int [][] present = new int[4][2];
    String need_to_freeze = "no";
    int mouseX,  mouseY;
    boolean pressed = false;
    boolean want_to_stay_in_pause = true ;
    boolean game_over = false;
    boolean just_out_of_main = false;
    boolean new_born = true;
    float maxX ,  maxY;
    float shapeX, shapeY;
   float endX, endY;
    float pixel_size;
    String [] shapes = { "line","box","right cart", "left cart", "right step", "left step" , "tank" };
    Random ran = new Random();
    int quit_x1 ,quit_x2  ,quit_y1 ,quit_y2;
    int main_x1,main_x2,main_y1,main_y2;
    int pause_x1,pause_x2,pause_y1,pause_y2;
    int next_x1,next_x2,next_y1,next_y2;
    int moveY=0 , moveX=0;
    int[][] occupied;
     int X = main_x1 + unit*3 ;
    int random_number = ran.nextInt(7);
    int next_random_number = ran.nextInt(7);
    
    public draw(int a, float b , int c)
    {
     rows = a;
     s =(11- b*10)*100;  //  speed ranges from 0.1 to 1.0  so higher the speed lower should be the time to thread.sleep();
     m = c;
     cols = 10;
      occupied = new int[rows+2][cols+2];  
      falling_speed = s ;
      /*System.out.println("n : "+ rows );
        System.out.println("s : "+ s);
        System.out.println("m : "+ m);
     */
    }
    
    public void ini()
    {
        for(int i =0 ; i<rows+2; i++)
            {   for(int j = 0 ; j<cols+2;j++)
                 {
                        if(i==0 || i==(rows+1) || j==0 || j== (cols+1) )
                        {  occupied[i][j] =1 ;  } 
            
                        else    
                        { occupied [i][j]=0;    }
                             
                }
           
            }
        
    }
    
    public void updateGraphics()
    { 
        repaint();
    }
    public void paint(Graphics g)
    { 
     Graphics2D g2 = (Graphics2D) g;
     Dimension d = getSize();
     maxX = d.width-1;
     maxY = d.height-1;
     pixel_size = (float) Math.max(717/maxX , 717/maxY);
    
     // main area
     main_x1=50;   main_x2=main_x1+ unit*cols ;        main_y1=50;    main_y2=main_y1+ unit*rows;     // 450  for rows = 20
     g2.setStroke(new BasicStroke(3));  //5
     g2.setColor(Color.black);
     g2.drawRect(iX(main_x1),   iX(main_y1),  iX( main_x2 - main_x1),   iX( main_y2 - main_y1));
     
     // next shape
     //g2.setStroke(new BasicStroke(10));
     g2.setColor(Color.black);
     g2.setStroke(new BasicStroke(3));
     next_x1= 300;  next_x2= 420 ;          next_y1=50;     next_y2=170;
     g2.drawRect(iX(next_x1),   iX(next_y1),    iX(next_x2-next_x1),    iX(next_y2-next_y1));
     
     //close 
      quit_x1=300; quit_x2 =375;        quit_y1= main_y2-50 ; quit_y2=  main_y2;                    //450;
     g2.setColor(Color.black);
     g2.drawRect(iX(quit_x1),   iX(quit_y1),   iX(quit_x2 - quit_x1) ,  iX(quit_y2 - quit_y1));
     g2.setFont(new Font("TimesRoman",Font.PLAIN,iX(20)));
     g2.drawString("QUIT", iX(315), iX( quit_y2-20));
     
     
     // other strings .
      g2.drawString("Levels :  "    +level        , iX(300), iX(290));
      g2.drawString("Rows :  "     +rows        , iX(300), iX(310));
      g2.drawString("Score :  "     + score        , iX(300), iX(330));
      g2.drawString("Next Shape ", iX(next_x1+10), iX(next_y2 + 2*unit));
      
  
     
      shapeX =  (main_x1 + main_x2)/2 ;
      shapeY =  main_y1 ;
      float X, Y ;
          
  
   if(iX(quit_x1)<mouseX && mouseX< iX(quit_x2)  && iX(quit_y1)<mouseY && mouseY < iX(quit_y2) && pressed)
   {
    System.exit(0);  // to terminate the program.
  
   }
   else if(iX(main_x1)<mouseX && mouseX< iX(main_x2)  && iX(main_y1)<mouseY && mouseY <iX( main_y2) && want_to_stay_in_pause)
   {
       // pause box
      
      // g2.setColor(Color.blue);
     g2.setColor(new Color(0,119,190));  //ocean boat blue color ;
     g2.setStroke(new BasicStroke(3));
     g2.setFont(new Font("TimesRoman",Font.PLAIN,iX(20)));
            paint_the_frozen_shapes( g2 );
            if( present[0][0] !=0 &&  present[0][1]!= 0)
            paint_temp_present_shape( g2 );
           // 
     int x1 =iX((main_x1 + main_x2)/3) ; int len =iX((main_x1 + main_x2)*2/3) - x1 ; int y1 = iX((main_y1+main_y2)/2 ); int height =  iX(40)  ;
     g2.drawRect(x1,  y1, len , height );
     g2.drawString("Pause",( x1  +iX(15)), (y1 +iX(25)));
     
        shapeDraw_next(g2, next_random_number);  
         //System.out.println(touching_shape());
     if(touching_shape())
                        {   System.out.println("touching ");
                          
                            score = score - level * m;
                                                   random_number = next_random_number;
                                                   orient_index = 0;
                                                   //next_random_number = ran.nextInt(7);
                                                  just_out_of_main = true;
                                           // start the whole procedure again .
                                                    moveY =0; new_born = true;
                                                    moveX = 0;
                            want_to_stay_in_pause = false ;                        
                        }
     
     
   }
   
   else 
      {   want_to_stay_in_pause = true ; 
          game_over_check();   // to check if squares are tiuching the upper boundary wall , then is game over.
 
          if (!game_over )      // if its not gave over then proceed else stop everything and write game over message and after 2 sec terminate the program.
                            { 
                        if(just_out_of_main)
                               {   next_random_number = ran.nextInt(7);
                                  just_out_of_main = false;
                               }

                           pressed = false;
                           clearMain( g2 );
                           paint_the_frozen_shapes( g2 );
                           shapeDraw_next(g2, next_random_number);  
                         // Correct_the_orient_index(2);
                            shapeDraw(g2,random_number) ;  // // (g2, random_number);
                            
                            
                         if(check_next() )  // check_next() returns false if collision is about to happen in next round . hence if its true we can carry on else freeit 
                         {moveY ++;
                         }

                         else 
                         {            // start the freezing procedure .
                                         freeze_it( g2 );
                                       // is_cutter_needed()
                                       is_cutter_needed();
                                  // call for new random number 
                                          random_number = next_random_number;
                                          orient_index = 0;
                                          next_random_number = ran.nextInt(7);

                                  // start the whole procedure again .
                                           moveY =0; new_born = true;
                                           moveX = 0;
                         // X = main_x1 + unit*4 ;     
                         }

                      }
      else   // game is over
      {
           g2.setColor(new Color(0,119,190));  //ocean boat blue color ;
     g2.setStroke(new BasicStroke(3));
     g2.setFont(new Font("TimesRoman",Font.PLAIN,iX(20)));
     int x1 =iX((main_x1 + main_x2)/4) ; int len =iX((main_x1 + main_x2)*3/4) - x1 ; int y1 = iX((main_y1+main_y2)/2 ); int height =  iX(40)  ;
     g2.drawRect(x1,  y1, len , height );
     g2.drawString("Game over",( x1  +iX(25)), (y1 +iX(25)));       // this will display the message " Game is over"
    game_over = true;
    
    try {    Thread.sleep(2000);          }    // wait for 2 secs
     catch (InterruptedException ex) {     Logger.getLogger(draw.class.getName()).log(Level.SEVERE, null, ex);       }
    //System.exit(0);                            // terminate the program.
                                     
      }
    }// outer else
    }  // paint ends

// L -> D 
    int iX(float x)    { return Math.round(x/pixel_size) ;     }
  //  int yX(float y)    { return Math.round(x) ;     }
    
    // D-> L
    float fX (int x)   { return (float)x*pixel_size ;               }
 //   float fY (int y)   { return (float)x ;               }
    
  public void  game_over_check()
  {  for(int j= 1 ; j <=cols; j++ )
            if(occupied[1][j]==1)
            { game_over=true;
              break;
            }
  
  }
  
  public boolean touching_shape()
  {   int r , c ;
    r =locate_i((int) mouseY);
    c = locate_i((int) mouseX);
    //System.out.println("r: "+r + "  c : "+ c +"and "+ present [0][0] + "  "+ present [0][1]);
      for( int i = 0 ; i<3 ; i++ )
      {  if(present [i][0] == r && present[i][1] ==c )
            return true ;
      }
  
      return false ;
  }
    
    public void clearMain(Graphics2D g2 )
    {
    // main_x1=50;   main_x2=250;        main_y1=50;    main_y2=450;
     g2.setStroke(new BasicStroke(3));  //5
      g2.setColor(Color.white);
     g2.fillRect(iX(main_x1),   iX(main_y1),  iX( main_x2 - main_x1),   iX( main_y2 - main_y1));
     g2.setColor(Color.black);
     g2.drawRect(iX(main_x1),   iX(main_y1),  iX( main_x2 - main_x1),   iX( main_y2 - main_y1));
     
    }

 public void freeze_it(Graphics2D g2 )
 {
     // turn the occupied array elemnts from 0 to 1 depending upon which bocks are occupied.
     for(int i =0 ; i< 4 ; i++)
     {  occupied[present[i][0] ][ present[i][1]]  =1;    }

     //time to draw the frozen shapes.
   //  paint_the_frozen_shapes( g2 );
     
 }
 public void paint_temp_present_shape(Graphics2D g2 )
 {   for(int i = 0 ; i<4 ; i++)
        {          
                    paint_a_square ( g2,  present[i][0], present[i][1] );
        }
       
 }
 
 public void paint_the_frozen_shapes(Graphics2D g2 )
 {   //System.out.println("inside the frozen shapes");
   for(int i =1 ; i<=rows; i++)
            {   for(int j = 1 ; j<=cols;j++)
                 {
                        if( occupied[i][j] ==1 )
                        {     // System.out.println("row: "+ i +"  col :"+j);
                            paint_a_square ( g2,i,j ); }
                   }
            }
 }
public void  paint_a_square (Graphics2D g2 , int i , int j )
{ 
                                           int  X = main_x1 + unit*(j-1) ;
                                           int Y =  main_y1 + unit*(i-1) ;
                                            g2.setColor(Color.green);
                                            g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                            g2.setColor(Color.black);
                                            g2.setStroke(new BasicStroke(1));
                                            g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );
    
}  
   
public boolean check_next()
{  int row;
   int col ;
   for(int i =0 ; i < 4 ; i++)
   {   row = next[i][0];
       col =   next[i][1]; 
       
         // System.out.println("next row "+ row+" and next col " +col );
         // System.out.println("next occupied[row][col] "+ occupied[row] [col] );
       if(occupied[row] [col]==1)
           return false;
       
   }
   
   return true ;
} 

public void is_cutter_needed()
{  int count ;
   for(int i =rows ; i>=1; i--)
   {   count = 0 ;  
       for(int j =1 ; j<=cols ; j++)
                if(occupied [i] [j]==1)
                        count ++;   
   
    if (count == cols)
        {   cutter(i) ;
          System.out.println("Cutter was called !!");
        }
   }
   
}

  public void cutter( int r ) 
  {     level++;
        falling_speed = falling_speed* (1+ level*m);
        score = score + level*m;
       // cut the line 
          // shift the down the upper blocks to downwards 
      if(r!=1)
      {
           for(int i =r ; i>1; i--)
            for(int j =1 ; j<=cols ; j++)
                      occupied [i][j]= occupied [i-1][j] ;

            for(int j =1 ; j<=cols ; j++)
                occupied [1][j]=0;

           is_cutter_needed();  // to checj if line cutting is needed at the above line also
      }
      
      else       
            for(int j =1 ; j<=cols ; j++)
                      occupied [r][j]= 0 ;
      
}  //cutter 
public void  drawingRectangle(Graphics2D g2 ,float X , float Y , float width , float height, int stroke_width , Color c)     // need to modify this ////////////////////////////////////
{
                                  /*          g2.setColor(Color.green);
                                            g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                            g2.setColor(Color.black);
                                            g2.setStroke(new BasicStroke(1));
                                            g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );
    */
}

   public int locate_j(int X){return ( ((X - main_x1)/ unit)+1 ); }
   public int locate_i(int Y){return (( (Y - main_y1)/ unit)+1 ); }
 
   public void Correct_the_orient_index( int number)
   {   if(shapes[number].equals("line"))
           {              
              
                            if( orientation[orient_index].equals("up") || orientation[orient_index].equals("down")  )
                                {   System.out.println("inside up"); 
                                    if(  (main_x1-1)<  (X) && (X+4*unit)< (main_x2+1)) 
                                        { System.out.println("inside the if");
                                        }
                                     else
                                        {orient_index = old_orient_index ;
                                        System.out.println("inside the else");
                                        }
                                
                                }
                            else if( orientation[orient_index].equals("right") ||  orientation[orient_index].equals("left") )
                            {  }
                          
                 
                }
   
      
          if(shapes[number].equals("right cart"))
           {              
              
                            if( orientation[orient_index].equals("up")  )
                            {  if(  (main_x1-1)<  (X-2*unit) && (X+unit)< (main_x2+1)) 
                                        {  // its ok 
                                        }
                                     else
                                    orient_index = old_orient_index ;
                            }
                            else if( orientation[orient_index].equals("right")  )
                            { if(  (main_x1-1)<  (X) && (X+2*unit)< (main_x2+1)) 
                                        {  // its ok 
                                        }
                                     else
                                    orient_index = old_orient_index ;
                            }
                            else if( orientation[orient_index].equals("down")  )
                            {  if(  (main_x1-1)<  (X) && (X+3*unit)< (main_x2+1)) 
                                        {  // its ok 
                                        }
                                     else
                                    orient_index = old_orient_index ;
                            }
                            else if(orientation[orient_index].equals("left") )
                            {  
                               if(  (main_x1-1)<  (X) && (X+2*unit)< (main_x2+1)) 
                                        {  // its ok 
                                        }
                                     else
                                    orient_index = old_orient_index ;
                            }
                 
                }
   
          
            if(shapes[number].equals("left cart"))
           {              
              
                            if( orientation[orient_index].equals("up")  )
                            {  }
                            else if( orientation[orient_index].equals("right")  )
                            {  }
                            else if( orientation[orient_index].equals("down")  )
                            {  }
                            else if(orientation[orient_index].equals("left") )
                            {   }
                 
                }
            
              if(shapes[number].equals("right step"))
           {              
              
                            if( orientation[orient_index].equals("up")  )
                            {  }
                            else if( orientation[orient_index].equals("right")  )
                            {  }
                            else if( orientation[orient_index].equals("down")  )
                            {  }
                            else if(orientation[orient_index].equals("left") )
                            {   }
                 
                }
   
              
                if(shapes[number].equals("left step"))
           {              
              
                            if( orientation[orient_index].equals("up")  )
                            {  }
                            else if( orientation[orient_index].equals("right")  )
                            {  }
                            else if( orientation[orient_index].equals("down")  )
                            {  }
                            else if(orientation[orient_index].equals("left") )
                            {   }
                 
                }
                
                  if(shapes[number].equals("tank"))
           {              
              
                                if( orientation[orient_index].equals("up")  )
                                {  }
                                else if( orientation[orient_index].equals("right")  )
                                {  }
                                else if( orientation[orient_index].equals("down")  )
                                {  }
                                else if(orientation[orient_index].equals("left") )
                                {   }
                 
                }
   
   }
   
public void shapeDraw(Graphics2D g2,  int number )
    {      int Y ;
           int newX = X+ moveX*unit;
           Y = main_y1 + unit*(0 + moveY) ; 
            
          
     
           if(shapes[number].equals("line"))
           { 
                if(orientation[orient_index].equals("left") || orientation[orient_index].equals("right")  )
                                   {  
                                       if (new_born)
                                      { X = main_x1 + unit*3 ;              // the very first unit of x starts from here depending on the shape .
                                        new_born = false;   }
                                   
                                        if(  (main_x1-1)<  newX && newX< (main_x2))
                                             {X = newX; 
                                             moveX =0; 
                                             }
                                            for(int i =0 ; i<4 ; i++)
                                            {    g2.setColor(Color.green);
                                                 g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                 g2.setColor(Color.black);
                                                 g2.setStroke(new BasicStroke(1));
                                                 g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                                       present[i][0] = locate_i((int)Y );
                                                       present[i][1] = locate_j((int)X);
                                                       next[i][0] = locate_i((int)Y)+1;
                                                       next[i][1] = locate_j((int)X);

                                                 Y = Y + unit ;
                                            }
                                            X = X ;
                      
                                     
                                   }                    
               
               if(orientation[orient_index].equals("up") || orientation[orient_index].equals("down")  )
                                   {    if (new_born)
                                      { X = main_x1 + unit*3 ;              // the very first unit of x starts from here depending on the shape .
                                        new_born = false;   }
                                   
                                         else if ( (main_x1-1)<  newX && newX< (main_x2+1)  &&  (main_x1-1)<  (newX+4*unit) && (newX+4*unit)< (main_x2+1)  )     // so that shape doesnt do beyond the right and left boundaries .
                                       {X = newX; 
                                       moveX =0;  }
                                       
                                       
                                       
                                            for(int i =0 ; i<4 ; i++)
                                            {    g2.setColor(Color.green);
                                                 g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                 g2.setColor(Color.black);
                                                 g2.setStroke(new BasicStroke(1));
                                                 g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                                       present[i][0] = locate_i((int)Y );
                                                       present[i][1] = locate_j((int)X);
                                                       next[i][0] = locate_i((int)Y)+1;
                                                       next[i][1] = locate_j((int)X);

                                                 X = X + unit ;
                                            }
                                            X = X - 4*unit;
                                     
                                   }
                                   
                                    

        } // if (line) ends
           
     
      else if(shapes[number].equals("box"))
      {
                                 if (new_born)
                                      { X = main_x1 + unit*4 ;              // the very first unit of x starts from here depending on the shape .
                                        new_born = false;   }
      
                                     else if ( (main_x1-1)<  newX && newX< (main_x2+1)  &&  (main_x1-1)<  (newX+2*unit) && (newX+2*unit)< (main_x2+1)  )     // so that shape doesnt do beyond the right and left boundaries .
                                       {X = newX; 
                                       moveX =0;  }
          
                                  for(int i =0 ; i<2 ; i++)
                                  {
                                       g2.setColor(Color.green);
                                       g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                       g2.setColor(Color.black);
                                       g2.setStroke(new BasicStroke(1));
                                       g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );
 
                                            present[i][0] = locate_i((int)Y );
                                            present[i][1] = locate_j((int)X);
                                            next[i][0] = locate_i((int)Y)+1;
                                            next[i][1] = locate_j((int)X);  
                                            
                                        X = X + unit ;
                                  }

                                       X = X - 2*unit;  //unit * j ;
                                      Y = Y+ unit ;

                                   for(int i =0 ; i<2 ; i++)
                                  {
                                       g2.setColor(Color.green);
                                       g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                       g2.setColor(Color.black);
                                       g2.setStroke(new BasicStroke(1));
                                       g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                       
                                            present[i+2][0] = locate_i((int)Y );
                                            present[i+2][1] = locate_j((int)X);
                                            next[i+2][0] = locate_i((int)Y)+1;
                                            next[i+2][1] = locate_j((int)X);
                                            
                                        X = X + unit ;

                                  }
                                  
                                   X = X - 2*unit; 
      
      }
    
      else if(shapes[number].equals("right cart"))
      {                          
                          if(orientation[orient_index].equals("up"))
                                                       {   
                                                            if (new_born)
                                                                { X = main_x1 + unit*5 ;            
                                                                  new_born = false; 
 
                                                                }   //  (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+3*unit  && newX+3*unit < (main_x2 +1) // left cart
                                                                   //  (main_x1-1)<  (newX-unit)  && (newX-unit)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 +1) // tank 
                                                                  
                                                           else if ( (main_x1-1)<  (newX-2*unit)  && (newX-2*unit)  < (main_x2+1)  &&  (main_x1-1)<  newX  && newX< (main_x2)  )     // so that shape doesnt do beyond the right and left boundaries .
                                                                        {X = newX; 
                                                                            moveX =0;  }
                                                                        // one loop 
                                                                                         for(int i =0 ; i<1 ; i++)
                                                                                                 {
                                                                                                      g2.setColor(Color.green);
                                                                                                      g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                                                                      g2.setColor(Color.black);
                                                                                                      g2.setStroke(new BasicStroke(1));
                                                                                                      g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );


                                                                                                          present[i][0] = locate_i((int)Y );
                                                                                                          present[i][1] = locate_j((int)X);
                                                                                                          next[i][0] = locate_i((int)Y)+1;
                                                                                                          next[i][1] = locate_j((int)X);

                                                                                                 }
                                                                         // row ++
                                                                         Y= Y + unit ;
                                                                         X= X- 2*unit ;    //  for left cart  X = X ;
                                                                         // the three loop in next row
                                                                                      for(int i =0 ; i<3 ; i++)
                                                                                                    {
                                                                                                         g2.setColor(Color.green);
                                                                                                         g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                                                                         g2.setColor(Color.black);
                                                                                                         g2.setStroke(new BasicStroke(1));
                                                                                                         g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                                                                                                present[i+1][0] = locate_i((int)Y );
                                                                                                                present[i+1][1] = locate_j((int)X);
                                                                                                                next[i+1][0] = locate_i((int)Y)+1;
                                                                                                                next[i+1][1] = locate_j((int)X);
                                                                                                                
                                                                                                           X = X + unit   ;   
                                                                                                       }
                                                                                      X = X - unit ;   // for left cart X = X- 3*units;
                                                                                      
                                                                     }
                                                                     else  if(orientation[orient_index].equals("right"))
                                                                     {  if (new_born)
                                                                            { X = main_x1 + unit*5 ;            
                                                                             new_born = false; 

                                                                           }   //  (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // left cart
                                                                              // (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // tank 

                                                                      else if ( (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 )  )     // so that shape doesnt do beyond the right and left boundaries .
                                                                                   {X = newX; 
                                                                                       moveX =0;  }
                                                                         // three loop with incrementtion in rows
                                                                           for(int i =0 ; i<3 ; i++)
                                                                                    {
                                                                                         g2.setColor(Color.green);
                                                                                         g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                                                         g2.setColor(Color.black);
                                                                                         g2.setStroke(new BasicStroke(1));
                                                                                         g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                                                                                 present[i][0] = locate_i((int)Y );
                                                                                                present[i][1] = locate_j((int)X);
                                                                                                next[i][0] = locate_i((int)Y)+1;
                                                                                                next[i][1] = locate_j((int)X);
                                                                                         
                                                                                        Y = Y + unit ;
                                                                                                
                                                                                       }
                                        
                                                                        // col++
                                                                        X = X + unit ;
                                                                        Y  = Y - unit ;  // left cart    Y = Y - 3*unit  
                                                                         // one loop
                                                                          for(int i =0 ; i<1 ; i++)
                                                                                                 {
                                                                                                      g2.setColor(Color.green);
                                                                                                      g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                                                                      g2.setColor(Color.black);
                                                                                                      g2.setStroke(new BasicStroke(1));
                                                                                                      g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );


                                                                                                          present[i+3][0] = locate_i((int)Y );
                                                                                                          present[i+3][1] = locate_j((int)X);
                                                                                                          next[i+3][0] = locate_i((int)Y)+1;
                                                                                                          next[i+3][1] = locate_j((int)X);

                                                                                                 }
                                                                          
                                                                          X = X - unit ;
                                                                     }
                                                                     
                                                                   else    if(orientation[orient_index].equals("down"))
                                                                     {     if (new_born)
                                                                            { X = main_x1 + unit*5 ;            
                                                                             new_born = false; 

                                                                           }   //  (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+3*unit  && newX+3*unit < (main_x2 +1) // left cart
                                                                              //  (main_x1-1)<  (newX-unit)  && (newX-unit)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 +1) // tank 

                                                                      else if ( (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+3*unit  && newX+3*unit < (main_x2 +1)  )     // so that shape doesnt do beyond the right and left boundaries .
                                                                                   {X = newX; 
                                                                                       moveX =0;  }
                                                                         

                                                                        // three loop with incrementtion in  cols
                                                                         for(int i =0 ; i<3 ; i++)
                                                                                    {
                                                                                         g2.setColor(Color.green);
                                                                                         g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                                                         g2.setColor(Color.black);
                                                                                         g2.setStroke(new BasicStroke(1));
                                                                                         g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                                                                                 present[i][0] = locate_i((int)Y );
                                                                                                present[i][1] = locate_j((int)X);
                                                                                                next[i][0] = locate_i((int)Y)+1;
                                                                                                next[i][1] = locate_j((int)X);
                                                                                          X = X + unit;
                                                                                                
                                                                                       }
                                                                         
                                                                        // col = cols-3
                                                                        X = X - 3*unit ; // for left cart  X = X - unit;
                                                                         // row++;
                                                                         Y = Y + unit;
                                                                         // one loop 
                                                                          for(int i =0 ; i<1 ; i++)
                                                                                                 {
                                                                                                      g2.setColor(Color.green);
                                                                                                      g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                                                                      g2.setColor(Color.black);
                                                                                                      g2.setStroke(new BasicStroke(1));
                                                                                                      g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );


                                                                                                          present[i+3][0] = locate_i((int)Y );
                                                                                                          present[i+3][1] = locate_j((int)X);
                                                                                                          next[i+3][0] = locate_i((int)Y)+1;
                                                                                                          next[i+3][1] = locate_j((int)X);

                                                                                                 }
                                                                       X = X ; // for left cart   X =  X - 2*unit ;
                                                                     }
                                                                   else   if(orientation[orient_index].equals("left"))
                                                                     {   if (new_born)
                                                                            { X = main_x1 + unit*5 ;            
                                                                             new_born = false; 

                                                                           }   //  (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // left cart
                                                                              // (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // tank 

                                                                      else if ( (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 )  )     // so that shape doesnt do beyond the right and left boundaries .
                                                                                   {X = newX; 
                                                                                       moveX =0;  }

                                                                        // one loop 
                                                                          for(int i =0 ; i<1 ; i++)
                                                                                                 {
                                                                                                      g2.setColor(Color.green);
                                                                                                      g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                                                                      g2.setColor(Color.black);
                                                                                                      g2.setStroke(new BasicStroke(1));
                                                                                                      g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );


                                                                                                          present[i][0] = locate_i((int)Y );
                                                                                                          present[i][1] = locate_j((int)X);
                                                                                                          next[i][0] = locate_i((int)Y)+1;
                                                                                                          next[i][1] = locate_j((int)X);

                                                                                                 }
                                                                     
                                                                         // col ++
                                                                         X = X + unit ; 
                                                                         Y = Y ; // for left cart   Y = Y-3*unit
                                                                         // three loop with incrementtion in rows }  
                                                                         for(int i =0 ; i<3 ; i++)
                                                                                    {
                                                                                         g2.setColor(Color.green);
                                                                                         g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                                                         g2.setColor(Color.black);
                                                                                         g2.setStroke(new BasicStroke(1));
                                                                                         g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                                                                                 present[i+1][0] = locate_i((int)Y );
                                                                                                present[i+1][1] = locate_j((int)X);
                                                                                                next[i+1][0] = locate_i((int)Y)+1;
                                                                                                next[i+1][1] = locate_j((int)X);
                                                                                                
                                                                                                Y = Y + unit;
                                                                                                
                                                                                       }
                                                                             X = X - unit ;
                                                                     }
 

      }
      
      else if(shapes[number].equals("left cart"))
      {                                  
                                              
                                                                    if(orientation[orient_index].equals("up"))
                                                                     { 
                                                                         if (new_born)
                                                                { X = main_x1 + unit*5 ;            
                                                                  new_born = false; 
 
                                                                }   //  (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+3*unit  && newX+3*unit < (main_x2 +1) // left cart
                                                                   //  (main_x1-1)<  (newX-unit)  && (newX-unit)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 +1) // tank 
                                                                  
                                                           else if ( (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+3*unit  && newX+3*unit < (main_x2 +1) )     // so that shape doesnt do beyond the right and left boundaries .
                                                                        {X = newX; 
                                                                            moveX =0;  }
                                                                        // one loop 
                                                                                         for(int i =0 ; i<1 ; i++)
                                                                                                 {
                                                                                                      g2.setColor(Color.green);
                                                                                                      g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                                                                      g2.setColor(Color.black);
                                                                                                      g2.setStroke(new BasicStroke(1));
                                                                                                      g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );


                                                                                                          present[i][0] = locate_i((int)Y );
                                                                                                          present[i][1] = locate_j((int)X);
                                                                                                          next[i][0] = locate_i((int)Y)+1;
                                                                                                          next[i][1] = locate_j((int)X);

                                                                                                 }
                                                                         // row ++
                                                                         Y= Y + unit ;
                                                                         X= X;    //  for left cart  X = X ;
                                                                         // the three loop in next row
                                                                                      for(int i =0 ; i<3 ; i++)
                                                                                                    {
                                                                                                         g2.setColor(Color.green);
                                                                                                         g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                                                                         g2.setColor(Color.black);
                                                                                                         g2.setStroke(new BasicStroke(1));
                                                                                                         g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                                                                                                present[i+1][0] = locate_i((int)Y );
                                                                                                                present[i+1][1] = locate_j((int)X);
                                                                                                                next[i+1][0] = locate_i((int)Y)+1;
                                                                                                                next[i+1][1] = locate_j((int)X);
                                                                                                                
                                                                                                           X = X + unit   ;   
                                                                                                       }
                                                                                      X = X - 3*unit ;   // for left cart X = X- 3*units;
                                                                                      
                                                                     }
                                                                     else  if(orientation[orient_index].equals("right"))
                                                                     {  if (new_born)
                                                                            { X = main_x1 + unit*5 ;            
                                                                             new_born = false; 

                                                                           }   //  (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // left cart
                                                                              // (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // tank 

                                                                      else if ( (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 )  )     // so that shape doesnt do beyond the right and left boundaries .
                                                                                   {X = newX; 
                                                                                       moveX =0;  }
                                                                         // three loop with incrementtion in rows
                                                                           for(int i =0 ; i<3 ; i++)
                                                                                    {
                                                                                         g2.setColor(Color.green);
                                                                                         g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                                                         g2.setColor(Color.black);
                                                                                         g2.setStroke(new BasicStroke(1));
                                                                                         g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                                                                                 present[i][0] = locate_i((int)Y );
                                                                                                present[i][1] = locate_j((int)X);
                                                                                                next[i][0] = locate_i((int)Y)+1;
                                                                                                next[i][1] = locate_j((int)X);
                                                                                         
                                                                                        Y = Y + unit ;
                                                                                                
                                                                                       }
                                        
                                                                        // col++
                                                                        X = X + unit ;
                                                                        Y  = Y - 3*unit ;  // left cart    Y = Y - 3*unit  
                                                                         // one loop
                                                                          for(int i =0 ; i<1 ; i++)
                                                                                                 {
                                                                                                      g2.setColor(Color.green);
                                                                                                      g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                                                                      g2.setColor(Color.black);
                                                                                                      g2.setStroke(new BasicStroke(1));
                                                                                                      g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );


                                                                                                          present[i+3][0] = locate_i((int)Y );
                                                                                                          present[i+3][1] = locate_j((int)X);
                                                                                                          next[i+3][0] = locate_i((int)Y)+1;
                                                                                                          next[i+3][1] = locate_j((int)X);

                                                                                                 }
                                                                          
                                                                          X = X - unit ;
                                                                     }
                                                                     
                                                                   else    if(orientation[orient_index].equals("down"))
                                                                     {    if (new_born)
                                                                            { X = main_x1 + unit*5 ;            
                                                                             new_born = false; 

                                                                           }   //  (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+3*unit  && newX+3*unit < (main_x2 +1) // left cart
                                                                              //  (main_x1-1)<  (newX-unit)  && (newX-unit)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 +1) // tank 

                                                                      else if ( (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+3*unit  && newX+3*unit < (main_x2 +1)  )     // so that shape doesnt do beyond the right and left boundaries .
                                                                                   {X = newX; 
                                                                                       moveX =0;  }
                                                                         // three loop with incrementtion in  cols
                                                                         for(int i =0 ; i<3 ; i++)
                                                                                    {
                                                                                         g2.setColor(Color.green);
                                                                                         g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                                                         g2.setColor(Color.black);
                                                                                         g2.setStroke(new BasicStroke(1));
                                                                                         g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                                                                                 present[i][0] = locate_i((int)Y );
                                                                                                present[i][1] = locate_j((int)X);
                                                                                                next[i][0] = locate_i((int)Y)+1;
                                                                                                next[i][1] = locate_j((int)X);
                                                                                          X = X + unit;
                                                                                                
                                                                                       }
                                                                         
                                                                        // col = cols-3
                                                                        X = X - unit ; // for left cart  X = X - unit;
                                                                         // row++;
                                                                         Y = Y + unit;
                                                                         // one loop 
                                                                          for(int i =0 ; i<1 ; i++)
                                                                                                 {
                                                                                                      g2.setColor(Color.green);
                                                                                                      g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                                                                      g2.setColor(Color.black);
                                                                                                      g2.setStroke(new BasicStroke(1));
                                                                                                      g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );


                                                                                                          present[i+3][0] = locate_i((int)Y );
                                                                                                          present[i+3][1] = locate_j((int)X);
                                                                                                          next[i+3][0] = locate_i((int)Y)+1;
                                                                                                          next[i+3][1] = locate_j((int)X);

                                                                                                 }
                                                                       X = X - 2*unit ; // for left cart   X =  X - 2*unit ;
                                                                     }
                                                                   else   if(orientation[orient_index].equals("left"))
                                                                     {  // one loop 
                                                                         if (new_born)
                                                                            { X = main_x1 + unit*5 ;            
                                                                             new_born = false; 

                                                                           }   //  (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // left cart
                                                                              // (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // tank 

                                                                      else if ( (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 )  )     // so that shape doesnt do beyond the right and left boundaries .
                                                                                   {X = newX; 
                                                                                       moveX =0;  }
                                                                          for(int i =0 ; i<1 ; i++)
                                                                                                 {
                                                                                                      g2.setColor(Color.green);
                                                                                                      g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                                                                      g2.setColor(Color.black);
                                                                                                      g2.setStroke(new BasicStroke(1));
                                                                                                      g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );


                                                                                                          present[i][0] = locate_i((int)Y );
                                                                                                          present[i][1] = locate_j((int)X);
                                                                                                          next[i][0] = locate_i((int)Y)+1;
                                                                                                          next[i][1] = locate_j((int)X);

                                                                                                 }
                                                                     
                                                                         // col ++
                                                                         X = X + unit ; 
                                                                         Y = Y-2*unit ; // for left cart   Y = Y-2*unit
                                                                         // three loop with incrementtion in rows }  
                                                                         for(int i =0 ; i<3 ; i++)
                                                                                    {
                                                                                         g2.setColor(Color.green);
                                                                                         g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                                                         g2.setColor(Color.black);
                                                                                         g2.setStroke(new BasicStroke(1));
                                                                                         g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                                                                                 present[i+1][0] = locate_i((int)Y );
                                                                                                present[i+1][1] = locate_j((int)X);
                                                                                                next[i+1][0] = locate_i((int)Y)+1;
                                                                                                next[i+1][1] = locate_j((int)X);
                                                                                                
                                                                                                Y = Y + unit;
                                                                                                
                                                                                       }
                                                                             X = X - unit ;
                                                                     }
 
                                  
      }
            else if(shapes[number].equals("right step"))       ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      {                          
                                   if(orientation[orient_index].equals("up"))
                                                                     { if (new_born)
                                                                            { X = main_x1 + unit*5 ;            
                                                                             new_born = false; 

                                                                           }   //  (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // left cart
                                                                              // (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // tank 

                                                                      else if ( (main_x1-1)<  (newX-unit)  && (newX-unit)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 )  )     // so that shape doesnt do beyond the right and left boundaries .
                                                                                   {X = newX; 
                                                                                       moveX =0;  }
                                                                     }
                                    if(orientation[orient_index].equals("down"))
                                                                     { if (new_born)
                                                                            { X = main_x1 + unit*5 ;            
                                                                             new_born = false; 

                                                                           }   //  (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // left cart
                                                                              // (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // tank 

                                                                      else if ( (main_x1-1)<  (newX-unit)  && (newX-unit)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 )  )     // so that shape doesnt do beyond the right and left boundaries .
                                                                                   {X = newX; 
                                                                                       moveX =0;  }
                                                                     }
                                    
                                     if(orientation[orient_index].equals("right"))
                                                                     { if (new_born)
                                                                            { X = main_x1 + unit*5 ;            
                                                                             new_born = false; 

                                                                           }   //  (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // left cart
                                                                              // (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // tank 

                                                                      else if ( (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 )  )      // so that shape doesnt do beyond the right and left boundaries .
                                                                                   {X = newX; 
                                                                                       moveX =0;  }
                                                                     }
                                      if(orientation[orient_index].equals("left"))
                                                                     { if (new_born)
                                                                            { X = main_x1 + unit*5 ;            
                                                                             new_born = false; 

                                                                           }   //  (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // left cart
                                                                              // (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // tank 

                                                                      else if ( (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 )  )     // so that shape doesnt do beyond the right and left boundaries .
                                                                                   {X = newX; 
                                                                                       moveX =0;  }
                                                                     }
                                   
                                                                for(int i =0 ; i<2 ; i++)
                                                                {
                                                                     g2.setColor(Color.green);
                                                                     g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                                     g2.setColor(Color.black);
                                                                     g2.setStroke(new BasicStroke(1));
                                                                     g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                                                     present[i][0] = locate_i((int)Y );
                                                                     present[i][1] = locate_j((int)X);

                                                                     next[i][0] = locate_i((int)Y)+1;
                                                                     next[i][1] = locate_j((int)X);
                                                                      
                                                                     if(orientation[orient_index].equals("up"))
                                                                     {X = X + unit ;  }
                                                                      if(orientation[orient_index].equals("right"))
                                                                     { Y = Y + unit ;  }
                                                                       if(orientation[orient_index].equals("down"))
                                                                     {X = X + unit ;  }
                                                                      if(orientation[orient_index].equals("left"))
                                                                     { Y = Y + unit ;  }
                                                                     
                                                                }

                                                                 if(orientation[orient_index].equals("up"))
                                                                     {X = X- 3*unit ; 
                                                                      Y = main_y1 + unit*(0 + moveY) + unit ;  }
                                                                  if(orientation[orient_index].equals("right"))
                                                                     {X = X + unit ; 
                                                                     Y = main_y1 + unit*(0 + moveY) - unit ; }
                                                                   if(orientation[orient_index].equals("down"))
                                                                     {X = X- 3*unit ; 
                                                                      Y = main_y1 + unit*(0 + moveY) + unit ;  }
                                                                    if(orientation[orient_index].equals("left"))
                                                                     {X = X + unit ; 
                                                                     Y = main_y1 + unit*(0 + moveY) - unit ; }
                                                                    
                                                                      

                                                                 for(int i =0 ; i<2 ; i++)
                                                                {
                                                                     g2.setColor(Color.green);
                                                                     g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                                     g2.setColor(Color.black);
                                                                     g2.setStroke(new BasicStroke(1));
                                                                     g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );


                                                                     present[i+2][0] = locate_i((int)Y);
                                                                     present[i+2][1] = locate_j((int)X);

                                                                     next[i+2][0] = locate_i((int)Y)+1;
                                                                     next[i+2][1] = locate_j((int)X);
                                                                      
                                                                     if(orientation[orient_index].equals("up"))
                                                                     {X = X + unit ;  }
                                                                      if(orientation[orient_index].equals("right"))
                                                                     { Y = Y + unit ;  }
                                                                       if(orientation[orient_index].equals("down"))
                                                                     {X = X + unit ;  }
                                                                      if(orientation[orient_index].equals("left"))
                                                                     { Y = Y + unit ;  }

                                                                }
                                                                 
                                                                 if(orientation[orient_index].equals("up"))
                                                                     {X = X - unit ;  }
                                                                      if(orientation[orient_index].equals("right"))
                                                                     { Y = Y - unit ;
                                                                      X = X - unit ;}
                                                                       if(orientation[orient_index].equals("down"))
                                                                     {X = X - unit ;  }
                                                                      if(orientation[orient_index].equals("left"))
                                                                     { Y = Y - unit ; 
                                                                       X = X - unit ;
                                                                     }
                                                                
                                                            //     endX = X;
                                                            //     endY = Y+ unit;
                                        
                                    
      }
           /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
           
            else if(shapes[number].equals("left step"))
      {
                 if(orientation[orient_index].equals("up"))
                                                                     { if (new_born)
                                                                            { X = main_x1 + unit*5 ;            
                                                                             new_born = false; 

                                                                           }   //  (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // left cart
                                                                              // (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // tank 

                                                                      else if ( (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+2*unit  && newX+2*unit < (main_x2 )  )         // so that shape doesnt do beyond the right and left boundaries .
                                                                                   {X = newX; 
                                                                                       moveX =0;  }
                                                                     }
                                    if(orientation[orient_index].equals("down"))
                                                                     { if (new_born)
                                                                            { X = main_x1 + unit*5 ;            
                                                                             new_born = false; 

                                                                           }   //  (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // left cart
                                                                              // (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // tank 

                                                                      else if ( (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+2*unit  && newX+2*unit < (main_x2 )  )     // so that shape doesnt do beyond the right and left boundaries .
                                                                                   {X = newX; 
                                                                                       moveX =0;  }
                                                                     }
                                    
                                     if(orientation[orient_index].equals("right"))
                                                                     { if (new_born)
                                                                            { X = main_x1 + unit*5 ;            
                                                                             new_born = false; 

                                                                           }   //  (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // left cart
                                                                              // (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // tank 

                                                                      else if ( (main_x1-1)<  (newX-unit)  && (newX-unit)  < (main_x2+1)  &&  (main_x1-1)<  newX  && newX < (main_x2 )  )       // so that shape doesnt do beyond the right and left boundaries .
                                                                                   {X = newX; 
                                                                                       moveX =0;  }
                                                                     }
                                      if(orientation[orient_index].equals("left"))
                                                                     { if (new_born)
                                                                            { X = main_x1 + unit*5 ;            
                                                                             new_born = false; 

                                                                           }   //  (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // left cart
                                                                              // (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // tank 

                                                                      else if ( (main_x1-1)<  (newX-unit)  && (newX-unit)  < (main_x2+1)  &&  (main_x1-1)<  newX  && newX < (main_x2 )  )     // so that shape doesnt do beyond the right and left boundaries .
                                                                                   {X = newX; 
                                                                                       moveX =0;  }
                                                                     }
          
                                     
                                       for(int i =0 ; i<2 ; i++)
                                       {
                                            g2.setColor(Color.green);
                                            g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                            g2.setColor(Color.black);
                                            g2.setStroke(new BasicStroke(1));
                                            g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                                 present[i][0] = locate_i((int)Y );
                                                 present[i][1] = locate_j((int)X);
                                                 next[i][0] = locate_i((int)Y)+1;
                                                 next[i][1] = locate_j((int)X);
                                                 
                                                                     if(orientation[orient_index].equals("up"))
                                                                     {X = X + unit ;  }
                                                                      if(orientation[orient_index].equals("right"))
                                                                     { Y = Y + unit ;  }
                                                                       if(orientation[orient_index].equals("down"))
                                                                     {X = X + unit ;  }
                                                                      if(orientation[orient_index].equals("left"))
                                                                     { Y = Y + unit ;  }
                                       }

                                                                     if(orientation[orient_index].equals("up"))
                                                                     {X = X- unit ; 
                                                                      Y = main_y1 + unit*(0 + moveY) + unit ;  }
                                                                  if(orientation[orient_index].equals("right"))
                                                                     {X = X - unit ; 
                                                                     Y = main_y1 + unit*(0 + moveY) - unit ; }
                                                                   if(orientation[orient_index].equals("down"))
                                                                     {X = X- unit ; 
                                                                      Y = main_y1 + unit*(0 + moveY) + unit ;  }
                                                                    if(orientation[orient_index].equals("left"))
                                                                     {X = X - unit ; 
                                                                     Y = main_y1 + unit*(0 + moveY) - unit ; }
                                      //   X = main_x1 + unit*4; 
                                      //    Y = main_y1 + unit*(0 + moveY) + unit ; 

                                        for(int i =0 ; i<2 ; i++)
                                       {
                                            g2.setColor(Color.green);
                                            g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                            g2.setColor(Color.black);
                                            g2.setStroke(new BasicStroke(1));
                                            g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                                    present[i+2][0] = locate_i((int)Y );
                                                    present[i+2][1] = locate_j((int)X);
                                                    next[i+2][0] = locate_i((int)Y)+1;
                                                    next[i+2][1] = locate_j((int)X);
                                            
                                            
                                            if(orientation[orient_index].equals("up"))
                                                                     {X = X + unit ;  }
                                                                   else if(orientation[orient_index].equals("right"))
                                                                     { Y = Y + unit ;  }
                                                                  else if(orientation[orient_index].equals("down"))
                                                                     {X = X + unit ;  }
                                                                  else if(orientation[orient_index].equals("left"))
                                                                     { Y = Y + unit ;  }

                                       }
                                        
                                        
                                        if(orientation[orient_index].equals("up"))
                                                                     {X = X -3* unit ;
                                                                  //   System.out.println("X: "+ X);
                                                                 //    System.out.println("Y: "+ (main_y1 + unit*(1 + moveY))  );
                                                                     }
                                                                  else if(orientation[orient_index].equals("right"))
                                                                     { Y = Y - unit ;
                                                                      X = X + unit ;}
                                                                 else  if(orientation[orient_index].equals("down"))
                                                                     {X = X - 3*unit ;  }
                                                                      if(orientation[orient_index].equals("left"))
                                                                     { Y = Y - unit ; 
                                                                       X = X + unit;
                                                                     }
                                        
                                      //  endX = X;
                                      //  endY = Y+ unit;  
                }
                                    
                                        
                   else if(shapes[number].equals("tank"))
      {             
                                                               if(orientation[orient_index].equals("up"))
                                                                     { if (new_born)
                                                                            { X = main_x1 + unit*5 ;            
                                                                             new_born = false; 

                                                                           }   //  (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // left cart
                                                                              // (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // tank 

                                                                      else if ( (main_x1-1)<  (newX-unit)  && (newX-unit)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 )  )     // so that shape doesnt do beyond the right and left boundaries .
                                                                                   {X = newX; 
                                                                                       moveX =0;  }
                                                                    
                                                                        // one loop 
                                                                                         for(int i =0 ; i<1 ; i++)
                                                                                                 {
                                                                                                      g2.setColor(Color.green);
                                                                                                      g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                                                                      g2.setColor(Color.black);
                                                                                                      g2.setStroke(new BasicStroke(1));
                                                                                                      g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );


                                                                                                          present[i][0] = locate_i((int)Y );
                                                                                                          present[i][1] = locate_j((int)X);
                                                                                                          next[i][0] = locate_i((int)Y)+1;
                                                                                                          next[i][1] = locate_j((int)X);

                                                                                                 }
                                                                         // row ++
                                                                         Y= Y + unit ;
                                                                         X= X- unit;    //  for left cart  X = X ;
                                                                         // the three loop in next row
                                                                                      for(int i =0 ; i<3 ; i++)
                                                                                                    {
                                                                                                         g2.setColor(Color.green);
                                                                                                         g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                                                                         g2.setColor(Color.black);
                                                                                                         g2.setStroke(new BasicStroke(1));
                                                                                                         g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                                                                                                present[i+1][0] = locate_i((int)Y );
                                                                                                                present[i+1][1] = locate_j((int)X);
                                                                                                                next[i+1][0] = locate_i((int)Y)+1;
                                                                                                                next[i+1][1] = locate_j((int)X);
                                                                                                                
                                                                                                           X = X + unit   ;   
                                                                                                       }
                                                                                      X = X - 2*unit ;   // for left cart X = X- 3*units;
                                                                                      
                                                                     }
                                                                     else  if(orientation[orient_index].equals("right"))
                                                                     { if (new_born)
                                                                            { X = main_x1 + unit*5 ;            
                                                                             new_born = false; 

                                                                           }   //  (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // left cart
                                                                              // (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // tank 

                                                                      else if ( (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 )  )     // so that shape doesnt do beyond the right and left boundaries .
                                                                                   {X = newX; 
                                                                                       moveX =0;  }
                                                                         // three loop with incrementtion in rows
                                                                           for(int i =0 ; i<3 ; i++)
                                                                                    {
                                                                                         g2.setColor(Color.green);
                                                                                         g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                                                         g2.setColor(Color.black);
                                                                                         g2.setStroke(new BasicStroke(1));
                                                                                         g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                                                                                 present[i][0] = locate_i((int)Y );
                                                                                                present[i][1] = locate_j((int)X);
                                                                                                next[i][0] = locate_i((int)Y)+1;
                                                                                                next[i][1] = locate_j((int)X);
                                                                                         
                                                                                        Y = Y + unit ;
                                                                                                
                                                                                       }
                                        
                                                                        // col++
                                                                        X = X + unit ;
                                                                        Y  = Y - 2*unit ;  // left cart    Y = Y - 3*unit  
                                                                         // one loop
                                                                          for(int i =0 ; i<1 ; i++)
                                                                                                 {
                                                                                                      g2.setColor(Color.green);
                                                                                                      g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                                                                      g2.setColor(Color.black);
                                                                                                      g2.setStroke(new BasicStroke(1));
                                                                                                      g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );


                                                                                                          present[i+3][0] = locate_i((int)Y );
                                                                                                          present[i+3][1] = locate_j((int)X);
                                                                                                          next[i+3][0] = locate_i((int)Y)+1;
                                                                                                          next[i+3][1] = locate_j((int)X);

                                                                                                 }
                                                                          
                                                                          X = X - unit ;
                                                                     }
                                                                     
                                                                   else    if(orientation[orient_index].equals("down"))
                                                                     {     
                                                                        if (new_born)
                                                                            { X = main_x1 + unit*5 ;            
                                                                             new_born = false; 

                                                                           }   //  (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // left cart
                                                                              // (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // tank 

                                                                      else if ( (main_x1-1)<  (newX-unit)  && (newX-unit)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 )  )     // so that shape doesnt do beyond the right and left boundaries .
                                                                                   {X = newX; 
                                                                                       moveX =0;  }// three loop with incrementtion in  cols
                                                                         for(int i =0 ; i<3 ; i++)
                                                                                    {
                                                                                         g2.setColor(Color.green);
                                                                                         g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                                                         g2.setColor(Color.black);
                                                                                         g2.setStroke(new BasicStroke(1));
                                                                                         g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                                                                                 present[i][0] = locate_i((int)Y );
                                                                                                present[i][1] = locate_j((int)X);
                                                                                                next[i][0] = locate_i((int)Y)+1;
                                                                                                next[i][1] = locate_j((int)X);
                                                                                          X = X + unit;
                                                                                                
                                                                                       }
                                                                         
                                                                        // col = cols-3
                                                                        X = X - 2*unit ; // for left cart  X = X - unit;
                                                                         // row++;
                                                                         Y = Y + unit;
                                                                         // one loop 
                                                                          for(int i =0 ; i<1 ; i++)
                                                                                                 {
                                                                                                      g2.setColor(Color.green);
                                                                                                      g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                                                                      g2.setColor(Color.black);
                                                                                                      g2.setStroke(new BasicStroke(1));
                                                                                                      g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );


                                                                                                          present[i+3][0] = locate_i((int)Y );
                                                                                                          present[i+3][1] = locate_j((int)X);
                                                                                                          next[i+3][0] = locate_i((int)Y)+1;
                                                                                                          next[i+3][1] = locate_j((int)X);

                                                                                                 }
                                                                       X = X - unit ; // for left cart   X =  X - 2*unit ;
                                                                     }
                                                                   else   if(orientation[orient_index].equals("left"))
                                                                     {  // one loop 
                                                                         if (new_born)
                                                                            { X = main_x1 + unit*5 ;            
                                                                             new_born = false; 

                                                                           }   //  (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // left cart
                                                                              // (main_x1-1)<  (newX)  && (newX)  < (main_x2+1)  &&  (main_x1-1)<  newX+unit  && newX+unit < (main_x2 ) // tank 

                                                                      else if ( (main_x1-1)<  (newX-unit)  && (newX-unit)  < (main_x2+1)  &&  (main_x1-1)<  newX  && newX < (main_x2 )  )     // so that shape doesnt do beyond the right and left boundaries .
                                                                                   {X = newX; 
                                                                                       moveX =0;  }
                                                                          for(int i =0 ; i<1 ; i++)
                                                                                                 {
                                                                                                      g2.setColor(Color.green);
                                                                                                      g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                                                                      g2.setColor(Color.black);
                                                                                                      g2.setStroke(new BasicStroke(1));
                                                                                                      g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );


                                                                                                          present[i][0] = locate_i((int)Y );
                                                                                                          present[i][1] = locate_j((int)X);
                                                                                                          next[i][0] = locate_i((int)Y)+1;
                                                                                                          next[i][1] = locate_j((int)X);

                                                                                                 }
                                                                     
                                                                         // col ++
                                                                         X = X + unit ; 
                                                                         Y = Y-unit ; // for left cart   Y = Y-2*unit
                                                                         // three loop with incrementtion in rows }  
                                                                         for(int i =0 ; i<3 ; i++)
                                                                                    {
                                                                                         g2.setColor(Color.green);
                                                                                         g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                                                         g2.setColor(Color.black);
                                                                                         g2.setStroke(new BasicStroke(1));
                                                                                         g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                                                                                 present[i+1][0] = locate_i((int)Y );
                                                                                                present[i+1][1] = locate_j((int)X);
                                                                                                next[i+1][0] = locate_i((int)Y)+1;
                                                                                                next[i+1][1] = locate_j((int)X);
                                                                                                
                                                                                                Y = Y + unit;
                                                                                                
                                                                                       }
                                                                             X = X - unit ;
                                                                     }
     }
      
          try {
                                   Thread.sleep(200);
                                     } catch (InterruptedException ex) {
                                         Logger.getLogger(draw.class.getName()).log(Level.SEVERE, null, ex);
                                     }
                                     
          updateGraphics();  
      
      
    
      
    }  // shapeDraw ends
    
  
  
  public void shapeDraw_next(Graphics2D g2,  int number )
    {      float Y ;
           float X  ;
          
           if(shapes[number].equals("line"))
           { 
                                    X = next_x1 + unit* 1 ;    // unit*j ;               
                                    Y = next_y1 + unit*3 ;    // (i+ moveY)

                                     for(int i =0 ; i<4 ; i++)
                                     {    g2.setColor(Color.green);
                                          g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                          g2.setColor(Color.black);
                                          g2.setStroke(new BasicStroke(1));
                                          g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );
                                          X = X + unit ;
                                     }
                                   //  endX = X;
                                 //    endY = Y+ unit;

        } // if (line) ends
           
     
      else if(shapes[number].equals("box"))
      {
                                  X = next_x1 + unit*2;  //unit * j ;
                                 Y = next_y1 + unit*2 ; // (i+ moveY)
                                  for(int i =0 ; i<2 ; i++)
                                  {
                                       g2.setColor(Color.green);
                                       g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                       g2.setColor(Color.black);
                                       g2.setStroke(new BasicStroke(1));
                                       g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                        X = X + unit ;
                                  }

                                        X = next_x1 + unit*2;   //unit * j ;
                                        Y = next_y1 + unit*2+ unit ;

                                   for(int i =0 ; i<2 ; i++)
                                  {
                                       g2.setColor(Color.green);
                                       g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                       g2.setColor(Color.black);
                                       g2.setStroke(new BasicStroke(1));
                                       g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                        X = X + unit ;

                                  }
                                 //  endX = X;
                                //   endY = Y+ unit;
      
      }
    
      else if(shapes[number].equals("right cart"))
      {
                                         X =  (float) (( next_x1+ next_x2)/ 2 + unit* 0.5);  //unit * j ;
                                         Y = next_y1 + unit*2 ; // (i+ moveY)
                                        for(int i =0 ; i<1 ; i++)
                                        {
                                             g2.setColor(Color.green);
                                             g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                             g2.setColor(Color.black);
                                             g2.setStroke(new BasicStroke(1));
                                             g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                              X = X + unit ;
                                        }

                                            
                                              X =  (float) (( next_x1+ next_x2)/ 2 - unit* 1.5);  //unit * j ;
                                              Y = next_y1 + unit*3 ;// (i+ moveY)

                                         for(int i =0 ; i<3 ; i++)
                                        {
                                             g2.setColor(Color.green);
                                             g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                             g2.setColor(Color.black);
                                             g2.setStroke(new BasicStroke(1));
                                             g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                              X = X + unit ;

                                        }
                                        // endX = X;
                                         //endY = Y+ unit;

      }
      
      else if(shapes[number].equals("left cart"))
      {                                  
                                        X =  (float) (( next_x1+ next_x2)/ 2 - unit* 1.5);  //unit * j ;
                                         Y = next_y1 + unit*2 ;
                                       for(int i =0 ; i<1 ; i++)
                                       {
                                            g2.setColor(Color.green);
                                            g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                            g2.setColor(Color.black);
                                            g2.setStroke(new BasicStroke(1));
                                            g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                             X = X + unit ;
                                       }

                                              X =  (float) (( next_x1+ next_x2)/ 2 - unit* 1.5);  //unit * j ;
                                              Y = next_y1 + unit*3 ;

                                        for(int i =0 ; i<3 ; i++)
                                       {
                                            g2.setColor(Color.green);
                                            g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                            g2.setColor(Color.black);
                                            g2.setStroke(new BasicStroke(1));
                                            g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                             X = X + unit ;

                                       }
                                     //   endX = X;
                                     //   endY = Y+ unit;
      }
            else if(shapes[number].equals("right step"))       ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      {                           
                                      
                                         X =  (float) (( next_x1+ next_x2)/ 2 - unit* 0.5);  //unit * j ;
                                         Y = next_y1 + unit*2 ;
                                   
                                                                for(int i =0 ; i<2 ; i++)
                                                                {
                                                                     g2.setColor(Color.green);
                                                                     g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                                     g2.setColor(Color.black);
                                                                     g2.setStroke(new BasicStroke(1));
                                                                     g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                                              /*      present[i][0] = locate_i((int)Y );
                                                                     present[i][1] = locate_j((int)X);

                                                                     next[i][0] = locate_i((int)Y)+1;
                                                                     next[i][1] = locate_j((int)X);
                                                                */
                                                                      X = X + unit ;
                                                                }

                                                                      X =  (float) (( next_x1+ next_x2)/ 2 - unit* 1.5);  //unit * j ;
                                                                      Y = next_y1 + unit*3 ;

                                                                 for(int i =0 ; i<2 ; i++)
                                                                {
                                                                     g2.setColor(Color.green);
                                                                     g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                                                     g2.setColor(Color.black);
                                                                     g2.setStroke(new BasicStroke(1));
                                                                     g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );


                                                                     present[i+2][0] = locate_i((int)Y);
                                                                     present[i+2][1] = locate_j((int)X);

                                                                     next[i+2][0] = locate_i((int)Y)+1;
                                                                     next[i+2][1] = locate_j((int)X);
                                                                      X = X + unit ;

                                                                }
                                                                 X = X-unit;
                                                                // endX = X;
                                                               //  endY = Y+ unit;
                                        
                                    
      }
           /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
           
            else if(shapes[number].equals("left step"))
      {
                                            X =  (float) (( next_x1+ next_x2)/ 2 - unit* 1.5);  //unit * j ;
                                            Y = next_y1 + unit*2 ;
                                       for(int i =0 ; i<2 ; i++)
                                       {
                                            g2.setColor(Color.green);
                                            g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                            g2.setColor(Color.black);
                                            g2.setStroke(new BasicStroke(1));
                                            g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                             X = X + unit ;
                                       }

                                          X =  (float) (( next_x1+ next_x2)/ 2 - unit* 0.5);  //unit * j ;
                                          Y = next_y1 + unit*3 ; 

                                        for(int i =0 ; i<2 ; i++)
                                       {
                                            g2.setColor(Color.green);
                                            g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                            g2.setColor(Color.black);
                                            g2.setStroke(new BasicStroke(1));
                                            g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                             X = X + unit ;

                                       }
                                    //    endX = X;
                                     //   endY = Y+ unit;  
                }
                                    
                                        
                   else if(shapes[number].equals("tank"))
      {             
                                       X =  (float) (( next_x1+ next_x2)/ 2 - unit* 0.5);  //unit * j ;
                                         Y = next_y1 + unit*2 ;
                                       for(int i =0 ; i<1 ; i++)
                                       {
                                            g2.setColor(Color.green);
                                            g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                            g2.setColor(Color.black);
                                            g2.setStroke(new BasicStroke(1));
                                            g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                             X = X + unit ;
                                       }

                                          X =  (float) (( next_x1+ next_x2)/ 2 - unit* 1.5);  //unit * j ;
                                          Y = next_y1 + unit*3 ;

                                        for(int i =0 ; i<3 ; i++)
                                       {
                                            g2.setColor(Color.green);
                                            g2.fillRect(iX(X),  iX(Y) , iX(unit) , iX(unit) );
                                            g2.setColor(Color.black);
                                            g2.setStroke(new BasicStroke(1));
                                            g2.drawRect(iX( X),  iX( Y) , iX(unit) , iX(unit) );

                                             X = X + unit ;

                                       }
                                        //endX = X;
     }
      
           try {
                                    Thread.sleep((long) s-  Math.min(100, level*m) );
                                     } catch (InterruptedException ex) {
                                         Logger.getLogger(draw.class.getName()).log(Level.SEVERE, null, ex);
                                     }
                                     updateGraphics();  
      
      
    
      
    }  // shapeDraw ends
  
} // class draw ends .
