package it.jjdoes.PhysicsEngine.SoftBody;

import com.badlogic.gdx.math.Vector2;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.Arrays;

import it.jjdoes.PhysicsEngine.RigidBody.RigidPolygonShape;
import it.jjdoes.PhysicsEngine.World;

public class SoftCollisionDetection {
    // Check for collision between two polygons
    public static Object[] checkCollision(SoftPolygonShape shapeOne, SoftPolygonShape shapeTwo) {
        // Get vertices for each shape
        Vector2[] shapeOneVertices = shapeOne.getVerticesVector();
        Vector2[] shapeTwoVertices = shapeTwo.getVerticesVector();

        // Get edges (each edge is represented as a pair of consecutive vertices)
        Vector2[] shapeOneEdges = shapeOne.getVectors(shapeOne.getEdges());
        Vector2[] shapeTwoEdges = shapeTwo.getVectors(shapeTwo.getEdges());

        // Declare return values
        float depth = Float.MAX_VALUE;
        Vector2 normal = new Vector2();

        // Loop through each edge in the first shape
        for (int i = 0; i < shapeOneEdges.length; i++) {
            // Get both axis perpendicular to the edge
            Vector2 leftNormal = new Vector2(-shapeOneEdges[i].y, shapeOneEdges[i].x);
            Vector2 rightNormal = new Vector2(shapeOneEdges[i].y, -shapeOneEdges[i].x);

            // Normalize the axis
            leftNormal.nor();
            rightNormal.nor();

            // Project the vertices of each shape onto the left normal axis
            float[] leftNormalShapeOneMinMax = projectVertices(shapeOneVertices, leftNormal);
            float[] leftNormalShapeTwoMinMax = projectVertices(shapeTwoVertices, leftNormal);
            // Project the vertices of each shape onto the right normal axis
            float[] rightNormalShapeOneMinMax = projectVertices(shapeOneVertices, rightNormal);
            float[] rightNormalShapeTwoMinMax = projectVertices(shapeTwoVertices, rightNormal);

            // Check for separation on the left normal
            if (leftNormalShapeOneMinMax[0] >= leftNormalShapeTwoMinMax[1] || leftNormalShapeTwoMinMax[0] >= leftNormalShapeOneMinMax[1]) {
                return null;
            }
            // Check for separation on the right normal
            if (rightNormalShapeOneMinMax[0] >= rightNormalShapeTwoMinMax[1] || rightNormalShapeTwoMinMax[0] >= rightNormalShapeOneMinMax[1]) {
                return null;
            }

            // Find the minimum penetration distance of the left normal
            float leftDepth = Math.min(leftNormalShapeTwoMinMax[1] - leftNormalShapeOneMinMax[0], leftNormalShapeOneMinMax[1] - leftNormalShapeTwoMinMax[0]);
            // Find the minimum penetration distance of the right normal
            float rightDepth = Math.min(rightNormalShapeTwoMinMax[1] - rightNormalShapeOneMinMax[0], rightNormalShapeOneMinMax[1] - rightNormalShapeTwoMinMax[0]);
            // Find the minimum penetration distance between both normals
            // If the minimum distance is a new low, save the depth and normal
            if (leftDepth < rightDepth && leftDepth < depth){
                depth = leftDepth;
                normal.set(leftNormal);
            }
            else if (rightDepth < depth){
                depth = rightDepth;
                normal.set(rightNormal);
            }
        }
        // Loop through each edge in the second shape
        for (int i = 0; i < shapeTwoEdges.length; i++) {
            // Get both axis perpendicular to the edge
            Vector2 leftNormal = new Vector2(-shapeTwoEdges[i].y, shapeTwoEdges[i].x);
            Vector2 rightNormal = new Vector2(shapeTwoEdges[i].y, -shapeTwoEdges[i].x);

            // Normalize the axis
            leftNormal.nor();
            rightNormal.nor();

            // Project the vertices of each shape onto the left normal axis
            float[] leftNormalShapeOneMinMax = projectVertices(shapeOneVertices, leftNormal);
            float[] leftNormalShapeTwoMinMax = projectVertices(shapeTwoVertices, leftNormal);
            // Project the vertices of each shape onto the right normal axis
            float[] rightNormalShapeOneMinMax = projectVertices(shapeOneVertices, rightNormal);
            float[] rightNormalShapeTwoMinMax = projectVertices(shapeTwoVertices, rightNormal);

            // Check for separation on the left normal
            if (leftNormalShapeOneMinMax[0] >= leftNormalShapeTwoMinMax[1] || leftNormalShapeTwoMinMax[0] >= leftNormalShapeOneMinMax[1]) {
                return null;
            }
            // Check for separation on the right normal
            if (rightNormalShapeOneMinMax[0] >= rightNormalShapeTwoMinMax[1] || rightNormalShapeTwoMinMax[0] >= rightNormalShapeOneMinMax[1]) {
                return null;
            }

            // Find the minimum penetration distance of the left normal
            float leftDepth = Math.min(leftNormalShapeTwoMinMax[1] - leftNormalShapeOneMinMax[0], leftNormalShapeOneMinMax[1] - leftNormalShapeTwoMinMax[0]);
            // Find the minimum penetration distance of the right normal
            float rightDepth = Math.min(rightNormalShapeTwoMinMax[1] - rightNormalShapeOneMinMax[0], rightNormalShapeOneMinMax[1] - rightNormalShapeTwoMinMax[0]);
            // Find the minimum penetration distance between both normals
            if (leftDepth < rightDepth && leftDepth < depth){
                depth = leftDepth;
                normal.set(leftNormal);
            }
            else if (rightDepth < depth){
                depth = rightDepth;
                normal.set(rightNormal);
            }
        }

        // No matter if we choose the left or right normal, we will always scale
        // it to be the correct direction (pointing from shape one to shape two)
        Vector2 direction = shapeTwo.getOrigin().sub(shapeOne.getOrigin());
        if (direction.dot(normal) < 0)
        {
            normal.scl(-1);
        }

        Object[] collisionData = checkCollisionRaycast(shapeOne, shapeTwo, normal);
        Object[] test = new Object[collisionData.length + 2];
        for (int i = 0; i < collisionData.length; i++){
            test[i] = collisionData[i];
        }
        test[collisionData.length] = depth;
        test[collisionData.length + 1] = normal;
        return test;
    }

