/**
 * Determine the dimensions of the solar box
 * Allow easy modification and debugging
 **/

public class SolarConstants {
    // Sizes of planets
    public static final int BOX_SIZE = 400;
    public static final int SUN_SIZE = 60;
    public static final int MARS_SIZE = 30;
    public static final int EARTH_SIZE = 20;
    public static final int JUPITER_SIZE = 25;
    public static final int PLUTO_SIZE = 10;

    // Sizes of satellites (ratio respect to their parent planet)
    public static final float EARTH_SATELLITE_SIZE_RATIO = 0.4f;
    public static final float MARS_SATELLITE_SIZE_RATIO1 = 0.5f;
    public static final float MARS_SATELLITE_SIZE_RATIO2 = 0.2f;

    // Colors of planets
    public static final float[] BOX_COLOR = new float[] {0.5f,0.5f,0.5f}; //gray
    public static final float[] SUN_COLOR = new float[] {1,1,0}; //yellow
    public static final float[] MARS_COLOR = new float[] {1,0,0}; //red
    public static final float[] EARTH_COLOR = new float[] {0,0,1}; //blue
    public static final float[] JUPITER_COLOR = new float[] {0,1,0}; //bright green
    public static final float[] PLUTO_COLOR = new float[] {0,0.5f,0}; //dark green

    // Colors of satellites
    public static final float[] EARTH_SATELLITE_COLOR = new float[] {0.690196f,0.878431f,0.901961f};
    public static final float[] MARS_SATELLITE_COLOR1 = new float[] {1,0.713725f,0.756863f};
    public static final float[] MARS_SATELLITE_COLOR2 = new float[] {0.541176f,0.168627f,0.886275f};


    // Planet's orbits
    public static final int MARS_ORBIT = 120;
    public static final int EARTH_ORBIT = 80;
    public static final int JUPITER_ORBIT = 180;
    public static final int PLUTO_ORBIT = 45;

    // Satellite's orbits
    public static final float MARS_SATELLITE_ORBIT1 = 1.5f;
    public static final float MARS_SATELLITE_ORBIT2 = 1.0f;
    public static final float EARTH_SATELLITE_ORBITX = 0.8f;
    public static final float EARTH_SATELLITE_ORBITY = 1.5f;


    // Years on planets
    public static final int MARS_YEAR = 600;
    public static final int EARTH_YEAR = 365;
    public static final int JUPITER_YEAR = 400;
    public static final int PLUTO_YEAR = 200;

    // Years on satellites
    public static final int EARTH_SATELLITE_YEAR = 200;
    public static final int MARS_SATELLITE_YEAR1 = 360;
    public static final int MARS_SATELLITE_YEAR2 = 500;


}
