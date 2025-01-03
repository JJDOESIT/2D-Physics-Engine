package it.jjdoes.PhysicsEngine.SoftBody;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

import it.jjdoes.PhysicsEngine.RigidBody.AABB;
import it.jjdoes.PhysicsEngine.RigidBody.Triangulation;

public class SoftPolygonShape {
    private final int numPoints;
    private final float sideLength;
    private final float step;
    private final float creationRotation;
    private float springLength;
    private float springStrength;
    private float angularVelocity;
    private final Color color;
    private final boolean moveable;
    private final ArrayList<SoftPoint> points;
    private ArrayList<Spring> springs;
    private float inertia;
    private SoftPolygonShape matcher;

    public SoftPolygonShape(Vector2 origin, Color color, int numPoints, float sideLength, float step, float creationRotation, float mass, boolean moveable, float springLength, float springStrength){
        // Set the number of points the polygon has
        this.numPoints = numPoints;
        this.angularVelocity = 0;
        this.step = step;
        this.creationRotation = creationRotation;
        this.sideLength = sideLength;
        this.springLength = springLength;
        this.springStrength = springStrength;
        this.color = color;
        // Calculate the radius based on the given side length (side length / 2 * sin(PI / numPoints)
        float radius = (float) (sideLength / (2 * Math.sin(Math.PI / numPoints)));
        // Initialize the points ArrayList
        points = new ArrayList<>();
        for (int point = 0; point < numPoints; point++){
            float localX = MathUtils.cosDeg(creationRotation) * radius;
            float localY = MathUtils.sinDeg(creationRotation) * radius;
            points.add(new SoftPoint(new Vector2(origin.x + localX, origin.y + localY), mass));
            creationRotation += step;
        }
        this.springs = new ArrayList<>();
        this.moveable = moveable;
        this.calculateInertia();
        this.initializeOuterSprings();
    }

    // Initialize the outer springs of the shape
    public void initializeOuterSprings(){
        for (int index = 0; index < this.points.size() - 1; index++){
            Spring spring = new Spring(this.points.get(index), this.points.get(index + 1), this.springLength, this.springStrength);
            this.springs.add(spring);
        }
        Spring spring = new Spring(this.points.get(this.points.size() - 1), this.points.get(0), this.springLength, this.springStrength);
        this.springs.add(spring);
    }

    // Initialize the shaper matcher
    public void initializeMatcher(){
        this.matcher = new SoftPolygonShape(this.getOrigin(), this.color, this.numPoints, this.sideLength, this.step, this.creationRotation, this.getMass(), false, this.springLength, this.springStrength);
        for (int index = 0; index < this.points.size(); index++){
            this.matcher.addSpring(new Spring(this.matcher.getPoints().get(index), this.points.get(index), 0, 50));
        }
    }

    public void calculatePressure(float pressure) {
        float totalVolume = 0;

        // Calculate the total volume of all springs
        for (Spring spring : this.springs) {
            totalVolume += spring.calculateVolume();
        }

        // Apply pressure forces to each spring
        for (Spring spring : this.springs) {
            spring.calculatePressure(totalVolume, pressure);
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
            softPoint.offsetVelocity((softPoint.getForce().x / softPoint.getMass()) * time,
                (softPoint.getForce().y / softPoint.getMass()) * time);
        }

        // Apply displacements
        for (SoftPoint softPoint : this.points) {
            drx = softPoint.getVelocity().x * time;
            dry = softPoint.getVelocity().y * time;

            softPoint.offsetOrigin(drx, dry);
            softPoint.resetForce();
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
                (((softPoint.getForce().x + savedForces.get(index).x) / softPoint.getMass()) * time) / 2;
            float vy = savedVelocities.get(index).y +
                (((softPoint.getForce().y + savedForces.get(index).y) / softPoint.getMass()) * time) / 2;

            softPoint.setVelocity(vx, vy);

            drx = softPoint.getVelocity().x * time;
            dry = softPoint.getVelocity().y * time;

            softPoint.offsetOrigin(drx, dry);
            softPoint.resetForce();
        }
    }

    // Return the matcher shape
    public SoftPolygonShape getMatcher(){
        return this.matcher;
    }

    public Color getColor(){
        return this.color;
    }

    public float getInverseInertia(){
        if (this.moveable){
            return 1 / this.inertia;
        }
        return 0;
    }

    public void setAngularVelocity(float angularVelocity){
        this.angularVelocity = angularVelocity;
    }

    public Vector2 getOrigin(){
        Vector2 origin = new Vector2();
        float totalX = 0;
        float totalY = 0;
        for (SoftPoint sp : this.points){
            Vector2 spOrigin = sp.getOrigin();
            totalX += spOrigin.x;
            totalY += spOrigin.y;
        }
        totalX /= this.points.size();
        totalY /= this.points.size();
        origin.set(totalX, totalY);
        return origin;
    }

    public ArrayList<SoftPoint> getPoints(){
        return this.points;
    }

    public void addPoint(SoftPoint point){
        this.points.add(point);
    }

    public ArrayList<Spring> getSprings(){
        return this.springs;
    }

    public void addSpring(Spring spring){
        this.springs.add(spring);
    }

    public float[] getVerticesArray(){
        float[] vertices = new float[this.points.size() * 2];
        for (int index = 0; index < this.points.size(); index++){
            Vector2 origin = this.points.get(index).getOrigin();
            vertices[index * 2] = origin.x;
            vertices[(index * 2) + 1] = origin.y;
        }
        return vertices;
    }

