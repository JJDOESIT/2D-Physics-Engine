package it.jjdoes.PhysicsEngine.SoftBody.SoftPolygonShapeTypes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

import it.jjdoes.PhysicsEngine.SoftBody.SoftPoint;
import it.jjdoes.PhysicsEngine.SoftBody.SoftPolygonShape;
import it.jjdoes.PhysicsEngine.SoftBody.Spring;

public class SoftOctagon extends SoftPolygonShape {
    public SoftOctagon(Vector2 origin, Color color, float sideLength, float creationRotation, float mass, boolean moveable, float springLength){
        super(origin, color, 8, sideLength, 45, creationRotation, mass, moveable, springLength);
    }

    @Override
    // Initialize inner springs
    public void initializeInnerSprings(){
        ArrayList<SoftPoint> points = super.getPoints();
        ArrayList<SoftPoint> desiredPoints = super.getMatcher().getPoints();

        Spring spring1 = new Spring(points.get(0), points.get(2), desiredPoints.get(0).getOrigin().dst(desiredPoints.get(2).getOrigin()));
        Spring spring2 = new Spring(points.get(1), points.get(3), desiredPoints.get(1).getOrigin().dst(desiredPoints.get(3).getOrigin()));
        Spring spring3 = new Spring(points.get(2), points.get(4), desiredPoints.get(2).getOrigin().dst(desiredPoints.get(4).getOrigin()));
        Spring spring4 = new Spring(points.get(3), points.get(5), desiredPoints.get(3).getOrigin().dst(desiredPoints.get(5).getOrigin()));
        Spring spring5 = new Spring(points.get(4), points.get(6), desiredPoints.get(4).getOrigin().dst(desiredPoints.get(6).getOrigin()));
        Spring spring6 = new Spring(points.get(5), points.get(7), desiredPoints.get(5).getOrigin().dst(desiredPoints.get(7).getOrigin()));
        Spring spring7 = new Spring(points.get(6), points.get(0), desiredPoints.get(6).getOrigin().dst(desiredPoints.get(0).getOrigin()));
        Spring spring8 = new Spring(points.get(7), points.get(1), desiredPoints.get(7).getOrigin().dst(desiredPoints.get(1).getOrigin()));
        Spring spring9 = new Spring(points.get(0), points.get(4), desiredPoints.get(0).getOrigin().dst(desiredPoints.get(4).getOrigin()));
        Spring spring10 = new Spring(points.get(6), points.get(2), desiredPoints.get(6).getOrigin().dst(desiredPoints.get(2).getOrigin()));

        super.addSpring(spring1);
        super.addSpring(spring2);
        super.addSpring(spring3);
        super.addSpring(spring4);
        super.addSpring(spring5);
        super.addSpring(spring6);
        super.addSpring(spring7);
        super.addSpring(spring8);
        super.addSpring(spring9);
        super.addSpring(spring10);
    }
}
