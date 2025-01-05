package it.jjdoes.PhysicsEngine.SoftBody;

import com.badlogic.gdx.math.Vector2;

public class SoftPoint {
    private Vector2 velocity;
    private final Vector2 force;
    private Vector2 origin;
    private final float mass;
    private final boolean moveable;
    public SoftCollisionInfo softCollisionInfo;

    public SoftPoint(Vector2 origin, float mass, boolean moveable){
        this.origin = origin;
        this.mass = mass;
        this.moveable = moveable;
        this.velocity = new Vector2();
        this.force = new Vector2();
        this.softCollisionInfo = new SoftCollisionInfo();
    }

    public Vector2 getForce(){
        return this.force.cpy();
    }

    public void offsetForce(Vector2 force){
        this.force.x += force.x;
        this.force.y += force.y;
    }

    public void resetForce(){
        this.force.set(0,0);
    }

    public float getInverseMass(){
        if (moveable) {
            return 1 / this.mass;
        }
        return 0;
    }

    // Offset the velocity
    public void offsetVelocity(Vector2 velocity){
        this.velocity.x += velocity.x;
        this.velocity.y += velocity.y;
    }

    // Offset the velocity
    public void offsetVelocity(float dx, float dy){
        this.velocity.x += dx;
        this.velocity.y += dy;
    }

    // Set the velocity
    public void setVelocity(Vector2 velocity){
        this.velocity = velocity;
    }

    public void setVelocity(float dx, float dy){
        this.velocity.x = dx;
        this.velocity.y = dy;
    }

    public Vector2 getVelocity(){
        return this.velocity.cpy();
    }

    public void setOrigin(Vector2 origin){
        this.origin = origin;
    }

    public void setOrigin(float x, float y){
        this.origin.x = x;
        this.origin.y = y;
    }

    public void offsetOrigin(float dx, float dy){
        this.origin.x += dx;
        this.origin.y += dy;
    }

    public Vector2 getOrigin(){
        return this.origin.cpy();
    }

    public float getMass(){
        return this.mass;
    }

    public void offsetOrigin(Vector2 offset){
        this.origin.x += offset.x;
        this.origin.y += offset.y;
    }

    public void calculateGravity(float gravity){
        this.force.y += gravity;
    }

    public void calculateResistance(float resistance){
        this.velocity.scl(resistance);
    }
}
