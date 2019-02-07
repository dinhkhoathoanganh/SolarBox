import com.jogamp.opengl.*;
import util.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

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
 */
public class View {
  private int WINDOW_WIDTH,WINDOW_HEIGHT,ORTHO_VERTICAL = 360, ORTHO_HORIZONTAL = 360;
  private Matrix4f proj;
  Stack<Matrix4f> modelview;

  private ObjectInfo box = new ObjectInfo(),
          sun = new ObjectInfo(), // yellow star at the centre of the box
          mars = new ObjectInfo(), // red planet with 2 satellites
          earth = new ObjectInfo(), // blue planet with 1 elliptical satellite
          jupiter = new ObjectInfo(), // bright green planet
          pluto = new ObjectInfo(), // dark green planet
          earthSatellite = new ObjectInfo(), // bright blue with elliptical orbit
          marsSatellite1 = new ObjectInfo(), // pink with circular orbit
          marsSatellite2 = new ObjectInfo() // purple with circular orbit
  ;


  // Angle for the box in view
  private float angleOfRotationX = 0, angleOfRotationY = 0;

  // Starting position of the planets/satellites
  private float marsAngle = 0,
          earthAngle = 30,
          jupiterAngle = -100,
          plutoAngle = 60,
          earthSatelliteAngle = 0,
          marsSatelliteAngle1 = 0,
          marsSatelliteAngle2 = 40
  ;

  private OrbitInfo orbitInfo = new OrbitInfo();
  private ObjectInstance orbitObj;

  private ShaderProgram program;
  private ShaderLocationsVault shaderLocations;


  public View() {
    proj = new Matrix4f();
    proj.identity();

    modelview = new Stack<Matrix4f>();

  }

  // Set angle of rotation for the solar box (trackball)
  public void setAngleOfRotationX(float change) {
    this.angleOfRotationX = angleOfRotationX + change;
  }

  public void setAngleOfRotationY(float change) {
    this.angleOfRotationY = angleOfRotationY + change;
  }

  // Utility function to initialize objects in solar box
  private void initObjects(GL3 gl, ObjectInfo object, String objType, float[] ambient) throws FileNotFoundException
  {
    PolygonMesh tmesh;

    InputStream in;

    in = new FileInputStream(objType);

    tmesh = util.ObjImporter.importFile(new VertexAttribProducer(),in,true);

    Map<String, String> shaderToVertexAttribute = new HashMap<String, String>();

    //currently there is only one per-vertex attribute: position
    shaderToVertexAttribute.put("vPosition", "position");

    object.setMeshObject(gl,program,shaderLocations,shaderToVertexAttribute,tmesh);

    //set color of the object
    object.setObjMaterial(ambient);

  }

  public void init(GLAutoDrawable gla) throws Exception {
    GL3 gl = (GL3) gla.getGL().getGL3();

    //compile and make our shader program. Look at the ShaderProgram class for details on how this is done
    program = new ShaderProgram();
    program.createProgram(gl, "shaders/default.vert", "shaders/default.frag");

    shaderLocations = program.getAllShaderVariables(gl);

    //initialize box and planets in the solar box
    initObjects(gl, box,"models/box.obj", SolarConstants.BOX_COLOR); //grey
    initObjects(gl, sun,"models/sphere.obj", SolarConstants.SUN_COLOR); //yellow
    initObjects(gl, mars,"models/sphere.obj", SolarConstants.MARS_COLOR); //red
    initObjects(gl, earth,"models/sphere.obj", SolarConstants.EARTH_COLOR); //blue
    initObjects(gl, jupiter,"models/sphere.obj", SolarConstants.JUPITER_COLOR); //green
    initObjects(gl, pluto,"models/sphere.obj", SolarConstants.PLUTO_COLOR); //dark green

    //create satellite object
    initObjects(gl, earthSatellite,"models/sphere.obj", SolarConstants.EARTH_SATELLITE_COLOR);
    initObjects(gl, marsSatellite1,"models/sphere.obj", SolarConstants.MARS_SATELLITE_COLOR1);
    initObjects(gl, marsSatellite2,"models/sphere.obj", SolarConstants.MARS_SATELLITE_COLOR2);

    //create orbit object
    orbitObj = new ObjectInstance(gl, program, shaderLocations, orbitInfo.getShaderToVertexAttribute(), orbitInfo.getMesh(), "triangles");

  }


  public void draw(GLAutoDrawable gla) {

    GL3 gl = gla.getGL().getGL3();

    //set the background color to be black
    gl.glClearColor(0, 0, 0, 1);
    //clear the background
    gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT);
    gl.glEnable(GL.GL_DEPTH_TEST);
    //enable the shader program
    program.enable(gl);

    ////////////////////Set view angle/////////////////////

    modelview.push(new Matrix4f());

    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .lookAt(new Vector3f(0,0,450),new Vector3f(0,
                    0,0),new Vector3f(0,1,0));

