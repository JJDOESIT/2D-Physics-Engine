package it.jjdoes.PhysicsEngine.RigidBody;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Arrays;

public class Triangulation {
    // Function to calculate the triangles for a polygonal shape
    public static short[] calculateTriangulation(Vector2[] verticesVector){
        // Transfer the vertices into an array list
        ArrayList<Vector2> verticesArrayList = new ArrayList<>(Arrays.asList(verticesVector));

        ArrayList<Integer> indexList = new ArrayList<>();

        // Create an index list
        for (int index = 0; index < verticesArrayList.size(); index++){
            indexList.add(index);
        }

        int triangleIndexCount = verticesArrayList.size() * 3;
        short[] triangles = new short[triangleIndexCount];
        int triangleIndex = 0;

        // While there more than 2 indices left in the list
        while (indexList.size() > 2){
            for (int i = 0; i < indexList.size(); i++) {
                int j = (i + 1) % indexList.size();
                int k = i - 1 >= 0 ? i - 1 : indexList.size() - 1;

                // Check if the triangle formed by vertices k, i, j is convex ( angle formed < 180 )
                boolean isConvex = isConvex(verticesArrayList.get(indexList.get(k)), verticesArrayList.get(indexList.get(i)), verticesArrayList.get(indexList.get(j)));

                // Continue to the next set of vertices
                if (!isConvex){
                    continue;
                }

                boolean isEar = true;

                // Check to make sure no other vertex is inside the triangle formed by k, i, j
                for (int pointIndex = 0; pointIndex < indexList.size(); pointIndex++){
                    // Don't check vertices k, i, j
                    if (pointIndex != k && pointIndex != i && pointIndex != j) {
                        // If a vertex is inside the triangle, set isEar to false
                        isEar = !isInsideTriangle(verticesArrayList.get(indexList.get(k)), verticesArrayList.get(indexList.get(i)), verticesArrayList.get(indexList.get(j)), verticesArrayList.get(indexList.get(pointIndex)));
                    }
                }

                // If an ear was found
                if (isEar) {
                    // Save the 3 indices that correspond the the vertices
                    triangles[triangleIndex] = indexList.get(k).shortValue();
                    triangles[triangleIndex + 1] = indexList.get(i).shortValue();
                    triangles[triangleIndex + 2] = indexList.get(j).shortValue();
                    triangleIndex += 3;
                    // Remove the middle vertex
                    indexList.remove(i);
                }
            }
        }
        return triangles;
    }

    // Return whether the internal angle is convex ( < 180 )
    private static boolean isConvex(Vector2 a, Vector2 b, Vector2 c) {
        Vector2 ab = b.cpy().sub(a);
        Vector2 bc = c.cpy().sub(b);
        return ab.crs(bc) > 0; // Positive cross product means convex
    }

    // Return whether a point is inside a triangle
    private static boolean isInsideTriangle(Vector2 a, Vector2 b, Vector2 c, Vector2 p){
        // Compute vectors
        Vector2 v0 = c.cpy().sub(a);
        Vector2 v1 = b.cpy().sub(a);
        Vector2 v2 = p.cpy().sub(a);

        // Compute dot products
        float dot00 = v0.dot(v0);
        float dot01 = v0.dot(v1);
        float dot02 = v0.dot(v2);
        float dot11 = v1.dot(v1);
        float dot12 = v1.dot(v2);

        // Compute barycentric coordinates
        float denom = dot00 * dot11 - dot01 * dot01;
        if (Math.abs(denom) < 1e-20){
            return true;
        }
        float invDenom = 1 / denom;
        float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
        float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

        // Check if point is in triangle
        return (u >= 0) && (v >= 0) && (u + v < 1);
    }
}
