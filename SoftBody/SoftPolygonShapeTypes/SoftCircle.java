package it.jjdoes.PhysicsEngine.SoftBody.SoftPolygonShapeTypes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import it.jjdoes.PhysicsEngine.SoftBody.SoftPolygonShape;

public class SoftCircle extends SoftPolygonShape {
    public SoftCircle(Vector2 origin, Color color, float sideLength, float creationRotation, float mass, boolean moveable, float springLength, float springStrength) {
        super(origin, color, 20, sideLength, 18, creationRotation, mass, moveable, springLength, springStrength);
    }
}
