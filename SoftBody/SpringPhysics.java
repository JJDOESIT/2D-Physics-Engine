package it.jjdoes.PhysicsEngine.SoftBody;

import com.badlogic.gdx.math.Vector2;

public interface SpringPhysics {
    public Vector2[] getEdge();
    public void calculateSpringForce(float strength, float dampener);
    public float calculateVolume();
    public void calculatePressure(float volume, float pressure);
}
