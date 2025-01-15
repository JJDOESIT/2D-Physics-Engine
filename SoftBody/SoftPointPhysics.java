package it.jjdoes.PhysicsEngine.SoftBody;

import com.badlogic.gdx.math.Vector2;

public interface SoftPointPhysics {
    public void offsetForce(Vector2 force);
    public Vector2 getForce();
    public void resetForce();
    public float getInverseMass();
    public void setVelocity(Vector2 velocity);
    public void offsetVelocity(Vector2 velocity);
    public Vector2 getVelocity();
    public void setOrigin(Vector2 origin);
    public void offsetOrigin(Vector2 offset);
    public Vector2 getOrigin();
    public float getMass();
    public void calculateGravity(float gravity, float time);
    public void calculateResistance(float resistance);
}
