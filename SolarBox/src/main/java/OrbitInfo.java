import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import com.jogamp.opengl.GL;
import util.IVertexData;
import util.ObjectInstance;
import util.PolygonMesh;
import util.ShaderLocationsVault;

import java.nio.FloatBuffer;
import java.util.*;

public class OrbitInfo {

    private PolygonMesh mesh = new PolygonMesh();
    private Map<String, String> shaderToVertexAttribute = new HashMap<String, String>();

    private FloatBuffer fb16 = Buffers.newDirectFloatBuffer(16);
    FloatBuffer fb4 = Buffers.newDirectFloatBuffer(4);
    Vector4f colors = new Vector4f(1, 1, 1, 1); // white
    private int SLICES = 50;

    public PolygonMesh<IVertexData> getMesh() {
        return mesh;
    }

    public Map<String, String> getShaderToVertexAttribute() {
        return shaderToVertexAttribute;
    }

    // drawing of the circle, called in method init()
    public OrbitInfo() {

        List<Vector4f> positions = new ArrayList<Vector4f>();

        for (int i=0;i<SLICES;i++) {
            float theta = (float)(i*2*Math.PI/SLICES);
            positions.add(new Vector4f(
                    (float)Math.cos(theta),
                    (float)Math.sin(theta),
                    0,
                    1));
        }
        positions.add(new Vector4f(1,0,0,1));

        //set up vertex attributes
        List<IVertexData> vertexData = new ArrayList<IVertexData>();
        VertexAttribWithColorProducer producer = new VertexAttribWithColorProducer();
        for (int i = 0; i < positions.size(); i++) {
            IVertexData v = producer.produce();
            v.setData("position", new float[]{positions.get(i).x,
                    positions.get(i).y,
                    positions.get(i).z,
                    positions.get(i).w});
            v.setData("color", new float[]{colors.x,colors.y,colors.z,colors.w});

            vertexData.add(v);
        }


        //set up the indices
        List<Integer> indices = new ArrayList<Integer>();

        for (int i=0;i<positions.size();i++) {
            indices.add(i);
        }

        //now we create a polygon mesh object

        mesh.setVertexData(vertexData);
        mesh.setPrimitives(indices);

        //circle outline
        mesh.setPrimitiveType(GL.GL_TRIANGLE_STRIP);
        mesh.setPrimitiveSize(3);

        shaderToVertexAttribute.put("vPosition", "position");
        shaderToVertexAttribute.put("vColor", "color");
    }


    public void OrbitDrawable(GL3 gl, GLAutoDrawable gla, ObjectInstance obj, Stack<Matrix4f> modelView, Matrix4f proj, ShaderLocationsVault shaderLocations){

        //pass the projection matrix to the shader
        gl.glUniformMatrix4fv(
                shaderLocations.getLocation("projection"),
                1, false, proj.get(fb16));

        //pass the modelview matrix to the shader
        gl.glUniformMatrix4fv(
                shaderLocations.getLocation("modelview"),
                1, false,  modelView.peek().get(fb16));

        //send the color of the triangle
        gl.glUniform4fv(
                shaderLocations.getLocation("vColor")
                , 1, colors.get(fb4));

        gl.glPolygonMode(GL.GL_FRONT_AND_BACK,GL3.GL_LINE); //OUTLINES
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK,GL3.GL_LINE); //OUTLINES

        obj.draw(gla);

    }
}
