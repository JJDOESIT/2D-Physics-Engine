package it.jjdoes.PhysicsEngine.SoftBody;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

import it.jjdoes.PhysicsEngine.AABB;
import it.jjdoes.PhysicsEngine.RigidBody.Triangulation;

public class SoftPolygonShape {
    private final int numPoints;
    private final float sideLength;
    private final float step;
    private final float creationRotation;
    private final float springLength;
    private final Color color;
    private final float mass;
    private final boolean moveable;
    private final ArrayList<SoftPoint> points;
    private final ArrayList<Spring> springs;
    private SoftPolygonShape matcher;
    public boolean dislodge = false;
    public Vector2 dislogeAmount = new Vector2();

    // Constructor
    public SoftPolygonShape(Vector2 origin, Color color, int numPoints, float sideLength, float step, float creationRotation, float mass, boolean moveable, float springLength){
        // Initialize data
        this.numPoints = numPoints;
        this.step = step;
        this.creationRotation = creationRotation;
        this.sideLength = sideLength;
        this.springLength = springLength;
        this.color = color;
        this.springs = new ArrayList<>();
        this.points = new ArrayList<>();
        this.mass = mass;
        this.moveable = moveable;

        // Calculate the radius based on the given side length (side length / 2 * sin(PI / numPoints)
        float radius = (float) (sideLength / (2 * Math.sin(Math.PI / numPoints)));
        // Create the polygon by adding SoftPoints
        for (int point = 0; point < numPoints; point++){
            float localX = MathUtils.cosDeg(creationRotation) * radius;
            float localY = MathUtils.sinDeg(creationRotation) * radius;
            this.points.add(new SoftPoint(new Vector2(origin.x + localX, origin.y + localY), mass, moveable));
            creationRotation += step;
        }
        // Initialize the outer springs to connect each vertex
        this.initializeOuterSprings();
    }

    // Initialize the outer springs of the shape
    public void initializeOuterSprings(){
        // Loop through each vertex
        for (int index = 0; index < this.points.size() - 1; index++){
            Spring spring = new Spring(this.points.get(index), this.points.get(index + 1), this.springLength);
            this.springs.add(spring);
        }
        // Connect the last vertex to the first
        Spring spring = new Spring(this.points.get(this.points.size() - 1), this.points.get(0), this.springLength);
        this.springs.add(spring);
    }

    // Override
    public void initializeInnerSprings(){}

    // Remove inner springs
    public void removeInnerSprings(){
        int start = this.springs.size() - 1;
        int stop = this.points.size() - 1;

        for (int index = start; index > stop; index--){
            this.springs.remove(index);
        }
    }

    // Initialize the shaper matcher
    public void initializeMatcher(){
        this.matcher = new SoftPolygonShape(this.getOrigin(), this.color, this.numPoints, this.sideLength, this.step, this.creationRotation, this.mass, false, this.springLength);
        for (int index = 0; index < this.points.size(); index++){
            this.matcher.addSpring(new Spring(this.matcher.getPoints().get(index), this.points.get(index), 0));
        }
    }

    // Part one of euler integration
    public Object[] integratePartOne(float time){
        float drx, dry;
        ArrayList<Vector2> savedForces = new ArrayList<>();
        ArrayList<Vector2> savedVelocities = new ArrayList<>();

        // Save forces and velocities
        for (SoftPoint softPoint : this.points) {
            savedForces.add(softPoint.getForce());
            savedVelocities.add(softPoint.getVelocity());

            // Update velocity with current forces
            softPoint.offsetVelocity((softPoint.getForce().x) * time,
                (softPoint.getForce().y) * time);

            drx = softPoint.getVelocity().x * time;
            dry = softPoint.getVelocity().y * time;

            softPoint.offsetOrigin(drx, dry);
        }
        return new Object[] {savedForces, savedVelocities};
    }

