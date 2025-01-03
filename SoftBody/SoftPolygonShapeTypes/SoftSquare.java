package it.jjdoes.PhysicsEngine.SoftBody.SoftPolygonShapeTypes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import it.jjdoes.PhysicsEngine.SoftBody.SoftPolygonShape;
import it.jjdoes.PhysicsEngine.SoftBody.Spring;

public class SoftSquare extends SoftPolygonShape {
    public SoftSquare(Vector2 origin, Color color, float sideLength, float creationRotation, float mass, boolean moveable, float springLength, float springStrength){
        super(origin, color, 4, sideLength, 90, creationRotation, mass, moveable, springLength, springStrength);
        Spring spring1 = new Spring(super.getPoints().get(0), super.getPoints().get(2), super.getPoints().get(0).getOrigin().dst(super.getPoints().get(2).getOrigin()), springStrength);
        Spring spring2 = new Spring(super.getPoints().get(1), super.getPoints().get(3), super.getPoints().get(1).getOrigin().dst(super.getPoints().get(3).getOrigin()), springStrength);

//        super.addSpring(spring1);
//        super.addSpring(spring2);
    }
}
