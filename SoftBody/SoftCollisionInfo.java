package it.jjdoes.PhysicsEngine.SoftBody;

import com.badlogic.gdx.math.Vector2;

public class SoftCollisionInfo {
    private boolean update;
    private float depth;
    private Vector2 normal;
    private Vector2 closestPoint;
    private int otherEdgeIndex;

    // Constructor
    public SoftCollisionInfo(){
        this.update = false;
        this.depth = 0;
        this.normal = new Vector2();
        this.closestPoint = new Vector2();
        this.otherEdgeIndex = 0;
    }

    // Reset collision info
    public void reset(){
        this.update = false;
        this.depth = 0;
        this.normal = new Vector2();
        this.closestPoint = new Vector2();
        this.otherEdgeIndex = 0;
    }

    // Set the update info
    public void setUpdate(boolean update){
        this.update = update;
    }

    // Get the update info
    public boolean getUpdate(){
        return this.update;
    }

    // Set the depth
    public void setDepth(float depth){
        this.depth = depth;
    }

    // Get the depth
    public float getDepth(){
        return this.depth;
    }

    // Set the normal
    public void setNormal(Vector2 normal){
        this.normal.set(normal);
    }

    // Get the normal
    public Vector2 getNormal(){
        return this.normal.cpy();
    }

    // Set the closest point
    public void setClosestPoint(Vector2 closestPoint){
        this.closestPoint.set(closestPoint);
    }

    // Get the closest point
    public Vector2 getClosestPoint(){
        return this.closestPoint;
    }

    // Set the other edge index
    public void setOtherEdge(int edgeIndex){
        this.otherEdgeIndex = edgeIndex;
    }

    // Get the other edge index
    public int getOtherEdge(){
        return this.otherEdgeIndex;
    }
}
