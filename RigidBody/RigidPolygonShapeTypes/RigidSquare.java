package it.jjdoes.PhysicsEngine.RigidBody.RigidPolygonShapeTypes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import it.jjdoes.PhysicsEngine.RigidBody.RigidPolygonShape;

public class RigidSquare extends RigidPolygonShape {
    public RigidSquare(Vector2 origin, Color color, float sideLength, float creationRotation, float mass, boolean moveable){
        super(origin, color, 4, sideLength, 90, creationRotation, mass, moveable);
    }
}
