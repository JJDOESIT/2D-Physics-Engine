package it.jjdoes.PhysicsEngine.PolygonShapeTypes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import it.jjdoes.PhysicsEngine.RigidBody.RigidPolygonShape;

public class Pentagon extends RigidPolygonShape {
    public Pentagon(Vector2 origin, Color color, float sideLength, float creationRotation, float mass, boolean moveable){
        super(origin, color, 5, sideLength, 72, creationRotation, mass, moveable);
    }
}