    // Part two of euler integration
    public void integratePartTwo(ArrayList<Vector2> savedForces, ArrayList<Vector2> savedVelocities, float time){
        float drx, dry;
        // Final velocity updates
        for (int index = 0; index < this.points.size(); index++) {
            SoftPoint softPoint = this.points.get(index);

            float vx = savedVelocities.get(index).x +
                ((((softPoint.getForce().x + savedForces.get(index).x)) * time) / 2);
            float vy = savedVelocities.get(index).y +
                ((((softPoint.getForce().y + savedForces.get(index).y)) * time) / 2);

            softPoint.setVelocity(vx, vy);

            drx = softPoint.getVelocity().x * time;
            dry = softPoint.getVelocity().y * time;

            softPoint.offsetOrigin(drx, dry);
            softPoint.resetForce();
        }
    }

    // Apply resistance to each point
    public void calculateResistance(float resistance){
        for (SoftPoint softPoint : this.points){
            softPoint.calculateResistance(resistance);
        }
    }

    // Apply gravity to each point
    public void calculateGravity(float gravity){
        for (SoftPoint softPoint : this.points){
            softPoint.calculateGravity(gravity);
        }
    }

    // Calculate the spring force for each spring
    public void calculateSpringForce(float strength, float dampener){
        for (Spring spring : this.springs){
            spring.applyForce(strength, dampener);
        }
    }

    // Calculate the pressure of the shape
    public void calculatePressure(float pressure) {
        float totalVolume = 0;

        // Calculate the total volume of all springs
        for (Spring spring : this.springs) {
            totalVolume += spring.calculateVolume();
        }

        // Avoid division by 0
        if (totalVolume == 0){
            totalVolume = 1;
        }

        // Apply pressure forces to each spring
        for (Spring spring : this.springs) {
            spring.calculatePressure(totalVolume, pressure);
        }
    }

    // Return the matcher shape
    public SoftPolygonShape getMatcher(){
        return this.matcher;
    }

    public Color getColor(){
        return this.color;
    }

    // Return the list of springs
    public ArrayList<Spring> getSprings(){
        return this.springs;
    }

    // Return the estimated origin of the shape
    // Note: Done by averaging the (x,y) coordinates of each vertex
    public Vector2 getOrigin(){
        float totalX = 0;
        float totalY = 0;

        // Loop through each vertex
        for (SoftPoint softPoint : this.points){
            // Add the (x,y) coordinates to the running sum
            Vector2 origin = softPoint.getOrigin();
            totalX += origin.x;
            totalY += origin.y;
        }

        // Divide the total by the number of vertices
        totalX /= this.points.size();
        totalY /= this.points.size();

        // Return the origin
        return new Vector2(totalX, totalY);
    }

    // Return the points list
    public ArrayList<SoftPoint> getPoints(){
        return this.points;
    }

    // Add a spring
    public void addSpring(Spring spring){
        this.springs.add(spring);
    }

    // Return the vertices coordinates as a float array
    public float[] getVerticesArray(){
        float[] vertices = new float[this.points.size() * 2];
        for (int index = 0; index < this.points.size(); index++){
            Vector2 origin = this.points.get(index).getOrigin();
            vertices[index * 2] = origin.x;
            vertices[(index * 2) + 1] = origin.y;
        }
        return vertices;
    }

    // Return the vertices coordinates as an a Vector2 array
    public Vector2[] getVerticesVector(){
        Vector2[] vertices = new Vector2[this.points.size()];
        for (int index = 0; index < this.points.size(); index++){
            vertices[index] = this.points.get(index).getOrigin();
        }
        return vertices;
    }

    // Return the edges of all springs as an array of Vector2 arrays
    public Vector2[][] getSpringsEdges(){
        Vector2[][] springs = new Vector2[this.springs.size()][2];
        for (int index = 0; index < this.springs.size(); index++){
            springs[index] = this.springs.get(index).getEdge();
        }
        return springs;
    }

    // Return the edges of the shape as an array of Vector2 arrays
    public Vector2[][] getEdges(){
        Vector2[][] edges = new Vector2[this.points.size()][2];
        for (int index = 0; index < this.points.size() - 1; index++){
            Vector2[] edge = new Vector2[2];
            edge[0] = this.points.get(index).getOrigin();
            edge[1] = this.points.get(index + 1).getOrigin();
            edges[index] = edge;
        }
        Vector2[] edge = new Vector2[2];
        edge[0] = this.points.get(this.points.size() - 1).getOrigin();
        edge[1] = this.points.get(0).getOrigin();
        edges[this.points.size() - 1] = edge;
        return edges;
    }

