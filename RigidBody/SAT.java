package it.jjdoes.PhysicsEngine.RigidBody;

import com.badlogic.gdx.math.Vector2;

public class SAT {
    // Collision container
    public static Object[] checkCollision(RigidShape shapeOne, RigidShape shapeTwo){
        if (shapeOne.getType() == 0 && shapeTwo.getType() == 0){
            return checkCollision((RigidPolygonShape) shapeOne, (RigidPolygonShape) shapeTwo);
        }
        else if (shapeOne.getType() == 1 && shapeTwo.getType() == 0){
            return checkCollision((RigidCircleShape) shapeOne, (RigidPolygonShape) shapeTwo);
        }
        else if (shapeOne.getType() == 0 && shapeTwo.getType() == 1){
            Object[] result = checkCollision((RigidCircleShape) shapeTwo, (RigidPolygonShape) shapeOne);
            if (result == null) return null;
            Vector2 normal = (Vector2) result[0];
            normal.scl(-1);
            return new Object[] {normal, result[1]};
        }
        else{
            return checkCollision((RigidCircleShape) shapeOne, (RigidCircleShape) shapeTwo);
        }
    }

    // Check for collision between two AABB's
    public static boolean checkCollision(AABB shapeOne, AABB shapeTwo){
        Vector2 shapeOneMin = shapeOne.getMin();
        Vector2 shapeOneMax = shapeOne.getMax();
        Vector2 shapeTwoMin = shapeTwo.getMin();
        Vector2 shapeTwoMax = shapeTwo.getMax();

        // Separated on the x-axis
        if (shapeOneMax.x <= shapeTwoMin.x || shapeOneMin.x >= shapeTwoMax.x){
            return false;
        }
        // Separated on the y-axis
        if (shapeOneMax.y <= shapeTwoMin.y || shapeOneMin.y >= shapeTwoMax.y){
            return false;
        }
        return true;
    }

    // Check for collision between two polygons
    private static Object[] checkCollision(RigidPolygonShape shapeOne, RigidPolygonShape shapeTwo) {
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
        return new Object[] {normal, depth};
    }

