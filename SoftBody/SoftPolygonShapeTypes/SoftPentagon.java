package it.jjdoes.PhysicsEngine.SoftBody.SoftPolygonShapeTypes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

import it.jjdoes.PhysicsEngine.SoftBody.SoftPoint;
import it.jjdoes.PhysicsEngine.SoftBody.SoftPolygonShape;
import it.jjdoes.PhysicsEngine.SoftBody.Spring;

public class SoftPentagon extends SoftPolygonShape {
    public SoftPentagon(Vector2 origin, Color color, float sideLength, float creationRotation, float mass, boolean moveable, float springLength) {
        super(origin, color, 5, sideLength, 72, creationRotation, mass, moveable, springLength);
    }

    @Override
    public void initializeInnerSprings(){
        int springStep = 2;

        ArrayList<SoftPoint> points = super.getPoints();
        ArrayList<SoftPoint> desiredPoints = super.getMatcher().getPoints();
        int numberOfPoints = points.size();

        for (int index = 0; index < numberOfPoints; index++){
            Spring spring = new Spring(points.get(index), points.get((index + springStep) % numberOfPoints), desiredPoints.get(index).getOrigin().dst(desiredPoints.get((index + springStep) % numberOfPoints).getOrigin()));
            super.addSpring(spring);
        }
    }
}