    // Return the vectors of a given set of edges as a Vector2 array
    public Vector2[] getVectors(Vector2[][] edges){
        Vector2[] vectors = new Vector2[edges.length];
        for (int edge = 0; edge < edges.length; edge++){
            Vector2 vector = new Vector2();
            vector.x = edges[edge][1].x - edges[edge][0].x;
            vector.y = edges[edge][1].y - edges[edge][0].y;
            vectors[edge] = vector;
        }
        return vectors;
    }

    // Return the AABB of the shape
    public AABB getAABB(){
        float minX = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float minY = Float.MAX_VALUE;
        float maxY = Float.MIN_VALUE;

        Vector2[] vertices = this.getVerticesVector();

        for (Vector2 vertex : vertices){
            minX = Math.min(minX, vertex.x);
            maxX = Math.max(maxX, vertex.x);
            minY = Math.min(minY, vertex.y);
            maxY = Math.max(maxY, vertex.y);
        }
        return new AABB(minX, minY, maxX, maxY);
    }

    // Return whether the shape is moveable or not
    public boolean isMoveable(){
        return this.moveable;
    }

    // Move all vertices by the given offset
    public void offsetAll(Vector2 offset){
        for (SoftPoint sp : this.points){
            sp.offsetOrigin(offset);
        }
    }

    // Rotate all vertices by the given angle
    public void rotateAll(float angle){
        // Get the estimated origin of the shape
        Vector2 origin = this.getOrigin();
        // Loop through all vertices
        for (SoftPoint softPoint : this.points){
            // Get the origin of the vertex
            Vector2 spOrigin = softPoint.getOrigin();
            // Rotate the vertex by the given angle around the origin
            float newX = (float) (origin.x + ((spOrigin.x - origin.x) * Math.cos(Math.toRadians(angle)) - (spOrigin.y - origin.y) * Math.sin(Math.toRadians(angle))));
            float newY = (float) (origin.y + ((spOrigin.x - origin.x) * Math.sin(Math.toRadians(angle)) + (spOrigin.y - origin.y) * Math.cos(Math.toRadians(angle))));
            // Set the vertex to be at the new origin
            softPoint.setOrigin(new Vector2(newX, newY));
        }
    }

    // Set each point's velocity to the given velocity
    public void setVelocity(Vector2 velocity){
        for (SoftPoint softPoint : this.points){
            softPoint.setVelocity(velocity);
        }
    }

    // Offset each point's velocity by the given velocity
    public void offsetVelocity(Vector2 velocity){
        for (SoftPoint softPoint : this.points){
            softPoint.offsetVelocity(velocity);
        }
    }

    // Return the estimated rotation angle of the shape given another shape
    // Note: The given shape should be of same type!
    public float getAngle(SoftPolygonShape shapeTwo){
        float angle = 0;
        // Get the estimated origin of the shape
        Vector2 origin = this.getOrigin();
        // Loop through each vertex
        for (int index = 0; index < this.points.size(); index++){
            // Calculate the angle between the origin and each vertex
            Vector2 shapeOneVector = new Vector2(this.points.get(index).getOrigin().sub(origin));
            Vector2 shapeTwoVector = new Vector2(shapeTwo.getPoints().get(index).getOrigin().sub(origin));
            angle += (float) Math.atan2(shapeOneVector.crs(shapeTwoVector), shapeOneVector.dot(shapeTwoVector));
        }
        // Return the rotation angle
        return (float) Math.toDegrees(angle / this.points.size());
    }

    // Function to draw the shape
    public void draw(PolygonSpriteBatch polygonSpriteBatch, TextureRegion texture){
        polygonSpriteBatch.setColor(this.getColor());
        PolygonRegion region = new PolygonRegion(texture, this.getVerticesArray(), Triangulation.calculateTriangulation(this.getVerticesVector()));
        polygonSpriteBatch.draw(region, 0, 0);
    }
}