    // Project the vertices of a polygon onto an axis, and return the min and max values
    // ie. [min, max]
    private static float[] projectVertices(Vector2[] vertices, Vector2 axis){
        float[] result = new float[2];

        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;

        for (Vector2 vertex: vertices){
            float projection = vertex.dot(axis);
            if (projection < min){
                min = projection;
            }
            if (projection > max){
                max = projection;
            }
        }
        result[0] = min;
        result[1] = max;
        return result;
    }

    public static Object[] checkCollisionRaycast(SoftPolygonShape shapeOne, SoftPolygonShape shapeTwo, Vector2 normal){
        boolean collide = false;
        ArrayList<Float> depths = new ArrayList<>();
        ArrayList<Integer> shapeOnePenetratingVertices = new ArrayList<>();
        ArrayList<Integer> shapeTwoPenetratingVertices = new ArrayList<>();
        ArrayList<Integer> shapeOnePenetratingEdges = new ArrayList<>();
        ArrayList<Integer> shapeTwoPenetratingEdges = new ArrayList<>();

        Vector2[][] shapeOneEdges = shapeOne.getEdges();
        Vector2[][] shapeTwoEdges = shapeTwo.getEdges();

        // Loop through shape one vertices
        for (int index = 0; index < shapeOneEdges.length; index++){
            Vector2 vertex = shapeOneEdges[index][0];

            Object[] rcData = raycastPoint(shapeTwo.getVerticesVector(), vertex);
            if ((boolean) rcData[0]){
                Vector2 direction = shapeTwo.getOrigin().sub(shapeOne.getOrigin());
                Object[] penetrationData = getPenetrationData(vertex, shapeTwoEdges, normal, direction, (boolean) rcData[1]);
                depths.add((float) penetrationData[0]);
                World.allContactPoints.add((Vector2) penetrationData[3]);
                shapeOnePenetratingVertices.add(index);
                shapeOnePenetratingEdges.add((int) penetrationData[2]);
                collide = true;
            }
        }

        // Loop through shape two vertices
        for (int index = 0; index < shapeTwoEdges.length; index++){
            Vector2 vertex = shapeTwoEdges[index][0];

            Object[] rcData = raycastPoint(shapeOne.getVerticesVector(), vertex);
            if ((boolean) rcData[0]){
                Vector2 direction = shapeTwo.getOrigin().sub(shapeOne.getOrigin());
                Object[] penetrationData = getPenetrationData(vertex, shapeOneEdges, normal, direction, (boolean) rcData[1]);
                depths.add((float) penetrationData[0]);
                World.allContactPoints.add((Vector2) penetrationData[3]);
                shapeTwoPenetratingVertices.add(index);
                shapeTwoPenetratingEdges.add((int) penetrationData[2]);
                collide = true;
            }
        }

        // Loop through shape one edges
        for (int shapeOneIndex = 0; shapeOneIndex < shapeOneEdges.length; shapeOneIndex++){
            // Loop through shape two edges
            for (int shapeTwoIndex = 0; shapeTwoIndex < shapeTwoEdges.length; shapeTwoIndex++){
                if (doEdgesIntersect(shapeOneEdges[shapeOneIndex][0], shapeOneEdges[shapeOneIndex][1], shapeTwoEdges[shapeTwoIndex][0], shapeTwoEdges[shapeTwoIndex][1])){
                    if (shapeOnePenetratingVertices.contains(shapeOneIndex) || shapeTwoPenetratingEdges.contains(shapeOneIndex) || shapeOnePenetratingVertices.contains((shapeOneIndex + 1) % shapeOne.getVerticesVector().length) || shapeTwoPenetratingEdges.contains((shapeOneIndex + 1) % shapeOne.getVerticesVector().length)){
                        continue;
                    }

                    Vector2 direction = shapeTwo.getOrigin().sub(shapeOne.getOrigin());
                    Object[] penetrationData = getPenetrationData(shapeOne.getVerticesVector()[shapeOneIndex], shapeTwoEdges, normal, direction, false);
                    Vector2 direction1 = shapeTwo.getOrigin().sub(shapeOne.getOrigin());
                    Object[] penetrationData1 = getPenetrationData(shapeOne.getVerticesVector()[(shapeOneIndex + 1) % shapeOne.getVerticesVector().length], shapeTwoEdges, normal, direction1, false);

                    if ((float) penetrationData[0] <= (float) penetrationData1[0]) {
                        depths.add((float) penetrationData[0]);
                        World.allContactPoints.add((Vector2) penetrationData[3]);
                        shapeOnePenetratingVertices.add(shapeOneIndex);
                        shapeOnePenetratingEdges.add((int) penetrationData[2]);
                        collide = true;
                    }
                    else {
                        depths.add((float) penetrationData1[0]);
                        World.allContactPoints.add((Vector2) penetrationData1[3]);
                        shapeOnePenetratingVertices.add((shapeOneIndex + 1) % shapeOne.getVerticesVector().length);
                        shapeOnePenetratingEdges.add((int) penetrationData1[2]);
                        collide = true;
                    }
                }
            }
        }
        return new Object[] {collide, depths, normal, shapeOnePenetratingVertices, shapeTwoPenetratingVertices, shapeOnePenetratingEdges, shapeTwoPenetratingEdges};
    }

