package it.jjdoes.PhysicsEngine.SoftBody;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

import it.jjdoes.PhysicsEngine.AABB;

public interface SoftPolygonShapePhysics {
    public void initializeOuterSprings();
    public void initializeInnerSprings();
    public void removeInnerSprings();
    public void initializeMatcher();
    public void integrate(float time);
    public void calculateResistance(float resistance);
    public void calculateGravity(float gravity, float time);
    public void calculateSpringForce(float strength, float dampener);
    public void calculatePressure(float pressure);
    public SoftPolygonShape getMatcher();
    public void setDislodged(boolean dislodge);
    public boolean isDislodged();
    public void setDislodgeAmount(Vector2 amount);
    public Vector2 getDislodgeAmount();
    public void addSpring(Spring spring);
    public ArrayList<SoftPoint> getPoints();
    public Vector2 getOrigin();
    public Vector2[] getVerticesVector();
    public Vector2[][] getSpringsEdges();
    public Vector2[][] getEdges();
    public Vector2[] getVectors(Vector2[][] edges);
    public AABB getAABB();
    public void offsetVelocity(Vector2 velocity);
    public void offsetAll(Vector2 offset);
    public void rotateAll(float angle);
    public float getAngle(SoftPolygonShape shapeTwo);
}
