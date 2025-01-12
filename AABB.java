package it.jjdoes.PhysicsEngine;

import com.badlogic.gdx.math.Vector2;

public class AABB {
    private final Vector2 min;
    private final Vector2 max;

    // Constructor
    public AABB(float minX, float minY, float maxX, float maxY){
        this.min = new Vector2(minX, minY);
        this.max = new Vector2(maxX, maxY);
    }

    // Return the min vector
    public Vector2 getMin(){
        return this.min.cpy();
    }

    // Return the max vector
    public Vector2 getMax(){
        return this.max.cpy();
    }
}
