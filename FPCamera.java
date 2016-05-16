/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.pkg445.pkgfinal.project;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

public class FPCamera {

    private Vector3f position = null;
    private Vector3f lPosition = null;
    private float yaw = 0.0f;
    private float pitch = 0.0f;
    private Vector3Float me;
    private Chunk world;

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
    }
    
    // Method: gameLoop
    // Purpose: This method contains the controls for the camera and calls the render method.
    public void gameLoop(){
        FPCamera camera = new FPCamera(0,0,0);
        float dx = 0.0f;
        float dy = 0.0f;
        float dt = 0.0f; //Length of the frame.
        float lastTime = 0.0f; //When the last frame occured
        long time = 0;
        float mouseSensitivity = 0.09f;
        float movementSpeed = .35f;
        Mouse.setGrabbed(true);
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
            time = Sys.getTime();
            lastTime = time;
            dx = Mouse.getDX();
            dy = Mouse.getDY();
            camera.yaw(dx * mouseSensitivity);
            camera.pitch(dy * mouseSensitivity);
            if (Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP)){
                camera.walkForward(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN)){
                camera.walkBackwards(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT)){
                camera.strafeLeft(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){
                camera.strafeRight(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
                camera.moveUp(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
                camera.moveDown(movementSpeed);
            }
            glLoadIdentity();
            camera.lookThrough();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            world.render();
            Display.update();
            Display.sync(60);
            
        }
        Display.destroy();
    }
    
    // Method: Render
    // Purpose: This method draws the cube for the program.
     private void render(){
        try{
            
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            
            //Top
            glBegin(GL_POLYGON);
            glColor3f(1.0f,0.0f,0.0f);
            glVertex3f(1.0f,1.0f,-1.0f);
            glVertex3f(-1.0f,1.0f,-1.0f);
            glVertex3f(-1.0f,1.0f,1.0f);
            glVertex3f(1.0f,1.0f,1.0f);
            glEnd();
            
            //Bottom
            glBegin(GL_POLYGON);
            glColor3f(1.0f,0.0f,1.0f);
            glVertex3f(1.0f,-1.0f,1.0f);
            glVertex3f(-1.0f,-1.0f,1.0f);
            glVertex3f(-1.0f,-1.0f,-1.0f);
            glVertex3f(1.0f,-1.0f,-1.0f);
            glEnd();
            
            //Front
            glBegin(GL_POLYGON);
            glColor3f(0.0f,0.0f,1.0f);
            glVertex3f(1.0f,1.0f,1.0f);
            glVertex3f(-1.0f,1.0f,1.0f);
            glVertex3f(-1.0f,-1.0f,1.0f);
            glVertex3f(1.0f,-1.0f,1.0f);
            glEnd();
            
            //Back
            glBegin(GL_POLYGON);
            glColor3f(1.0f,1.0f,0.0f);
            glVertex3f(1.0f,-1.0f,-1.0f);
            glVertex3f(-1.0f,-1.0f,-1.0f);
            glVertex3f(-1.0f,1.0f,-1.0f);
            glVertex3f(1.0f,1.0f,-1.0f);
            glEnd();
            
            //Left
            glBegin(GL_POLYGON);
            glColor3f(0.0f,1.0f,1.0f);
            glVertex3f(-1.0f,1.0f,1.0f);
            glVertex3f(-1.0f,1.0f,-1.0f);
            glVertex3f(-1.0f,-1.0f,-1.0f);
            glVertex3f(-1.0f,-1.0f,1.0f);
            glEnd();
            
            //Right
            glBegin(GL_POLYGON);
            glColor3f(1.0f,1.0f,1.0f);
            glVertex3f(1.0f,1.0f,-1.0f);
            glVertex3f(1.0f,1.0f,1.0f);
            glVertex3f(1.0f,-1.0f,1.0f);
            glVertex3f(1.0f,-1.0f,-1.0f);
            glEnd();
            
        }catch(Exception e){}
        
    }
}
