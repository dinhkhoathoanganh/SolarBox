import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;
import javax.swing.*;
import java.awt.event.*;

/**
 * Created by ashesh on 9/18/2015.
 */
public class JOGLFrame extends JFrame {
  private View view;
  private int oldmouseX, oldmouseY, newmouseX, newmouseY;
  private GLCanvas canvas;
  private int ORG_WINDOW_HEIGHT = 400, ORG_WINDOW_WIDTH = 400;

  public JOGLFrame(String title) {
    //routine JFrame setting stuff
    super(title);
    setSize(ORG_WINDOW_WIDTH, ORG_WINDOW_HEIGHT); //this opens a 400x400 window
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //when X is pressed, close program

    //Our View class is the actual driver of the OpenGL stuff
    view = new View();

    GLProfile glp = GLProfile.getGL2GL3();
    GLCapabilities caps = new GLCapabilities(glp);

    canvas = new GLCanvas(caps);

    add(canvas);

    // add mouse listener for the frame
    EventListener listener = new EventListener();

    canvas.addGLEventListener(listener);
    canvas.addMouseListener(listener);
    canvas.addMouseMotionListener(listener);

    //Add an animator to the canvas
    AnimatorBase animator = new FPSAnimator(canvas, 300);
    animator.setUpdateFPSFrames(100, null);
    animator.start();
  }

  // Customize MouseAdapter and GLEventListener
  class EventListener extends MouseAdapter implements GLEventListener {

    @Override
    public void init(GLAutoDrawable glAutoDrawable) { //called the first time this canvas is created. Do your initialization here
      try {
        view.init(glAutoDrawable);
        glAutoDrawable.getGL().setSwapInterval(1);
      } catch (Exception e) {
        JOptionPane.showMessageDialog(JOGLFrame.this, e.getMessage(), "Error while loading", JOptionPane.ERROR_MESSAGE);
      }
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) { //called when the canvas is destroyed.
      view.dispose(glAutoDrawable);
    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) { //called every time this window must be redrawn

      view.draw(glAutoDrawable);

    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) { //called every time this canvas is resized
      view.reshape(glAutoDrawable, x, y, width, height, ORG_WINDOW_WIDTH, ORG_WINDOW_HEIGHT);
      repaint(); //refresh window
    }

    @Override
    public void mousePressed(MouseEvent e) {
      //record initial mouse position
      oldmouseX = e.getX();
      oldmouseY = canvas.getHeight() - e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      //record new mouse position
      newmouseX = e.getX();
      newmouseY = canvas.getHeight() - e.getY();

      //calculate and change the angle rotation of the box (trackball)
      view.setAngleOfRotationX((float)(newmouseX-oldmouseX)*360/canvas.getHeight());
      view.setAngleOfRotationY((float)(newmouseY-oldmouseY)*360/canvas.getWidth());

      //refresh image
      canvas.repaint();

      //reset the reference point of last mouse position
      oldmouseX = newmouseX;
      oldmouseY = newmouseY;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      System.out.println("HERE");
    }

  };

}
