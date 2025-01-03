package it.jjdoes.PhysicsEngine.RigidBody;

import com.badlogic.gdx.math.Vector2;

public interface RigidPolygonShapePhysics {
    public float[] getVerticesArray();
    public Vector2[] getVerticesVector();
    public Vector2[][] getEdges();
    public Vector2[] getVectors(Vector2[][] edges);
    public void calculateInertia();
}
