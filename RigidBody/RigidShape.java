package it.jjdoes.PhysicsEngine.RigidBody;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class RigidShape implements RigidShapePhysics {
    private final int type;
    private Vector2 origin;
    private Color color;
    private final Vector2 velocity;
    private float angle;
    private float angularVelocity;
    private final float mass;
    private float inertia;
    private final boolean moveable;

    public RigidShape(int type, Vector2 origin, Color color, float mass, boolean moveable){
        this.type = type;
        this.origin = origin;
        this.color = color;
        velocity = new Vector2(0,0);
        this.angularVelocity = 0;
        this.angle = 0;
        this.mass = mass;
        this.moveable = moveable;
    }

    // Return the type of the shape
    public int getType(){
        return this.type;
    }

    // Returns whether the shape is static or not
    public boolean isMoveable(){
        return this.moveable;
    }

    // Set the color of the shape
    public void setColor(Color color){
        this.color = color;
    }

    // Return the color of the shape
    public Color getColor(){
        return this.color;
    }

    public AABB getAABB(){
        return new AABB(0,0,0,0);
    }

    @Override
    // Set the shapes origin to the given origin
    public void setOrigin(Vector2 origin){
        this.origin = origin;
    }

    @Override
    // Move the origin by the given distance
    public void moveOrigin(Vector2 distance){
        this.origin.x += distance.x;
        this.origin.y += distance.y;
    }

    @Override
    // Return the origin of the shape
    public Vector2 getOrigin(){
        return this.origin.cpy();
    }

    @Override
    // Return the mass of the shape
    public float getMass(){
        return this.mass;
    }

    @Override
    // Return the inverse mass of the shape
    public float getInverseMass(){
        // If the shape is moveable, return 1 / mass
        if (this.isMoveable()){
            return 1 / this.getMass();
        }
        // Else return 0
        return 0;
    }

    @Override
    // Set the velocity to the given velocity
    public void setVelocity(Vector2 velocity){
        // If the shape is not moveable, don't apply any velocity
        if (!this.moveable){
            return;
        }
        // Set the velocity
        this.velocity.x = velocity.x;
        this.velocity.y = velocity.y;
    }

    @Override
    // Return the velocity
    public Vector2 getVelocity(){
        return this.velocity.cpy();
    }

    @Override
    // Apply velocity to the origin coordinates
    public void applyVelocity(float time){
        origin.y += getVelocity().y * time;
        origin.x += getVelocity().x * time;
    }

    @Override
    // Apply resistance to the velocity
    public void addResistance(float resistance){
        // Get the velocity
        Vector2 velocity = this.getVelocity();

        // Multiply the velocity by the resistance constant
        velocity.x *= resistance;
        velocity.y *= resistance;

        // Set the velocity
        this.setVelocity(velocity);

        this.angularVelocity *= resistance;
    }

    @Override
    // Apply gravity to the shape
    public void addGravity(float gravity, float time){
        // Get the velocity
        Vector2 velocity = this.getVelocity();
        // Multiply the velocity by the GRAVITY_CONSTANT
        velocity.y += gravity * time;
        // Set the velocity
        this.setVelocity(velocity);
    }

    @Override
    // Set the inertia of the shape
    public void setInertia(float inertia){
        this.inertia = inertia;
    }

    @Override
    // Return the inertia of the shape
    public float getInertia(){
        return this.inertia;
    }

    @Override
    // Return the inverse mass of the shape
    public float getInverseInertia() {
        // If the shape is moveable, return 1 / mass
        if (this.isMoveable()) {
            return 1 / this.getInertia();
        }
        // Else return 0
        return 0;
    }

    @Override
    // Return the current rotation of the shape
    public float getAngle(){
        return this.angle;
    }

    @Override
    // Set the angular velocity
    public void setAngularVelocity(float angularVelocity){
        // If the shape is not moveable, don't apply any angular velocity
        if (!this.moveable){
            return;
        }
        this.angularVelocity = angularVelocity;
    }

    @Override
    // Return the angular velocity of the shape
    public float getAngularVelocity(){
        return this.angularVelocity;
    }

    @Override
    // Add the angularVelocity to the current rotation
    public void applyAngularVelocity(float time) {
        // Convert from radians to degrees
        this.angle += (float) Math.toDegrees(this.angularVelocity) * time;
        // Clamp the angle from -360 to 360
        if (this.angle > 360 || this.angle < -360) {
            this.angle %= 360;
        }
    }
}
