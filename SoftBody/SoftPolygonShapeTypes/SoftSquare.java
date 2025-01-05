package it.jjdoes.PhysicsEngine.SoftBody.SoftPolygonShapeTypes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

import it.jjdoes.PhysicsEngine.SoftBody.SoftPoint;
import it.jjdoes.PhysicsEngine.SoftBody.SoftPolygonShape;
import it.jjdoes.PhysicsEngine.SoftBody.Spring;

public class SoftSquare extends SoftPolygonShape {
    public SoftSquare(Vector2 origin, Color color, float sideLength, float creationRotation, float mass, boolean moveable, float springLength) {
        super(origin, color, 4, sideLength, 90, creationRotation, mass, moveable, springLength);
    }

    @Override
    public void initializeInnerSprings(){
        ArrayList<SoftPoint> points = super.getPoints();
        ArrayList<SoftPoint> desiredPoints = super.getMatcher().getPoints();

        Spring spring1 = new Spring(points.get(0), points.get(2), desiredPoints.get(0).getOrigin().dst(desiredPoints.get(2).getOrigin()));
        Spring spring2 = new Spring(points.get(1), points.get(3), desiredPoints.get(1).getOrigin().dst(desiredPoints.get(3).getOrigin()));

        super.addSpring(spring1);
        super.addSpring(spring2);
    }
}
