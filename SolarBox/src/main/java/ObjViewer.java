import javax.swing.*;

/**
 * Created by dinh khoat hoang anh on 2/5/2019.
 *
 * Solar system in a box
 *
 * Use mouse to rotate the solar box
 *
 * Use SolarConstants class to change the measurements of the solar system
 * (size, color, rotating speed, orbit etc.)
 *
 * Drawing of the objects is done in class View
 *
 */

public class ObjViewer {


  public static void main(String[] args) {
    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        createAndShowGUI();
      }
    });
  }

  private static void createAndShowGUI() {
    JFrame frame = new JOGLFrame("Solar System in a Box");
    frame.setVisible(true);
  }
}