    // Check for collision between a circle and a polygon
    private static Object[] checkCollision(RigidCircleShape shapeOne, RigidPolygonShape shapeTwo) {
        // Get vertices for shape one
        Vector2[] shapeTwoVertices = shapeTwo.getVerticesVector();
        // Get the origin for shape one
        Vector2 shapeOneOrigin = shapeOne.getOrigin();

        // Get edges (each edge is represented as a pair of consecutive vertices)
        Vector2[] shapeTwoEdges = shapeTwo.getVectors(shapeTwo.getEdges());

        // Declare return values
        float depth = Float.MAX_VALUE;
        Vector2 normal = new Vector2();

        // Loop through each edge in the first shape
        for (int i = 0; i < shapeTwoEdges.length; i++) {
            // Get both axis perpendicular to the edge
            Vector2 leftNormal = new Vector2(-shapeTwoEdges[i].y, shapeTwoEdges[i].x);
            Vector2 rightNormal = new Vector2(shapeTwoEdges[i].y, -shapeTwoEdges[i].x);

            // Normalize the axis
            leftNormal.nor();
            rightNormal.nor();

            // Project the vertices of each shape onto the left normal axis
            float[] leftNormalShapeTwoMinMax = projectVertices(shapeTwoVertices, leftNormal);
            float[] leftNormalShapeOneMinMax = projectCircle(shapeOneOrigin, shapeOne.getRadius() ,leftNormal);
            // Project the vertices of each shape onto the right normal axis
            float[] rightNormalShapeTwoMinMax = projectVertices(shapeTwoVertices, rightNormal);
            float[] rightNormalShapeOneMinMax = projectCircle(shapeOneOrigin, shapeOne.getRadius() ,rightNormal);

            // Check for separation on the left normal
            if (leftNormalShapeTwoMinMax[0] >= leftNormalShapeOneMinMax[1] || leftNormalShapeOneMinMax[0] >= leftNormalShapeTwoMinMax[1]) {
                return null;
            }
            // Check for separation on the right normal
            if (rightNormalShapeTwoMinMax[0] >= rightNormalShapeOneMinMax[1] || rightNormalShapeOneMinMax[0] >= rightNormalShapeTwoMinMax[1]) {
                return null;
            }

            // Find the minimum penetration distance of the left normal
            float leftDepth = Math.min(leftNormalShapeOneMinMax[1] - leftNormalShapeTwoMinMax[0], leftNormalShapeTwoMinMax[1] - leftNormalShapeOneMinMax[0]);
            // Find the minimum penetration distance of the right normal
            float rightDepth = Math.min(rightNormalShapeOneMinMax[1] - rightNormalShapeTwoMinMax[0], rightNormalShapeTwoMinMax[1] - rightNormalShapeOneMinMax[0]);
            // Find the minimum penetration distance between both normals
            // If the minimum distance is a new low, save the depth and normal
            if (leftDepth < rightDepth && leftDepth < depth) {
                depth = leftDepth;
                normal.set(leftNormal);
            } else if (rightDepth < depth) {
                depth = rightDepth;
                normal.set(rightNormal);
            }
        }

        // Find the closest vertex to the circle
        Vector2 result = new Vector2();
        float minDistance = Float.MAX_VALUE;

        for (Vector2 vertex: shapeTwoVertices){
            float distance = vertex.dst(shapeOneOrigin);
            if (distance < minDistance){
                minDistance = distance;
                result.set(vertex);
            }
        }

        // Calculate and normalize the axis
        Vector2 axis = result.sub(shapeOneOrigin);
        axis.nor();

        // Project the vertices of each shape onto the axis
        float[] normalShapeTwoMinMax = projectVertices(shapeTwoVertices, axis);
        float[] normalShapeOneMinMax = projectCircle(shapeOne.getOrigin(), shapeOne.getRadius() ,axis);
        // Check for separation on the normal
        if (normalShapeTwoMinMax[0] >= normalShapeOneMinMax[1] || normalShapeOneMinMax[0] >= normalShapeTwoMinMax[1]) {
            return null;
        }

        // Find the minimum penetration distance
        float axisDepth = Math.min(normalShapeOneMinMax[1] - normalShapeTwoMinMax[0], normalShapeTwoMinMax[1] - normalShapeOneMinMax[0]);
        // If the minimum distance is a new low, save the depth and normal
        if (axisDepth < depth) {
            depth = axisDepth;
            normal.set(axis);
        }
        // Scale it to be the correct direction (pointing from shape one to shape two)
        Vector2 direction = shapeTwo.getOrigin().sub(shapeOne.getOrigin());
        if (direction.dot(normal) < 0)
        {
            normal.scl(-1);
        }
        return new Object[] {normal, depth};
    }

    private static Object[] checkCollision(RigidCircleShape shapeOne, RigidCircleShape shapeTwo) {
        // Fetch the origins of both circles
        Vector2 shapeOneOrigin = shapeOne.getOrigin();
        Vector2 shapeTwoOrigin = shapeTwo.getOrigin();

        // Find the distance between their origins and their combined radii
        float distance = shapeTwoOrigin.dst(shapeOneOrigin);
        float combinedRadii = shapeOne.getRadius() + shapeTwo.getRadius();

        // If separation
        if (distance >= combinedRadii){
            return null;
        }

        Vector2 normal = shapeTwoOrigin.sub(shapeOneOrigin);
        normal.nor();
        float depth = combinedRadii - distance;
        return new Object[] {normal, depth};
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

    // Function to project 2 points onto the axis
    // ie. center +- (radius * direction) projected onto the axis
    private static float[] projectCircle(Vector2 origin, float radius, Vector2 axis){
        Vector2 direction = axis.cpy().nor();
        Vector2 p1 = origin.cpy().add(direction.cpy().scl(radius));
        Vector2 p2 = origin.cpy().sub(direction.scl(radius));

        float min = p1.dot(axis);
        float max = p2.dot(axis);

        if (min > max){
            float temp = min;
            min = max;
            max = temp;
        }
        return new float[] {min, max};
    }
}
