package it.jjdoes.PhysicsEngine.RigidBody;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import it.jjdoes.PhysicsEngine.AABB;

public class RigidPolygonShape extends RigidShape implements RigidPolygonShapePhysics {
    Vector2[] localMappedVertices;
    private final int numPoints;

    // Constructor
    public RigidPolygonShape(Vector2 origin, Color color, int numPoints, float sideLength, float step, float creationRotation, float mass, boolean moveable){
        // Call 'Shape' class constructor
        super(0, origin, color, mass, moveable);
        // Set the number of points the polygon has
        this.numPoints = numPoints;
        // Calculate the radius based on the given side length (side length / 2 * sin(PI / numPoints)
        float radius = (float) (sideLength / (2 * Math.sin(Math.PI / numPoints)));
        // Initialize the 'localMappedVertices' array
        localMappedVertices = new Vector2[numPoints];
        for (int point = 0; point < numPoints; point++){
            float localX = MathUtils.cosDeg(creationRotation) * radius;
            float localY = MathUtils.sinDeg(creationRotation) * radius;
            localMappedVertices[point] = new Vector2(localX, localY);
            creationRotation += step;
        }
        // Set the inertia of the polygon shape
        this.calculateInertia();
    }

    @Override
    // Return the AABB of the polygon shape
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

    @Override
    // Return the global mapped vertices as a float[]
    public float[] getVerticesArray(){
        // Get the center coordinates of the shape
        Vector2 origin = super.getOrigin();
        // Initialize a new vertices array
        float[] globalMappedVertices = new float[this.localMappedVertices.length * 2];
        // Loop through the localMappedVertices array
        for (int point = 0; point < this.localMappedVertices.length; point++){
            // Rotate the shape the the angle 'super.getRotation()' returns
            float cos = MathUtils.cos(MathUtils.degreesToRadians * super.getAngle());
            float sin = MathUtils.sin(MathUtils.degreesToRadians * super.getAngle());

            float xVert = this.localMappedVertices[point].x;
            float yVert = this.localMappedVertices[point].y;

            globalMappedVertices[point * 2] = xVert * cos - yVert * sin;
            globalMappedVertices[point * 2 + 1] = xVert * sin + yVert * cos;

            // Translate the shape relative to the origin
            globalMappedVertices[point * 2] = origin.x + globalMappedVertices[point * 2];
            globalMappedVertices[point * 2+ 1] = origin.y + globalMappedVertices[point * 2 + 1];
        }
        return globalMappedVertices;
    }

    @Override
    // Return the global mapped vertices as a Vector2[]
    public Vector2[] getVerticesVector(){
        // Get the vertices mapped to global coordinates
        float[] globalMappedVertices = this.getVerticesArray();
        // Create a temporary Vector2[] to hold the vertices in a Vector2
        Vector2[] verticesVector = new Vector2[globalMappedVertices.length / 2];

        // Loop through the globalMappedVertices to create a Vector2 for each vertices
        for (int point = 0; point < globalMappedVertices.length; point += 2){
            Vector2 verticesPair = new Vector2(globalMappedVertices[point], globalMappedVertices[point + 1]);
            verticesVector[point / 2] = verticesPair;
        }
        return verticesVector;
    }

    @Override
    // Return the edges of the polygon in a Vector2[][]
    // ie. [ [[0,0], [1,0]], [[1,0], [0,1]] ]
    public Vector2[][] getEdges(){
        // Create the Vector2[][] to hold the edges
        Vector2[][] globalMappedEdges = new Vector2[this.numPoints][2];

        // Create a temporary Vector2[] to hold the vertices in a Vector2 format
        Vector2[] verticesVector = this.getVerticesVector();

        // Loop through the verticesVector to create the Vector2[][] to hold the edges
        for (int index = 0; index < verticesVector.length - 1; index++){
            globalMappedEdges[index] = new Vector2[2];
            globalMappedEdges[index][0] = verticesVector[index];
            globalMappedEdges[index][1] = verticesVector[index + 1];
        }
        // Account for the link between the first and last vertices
        globalMappedEdges[globalMappedEdges.length - 1][0] = verticesVector[verticesVector.length - 1];
        globalMappedEdges[globalMappedEdges.length - 1][1] = verticesVector[0];

        return globalMappedEdges;
    }

    @Override
    // Calculate the vector distances between a given Vector2[][] of edges
    // ie. [ [[0,0], [1,0]], [[1,0], [0,1]] ]
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

    @Override
    // Function to calculate the inertia of a polygon
    public void calculateInertia(){
        // Create a temporary Vector2[] to hold the vertices in a Vector2 format
        int n = this.localMappedVertices.length;
        float area = 0f;
        float inertia = 0f;

        for (int i = 0; i < n; i++) {
            // Current and next vertex (wrapping around for the last vertex)
            float x1 = this.localMappedVertices[i].x;
            float y1 = this.localMappedVertices[i].y;
            float x2 = this.localMappedVertices[(i + 1) % n].x;
            float y2 = this.localMappedVertices[(i + 1) % n].y;

            // Shoelace area contribution
            float crossProduct = (x1 * y2 - x2 * y1);
            area += crossProduct;

            // Inertia contribution
            inertia += crossProduct * (x1 * x1 + x1 * x2 + x2 * x2 + y1 * y1 + y1 * y2 + y2 * y2);
        }

        // Final area
        area = Math.abs(area) / 2.0f;

        // Density (mass per unit area)
        float density = super.getMass() / area;

        // Final moment of inertia
        inertia = Math.abs(inertia) * density / 12.0f;
        super.setInertia(inertia);
    }

    // Draw function
    public void draw(PolygonSpriteBatch polygonSpriteBatch, TextureRegion texture){
        polygonSpriteBatch.setColor(super.getColor());
        PolygonRegion region = new PolygonRegion(texture, this.getVerticesArray(), Triangulation.calculateTriangulation(this.getVerticesVector()));
        polygonSpriteBatch.draw(region, 0, 0);
    }
}
