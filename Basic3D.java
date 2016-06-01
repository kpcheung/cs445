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

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import static org.lwjgl.openal.AL10.AL_BUFFER;
import static org.lwjgl.openal.AL10.alBufferData;
import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.AL10.alGenBuffers;
import static org.lwjgl.openal.AL10.alGenSources;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.AL10.alSourceStop;
import static org.lwjgl.openal.AL10.alSourcei;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.WaveData;
import org.lwjgl.util.glu.GLU;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;

//Class: Basic3D
//Purpose: Instance of the Program
public class Basic3D {

    private DisplayMode displayMode;
    private FPCamera camera;
    private FloatBuffer lightPosition;
    private FloatBuffer whiteLight;
    static boolean w=false,a=false,s=false,d=false,up=false,down=false;
    private boolean isPlaying = false;
    
    // Method: start
    // Purpose: This method calls the create window and initGL methods to draw the scene.
    public void start() {
        try {
            createWindow();
            initGL();
            camera = new FPCamera(-30,-50,-30);
            gameLoop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //Method:initLightArrays
    //Purpose: initialies the light source for the program
    private void initLightArrays() {
        lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();
        whiteLight = BufferUtils.createFloatBuffer(4);
        whiteLight.put(1.0f).put(1.0f).put(1.0f).put(0.0f).flip();
    }

    // Method: createWindow
    // Purpose: This method sets the title and size of the window
    private void createWindow() throws Exception {
        Display.setFullscreen(false);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for (int i = 0; i < d.length; i++) {
            if (d[i].getWidth() == 640 && d[i].getHeight() == 480 && d[i].getBitsPerPixel() == 32) {
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle("CS 445 Checkpoint 2");
        Display.create();
        AL.create();
    }

    // Method: initGL
    // Purpose: This method sets various options for openGL
    private void initGL() {
        initLightArrays();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition); //sets our light’s position
        glLight(GL_LIGHT0, GL_SPECULAR, whiteLight);//sets our specular light
        glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight);//sets our diffuse light
        glLight(GL_LIGHT0, GL_AMBIENT, whiteLight);//sets our ambient light
        glEnable(GL_LIGHTING);//enables our lighting
        glEnable(GL_LIGHT0);//enables light0
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GLU.gluPerspective(100.0f, (float) displayMode.getWidth() / (float) displayMode.getHeight(), 0.1f, 300.0f);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
    }
    
    
    // Method: gameLoop
    // Purpose: This method contains the controls for the camera and calls the render method.
    //          It also helps check for the collision between objects
    //			It also intiates the music to be played in the OpenGL Window
    public void gameLoop() throws FileNotFoundException{
        float dx = 0.0f;
        float dy = 0.0f;
        float dt = 0.0f; //Length of the frame.
        float lastTime = 0.0f; //When the last frame occured
        long time = 0;
        float mouseSensitivity = 0.09f;
        float movementSpeed = .35f;
        Mouse.setGrabbed(true);
        
        //Music
        WaveData data = WaveData.create(new BufferedInputStream(new FileInputStream("03-calm-3.wav")));
        int buffer = alGenBuffers();
        alBufferData(buffer, data.format, data.data, data.samplerate);
        data.dispose();
        int source = alGenSources();
        alSourcei(source, AL_BUFFER, buffer);
        
        
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
            time = Sys.getTime();
            lastTime = time;
            dx = Mouse.getDX();
            dy = Mouse.getDY();
            camera.yaw(dx * mouseSensitivity);
            camera.pitch(dy * mouseSensitivity);
            if (Keyboard.isKeyDown(Keyboard.KEY_M)) {
            	if(isPlaying == false) {
					alSourcePlay(source);
                	isPlaying = true;
                }
                else {
					alSourceStop(source);
                	isPlaying = false;
                }
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP)){
                if(w!=true){
                    s=false;
                    boolean check = camera.collision(movementSpeed, 0);
                    if(check)
                        camera.walkForward(movementSpeed);
                    else{
                        camera.walkBackwards(movementSpeed);
                        w=true;
                    }
                }
                System.out.println(w);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN)){
                if(s!=true){
                    w=false;
                    boolean check = camera.collision(movementSpeed, 1);
                    if(check)
                        camera.walkBackwards(movementSpeed);
                    else{
                        camera.walkForward(movementSpeed);
                        s=true;
                    }
                }
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT)){
                if(a!=true){
                    d = false;
                    boolean check = camera.collision(movementSpeed, 2);
                    if(check)
                        camera.strafeLeft(movementSpeed);
                    else{
                        camera.strafeRight(movementSpeed);
                        a=true;
                    }
                }
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){
                if(d!=true){
                    a = false;
                    boolean check = camera.collision(movementSpeed, 3);
                    if(check)
                        camera.strafeRight(movementSpeed);
                    else{
                        camera.strafeLeft(movementSpeed);
                        d = false;
                    }
                }
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
                if(up!=true){
                    down = false;
                    boolean check = camera.collision(movementSpeed, 4);
                    if(check)
                        camera.moveUp(movementSpeed);
                    else{
                        camera.moveDown(movementSpeed);
                        up = true;
                    }
                }
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
                if(down!=true){
                    up = false;
                    boolean check = camera.collision(movementSpeed, 5);
                    if(check)
                        camera.moveDown(movementSpeed);
                    else{
                        camera.moveUp(movementSpeed);
                        down = true;
                    }
                }
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_U)){
                camera = new FPCamera(-30,-50,-30);
            }
            
            
            
            
            glLoadIdentity();
            camera.lookThrough();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            camera.world.render();
            Display.update();
            Display.sync(60);
            
        }
        alDeleteBuffers(alGenBuffers());
        AL.destroy();
        Display.destroy();
    }
    

    // Method: main
    // Purpose: The start point of the program. 
    public static void main(String[] args) {
        Basic3D basic = new Basic3D();
        basic.start();
    }
}