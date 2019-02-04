import com.jogamp.opengl.*;

import util.*;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by ashesh on 9/18/2015.
 *
 * The View class is the "controller" of all our OpenGL stuff. It cleanly
 * encapsulates all our OpenGL functionality from the rest of Java GUI, managed
 * by the JOGLFrame class.
 */
public class View {
  private int WINDOW_WIDTH,WINDOW_HEIGHT,ORTHO_VERTICAL = 300, ORTHO_HORIZONTAL = 300;
  private Matrix4f proj;
  Stack<Matrix4f> modelview;
  private int angleOfRotation = 0;
  private ObjectInfo box = new ObjectInfo(),
          sun = new ObjectInfo(),
          mars = new ObjectInfo(),
          earth = new ObjectInfo(),
          jupiter = new ObjectInfo(),
          pluto = new ObjectInfo(),
          earthSatellite = new ObjectInfo(),
          marsSatellite1 = new ObjectInfo(),
          marsSatellite2 = new ObjectInfo()
  ;

  private float marsAngle = 0,
          earthAngle = 30,
          jupiterAngle = -100,
          plutoAngle = 60,
          earthSatelliteAngle = 0,
          marsSatelliteAngle1 = 0,
          marsSatelliteAngle2 = 40
  ;

  private OrbitInfo marsOrbit = new OrbitInfo();
  private ObjectInstance marsOrbitObj;

  private ShaderProgram program;
  private ShaderLocationsVault shaderLocations;




