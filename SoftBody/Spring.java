package it.jjdoes.PhysicsEngine.SoftBody;

import com.badlogic.gdx.math.Vector2;

public class Spring implements SpringPhysics {
    private final SoftPoint pointOne;
    private final SoftPoint pointTwo;
    private final float length;
    private final Vector2 normal;

    // Constructor
    public Spring(SoftPoint pointOne, SoftPoint pointTwo, float length){
        this.pointOne = pointOne;
        this.pointTwo = pointTwo;
        this.length = length;
        this.normal = new Vector2();
    }

    @Override
    // Return the edge as a Vector2 array
    public Vector2[] getEdge(){
        return new Vector2[] {pointOne.getOrigin(), pointTwo.getOrigin()};
    }

    @Override
    // Apply force to the spring so that it achieves it's natural resting length
    public void calculateSpringForce(float strength, float dampener) {
        // Get the origin of both points
        Vector2 pointOneOrigin = pointOne.getOrigin();
        Vector2 pointTwoOrigin = pointTwo.getOrigin();

        // Calculate the distance between the two points
        float distance = pointOne.getOrigin().dst(pointTwo.getOrigin());

        // Avoid division by zero
        if (distance != 0) {
            // Calculate the relative velocity
            Vector2 relativeVelocity = pointOne.getVelocity().sub(pointTwo.getVelocity());

            // Calculate the spring force
            float springForce = ((distance - this.length) * strength) + ((relativeVelocity.x * (pointOneOrigin.x - pointTwoOrigin.x) + relativeVelocity.y * (pointOneOrigin.y - pointTwoOrigin.y)) * dampener) / distance;

            // Calculate the force components
            Vector2 force = new Vector2(pointOneOrigin.cpy().sub(pointTwoOrigin)).scl(springForce / distance);

            // Add the force
            this.pointOne.offsetForce(force.cpy().scl(-1));
            this.pointTwo.offsetForce(force);
        }
        // Calculate the normal
        this.normal.set((pointTwoOrigin.y - pointOneOrigin.y) / distance, (pointOneOrigin.x - pointTwoOrigin.x) / distance);
    }

    @Override
    // Calculate the volume of the shape
    public float calculateVolume(){
        // Area using the cross product
        Vector2 edgeVector = pointTwo.getOrigin().sub(pointOne.getOrigin());
        return Math.abs(edgeVector.crs(this.normal)) / 2.0f;
    }

    @Override
    // Function to calculate the pressure of a shape given its volume and desired pressure
    public void calculatePressure(float volume, float pressure){
        // Calculate the distance between the two points
        float distance = pointOne.getOrigin().dst(pointTwo.getOrigin());

        // Calculate the pressure force
        float pressureVolume = distance * pressure * (1.0f / volume);

        // Apply the force to both points
        Vector2 force = this.normal.cpy().scl(pressureVolume);
        pointOne.offsetForce(force);
        pointTwo.offsetForce(force);
    }
}