    //angle of rotation for the solar box (track ball)
    //no Gimbal lock
    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .rotate((float)Math.toRadians(angleOfRotationX),0,1,0)
            .rotate((float)Math.toRadians(angleOfRotationY),-1,0,0);

    //////////////////////////////////////////////////////

    ///////////////////Draw the box///////////////////////

    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .scale(SolarConstants.BOX_SIZE,SolarConstants.BOX_SIZE,SolarConstants.BOX_SIZE);

    box.objectDrawable(gl,gla,modelview,proj,shaderLocations);

    modelview.pop();
    /////////////////////////////////////////////////////

    ///////////////////Draw the sun//////////////////////
    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .scale(SolarConstants.SUN_SIZE,SolarConstants.SUN_SIZE,SolarConstants.SUN_SIZE);

    sun.objectDrawable(gl,gla,modelview,proj,shaderLocations);

    modelview.pop();
    /////////////////////////////////////////////////////


    ///////////////////Draw the mars///////////////////
    marsAngle = (marsAngle+ 360f/SolarConstants.MARS_YEAR) % 360 ;
    marsSatelliteAngle1 = (marsSatelliteAngle1 - 360f/SolarConstants.MARS_SATELLITE_YEAR1) % 360 ;
    marsSatelliteAngle2 = (marsSatelliteAngle2 - 360f/SolarConstants.MARS_SATELLITE_YEAR2) % 360 ;

    // Draw mars
    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .rotate((float)Math.toRadians(marsAngle),0,1,0)
            .translate(-SolarConstants.MARS_ORBIT,0,0)
            .scale(SolarConstants.MARS_SIZE,SolarConstants.MARS_SIZE,SolarConstants.MARS_SIZE);

    mars.objectDrawable(gl,gla,modelview,proj,shaderLocations);

    // Draw satellite 1
    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .rotate(-(float)Math.toRadians(marsSatelliteAngle1),0,1,0)
            .translate(SolarConstants.MARS_SATELLITE_ORBIT1,0,0)
            .scale(SolarConstants.MARS_SATELLITE_SIZE_RATIO1,SolarConstants.MARS_SATELLITE_SIZE_RATIO1, SolarConstants.MARS_SATELLITE_SIZE_RATIO1);

    marsSatellite1.objectDrawable(gl,gla,modelview,proj,shaderLocations);

    modelview.pop();


    // Draw satellite 2
    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .rotate(-(float)Math.toRadians(marsSatelliteAngle2),0,0,1)
            .translate(0,-SolarConstants.MARS_SATELLITE_ORBIT2,0)
            .scale(SolarConstants.MARS_SATELLITE_SIZE_RATIO2,SolarConstants.MARS_SATELLITE_SIZE_RATIO2, SolarConstants.MARS_SATELLITE_SIZE_RATIO2);

    marsSatellite2.objectDrawable(gl,gla,modelview,proj,shaderLocations);

    modelview.pop();

    // Draw Mars satellite's orbit
    // Satellite orbit 1
    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .rotate((float)Math.toRadians(90),1,0,0)
            .scale(SolarConstants.MARS_SATELLITE_ORBIT1,SolarConstants.MARS_SATELLITE_ORBIT1,1);

    orbitInfo.OrbitDrawable(gl, gla, orbitObj, modelview, proj, shaderLocations);

    modelview.pop();

    // Satellite orbit2
    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .scale(SolarConstants.MARS_SATELLITE_ORBIT2,SolarConstants.MARS_SATELLITE_ORBIT2,1);

    orbitInfo.OrbitDrawable(gl, gla, orbitObj, modelview, proj, shaderLocations);

    modelview.pop();

    modelview.pop();

    // Draw Mars orbit
    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .rotate((float)Math.toRadians(90),1,0,0)
            .scale(SolarConstants.MARS_ORBIT,SolarConstants.MARS_ORBIT,1);

    orbitInfo.OrbitDrawable(gl, gla, orbitObj, modelview, proj, shaderLocations);

    modelview.pop();

    /////////////////////////////////////////////////////

    ///////////////////Draw the earth///////////////////

    earthAngle = (earthAngle+ 360f/SolarConstants.EARTH_YEAR) % 360 ;
    earthSatelliteAngle = (earthSatelliteAngle - 360f/SolarConstants.EARTH_SATELLITE_YEAR) % 360 ;


    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .rotate((float)Math.toRadians(earthAngle),0,0,1)
            .translate(0,-SolarConstants.EARTH_ORBIT,0).scale(SolarConstants.EARTH_SIZE,SolarConstants.EARTH_SIZE,SolarConstants.EARTH_SIZE);

    earth.objectDrawable(gl,gla,modelview,proj,shaderLocations);

    // Draw earth's satellite
    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .translate((float)Math.cos((float)Math.toRadians(earthSatelliteAngle))*SolarConstants.EARTH_SATELLITE_ORBITX,((float)Math.sin((float)Math.toRadians(earthSatelliteAngle))*SolarConstants.EARTH_SATELLITE_ORBITY)-SolarConstants.EARTH_SATELLITE_ORBITX/2,0)
            .scale(SolarConstants.EARTH_SATELLITE_SIZE_RATIO,SolarConstants.EARTH_SATELLITE_SIZE_RATIO, SolarConstants.EARTH_SATELLITE_SIZE_RATIO);