  public View() {
    proj = new Matrix4f();
    proj.identity();

    modelview = new Stack<Matrix4f>();

  }

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
    object.setObjMaterial(ambient);

  }

  public void init(GLAutoDrawable gla) throws Exception {
    GL3 gl = (GL3) gla.getGL().getGL3();


    //compile and make our shader program. Look at the ShaderProgram class for details on how this is done
    program = new ShaderProgram();
    program.createProgram(gl, "shaders/default.vert", "shaders/default.frag");

    shaderLocations = program.getAllShaderVariables(gl);

    initObjects(gl, box,"models/box.obj", SolarConstants.BOX_COLOR); //grey
    initObjects(gl, sun,"models/neptune.obj", SolarConstants.SUN_COLOR); //yellow
    initObjects(gl, mars,"models/sphere.obj", SolarConstants.MARS_COLOR); //red
    initObjects(gl, earth,"models/sphere.obj", SolarConstants.EARTH_COLOR); //blue
    initObjects(gl, jupiter,"models/sphere.obj", SolarConstants.JUPITER_COLOR); //green
    initObjects(gl, pluto,"models/sphere.obj", SolarConstants.PLUTO_COLOR); //dark green

    initObjects(gl, earthSatellite,"models/sphere.obj", SolarConstants.EARTH_SATELLITE_COLOR);
    initObjects(gl, marsSatellite1,"models/sphere.obj", SolarConstants.MARS_SATELLITE_COLOR1);
    initObjects(gl, marsSatellite2,"models/sphere.obj", SolarConstants.MARS_SATELLITE_COLOR2);

    //create a segment object
    marsOrbitObj = new ObjectInstance(gl, program, shaderLocations, marsOrbit.getShaderToVertexAttribute(), marsOrbit.getMesh(), "triangles");


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

    float[] lookAngle = new float[] {0,0,200};

    ///////////////////Draw the box///////////////////////
    modelview.push(new Matrix4f());

    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .lookAt(new Vector3f(lookAngle[0],lookAngle[1],lookAngle[2]),new Vector3f(0,
            0,0),new Vector3f(0,1,0));

    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .scale(SolarConstants.BOX_SIZE,SolarConstants.BOX_SIZE,SolarConstants.BOX_SIZE);

    box.objectDrawable(gl,gla,modelview,proj,shaderLocations);

    modelview.pop(); modelview.pop(); modelview.pop();
    /////////////////////////////////////////////////////

    ///////////////////Draw the sun//////////////////////
    modelview.push(new Matrix4f());

    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .lookAt(new Vector3f(lookAngle[0],lookAngle[1],lookAngle[2]),new Vector3f(0,
            0,0),new Vector3f(0,1,0));

    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .scale(SolarConstants.SUN_SIZE,SolarConstants.SUN_SIZE,SolarConstants.SUN_SIZE);

    sun.objectDrawable(gl,gla,modelview,proj,shaderLocations);

    modelview.pop(); modelview.pop(); modelview.pop();
    /////////////////////////////////////////////////////


    ///////////////////Draw the mars///////////////////

    marsAngle = (marsAngle+ 360f/SolarConstants.MARS_YEAR) % 360 ;
    marsSatelliteAngle1 = (marsSatelliteAngle1 - 360f/SolarConstants.MARS_SATELLITE_YEAR1) % 360 ;
    marsSatelliteAngle2 = (marsSatelliteAngle2 - 360f/SolarConstants.MARS_SATELLITE_YEAR2) % 360 ;


    modelview.push(new Matrix4f());

    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .lookAt(new Vector3f(lookAngle[0],lookAngle[1],lookAngle[2]),new Vector3f(0,
            0,0),new Vector3f(0,1,0));

    // Draw mars
    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .rotate((float)Math.toRadians(marsAngle),0,1,0)
            .translate(-120,0,0)
            .scale(SolarConstants.MARS_SIZE,SolarConstants.MARS_SIZE,SolarConstants.MARS_SIZE);

    mars.objectDrawable(gl,gla,modelview,proj,shaderLocations);

    // Draw satellite 1
    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .rotate(-(float)Math.toRadians(marsSatelliteAngle1),0,1,0)
            .translate(1,0,0).scale(SolarConstants.MARS_SATELLITE_SIZE_RATIO1,SolarConstants.MARS_SATELLITE_SIZE_RATIO1, SolarConstants.MARS_SATELLITE_SIZE_RATIO1);

    marsSatellite1.objectDrawable(gl,gla,modelview,proj,shaderLocations);

    modelview.pop();


    // Draw satellite 2
    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .rotate(-(float)Math.toRadians(marsSatelliteAngle2),0,0,1)
            .translate(0,-1,0).scale(SolarConstants.MARS_SATELLITE_SIZE_RATIO2,SolarConstants.MARS_SATELLITE_SIZE_RATIO2, SolarConstants.MARS_SATELLITE_SIZE_RATIO2);

    marsSatellite2.objectDrawable(gl,gla,modelview,proj,shaderLocations);

    modelview.pop();


    modelview.pop(); modelview.pop(); modelview.pop();

    marsOrbit.DotDrawable(gl, gla, marsOrbitObj, proj, shaderLocations);
    /////////////////////////////////////////////////////

    ///////////////////Draw the earth///////////////////

    earthAngle = (earthAngle+ 360f/SolarConstants.EARTH_YEAR) % 360 ;
    earthSatelliteAngle = (earthSatelliteAngle - 360f/SolarConstants.EARTH_SATELLITE_YEAR) % 360 ;

    modelview.push(new Matrix4f());

    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .lookAt(new Vector3f(lookAngle[0],lookAngle[1],lookAngle[2]),new Vector3f(0,
            0,0),new Vector3f(0,1,0));

    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .rotate((float)Math.toRadians(earthAngle),1,0,0)
            .translate(0,-80,0).scale(SolarConstants.EARTH_SIZE,SolarConstants.EARTH_SIZE,SolarConstants.EARTH_SIZE);

    earth.objectDrawable(gl,gla,modelview,proj,shaderLocations);

    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .rotate(-(float)Math.toRadians(earthSatelliteAngle),0,1,0)
            .translate(1,0,0).scale(SolarConstants.EARTH_SATELLITE_SIZE_RATIO,SolarConstants.EARTH_SATELLITE_SIZE_RATIO, SolarConstants.EARTH_SATELLITE_SIZE_RATIO);

    earthSatellite.objectDrawable(gl,gla,modelview,proj,shaderLocations);

    modelview.pop(); modelview.pop(); modelview.pop(); modelview.pop();
    /////////////////////////////////////////////////////

    ///////////////////Draw the jupiter///////////////////

    jupiterAngle = (jupiterAngle + 360f/SolarConstants.JUPITER_YEAR) % 360 ;

    modelview.push(new Matrix4f());

    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .lookAt(new Vector3f(lookAngle[0],lookAngle[1],lookAngle[2]),new Vector3f(0,
            0,0),new Vector3f(0,1,0));
    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .rotate((float)Math.toRadians(jupiterAngle),0,0,1)
            .translate(0,-100,0).scale(SolarConstants.JUPITER_SIZE,SolarConstants.JUPITER_SIZE,SolarConstants.JUPITER_SIZE);

    jupiter.objectDrawable(gl,gla,modelview,proj,shaderLocations);

    modelview.pop(); modelview.pop(); modelview.pop();
    /////////////////////////////////////////////////////

    ///////////////////Draw the pluto///////////////////

    plutoAngle =  (plutoAngle + 360f/SolarConstants.PLUTO_YEAR) % 360 ;

    modelview.push(new Matrix4f());

    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .lookAt(new Vector3f(lookAngle[0],lookAngle[1],lookAngle[2]),new Vector3f(0,
            0,0),new Vector3f(0,1,0));

    modelview.push(new Matrix4f(modelview.peek()));
    modelview.peek()
            .rotate((float)Math.toRadians(plutoAngle),0,-1,0)
            .translate(-50,0,0).scale(SolarConstants.PLUTO_SIZE,SolarConstants.PLUTO_SIZE,SolarConstants.PLUTO_SIZE);

    pluto.objectDrawable(gl,gla,modelview,proj,shaderLocations);

    modelview.pop(); modelview.pop(); modelview.pop();
    /////////////////////////////////////////////////////

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
