package it.jjdoes.PhysicsEngine.RigidBody;

import com.badlogic.gdx.math.Vector2;

public interface RigidShapePhysics {
    public void setOrigin(Vector2 origin);
    public void moveOrigin(Vector2 distance);
    public Vector2 getOrigin();
    public float getMass();
    public float getInverseMass();
    public void setVelocity(Vector2 velocity);
    public Vector2 getVelocity();
    public void applyVelocity(float time);
    public void addResistance(float resistance);
    public void addGravity(float gravity, float time);
    public void setInertia(float inertia);
    public float getInertia();
    public float getInverseInertia();
    public float getAngle();
    public void setAngularVelocity(float angularVelocity);
    public float getAngularVelocity();
    public void applyAngularVelocity(float time);

}
