package it.jjdoes.PhysicsEngine.SoftBody.SoftPolygonShapeTypes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import it.jjdoes.PhysicsEngine.SoftBody.SoftPolygonShape;

public class SoftTriangle extends SoftPolygonShape {
    public SoftTriangle(Vector2 origin, Color color, float sideLength, float creationRotation, float mass, boolean moveable, float springLength) {
        super(origin, color, 3, sideLength, 120, creationRotation, mass, moveable, springLength);
    }

    @Override
    public void initializeInnerSprings() {
        // A triangle has no inner springs!
    }
}