    public static Object[] raycastPoint(Vector2[] vertices, Vector2 vertex) {
        int i, j;
        boolean c = false;
        boolean skip = false;

        // Check if the point is on any edge of the polygon
        for (i = 0, j = vertices.length - 1; i < vertices.length; j = i++) {
            if (isPointOnLineSegment(vertices[i], vertices[j], vertex)) {
                //return new Object[] {true, skip};
            }

            if (isPointOnPoint(vertices[i], vertices[j], vertex)){
//                skip = true;
//                return new Object[] {true, skip};
            }

            // Ray-casting logic
            if (((vertices[i].y >= vertex.y) != (vertices[j].y >= vertex.y)) &&
                (vertex.x <= (vertices[j].x - vertices[i].x) * (vertex.y - vertices[i].y) / (vertices[j].y - vertices[i].y) + vertices[i].x)) {
                c = !c;
            }
        }
        return new Object[] {c, skip};
    }

    // Helper function to compute the orientation of the triplet (p, q, r)
    private static int orientation(Vector2 p, Vector2 q, Vector2 r) {
        float val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
        if (val == 0) return 0; // Collinear
        return (val > 0) ? 1 : 2; // 1 -> Clockwise, 2 -> Counterclockwise
    }

    // Helper function to check if point q lies on segment pr
    private static boolean onSegment(Vector2 p, Vector2 q, Vector2 r) {
        return (q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) &&
            q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y));
    }

    // Function to check if two line segments p1p2 and p3p4 intersect
    public static boolean doEdgesIntersect(Vector2 p1, Vector2 p2, Vector2 p3, Vector2 p4) {
        // Find the four orientations needed for general and special cases
        int o1 = orientation(p1, p2, p3);
        int o2 = orientation(p1, p2, p4);
        int o3 = orientation(p3, p4, p1);
        int o4 = orientation(p3, p4, p2);

        // General case: If the segments straddle each other, they intersect
        if (o1 != o2 && o3 != o4) {
            return true;
        }

        // Special cases: Check if the points are collinear and on segment
        if (o1 == 0 && onSegment(p1, p3, p2)) return true;
        if (o2 == 0 && onSegment(p1, p4, p2)) return true;
        if (o3 == 0 && onSegment(p3, p1, p4)) return true;
        if (o4 == 0 && onSegment(p3, p2, p4)) return true;

        return false; // No intersection
    }


    // Helper function to check if a point is on a line segment
    private static boolean isPointOnLineSegment(Vector2 p1, Vector2 p2, Vector2 point) {
        // Define thresholds based on the edge length
        float edgeLength = p1.dst(p2); // Distance between the two endpoints
        float distanceThreshold = edgeLength * 1f; // Adjust based on edge size (10% of edge length)
        float crossProductThreshold = edgeLength * 2f; // Similar scaling for cross product tolerance

        // Check if the point is within the "expanded" bounding box
        boolean xBetween = ((point.x - p1.x) * (point.x - p2.x) <= distanceThreshold);
        boolean yBetween = ((point.y - p1.y) * (point.y - p2.y) <= distanceThreshold);

        // Calculate the cross product to check collinearity
        float crossProduct = (point.y - p1.y) * (p2.x - p1.x) - (point.x - p1.x) * (p2.y - p1.y);

        // Debug output
        //System.out.println(p1 + ", " + p2 + ", " + point + ", xBetween: " + xBetween + ", yBetween: " + yBetween + ", crossProduct: " + Math.abs(crossProduct));

        // Return true if the point is near the edge
        return xBetween && yBetween && Math.abs(crossProduct) <= crossProductThreshold;
    }


    // Helper function to check if a point is on point
    private static boolean isPointOnPoint(Vector2 p1, Vector2 p2, Vector2 point) {
        return (vectorEqualityCheck(p1, point)) || vectorEqualityCheck(p2, point);
    }



    // Method to calculate penetration depth along the normal direction
    private static Object[] getPenetrationData(Vector2 vertex, Vector2[][] edges, Vector2 normal, Vector2 direction, boolean skip) {
        float minDepth = Float.MAX_VALUE;
        Vector2 minNormal = new Vector2();
        int minEdge = 0;
        Vector2 cp = null;
        float[] tempDepths = new float[edges.length];
        Vector2[] tempPoints = new Vector2[edges.length];
        int tempIndex = 0;

        // Loop through all edges of the other shape
        int index = 0;
        for (Vector2[] edge : edges) {


            Object[] distanceData = pointLineDistanceInNormalDirection(vertex, edge, normal, direction, skip);
            float depth = (float) distanceData[0];
            Vector2 closestPoint = (Vector2) distanceData[1];
            if (closestPoint == null) {
                index++;
                tempDepths[tempIndex] = (float) distanceData[2];
                tempPoints[tempIndex] = (Vector2) distanceData[3];
                tempIndex++;
                continue; // Skip edges that don't satisfy the normal direction
            }
            if (depth < minDepth) {
                minDepth = depth;
                cp = closestPoint;
                minNormal.set(normal);
                minEdge = index;
            }
            index++;
        }

        if (cp == null){
            System.out.println(tempIndex);
            float minD = Float.MAX_VALUE;
            int foundIndex = 0;
            for (int j = 0; j < tempIndex; j++){
                if (tempDepths[j] < minD){
                    minD = tempDepths[j];
                    foundIndex = j;
                }
            }
            minDepth = tempDepths[foundIndex];
            cp = tempPoints[foundIndex];
        }
        System.out.println("Final Closest Point: " + cp + ", Vertex: " + vertex + ", Vertex Vector to CP: " + vertex.cpy().sub(cp).nor() + ", Depth: " + minDepth);
        System.out.println("\n");

        return new Object[] {minDepth, minNormal, minEdge, cp};
    }

    // Return the distance and closest contact point between a vertex and an edge in the given normal direction
    public static Object[] pointLineDistanceInNormalDirection(Vector2 vertex, Vector2[] edge, Vector2 normal, Vector2 direction, boolean skip) {
        Vector2 closestPoint = null;

        // Find the vector of the given edge
        Vector2 edgeVector = edge[1].cpy().sub(edge[0]);

        // Find the vector of the vertex and the first edge endpoint
        Vector2 pointVector = vertex.cpy().sub(edge[0]);

        // Project the point vector onto the edge vector
        float projection = pointVector.dot(edgeVector) / edgeVector.len2();

        // Clamp the projection to the edge segment
        if (projection <= 0) {
            closestPoint = edge[0].cpy();
        } else if (projection >= 1) {
            closestPoint = edge[1].cpy();
        } else {
            closestPoint = edge[0].cpy().add(edgeVector.cpy().scl(projection));
        }

        // Normalize the normal and direction vectors
        Vector2 n = normal.cpy().nor();
        Vector2 directionToClosestPoint = (vertex.cpy().sub(closestPoint)).nor();

        // Adjust direction if it opposes the given direction
        if (direction.dot(directionToClosestPoint) <= 0) {
            directionToClosestPoint.scl(-1);
        }

//        // Apply float equality checks for numerical stability
//        if (floatEqualityCheck(directionToClosestPoint.x, 0)) {
//            directionToClosestPoint.x = 0;
//        }
//        if (floatEqualityCheck(directionToClosestPoint.y, 0)) {
//            directionToClosestPoint.y = 0;
//        }
//        if (floatEqualityCheck(n.x, 0)) {
//            n.x = 0;
//        }
//        if (floatEqualityCheck(n.y, 0)) {
//            n.y = 0;
//        }

        System.out.println("Dot: " + directionToClosestPoint.dot(n) + ", Vertex: " + vertex + ", Closest Point: " + closestPoint + ", Distance: " + vertex.dst(closestPoint) + ", Normal: " + n + " DirectionToCP: " + directionToClosestPoint);
        // Check if the closest point lies in the normal direction
        if (!skip && directionToClosestPoint.dot(n) <= 0.7) {
            // The closest point is not in the normal direction
            return new Object[] {Float.MAX_VALUE, null, vertex.dst(closestPoint), closestPoint};
        }
        // Return the distance and the closest point
        return new Object[] {vertex.dst(closestPoint), closestPoint};
    }

    // Check for float equality within epsilon
    public static boolean floatEqualityCheck(float a, float b) {
        return Math.abs(a - b) <= 0.05;
    }

    // Check for vector equality within epsilon
    public static boolean vectorEqualityCheck(Vector2 a, Vector2 b){
        return Math.abs(a.dst2(b)) < 5;
    }
}
