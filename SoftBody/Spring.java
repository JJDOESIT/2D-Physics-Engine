package it.jjdoes.PhysicsEngine.SoftBody;

import com.badlogic.gdx.math.Vector2;

public class Spring {
    private final SoftPoint pointOne;
    private final SoftPoint pointTwo;
    private final float length;
    private final float strength;
    private Vector2 normal;

    public Spring(SoftPoint pointOne, SoftPoint pointTwo, float length, float strength){
        this.pointOne = pointOne;
        this.pointTwo = pointTwo;
        this.length = length;
        this.strength = strength;
        this.normal = new Vector2();
    }

    public Vector2[] getSpringVector(){
        return new Vector2[] {pointOne.getOrigin(), pointTwo.getOrigin()};
    }

    public void applyForce(float strength, float dampener) {
        float x1 = pointOne.getOrigin().x;
        float y1 = pointOne.getOrigin().y;
        float x2 = pointTwo.getOrigin().x;
        float y2 = pointTwo.getOrigin().y;

        // Calculate the distance between the two points
        float r12d = (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));

        // Avoid division by zero
        if (r12d != 0) {
            // Calculate the relative velocity
            float vx12 = pointOne.getVelocity().x - pointTwo.getVelocity().x;
            float vy12 = pointOne.getVelocity().y - pointTwo.getVelocity().y;

            // Calculate the spring force
            float force = (r12d - this.length) * strength + (vx12 * (x1 - x2) + vy12 * (y1 - y2)) * dampener / r12d;

            // Calculate the force components
            float fx0 = ((x1 - x2) / r12d ) * force;
            float fy0 = ((y1 - y2) / r12d ) * force;

            // Add the force
            this.pointOne.offsetForce(-fx0, -fy0);
            this.pointTwo.offsetForce(fx0, fy0);
        }
        this.normal.x = (y2 - y1) / r12d;
        this.normal.y = (x1 - x2) / r12d;
//        // Calculate the relative distance vector
//        Vector2 relativeDistance = pointTwo.getOrigin().sub(pointOne.getOrigin());
//        float currentDistance = relativeDistance.len(); // Magnitude of the distance vector
//
//        // Calculate the deviation from the desired length
//        float forceMagnitude = (this.length - currentDistance) * this.strength;
//
//        // Calculate the angle of the force
//        float theta = (float) Math.atan2(relativeDistance.y, relativeDistance.x);
//
//        // Decompose the force into X and Y components
//        float forceX = (float) Math.cos(theta) * forceMagnitude;
//        float forceY = (float) Math.sin(theta) * forceMagnitude;
//
//        // Apply forces to the velocities of pointOne and pointTwo
//        pointOne.setVelocity(pointOne.getVelocity().add(-forceX / pointOne.getMass(), -forceY / pointOne.getMass()).scl(0.99f));
//        pointTwo.setVelocity(pointTwo.getVelocity().add(forceX / pointTwo.getMass(), forceY / pointTwo.getMass()).scl(0.99f));
    }

    public Vector2 getNormal(){
        return this.normal.cpy();
    }

    public float calculateVolume(){
        float x1 = pointOne.getOrigin().x;
        float y1 = pointOne.getOrigin().y;
        float x2 = pointTwo.getOrigin().x;
        float y2 = pointTwo.getOrigin().y;

        // Calculate the distance between the two points
        float dx = x1 - x2;
        float dy = y1 - y2;
        float r12d = (float) Math.sqrt(dx * dx + dy * dy);

        float  vT1 = (Math.abs(x1 - x2) * Math.abs(this.normal.x) * r12d) / 2.0f;
        float vT2 = (Math.abs(y1 - y2) * Math.abs(this.normal.y) * r12d) / 2.0f;

        return ((vT1 + vT2) / 2.0f);

    }

    public void calculatePressure(float volume, float pressure){
        float x1 = pointOne.getOrigin().x;
        float y1 = pointOne.getOrigin().y;
        float x2 = pointTwo.getOrigin().x;
        float y2 = pointTwo.getOrigin().y;

        // Calculate the distance between the two points
        float dx = x1 - x2;
        float dy = y1 - y2;
        float r12d = (float) Math.sqrt(dx * dx + dy * dy);

        // Calculate the pressure force
        float pressurev = r12d * pressure * (1.0f / volume);

        // Apply the force to both points
        pointOne.offsetForce(this.normal.x * pressurev, this.normal.y * pressurev);
        pointTwo.offsetForce(this.normal.x * pressurev, this.normal.y * pressurev);
    }
}
