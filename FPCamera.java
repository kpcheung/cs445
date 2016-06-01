/***************************************************************
* file: Basic3D.java
* author: Karen Cheung, Mark Erickson, Kevin Kuhlman
* class: CS 445 - Computer Graphics
*
* assignment: Final Program Checkpoint 2 
* date last modified: 5/31/2016
*
* purpose: This program displays a chunk of cubes with 6 different block types with randomly generated terrain.
*
****************************************************************/ 

package cs.pkg445.pkgfinal.project;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;


//Class: FPCamera
//Purpose: Instance of the user-camera for the program
public class FPCamera {

    private Vector3f position = null;
    private Vector3f lPosition = null;
    private float yaw = 0.0f;
    private float pitch = 0.0f;
    private Vector3Float me;
    static Chunk world;

    // Method: FPCamera constructor
    // Purpose: This constructor initializes the position vectors to the x,y,z parameters.
    public FPCamera(float x, float y, float z) {
        position = new Vector3f(x, y, z);
        lPosition = new Vector3f(x, y, z); // Look position
        lPosition.x = 0f;
        lPosition.y = 15f;
        lPosition.z = 0f;
        world = new Chunk(0,0,0);
    }

    // Method: yaw
    // Purpose: This method changes the yaw of the camera based on mouse input.
    public void yaw(float amount) {
        yaw += amount;
    }

    // Method: pitch
    // Purpose: This method changes the pitch of the camera based on mouse input.
    public void pitch(float amount) {
        pitch -= amount;
    }

    // Method: walkForward
    // Purpose: This method simulates the camera moving forward by an amount input by user.
    public void walkForward(float distance) {
            float xOffset = distance * (float) Math.sin(Math.toRadians(yaw));
            float zOffset = distance * (float) Math.cos(Math.toRadians(yaw));
            position.x -= xOffset;
            position.z += zOffset;
    }

    // Method: walkBackwards
    // Purpose: This method moves the camera backwards.
    public void walkBackwards(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw));
        position.x += xOffset;
        position.z -= zOffset;
    }

    // Method: strafeLeft
    // Purpose: This method moves the camera to the left.
    public void strafeLeft(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw - 90));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw - 90));
        position.x -= xOffset;
        position.z += zOffset;
    }

    // Method: strafeRight
    // Purpose: This method moves the camera to the right.
    public void strafeRight(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw + 90));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw + 90));
        position.x -= xOffset;
        position.z += zOffset;
    }

    // Method: moveUp
    // Purpose: This method moves the camera up.
    public void moveUp(float distance) {
        position.y -= distance;
    }

    // Method: moveDown
    // Purpose: This method moves the camera down.
    public void moveDown(float distance) {
        position.y += distance;
    }

    // Method: lookThrough
    // Purpose: This method translates and rotates the matrix so that it looks through the camera.
    public void lookThrough() {
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        glTranslatef(position.x, position.y, position.z);
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(20.0f).put(50.0f).put(30).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    //Method: collision
    //Purpose: Check whether or not to stop movement based on collision with an object
    public boolean collision(float distance, int key){
        int check = 0;
        float nextX = Math.round(position.x); 
        float nextY = Math.round(position.y); 
        float nextZ = Math.round(position.z);

        System.out.println(nextX + " " + nextY + " " + nextZ);
        for(int i = 0; i<world.CHUNK_SIZE; i++){
            for(int j = 0; j<world.CHUNK_SIZE; j++){
                for(int k = 0; k<world.CHUNK_SIZE; k++){
                    int check1=0, check2=0, check3=0;
                    float blockX = world.Blocks[i][j][k].getX();
                    float blockY = world.Blocks[i][j][k].getY();
                    float blockZ = world.Blocks[i][j][k].getZ();
                    if(nextX == -1*(blockX-2) || nextX == -1*(blockX))
                        check1++;
                    if(nextY == -1*(blockY-2) || nextY == -1*(blockY))
                        check2++;
                    if(nextZ == -1*(blockZ-2) || nextZ == -1*(blockZ))
                        check3++;
                    if(check1 != 0 && check2 !=0 && check3 !=0){
                        check++;
                    }
                    check1=0; check2=0; check3=0;
                    blockX+=30;
                    if(nextX == -1*(blockX-2) || nextX == -1*(blockX))
                        check1++;
                    if(nextY == -1*(blockY-2) || nextY == -1*(blockY))
                        check2++;
                    if(nextZ == -1*(blockZ-2) || nextZ == -1*(blockZ))
                        check3++;
                    if(check1 != 0 && check2 !=0 && check3 !=0){
                        check++;
                    }

                    check1=0; check2=0; check3=0;
                    blockZ+=30;
                    if(nextX == -1*(blockX-2) || nextX == -1*(blockX))
                        check1++;
                    if(nextY == -1*(blockY-2) || nextY == -1*(blockY))
                        check2++;
                    if(nextZ == -1*(blockZ-2) || nextZ == -1*(blockZ))
                        check3++;
                    if(check1 != 0 && check2 !=0 && check3 !=0){
                        check++;
                    }

                    check1=0; check2=0; check3=0;
                    blockX-=30;
                    if(nextX == -1*(blockX-2) || nextX == -1*(blockX))
                        check1++;
                    if(nextY == -1*(blockY-2) || nextY == -1*(blockY))
                        check2++;
                    if(nextZ == -1*(blockZ-2) || nextZ == -1*(blockZ))
                        check3++;
                    if(check1 != 0 && check2 !=0 && check3 !=0){
                        check++;
                    }
                }
            }
        }
        if(check!=0){
            return false;
        }
        return true;
    }
}