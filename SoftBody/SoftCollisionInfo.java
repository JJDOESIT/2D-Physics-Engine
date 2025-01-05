package it.jjdoes.PhysicsEngine.SoftBody;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class SoftCollisionInfo {
    private boolean update;
    private float depth;
    private Vector2 normal;
    private Vector2 closestPoint;
    private int otherEdgeIndex;

    public SoftCollisionInfo(){
        this.update = false;
        this.depth = 0;
        this.normal = new Vector2();
        this.closestPoint = new Vector2();
        this.otherEdgeIndex = 0;
    }

    public void setUpdate(boolean update){
        this.update = update;
    }

    public boolean getUpdate(){
        return this.update;
    }

    public void setDepth(float depth){
        this.depth = depth;
    }

    public float getDepth(){
        return this.depth;
    }

    public void setNormal(Vector2 normal){
        this.normal.set(normal);
    }

    public Vector2 getNormal(){
        return this.normal.cpy();
    }

    public void setClosestPoint(Vector2 closestPoint){
        this.closestPoint.set(closestPoint);
    }

    public Vector2 getClosestPoint(){
        return this.closestPoint;
    }

    public void setOtherEdge(int edgeIndex){
        this.otherEdgeIndex = edgeIndex;
    }

    public int getOtherEdge(){
        return this.otherEdgeIndex;
    }
}
