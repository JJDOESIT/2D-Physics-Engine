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
        float distance = pointTwo.getOrigin().dst(pointOne.getOrigin());

        // Avoid division by zero
        if (distance != 0) {
            // Spring normal
            Vector2 springNormal = pointTwoOrigin.cpy().sub(pointOneOrigin);
            springNormal.nor();

            // Calculate the deviation from the desired length (spring force magnitude)
            float springForceMagnitude = (distance - this.length) * strength;

            // Calculate the relative velocity along the spring direction
            Vector2 relativeVelocity = pointOne.getVelocity().sub(pointTwo.getVelocity());
            float velocityAlongSpring = relativeVelocity.dot(springNormal);

            // Calculate the damping effect
            float dampingForceMagnitude = velocityAlongSpring * dampener;

            // Combine spring and damping forces into a single velocity adjustment magnitude
            float totalForceMagnitude = springForceMagnitude - dampingForceMagnitude;

            // Calculate the velocity adjustment vector (magnitude * direction)
            Vector2 velocityAdjustment = springNormal.scl(totalForceMagnitude);

            // Update the velocities of the two points
            pointOne.setVelocity(pointOne.getVelocity().add(velocityAdjustment.cpy().scl(1)));
            pointTwo.setVelocity(pointTwo.getVelocity().add(velocityAdjustment.cpy().scl(-1)));
        }
        // Calculate the normal for pressure
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

        // Calculate the pressure velocity adjustment
        float pressureVelocity = distance * pressure * (1.0f / volume);

        // Calculate the velocity adjustment vector
        Vector2 velocityAdjustment = this.normal.cpy().scl(pressureVelocity);

        // Apply the velocity adjustment to both points
        pointOne.setVelocity(pointOne.getVelocity().add(velocityAdjustment.cpy().scl(1)));
        pointTwo.setVelocity(pointTwo.getVelocity().add(velocityAdjustment.cpy().scl(1)));
    }
}
