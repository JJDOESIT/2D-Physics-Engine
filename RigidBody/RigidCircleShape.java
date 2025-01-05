package it.jjdoes.PhysicsEngine.RigidBody;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import it.jjdoes.PhysicsEngine.AABB;

public class RigidCircleShape extends RigidShape {
    private float radius;

    // Constructor
    public RigidCircleShape(Vector2 origin, Color color, float radius, float mass, boolean moveable) {
        // Call 'Shape' class constructor
        super(1, origin, color, mass, moveable);
        this.radius = radius;
        this.calculateInertia();
    }

    // Return the radius of the circle
    public float getRadius(){
        return this.radius;
    }

    @Override
    // Return the AABB of the circle shape
    public AABB getAABB(){
        float minX;
        float maxX;
        float minY;
        float maxY;

        Vector2 origin = super.getOrigin();

        minX = origin.x - this.radius;
        maxX = origin.x + this.radius;
        minY = origin.y - this.radius;
        maxY = origin.y + this.radius;

        return new AABB(minX, minY, maxX, maxY);
    }

    // Calculate the inertia for a circle (m * r^2)
    private void calculateInertia(){
        super.setInertia(super.getMass() * (this.radius * this.radius));
    }

    // Draw function
    public void draw(ShapeRenderer shapeRenderer){
        shapeRenderer.setColor(super.getColor());
        Vector2 origin = super.getOrigin();
        shapeRenderer.circle(origin.x, origin.y, this.getRadius());
    }
}
