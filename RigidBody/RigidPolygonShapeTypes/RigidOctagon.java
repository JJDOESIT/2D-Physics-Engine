package it.jjdoes.PhysicsEngine.RigidBody.RigidPolygonShapeTypes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import it.jjdoes.PhysicsEngine.RigidBody.RigidPolygonShape;

public class RigidOctagon extends RigidPolygonShape {
    public RigidOctagon(Vector2 origin, Color color, float sideLength, float creationRotation, float mass, boolean moveable){
        super(origin, color, 8, sideLength, 45, creationRotation, mass, moveable);
    }
}
