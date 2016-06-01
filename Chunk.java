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

import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

//Class: Chunk
//Purpose: Instance of the world for the program
public class Chunk {
    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;
    static Block[][][] Blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    static int StartX, StartY, StartZ;
    private Random r;
    private int VBOTextureHandle;
    private Texture texture;
    
    //Method: Chunk (Constructor)
    //Purpose: Instantiate the chunk object
    public Chunk(int startX, int startY, int startZ) {
    	try{
    		texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("terrain.png"));
    	}
    	catch(Exception e) {
    		System.out.print("Terrain image not found.");
    	}
        r= new Random();
        System.out.println();
        Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_SIZE; y++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {
                    Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
                    Blocks[x][y][z].setCoords((float)x,(float)y,(float)z);
                    Blocks[x][y][z].SetActive(false);
                }
            }
        }
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers(); 
        StartX = startX;
        StartY = startY;
        StartZ = startZ;
        rebuildMesh(startX, startY, startZ);
    }
    
    //Method: render
    //Purpose: renders the chunk into the world
    public void render(){
        glPushMatrix();
            glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
            glVertexPointer(3, GL_FLOAT,0,0L);
            glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
            glColorPointer(3,GL_FLOAT,0,0L);
            glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
            glBindTexture(GL_TEXTURE_2D, 1);
            glTexCoordPointer(2,GL_FLOAT,0,0L);
            glDrawArrays(GL_QUADS,0,CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE*24);
            glPopMatrix();
    }

    //Method: rebuildMesh
    //Purpose: creates all of the blocks and their placement
     public void rebuildMesh(float startX, float startY, float startZ) {
        r = new Random();
        float min = 0.0f;
        float max = 0.15f;
        SimplexNoise noise = new SimplexNoise(30,r.nextFloat()*(max-min)+min,r.nextInt());        
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE *CHUNK_SIZE)* 6 * 12);
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE *CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData =
        BufferUtils.createFloatBuffer((CHUNK_SIZE* CHUNK_SIZE *CHUNK_SIZE) * 6 * 12);
        int a = 0;
        int posX=0, posZ=0, posY=0;
        posX = r.nextInt(CHUNK_SIZE-8);
        posZ = r.nextInt(CHUNK_SIZE-8);
        
        for (float x = 0; x < CHUNK_SIZE; x += 1) {
            for (float z = 0; z < CHUNK_SIZE; z += 1) {
                double height = (startY + (int)(7*(1+noise.getNoise((int)x,(int)z))*CUBE_LENGTH));
                for(float y = 0; y < height; y++){
                if(y < 3)
                    Blocks[(int)(x)][(int) (y)][(int) (z)].SetID(5);
                else if(y < height-3)
                    Blocks[(int)(x)][(int) (y)][(int) (z)].SetID(4);
                else if(y < height-1)
                    Blocks[(int)(x)][(int) (y)][(int) (z)].SetID(3);
                else{
                    if(x >= posX && x<=posX+5 && (z == posZ||z==posZ+5)){
                       Blocks[(int)(x)][(int) (y)][(int) (z)].SetID(1);
                    }else if(z >= posZ && z<=posZ+5 && (x == posX||x==posX+5)){
                       Blocks[(int)(x)][(int) (y)][(int) (z)].SetID(1);
                    }else if(x>=posX+1 && x<=posX+4 && z>=posZ+1 && z<=posZ+4){
                       Blocks[(int)(x)][(int) (y)][(int) (z)].SetID(2);
                    }else{
                       Blocks[(int)(x)][(int) (y)][(int) (z)].SetID(0);
                    }
                }
                VertexPositionData.put(createCube((float) (startX + x* CUBE_LENGTH),(float)(startY + y*CUBE_LENGTH),(float) (startZ + z *CUBE_LENGTH)));
                VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks[(int) x][(int) y][(int) z])));
                VertexTextureData.put(createTexCube((float) 0, (float) 0,Blocks[(int)(x)][(int) (y)][(int) (z)]));
                Blocks[(int)x][(int)y][(int)z].SetActive(true);
                }
            }
        }
        
        for (int j = 0; j < r.nextInt(15); j++) {
             int treeX = r.nextInt(30);
             int treeZ = r.nextInt(30);
             float treeY = (startY + (int) (7 * (1 + noise.getNoise((int) treeX, (int) treeZ)) * CUBE_LENGTH));
             for (int i = 0; i < r.nextInt(15); i++) {
                 Blocks[(int) (treeX)][(int) (treeY)][(int) (treeZ)].SetID(6);
                 VertexPositionData.put(createCube((float) (startX + treeX * CUBE_LENGTH), (float) (startY + treeY * CUBE_LENGTH), (float) (startZ + treeZ * CUBE_LENGTH)));
                 VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks[(int) treeX][(int) treeY][(int) treeZ])));
                 VertexTextureData.put(createTexCube((float) 0, (float) 0, Blocks[(int) (treeX)][(int) (treeY)][(int) (treeZ)]));
                 Blocks[(int) treeX][(int) treeY][(int) treeZ].SetActive(true);
                 treeY++;
             }

         }
        VertexTextureData.flip();
        VertexColorData.flip();
        VertexPositionData.flip();
        glBindBuffer(GL_ARRAY_BUFFER,VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER,VertexPositionData,GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER,VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER,
        VertexColorData,GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }   
     
    //Method: createCubeVertexCol
    //Purpose: Sets and returns the colors for the cube
    private float[] createCubeVertexCol(float[] CubeColorArray) {
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i %
            CubeColorArray.length];
        }   
    return cubeColors;
    }
    
    //Method: CreateCube
    //Purpose: Creates the cube object based off the given info
    public static float[] createCube(float x, float y,float z) {
        int offset = CUBE_LENGTH / 2;
        return new float[] {
            // TOP QUAD
            x + offset, y + offset, z,
            x - offset, y + offset, z,
            x - offset, y + offset, z - CUBE_LENGTH,
            x + offset, y + offset, z - CUBE_LENGTH,
            // BOTTOM QUAD
            x + offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z,
            x + offset, y - offset, z,
            // FRONT QUAD
            x + offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            // BACK QUAD
            x + offset, y - offset, z,
            x - offset, y - offset, z,
            x - offset, y + offset, z,
            x + offset, y + offset, z,
            // LEFT QUAD
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z,
            x - offset, y - offset, z,
            x - offset, y - offset, z - CUBE_LENGTH,
            // RIGHT QUAD
            x + offset, y + offset, z,
            x + offset, y + offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z };
    }
    
    //Method: getCubeColor
    //Purpose: returns the color of the cube
    private float[] getCubeColor(Block block) {
//    switch (block.GetID()) {
//        case 1:
//            return new float[] { 0, 1, 0 };
//        case 2:
//            return new float[] { 1, 0.5f, 0 };
//        case 3:
//            return new float[] { 0, 0f, 1f };
//        }
    return new float[] { 1, 1, 1 };
    }
    
    //Method: createTexCube
    //Purpose: creates the textures for the cube
    public static float[] createTexCube(float x, float y, Block block) {
        float offset = (1024f / 16) / 1024f;
        switch (block.GetID()) {
            default:
            case 0: // Grass block texture
                return new float[]{
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 3, y + offset * 10,
                    x + offset * 2, y + offset * 10,
                    x + offset * 2, y + offset * 9,
                    x + offset * 3, y + offset * 9,
                    // TOP!
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // FRONT QUAD
                    x + offset * 3, y + offset * 0,
                    x + offset * 4, y + offset * 0,
                    x + offset * 4, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    // BACK QUAD
                    x + offset * 4, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 0,
                    x + offset * 4, y + offset * 0,
                    // LEFT QUAD
                    x + offset * 3, y + offset * 0,
                    x + offset * 4, y + offset * 0,
                    x + offset * 4, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    // RIGHT QUAD
                    x + offset * 3, y + offset * 0,
                    x + offset * 4, y + offset * 0,
                    x + offset * 4, y + offset * 1,
                    x + offset * 3, y + offset * 1};
            case 1: // Sand block texture
                return new float[]{
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    // TOP!
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    // FRONT QUAD 
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    // BACK QUAD
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    // LEFT QUAD
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    // RIGHT QUAD
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2};
            case 2: // Water block texture
                return new float[]{
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 1, y + offset * 12,
                    x + offset * 2, y + offset * 12,
                    x + offset * 2, y + offset * 11,
                    x + offset * 1, y + offset * 11,
                    // TOP!
                    x + offset * 1, y + offset * 12,
                    x + offset * 2, y + offset * 12,
                    x + offset * 2, y + offset * 11,
                    x + offset * 1, y + offset * 11,
                    // FRONT QUAD 
                    x + offset * 1, y + offset * 12,
                    x + offset * 2, y + offset * 12,
                    x + offset * 2, y + offset * 11,
                    x + offset * 1, y + offset * 11,
                    // BACK QUAD
                    x + offset * 1, y + offset * 12,
                    x + offset * 2, y + offset * 12,
                    x + offset * 2, y + offset * 11,
                    x + offset * 1, y + offset * 11,
                    // LEFT QUAD
                    x + offset * 1, y + offset * 12,
                    x + offset * 2, y + offset * 12,
                    x + offset * 2, y + offset * 11,
                    x + offset * 1, y + offset * 11,
                    // RIGHT QUAD
                    x + offset * 1, y + offset * 12,
                    x + offset * 2, y + offset * 12,
                    x + offset * 2, y + offset * 11,
                    x + offset * 1, y + offset * 11};
            case 3: // Dirt block texture
                return new float[]{
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    // TOP!
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    // FRONT QUAD 
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    // BACK QUAD
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    // LEFT QUAD
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    // RIGHT QUAD
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 0,
                    x + offset * 2, y + offset * 0};
            case 4: // Stone block texture
                return new float[]{
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 1, y + offset * 0,
                    // TOP!
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 1, y + offset * 0,
                    // FRONT QUAD 
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 1, y + offset * 0,
                    // BACK QUAD
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 1, y + offset * 0,
                    // LEFT QUAD
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 1, y + offset * 0,
                    // RIGHT QUAD
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 1, y + offset * 0};
            case 5: // Bedrock block texture
                return new float[]{
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    // TOP!
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    // FRONT QUAD 
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    // BACK QUAD
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    // LEFT QUAD
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    // RIGHT QUAD
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2};
                        case 6: // Wood block texture
                return new float[]{
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 6, y + offset * 2,
                    x + offset * 5, y + offset * 2,
                    x + offset * 5, y + offset * 1,
                    x + offset * 6, y + offset * 1,
                    // TOP!
                    x + offset * 6, y + offset * 2,
                    x + offset * 5, y + offset * 2,
                    x + offset * 5, y + offset * 1,
                    x + offset * 6, y + offset * 1,
                    // FRONT QUAD
                    x + offset * 5, y + offset * 2,
                    x + offset * 4, y + offset * 2,
                    x + offset * 4, y + offset * 1,
                    x + offset * 5, y + offset * 1,
                    // BACK QUAD
                    x + offset * 5, y + offset * 2,
                    x + offset * 4, y + offset * 2,
                    x + offset * 4, y + offset * 1,
                    x + offset * 5, y + offset * 1,
                    // LEFT QUAD
                    x + offset * 5, y + offset * 2,
                    x + offset * 4, y + offset * 2,
                    x + offset * 4, y + offset * 1,
                    x + offset * 5, y + offset * 1,
                    // RIGHT QUAD
                    x + offset * 5, y + offset * 2,
                    x + offset * 4, y + offset * 2,
                    x + offset * 4, y + offset * 1,
                    x + offset * 5, y + offset * 1};
            case 7: // Leaves block texture
                return new float[]{
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 6, y + offset * 9,
                    x + offset * 5, y + offset * 9,
                    x + offset * 5, y + offset * 8,
                    x + offset * 6, y + offset * 8,
                    // TOP!
                    x + offset * 6, y + offset * 9,
                    x + offset * 5, y + offset * 9,
                    x + offset * 5, y + offset * 8,
                    x + offset * 6, y + offset * 8, 
                    // FRONT QUAD
                    x + offset * 6, y + offset * 9,
                    x + offset * 5, y + offset * 9,
                    x + offset * 5, y + offset * 8,
                    x + offset * 6, y + offset * 8,
                    // BACK QUAD
                    x + offset * 6, y + offset * 9,
                    x + offset * 5, y + offset * 9,
                    x + offset * 5, y + offset * 8,
                    x + offset * 6, y + offset * 8,
                    // LEFT QUAD
                    x + offset * 6, y + offset * 9,
                    x + offset * 5, y + offset * 9,
                    x + offset * 5, y + offset * 8,
                    x + offset * 6, y + offset * 8,
                    // RIGHT QUAD
                    x + offset * 6, y + offset * 9,
                    x + offset * 5, y + offset * 9,
                    x + offset * 5, y + offset * 8,
                    x + offset * 6, y + offset * 8};

        }
    }
}