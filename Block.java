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

//Class: Block
//Purpose: Creates an instance of a block object
public class Block {
    
    private boolean IsActive;
    private BlockType Type;
    private float x,y,z;
    
    //Enum: BlockType
    //Purpose: Details what type of block that the block is
    public enum BlockType{
        BlockType_Grass(0),
        BlockType_Sand(1),
        BlockType_Water(2),
        BlockType_Dirt(3),
        BlockType_Stone(4),
        BlockType_Bedrock(5),
        BlockType_Wood(6),
        BlockType_Leaves(7);
        private int BlockID;
        
        //Method: BlockType (Constructor)
        //Purpose: Initialies BlockType based off given Integer
        BlockType(int i) {
            BlockID = i;
        }
        //Method: GetID
        //Purpose: Returns the ID of the block
        public int GetID(){
            return BlockID;
        }
        //Method: SetId
        //Purpose: Sets the ID of the block
        public void SetID(int i){
            BlockID = i;
        }
    }
    
    //Method: Block (Constructor)
    //Purpose: Creates a Block of the given BlockType
    public Block(BlockType type){
    Type= type;
    }
    
    //Method: setCoords
    //Purpose: sets the x, y, z coordinates of the Block
    public void setCoords(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    //Method: getX
    //Purpose: retrieves the x-coordinate
    public float getX(){
        return x;
    }
    
    //Method: getY
    //Purpose: retrieves the y-coordinate
    public float getY(){
        return y;
    }
    
    //Method: getZ
    //Purpose: retrieves the z-coordinate
    public float getZ(){
        return z;
    }
    
    //Method: IsActive
    //Purpose: returns the IsActive boolean
    public boolean IsActive() {
        return IsActive;
    }
    
    //Method: SetActive
    //Purpose: sets the IsActive boolean
    public void SetActive(boolean active){
        IsActive=active;
    }
    
    //Method: GetID
    //Purpose: gets the Type ID of the block
    public int GetID(){
        return Type.GetID();
    }
    
    //Method: SetID
    //Purpose: sets the Type ID of the block
    public void SetID(int i){
        Type.SetID(i);
    }
}