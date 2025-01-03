package it.jjdoes.PhysicsEngine.RigidBody;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

import it.jjdoes.PhysicsEngine.World;

public class Quadtree {
    private final Vector2 origin;
    private final float width;
    private final float height;
    private final int capacity;
    private final ArrayList<RigidShape> shapes;
    private final ArrayList<Quadtree> nodes;
    private boolean divided = false;
    private final int depth;
    private static final BitmapFont font = new BitmapFont();
    private static final int MAX_DEPTH = 5;
    private static final float TOLERANCE = 1.0f;

    // Constructor
    public Quadtree(Vector2 origin, float width, float height, int capacity, int depth){
        this.origin = origin;
        this.width = width;
        this.height = height;
        this.capacity = capacity;
        this.shapes = new ArrayList<>();
        this.nodes = new ArrayList<>();
        this.depth = depth;
    }

    // Scale the text font
    public static void scaleFont(float scale){
        font.getData().scale(scale);
    }

    // Clear the quadtree
    public void clear(){
        this.shapes.clear();
        this.nodes.clear();
        this.divided = false;
    }

    // Return the origin of the quadtree
    private Vector2 getOrigin(){
        return this.origin.cpy();
    }

    // Insert a shape into the quadtree
    public boolean insert(RigidShape shape) {
        // If the shape doesn't at least partially overlap the outer-most quadtree
        if (depth == 0 && !overlaps(shape.getAABB())) {
            return false;
        }
        // Else if the shape can't be fully contained in the quadtree
        else if (depth != 0 && !contains(shape.getAABB())) {
            return false;
        }

        // If the quadtree is not divided and the capacity is sufficient, store the shape here
        if (shapes.size() < this.capacity && !divided) {
            shapes.add(shape);
            return true;
        }

        // Subdivide only if necessary and not already divided
        if (!divided && this.depth < MAX_DEPTH) {
            this.subdivide();
        }

        // Insert into the best-fitting child node first
        for (Quadtree node : nodes) {
            if (node.insert(shape)) {
                return true;
            }
        }

        // If it doesn't fit in any child, keep it in the parent
        this.shapes.add(shape);
        return true;
    }

    // Function to subdivide once capacity has been reached
    private void subdivide(){
        Quadtree topRight = new Quadtree(this.getOrigin().add(this.width / 2, this.height / 2), this.width / 2, this.height / 2, this.capacity, this.depth + 1);
        Quadtree bottomRight = new Quadtree(this.getOrigin().add(this.width / 2, -this.height / 2), this.width / 2, this.height / 2, this.capacity, this.depth + 1);
        Quadtree bottomLeft = new Quadtree(this.getOrigin().add(-this.width / 2, -this.height / 2), this.width / 2, this.height / 2, this.capacity, this.depth + 1);
        Quadtree topLeft = new Quadtree(this.getOrigin().add(-this.width / 2, this.height / 2), this.width / 2, this.height / 2, this.capacity, this.depth + 1);

        this.nodes.add(topRight);
        this.nodes.add(bottomRight);
        this.nodes.add(bottomLeft);
        this.nodes.add(topLeft);

        this.divided = true;
    }

    // Return whether the AABB is partially contained in the quadtree grid
    public boolean overlaps(AABB aabb){
        Vector2 bottomLeft = aabb.getMin();
        Vector2 topRight = aabb.getMax();

        Vector2 bottomRight = new Vector2(topRight.x, bottomLeft.y);
        Vector2 topLeft = new Vector2(bottomLeft.x, topRight.y);
        return contains(bottomLeft) || contains(topRight) || contains(bottomRight) || contains(topLeft);
    }

    // Return whether the AABB is fully contained in the quadtree grid
    private boolean contains(AABB aabb){
        Vector2 bottomLeft = aabb.getMin();
        Vector2 topRight = aabb.getMax();

        Vector2 bottomRight = new Vector2(topRight.x, bottomLeft.y);
        Vector2 topLeft = new Vector2(bottomLeft.x, topRight.y);

        return contains(bottomLeft) && contains(topRight) && contains(bottomRight) && contains(topLeft);

    }

    // Whether the aabb is fully contained inside a grid
    private boolean contains(Vector2 point){
        Vector2 origin = this.getOrigin();
        float x = origin.x;
        float y = origin.y;

        return point.x >= x - this.width - TOLERANCE && point.x <= x + this.width + TOLERANCE && point.y >= y - this.height - TOLERANCE && point.y <= y + this.height + TOLERANCE;
    }

    // Find the quadtree and its descendants that overlap the shape
    public void query(RigidShape shape, ArrayList<RigidShape> foundShapes) {
        // If the shape is not within this node, return immediately
        if (!this.overlaps(shape.getAABB())) {
            return;
        }

        // Add all shapes in this node except the queried shape
        for (RigidShape s : this.shapes) {
            if (s != shape){
                foundShapes.add(s);
            }
        }

        // Recurse
        for (Quadtree n : this.nodes){
            n.query(shape, foundShapes);
        }
    }

    // Function to draw the quadtree
    public void drawQuadTree(Viewport viewport, ShapeRenderer shapeRenderer, SpriteBatch spriteBatch, boolean drawText) {
        // Draw the lines
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(this.origin.x - this.width, this.origin.y - this.height, this.width * 2, this.height * 2);
        shapeRenderer.end();

        // Draw the text
        if (drawText) {
            spriteBatch.begin();
            spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, World.getScreenWidth(), World.getScreenHeight());
            font.setColor(Color.RED);
            font.draw(spriteBatch, String.valueOf(shapes.size()), origin.x, origin.y);
            spriteBatch.end();
        }

        // Draw the child nodes recursively
        if (nodes != null) {
            for (Quadtree q : nodes) {
                q.drawQuadTree(viewport, shapeRenderer, spriteBatch, drawText);
            }
        }
    }
}
