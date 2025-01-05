package it.jjdoes.PhysicsEngine;

import com.badlogic.gdx.math.Vector2;

public class AABB {
    private Vector2 min;
    private Vector2 max;
    public AABB(Vector2 min, Vector2 max){
        this.min = min;
        this.max = max;
    }

    public AABB(float minX, float minY, float maxX, float maxY){
        this.min = new Vector2(minX, minY);
        this.max = new Vector2(maxX, maxY);
    }

    // Set the min vector
    public void setMin(Vector2 min){
        this.min.set(min);
    }

    // Return the min vector
    public Vector2 getMin(){
        return this.min.cpy();
    }

    // Set the max vector
    public void setMax(Vector2 max){
        this.max.set(max);
    }

    // Return the max vector
    public Vector2 getMax(){
        return this.max.cpy();
    }
}
