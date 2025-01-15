package it.jjdoes.PhysicsEngine.SoftBody;

import com.badlogic.gdx.math.Vector2;

public class SoftPoint implements SoftPointPhysics {
    private Vector2 velocity;
    private final Vector2 force;
    private Vector2 origin;
    private final float mass;
    private boolean moveable;
    public SoftCollisionInfo softCollisionInfo;

    // Constructor
    public SoftPoint(Vector2 origin, float mass, boolean moveable){
        this.origin = origin;
        this.mass = mass;
        this.moveable = moveable;
        this.velocity = new Vector2();
        this.force = new Vector2();
        this.softCollisionInfo = new SoftCollisionInfo();
    }

    @Override
    // Offset the force by the given force
    public void offsetForce(Vector2 force){
        this.force.x += force.x;
        this.force.y += force.y;
    }

    @Override
    // Return the force of the point
    public Vector2 getForce(){
        return this.force.cpy();
    }

    @Override
    // Reset the force
    public void resetForce(){
        this.force.set(0,0);
    }

    @Override
    // Return the inverse mass of the point
    public float getInverseMass(){
        // If the shape is moveable
        if (moveable) {
            // Return 1 / mass
            return 1 / this.mass;
        }
        // Else return 0
        return 0;
    }

    @Override
    // Set the velocity
    public void setVelocity(Vector2 velocity){
        this.velocity = velocity;
    }

    @Override
    // Offset the velocity
    public void offsetVelocity(Vector2 velocity){
        this.velocity.x += velocity.x;
        this.velocity.y += velocity.y;
    }

    @Override
    // Get the velocity
    public Vector2 getVelocity(){
        return this.velocity.cpy();
    }

    @Override
    // Set the origin
    public void setOrigin(Vector2 origin){
        this.origin = origin;
    }

    @Override
    // Offset the origin
    public void offsetOrigin(Vector2 offset){
        this.origin.x += offset.x;
        this.origin.y += offset.y;
    }

    @Override
    // Return the origin
    public Vector2 getOrigin(){
        return this.origin.cpy();
    }

    @Override
    // Return the mass
    public float getMass(){
        // If the shape is moveable
        if (moveable) {
            // Return mass
            return this.mass;
        }
        // Else return 0
        return 0;
    }

    @Override
    // Calculate the gravity applied
    public void calculateGravity(float gravity, float time){
        this.velocity.y += gravity * time;
    }

    @Override
    // Calculate the resistance applied
    public void calculateResistance(float resistance){
        this.velocity.scl(resistance);
    }
}