    public Vector2[] getVerticesVector(){
        Vector2[] vertices = new Vector2[this.points.size()];
        for (int index = 0; index < this.points.size(); index++){
            vertices[index] = this.points.get(index).getOrigin();
        }
        return vertices;
    }

    public Vector2[][] getSpringVectors(){
        Vector2[][] springs = new Vector2[this.springs.size()][2];
        for (int index = 0; index < this.springs.size(); index++){
            springs[index] = this.springs.get(index).getSpringVector();
        }
        return springs;
    }

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

    // Return the AABB of the circle shape
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

    public boolean isMoveable(){
        return this.moveable;
    }

    public void moveAll(Vector2 offset){
        for (SoftPoint sp : this.points){
            sp.moveOrigin(offset);
        }
    }

    public void rotateAll(float angle){
        Vector2 origin = this.getOrigin();
        for (SoftPoint sp : this.points){
            Vector2 spOrigin = sp.getOrigin();
            float newX = (float) (origin.x + ((spOrigin.x - origin.x) * Math.cos(Math.toRadians(angle)) - (spOrigin.y - origin.y) * Math.sin(Math.toRadians(angle))));
            float newY = (float) (origin.y + ((spOrigin.x - origin.x) * Math.sin(Math.toRadians(angle)) + (spOrigin.y - origin.y) * Math.cos(Math.toRadians(angle))));
            sp.setOrigin(new Vector2(newX, newY));
        }
    }

    public Vector2 getVelocity(){
        Vector2 velocity = new Vector2();
        for (SoftPoint sp : this.points){
            velocity.add(sp.getVelocity());
        }
        return velocity.scl(1f / this.points.size());
    }

    public float getInverseMass(){
        float mass = 0;
        for (SoftPoint sp : this.points){
            mass += sp.getMass();
        }
        mass /= this.points.size();
        if (this.moveable){
            return 1 / mass;
        }
        return 0;
    }

    public float getMass(){
        float mass = 0;
        for (SoftPoint sp : this.points){
            mass += sp.getMass();
        }
        mass /= this.points.size();
        if (this.moveable){
            return mass;
        }
        return 0;
    }

    public float getAngularVelocity(){
        return this.angularVelocity;
    }

    // Offset each point's velocity by the given velocity
    public void offsetVelocity(Vector2 velocity){
        for (SoftPoint softPoint : this.points){
            softPoint.offsetVelocity(velocity);
        }
    }

    // Set each point's velocity to the given velocity
    public void setVelocity(Vector2 velocity){
        for (SoftPoint softPoint : this.points){
            softPoint.setVelocity(velocity);
        }
    }

    public void calculateSpringForce(float strength, float dampener){
        for (Spring spring : this.springs){
            spring.applyForce(strength, dampener);
        }
    }

    public void applyGravity(float gravity, float time){
        for (SoftPoint softPoint : this.points){
            softPoint.applyGravity(gravity, time);
        }
    }

    public void integrate(float time){
        if (this.moveable){
            for (SoftPoint softPoint : this.points){
                softPoint.integrate(time);
            }
            this.rotateAll((float) Math.toDegrees(angularVelocity) * time);
        }
    }

    public float getAngle(SoftPolygonShape shapeTwo){
        float angle = 0;
        Vector2 origin = this.getOrigin();
        for (int index = 0; index < this.points.size(); index++){
            Vector2 shapeOneVector = new Vector2(this.points.get(index).getOrigin().sub(origin));
            Vector2 shapeTwoVector = new Vector2(shapeTwo.getPoints().get(index).getOrigin().sub(origin));
            angle += (float) Math.atan2(shapeOneVector.crs(shapeTwoVector), shapeOneVector.dot(shapeTwoVector));
        }
        return (float) Math.toDegrees(angle / this.points.size());
    }

    // Function to calculate the inertia of a polygon
    public void calculateInertia(){
        // Create a temporary Vector2[] to hold the vertices in a Vector2 format
        int n = this.points.size();
        float area = 0f;
        float inertia = 0f;

        for (int i = 0; i < n; i++) {
            // Current and next vertex (wrapping around for the last vertex)
            float x1 = this.points.get(i).getOrigin().x;
            float y1 = this.points.get(i).getOrigin().y;
            float x2 = this.points.get((i + 1) % n).getOrigin().x;
            float y2 = this.points.get((i + 1) % n).getOrigin().y;

            // Shoelace area contribution
            float crossProduct = (x1 * y2 - x2 * y1);
            area += crossProduct;

            // Inertia contribution
            inertia += crossProduct * (x1 * x1 + x1 * x2 + x2 * x2 + y1 * y1 + y1 * y2 + y2 * y2);
        }

        // Final area
        area = Math.abs(area) / 2.0f;

        // Density (mass per unit area)
        float density = getMass() / area;

        // Final moment of inertia
        inertia = Math.abs(inertia) * density / 12.0f;
        this.inertia = inertia;
    }

    public void draw(PolygonSpriteBatch polygonSpriteBatch, TextureRegion texture){
        polygonSpriteBatch.setColor(this.getColor());
        PolygonRegion region = new PolygonRegion(texture, this.getVerticesArray(), Triangulation.calculateTriangulation(this.getVerticesVector()));
        polygonSpriteBatch.draw(region, 0, 0);
    }
}
