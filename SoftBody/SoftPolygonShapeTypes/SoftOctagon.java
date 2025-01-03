package it.jjdoes.PhysicsEngine.SoftBody.SoftPolygonShapeTypes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import it.jjdoes.PhysicsEngine.SoftBody.SoftPolygonShape;
import it.jjdoes.PhysicsEngine.SoftBody.Spring;

public class SoftOctagon extends SoftPolygonShape {
    public SoftOctagon(Vector2 origin, Color color, float sideLength, float creationRotation, float mass, boolean moveable, float springLength, float springStrength){
        super(origin, color, 8, sideLength, 45, creationRotation, mass, moveable, springLength, springStrength);
        Spring spring1 = new Spring(super.getPoints().get(0), super.getPoints().get(2), springLength, springStrength);
        Spring spring2 = new Spring(super.getPoints().get(1), super.getPoints().get(3), springLength, springStrength);
        Spring spring3 = new Spring(super.getPoints().get(2), super.getPoints().get(4), springLength, springStrength);
        Spring spring4 = new Spring(super.getPoints().get(3), super.getPoints().get(5), springLength, springStrength);
        Spring spring5 = new Spring(super.getPoints().get(4), super.getPoints().get(6), springLength, springStrength);
        Spring spring6 = new Spring(super.getPoints().get(5), super.getPoints().get(7), springLength, springStrength);
        Spring spring7 = new Spring(super.getPoints().get(6), super.getPoints().get(0), springLength, springStrength);
        Spring spring8 = new Spring(super.getPoints().get(7), super.getPoints().get(1), springLength, springStrength);

//        super.addSpring(spring1);
//        super.addSpring(spring2);
//        super.addSpring(spring3);
//        super.addSpring(spring4);
//        super.addSpring(spring5);
//        super.addSpring(spring6);
//        super.addSpring(spring7);
//        super.addSpring(spring8);
    }
}
