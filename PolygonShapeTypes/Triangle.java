package it.jjdoes.PhysicsEngine.PolygonShapeTypes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import it.jjdoes.PhysicsEngine.RigidBody.RigidPolygonShape;

public class Triangle extends RigidPolygonShape {
    public Triangle(Vector2 origin, Color color, float sideLength, float creationRotation, float mass, boolean moveable){
        super(origin, color, 3, sideLength, 120, creationRotation, mass, moveable);
    }
}
