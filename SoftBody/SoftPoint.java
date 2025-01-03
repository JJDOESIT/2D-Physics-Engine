package it.jjdoes.PhysicsEngine.SoftBody;

import com.badlogic.gdx.math.Vector2;

public class SoftPoint {
    private Vector2 velocity;
    private Vector2 force;
    private Vector2 origin;
    float mass;

    public SoftPoint(Vector2 origin, float mass){
        this.origin = origin;
        this.mass = mass;
        this.velocity = new Vector2();
        this.force = new Vector2();
    }

    public Vector2 getForce(){
        return this.force.cpy();
    }

    public void offsetForce(Vector2 force){
        this.force.x += force.x;
        this.force.y += force.y;
    }

    public void offsetForce(float fx, float fy){
        this.force.x += fx;
        this.force.y += fy;
    }

    public void resetForce(){
        this.force.set(0,0);
    }

    public float getInverseMass(){
        return 1 / this.mass;
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

    public void moveOrigin(Vector2 offset){
        this.origin.x += offset.x;
        this.origin.y += offset.y;
    }

    public void applyGravity(float gravity, float time){
        this.velocity.y += gravity * time;
    }

    public void integrate(float time){
        // Calculate the change in velocity (acceleration * time step)
        float dvx = (this.force.x) / this.mass * time; // Assuming force is already accumulated
        float dvy = (this.force.y) / this.mass * time; // Assuming force is already accumulated

        // Update velocity using half of the change in velocity (semi-implicit Euler method)
        this.velocity.x += dvx / 2.0f;
        this.velocity.y += dvy / 2.0f;

        // Calculate the change in position based on updated velocity
        double deltaRotationX = this.velocity.x * time;
        double deltaRotationY = this.velocity.y * time;

        // Update position using the new velocity
        this.origin.x += (float) deltaRotationX;
        this.origin.y += (float) deltaRotationY;

        this.force.set(0,0);

    }

}
