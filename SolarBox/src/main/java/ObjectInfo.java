import com.jogamp.common.nio.Buffers;
import org.joml.Matrix4f;
import util.*;
import com.jogamp.opengl.*;

import java.nio.FloatBuffer;
import java.util.Map;
import java.util.Stack;

// Manage 3D object (planets and the box)

public class ObjectInfo {
    public ObjectInstance meshObject;
    public Material objMaterial;

    FloatBuffer fb16 = Buffers.newDirectFloatBuffer(16);
    FloatBuffer fb4 = Buffers.newDirectFloatBuffer(4);

    // set the Mesh for object
    public void setMeshObject(GL3 gl, ShaderProgram program, ShaderLocationsVault shaderLocations, Map<String, String> shaderToVertexAttribute, PolygonMesh tmesh) {
        this.meshObject = new ObjectInstance(gl,
                program,
                shaderLocations,
                shaderToVertexAttribute,
                tmesh,new
                String(""));
    }

    // set the color for object
    public void setObjMaterial(float[] ambient) {
        util.Material mat =  new util.Material();

        mat.setAmbient(ambient[0],ambient[1],ambient[2]);

        this.objMaterial = mat;
    }

    // call in view.draw() to draw object
    public void objectDrawable(GL3 gl, GLAutoDrawable gla, Stack<Matrix4f> modelView, Matrix4f proj, ShaderLocationsVault shaderLocations){


        //pass the projection matrix to the shader
        gl.glUniformMatrix4fv(
                shaderLocations.getLocation("projection"),
                1, false, proj.get(fb16));

        //pass the modelview matrix to the shader
        gl.glUniformMatrix4fv(
                shaderLocations.getLocation("modelview"),
                1, false,  modelView.peek().get(fb16));

        //send the color of the object
        gl.glUniform4fv(
                shaderLocations.getLocation("vColor")
                , 1, objMaterial.getAmbient().get(fb4));

        gl.glPolygonMode(GL.GL_FRONT_AND_BACK,GL3.GL_LINE); //OUTLINES
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK,GL3.GL_LINE); //OUTLINES

        //draw the object
        meshObject.draw(gla);

    }

}