    earthSatellite.objectDrawable(gl,gla,modelview,proj,shaderLocations);

    modelview.pop();

    // Draw Earth's satellite orbit
    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .translate(0,-SolarConstants.EARTH_SATELLITE_ORBITX/2,0)
            .scale(SolarConstants.EARTH_SATELLITE_ORBITX,SolarConstants.EARTH_SATELLITE_ORBITY,1);

    orbitInfo.OrbitDrawable(gl, gla, orbitObj, modelview, proj, shaderLocations);

    modelview.pop();

    modelview.pop();

    // Draw earth's orbit
    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .scale(SolarConstants.EARTH_ORBIT,SolarConstants.EARTH_ORBIT,1);

    orbitInfo.OrbitDrawable(gl, gla, orbitObj, modelview, proj, shaderLocations);

    modelview.pop();


    /////////////////////////////////////////////////////

    ///////////////////Draw the jupiter///////////////////

    jupiterAngle = (jupiterAngle + 360f/SolarConstants.JUPITER_YEAR) % 360 ;

    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .rotate((float)Math.toRadians(jupiterAngle),-1,0,0)
            .translate(0,-SolarConstants.JUPITER_ORBIT,0)
            .scale(SolarConstants.JUPITER_SIZE,SolarConstants.JUPITER_SIZE,SolarConstants.JUPITER_SIZE);

    jupiter.objectDrawable(gl,gla,modelview,proj,shaderLocations);

    modelview.pop();

    // Draw jupiter orbit
    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .rotate((float)Math.toRadians(90),0,1,0)
            .scale(SolarConstants.JUPITER_ORBIT,SolarConstants.JUPITER_ORBIT,1);

    orbitInfo.OrbitDrawable(gl, gla, orbitObj, modelview, proj, shaderLocations);

    modelview.pop();
    /////////////////////////////////////////////////////

    ///////////////////Draw the pluto///////////////////

    plutoAngle =  (plutoAngle + 360f/SolarConstants.PLUTO_YEAR) % 360 ;

    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .rotate((float)Math.toRadians(45),1,0,0)
            .rotate((float)Math.toRadians(45),0,-1,0)
            .rotate((float)Math.toRadians(plutoAngle),0,0,1)
            .translate(-SolarConstants.PLUTO_ORBIT,0,0)
            .scale(SolarConstants.PLUTO_SIZE,SolarConstants.PLUTO_SIZE,SolarConstants.PLUTO_SIZE);

    pluto.objectDrawable(gl,gla,modelview,proj,shaderLocations);

    modelview.pop();

    // Draw pluto orbit
    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .rotate((float)Math.toRadians(45),1,0,0)
            .rotate((float)Math.toRadians(45),0,-1,0)
            .scale(SolarConstants.PLUTO_ORBIT,SolarConstants.PLUTO_ORBIT,1);

    orbitInfo.OrbitDrawable(gl, gla, orbitObj, modelview, proj, shaderLocations);

    modelview.pop();

    /////////////////////////////////////////////////////

    modelview.pop(); modelview.pop(); modelview.pop();

    gl.glFlush();
    //disable the program
    program.disable(gl);
    gl.glPolygonMode(GL.GL_FRONT_AND_BACK,GL3.GL_FILL); //BACK TO FILL
  }

  //this method is called from the JOGLFrame class, everytime the window resizes
  public void reshape(GLAutoDrawable gla, int x, int y, int width, int height, int  ORG_WINDOW_WIDTH, int ORG_WINDOW_HEIGHT) {
    GL gl = gla.getGL();
    WINDOW_WIDTH = width;
    WINDOW_HEIGHT = height;
    gl.glViewport(0, 0, width, height);

    float newWindowRatio = ((float) WINDOW_WIDTH / (float) WINDOW_HEIGHT);

    proj = new Matrix4f().perspective((float)Math.toRadians(60.0f),
            (float) width/height,
            0.1f,
            10000.0f);

    //if the window is to stretched out horizontally
    if (newWindowRatio > (((float) ORG_WINDOW_WIDTH / (float) ORG_WINDOW_HEIGHT))){
      proj = new Matrix4f().ortho(-ORTHO_VERTICAL * newWindowRatio, ORTHO_VERTICAL * newWindowRatio, -ORTHO_VERTICAL, ORTHO_VERTICAL,0.1f,10000.0f);
    }

    //if the window is to stretched out vertically
    else {
      proj = new Matrix4f().ortho(-ORTHO_HORIZONTAL, ORTHO_HORIZONTAL, -ORTHO_HORIZONTAL / newWindowRatio, ORTHO_HORIZONTAL / newWindowRatio,0.1f,10000.0f);
    }

  }

  public void dispose(GLAutoDrawable gla) {
    box.meshObject.cleanup(gla);
  }
}
