package it.jjdoes.PhysicsEngine;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

import it.jjdoes.PhysicsEngine.RigidBody.RigidCircleShape;
import it.jjdoes.PhysicsEngine.RigidBody.RigidPolygonShape;
import it.jjdoes.PhysicsEngine.RigidBody.RigidShape;
import it.jjdoes.PhysicsEngine.SoftBody.SoftCollisionInfo;
import it.jjdoes.PhysicsEngine.SoftBody.SoftPoint;
import it.jjdoes.PhysicsEngine.SoftBody.SoftPolygonShape;

public interface WorldPhysics {
    public final float EQUALITY_EPSILON_VALUE = 0.0005f;

    // Function to displace two shapes given a normal and a penetration depth
    public static void displaceShapes(RigidShape shapeOne, RigidShape shapeTwo, Vector2 contactNormal, float depth){
        // Displacement of the shapes
        // If both the shapes are moveable, displace them 50 / 50
        if (shapeOne.isMoveable() && shapeTwo.isMoveable()){
            shapeOne.moveOrigin(contactNormal.cpy().scl(-depth / 2));
            shapeTwo.moveOrigin(contactNormal.cpy().scl(depth / 2));
        }
        // Else if only shape one is moveable, displace it 100%
        else if (shapeOne.isMoveable()){
            shapeOne.moveOrigin(contactNormal.cpy().scl(-depth));
        }
        // Else if only shape two is moveable, displace it 100%
        else if (shapeTwo.isMoveable()){
            shapeTwo.moveOrigin(contactNormal.cpy().scl(depth));
        }
    }

    public static void handleRigidCollision(SoftPolygonShape shape, float width, float height) {
        for (SoftPoint softPoint : shape.getPoints()) {
            Vector2 origin = softPoint.getOrigin();
            Vector2 velcotiy = softPoint.getVelocity();
            // collision with ground
            if (origin.y <= 0) {
                softPoint.setOrigin(origin.x, 0);
                softPoint.setVelocity(velcotiy.x, 0);
            }
            // collision with ceiling
            if (origin.y >= height) {
                softPoint.setOrigin(origin.x, height);
                softPoint.setVelocity(velcotiy.x, 0);
            }

            // collision with left wall
            if (origin.x <= 0) {
                softPoint.setOrigin(0, origin.y);
                softPoint.setVelocity(0, velcotiy.y);
            }

            // collision with right wall
            if (origin.x >= width) {
                softPoint.setOrigin(width, origin.y);
                softPoint.setVelocity(0, velcotiy.y);
            }
        }
    }

    // Helper method to handle displacement logic for both shapes
    public static void handleDisplacement(SoftPolygonShape shapeOne, SoftPolygonShape shapeTwo, float e) {
        ArrayList<SoftPoint> points = shapeOne.getPoints();
        ArrayList<SoftPoint> otherPoints = shapeTwo.getPoints();

        // Loop through each point in shape one
        for (SoftPoint softPoint : points) {
            if (softPoint.softCollisionInfo.getUpdate()) {
                SoftCollisionInfo softCollisionInfo = softPoint.softCollisionInfo;
                Vector2 closestPoint = softCollisionInfo.getClosestPoint();
                int otherEdgeIndexStart = softCollisionInfo.getOtherEdge();
                int otherEdgeIndexEnd = (otherEdgeIndexStart + 1) % otherPoints.size();
                float pointStartPercentage = 1;
                float pointEndPercentage = 1;

                // Fetch the normal
                Vector2 normal = softCollisionInfo.getNormal();

                // If shape one AND shape two are moveable
                if (shapeOne.isMoveable() && shapeTwo.isMoveable()) {
                    // Offset the penetrating point by half the depth
                    softPoint.offsetOrigin(normal.cpy().scl(-1 * softCollisionInfo.getDepth() / 2));

                    // Fetch the edge points
                    SoftPoint pointStart = otherPoints.get(otherEdgeIndexStart);
                    SoftPoint pointEnd = otherPoints.get(otherEdgeIndexEnd);

                    // Calculate the distances between the contact point and the edge points
                    float pointStartDistance = pointStart.getOrigin().dst(closestPoint);
                    float pointEndDistance = pointEnd.getOrigin().dst(closestPoint);

                    // Normalize to a percentage
                    pointStartPercentage = pointStartDistance / (pointStartDistance + pointEndDistance);
                    pointEndPercentage = 1 - pointStartPercentage;

                    // Offset the edge by half the depth, scaled by the distance to the contact point
                    pointStart.offsetOrigin(normal.cpy().scl( (softCollisionInfo.getDepth() / 2) * pointEndPercentage));
                    pointEnd.offsetOrigin(normal.cpy().scl((softCollisionInfo.getDepth() / 2) * pointStartPercentage));
                }
                // If shape one is moveable
                else if (shapeOne.isMoveable() && !shapeTwo.isMoveable()){
                    // Offset the penetrating point by half the depth
                    softPoint.offsetOrigin(normal.cpy().scl(-1 * softCollisionInfo.getDepth()));
                }
                // If shape two is moveable
                else if (shapeTwo.isMoveable()){
                    // Offset the edge by half the depth
                    otherPoints.get(otherEdgeIndexStart).offsetOrigin(normal.cpy().scl(softCollisionInfo.getDepth()));
                    otherPoints.get(otherEdgeIndexEnd).offsetOrigin(normal.cpy().scl(softCollisionInfo.getDepth()));
                }

                // If shape one OR shape two is moveable
                if (shapeOne.isMoveable() || shapeTwo.isMoveable()) {
                    // Apply an impulse
                    applyImpulse(softPoint, otherPoints, otherEdgeIndexStart, otherEdgeIndexEnd, normal, e, pointStartPercentage, pointEndPercentage);
                }

                // Update the collision info
                softPoint.softCollisionInfo.setUpdate(false);
            }
        }
    }

    // Apply an impulse to the penetrating vertex, and the respective edge
    public static void applyImpulse(SoftPoint softPoint, ArrayList<SoftPoint> otherPoints, int otherEdgeIndexStart, int otherEdgeIndexEnd, Vector2 normal, float e, float pointStartPercentage, float pointEndPercentage){
        // An impulse should be applied the both vertices of the respective edge
        // Fetch the edge points
        SoftPoint otherPointStart = otherPoints.get(otherEdgeIndexStart);
        SoftPoint otherPointEnd = otherPoints.get(otherEdgeIndexEnd);

        // Calculate the edge velocity ((start velocity + end velocity) / 2)
        Vector2 edgeVelocity = otherPointStart.getVelocity().add(otherPointEnd.getVelocity());
        edgeVelocity.scl(0.5f);

        // Calculate relative velocity
        Vector2 relativeVelocity = softPoint.getVelocity().sub(edgeVelocity);

        // Calculate the average edge inverse mass
        float edgeInverseMass = (otherPointStart.getInverseMass() + otherPointEnd.getInverseMass()) / 2;

        // Compute j value
        float j = 2 * relativeVelocity.dot(normal);
        j /= (edgeInverseMass + softPoint.getInverseMass());

        // Handle NaN case
        if (Float.isNaN(j)) {
            j = 0f;
        }

        // Impulse vector
        Vector2 impulse = normal.cpy().scl(j);

        // Update velocities of the three points
        softPoint.offsetVelocity(impulse.cpy().scl(-1 * softPoint.getInverseMass()));
        otherPointStart.offsetVelocity(impulse.cpy().scl(otherPointStart.getInverseMass() * pointEndPercentage));
        otherPointEnd.offsetVelocity(impulse.cpy().scl(otherPointEnd.getInverseMass() * pointStartPercentage));

        // Scale the velocities based off the elasticity value
        softPoint.setVelocity(softPoint.getVelocity().scl(e));
        otherPointStart.setVelocity(otherPointStart.getVelocity().scl(e));
        otherPointEnd.setVelocity(otherPointEnd.getVelocity().scl(e));
    }

    // Function to apply an impulse on two rigid shapes
    public static void applyImpulse(Vector2 contactNormal, RigidShape shapeOne, RigidShape shapeTwo, float e){
        // Calculate relative velocity
        Vector2 relativeVelocity = shapeTwo.getVelocity().sub(shapeOne.getVelocity());

        // If the objects are separating
        if (contactNormal.dot(relativeVelocity) > 0f)
        {
            return;
        }

        // Impulse scalar
        float j = -(1f + e) * relativeVelocity.dot(contactNormal);

        j /= shapeOne.getInverseMass() + shapeTwo.getInverseMass();

        // Impulse vector
        Vector2 impulse = contactNormal.cpy().scl(j);

        // Update velocities of both objects
        shapeOne.setVelocity(shapeOne.getVelocity().sub(impulse.cpy().scl(shapeOne.getInverseMass())));
        shapeTwo.setVelocity(shapeTwo.getVelocity().add(impulse.cpy().scl(shapeTwo.getInverseMass())));
    }

    // Function to apply an impulse with rotations to two rigid shapes
    public static void applyImpulseWithRotation(Vector2 contactNormal, RigidShape shapeOne, RigidShape shapeTwo, Vector2[] contactPoints, int contactCount, float e, float staticFriction, float dynamicFriction) {
        // Compute the average contact point
        Vector2 averageContactPoint = new Vector2();
        for (int i = 0; i < contactCount; i++) {
            averageContactPoint.add(contactPoints[i]);
        }
        averageContactPoint.scl(1f / contactCount); // Divide by contact count to get the average

        // Relative contact vectors
        Vector2 rOne = averageContactPoint.cpy().sub(shapeOne.getOrigin());
        Vector2 rTwo = averageContactPoint.cpy().sub(shapeTwo.getOrigin());

        // Perpendicular vectors for angular velocity
        Vector2 rOnePerp = new Vector2(-rOne.y, rOne.x); // Perpendicular to ra
        Vector2 rTwoPerp = new Vector2(-rTwo.y, rTwo.x); // Perpendicular to rb

        // Calculate the linear velocity contribution due to angular velocity
        Vector2 angularLinearVelocityOne = rOnePerp.cpy().scl(shapeOne.getAngularVelocity());
        Vector2 angularLinearVelocityTwo = rTwoPerp.cpy().scl(shapeTwo.getAngularVelocity());

        // Calculate the total velocity at the contact points (linear + angular contributions)
        Vector2 velocityOneAtContact = shapeOne.getVelocity().add(angularLinearVelocityOne);
        Vector2 velocityTwoAtContact = shapeTwo.getVelocity().add(angularLinearVelocityTwo);

        // Calculate the relative velocity at the contact point
        Vector2 relativeVelocity = velocityTwoAtContact.sub(velocityOneAtContact);

        // Contact velocity magnitude
        float contactVelocityMagnitude = relativeVelocity.dot(contactNormal);

        // Skip separating objects
        if (contactVelocityMagnitude > 0f) {
            return;
        }

        // Precompute terms for impulse scalar
        float rOnePerpN = rOnePerp.dot(contactNormal);
        float rTwoPerpN = rTwoPerp.dot(contactNormal);

        // Calculate the j value
        float j = ((-(1f + e) * contactVelocityMagnitude)
                / (shapeOne.getInverseMass() + shapeTwo.getInverseMass()
                + (rOnePerpN * rOnePerpN) * shapeOne.getInverseInertia()
                + (rTwoPerpN * rTwoPerpN) * shapeTwo.getInverseInertia()));

        // Handle NaN case
        if (Float.isNaN(j)) {
            j = 0f;
        }

        // Impulse vector
        Vector2 impulse = contactNormal.cpy().scl(j);

        // Apply linear and angular impulse
        shapeOne.setAngularVelocity(shapeOne.getAngularVelocity() - rOne.crs(impulse) * shapeOne.getInverseInertia());
        shapeOne.setVelocity(shapeOne.getVelocity().sub(impulse.cpy().scl(shapeOne.getInverseMass())));

        shapeTwo.setAngularVelocity(shapeTwo.getAngularVelocity() + rTwo.crs(impulse) * shapeTwo.getInverseInertia());
        shapeTwo.setVelocity(shapeTwo.getVelocity().add(impulse.scl(shapeTwo.getInverseMass())));

        // Calculate the tangent
        Vector2 tangent = relativeVelocity.cpy().sub(contactNormal.scl(relativeVelocity.dot(contactNormal)));

        // Skip
        if (vectorEqualityCheck(tangent, new Vector2(0,0))){
            return;
        }

        // Normalize the tangent
        tangent.nor();

        // Precompute terms for impulse scalar
        float rOnePerpDotT = rOnePerp.dot(tangent);
        float rTwoPerpDotT = rTwoPerp.dot(tangent);

        // Calculate the jt value
        float jt = (-relativeVelocity.dot(tangent))
                / (shapeOne.getInverseMass() + shapeTwo.getInverseMass()
                + (rOnePerpDotT * rOnePerpDotT) * shapeOne.getInverseInertia()
                + (rTwoPerpDotT * rTwoPerpDotT) * shapeTwo.getInverseInertia());

        // Friction impulse vector
        Vector2 frictionImpulse;
        float sf = (staticFriction + staticFriction) / 2;
        float df = (dynamicFriction + dynamicFriction) / 2;
        if (Math.abs(jt) <= j * sf){
            frictionImpulse = tangent.scl(jt);
        }
        else{
            frictionImpulse = tangent.scl(-j * df);
        }

        // Apply friction impulse
        shapeOne.setAngularVelocity(shapeOne.getAngularVelocity() - rOne.crs(frictionImpulse) * shapeOne.getInverseInertia());
        shapeOne.setVelocity(shapeOne.getVelocity().sub(frictionImpulse.cpy().scl(shapeOne.getInverseMass())));

        shapeTwo.setAngularVelocity(shapeTwo.getAngularVelocity() + rTwo.crs(frictionImpulse) * shapeTwo.getInverseInertia());
        shapeTwo.setVelocity(shapeTwo.getVelocity().add(frictionImpulse.scl(shapeTwo.getInverseMass())));
    }

    // Find contact points tree
    public static Object[] findContactPoints(RigidShape shapeOne, RigidShape shapeTwo){
        // Polygon - Polygon
        if (shapeOne.getType() == 0 && shapeTwo.getType() == 0){
            return findContactPoints((RigidPolygonShape) shapeOne, (RigidPolygonShape) shapeTwo);
        }
        // Circle - Polygon
        else if (shapeOne.getType() == 1 && shapeTwo.getType() == 0){
            return findContactPoints((RigidCircleShape) shapeOne, (RigidPolygonShape) shapeTwo);
        }
        // Polygon - Circle
        else if (shapeOne.getType() == 0 && shapeTwo.getType() == 1){
            return findContactPoints((RigidCircleShape) shapeTwo, (RigidPolygonShape) shapeOne);
        }
        // Circle - Circle
        else{
            return findContactPoints((RigidCircleShape) shapeOne, (RigidCircleShape) shapeTwo);
        }
    }

    public static Object[] findContactPoints(RigidCircleShape shapeOne, RigidCircleShape shapeTwo){
        Vector2 distance = shapeTwo.getOrigin().sub(shapeOne.getOrigin());
        distance.nor();
        distance.scl(shapeTwo.getRadius());
        distance.add(shapeOne.getOrigin());
        return new Object[] {1, distance};
    }

    public static Object[] findContactPoints(RigidCircleShape shapeOne, RigidPolygonShape shapeTwo){
        // Fetch the edge of shapeTwo
        Vector2[][] shapeTwoEdges = shapeTwo.getEdges();
        // Fetch the origin of shapeOne
        Vector2 shapeOneOrigin = shapeOne.getOrigin();

        // Declare the minimum distance
        float minimumDistance = Float.MAX_VALUE;

        // Return value
        Vector2 contactPoint = new Vector2(0,0);

        // Loop through the edges in shapeTwo
        for (int edgeIndex = 0; edgeIndex < shapeTwoEdges.length; edgeIndex++){
            // Fetch the distance, and closet point
            Object[] distance = pointLineDistance(shapeOneOrigin, shapeTwoEdges[edgeIndex]);
            float closestDistance = (float) distance[0];
            Vector2 closestPoint = (Vector2) distance[1];

            // If we find a minimum distance between a vertex and an edge
            // Note: This means we have found the closest point of contact
            if (closestDistance < minimumDistance){
                minimumDistance = closestDistance;
                contactPoint.set(closestPoint);
            }
        }
        return new Object[] {1, contactPoint};
    }

    // Find one or two contact points between two given shapes
    public static Object[] findContactPoints(RigidPolygonShape shapeOne, RigidPolygonShape shapeTwo){
        // Fetch the edges of both polygon shapes
        Vector2[][] shapeOneEdges = shapeOne.getEdges();
        Vector2[][] shapeTwoEdges = shapeTwo.getEdges();

        // Declare the minimum distance
        float minimumDistance = Float.MAX_VALUE;

        // Return values
        Vector2 contactPointOne = new Vector2(0,0);
        Vector2 contactPointTwo = new Vector2(0,0);
        int contactCount = 0;

        // Loop through the vertices in shapeOne
        for (int vertexIndex = 0; vertexIndex < shapeOneEdges.length; vertexIndex++){
            // Only look at the first vertex in the edges list, so we don't run into duplicates
            Vector2 vertex = new Vector2(shapeOneEdges[vertexIndex][1]);

            // Loop through all the edges in shapeTwo
            for (int edgeIndex = 0; edgeIndex < shapeTwoEdges.length; edgeIndex++){
                Vector2[] edge = shapeTwoEdges[edgeIndex];
                // Fetch the distance, and closet point
                Object[] distance = pointLineDistance(vertex, edge);
                float closestDistance = (float) distance[0];
                Vector2 closestPoint = (Vector2) distance[1];

                // If we find a distance equal to the current minimum distance
                // Note: This means we have found 2 points of contact since the vertices are equal
                // distance to the edge
                if (floatEqualityCheck(closestDistance, minimumDistance)){
                    if (!vectorEqualityCheck(closestPoint, contactPointOne) && !vectorEqualityCheck(closestPoint, contactPointTwo)){
                        contactPointTwo.set(closestPoint);
                        contactCount = 2;
                    }
                }
                // If we find a minimum distance between a vertex and an edge
                // Note: This means we have found the closest point of contact
                else if (closestDistance < minimumDistance){
                    minimumDistance = closestDistance;
                    contactPointOne.set(closestPoint);
                    contactCount = 1;
                }
            }
        }

        // Loop through the vertices in shapeTwo
        for (int vertexIndex = 0; vertexIndex < shapeTwoEdges.length; vertexIndex++){
            // Only look at the first vertex in the edges list, so we don't run into duplicates
            Vector2 vertex = new Vector2(shapeTwoEdges[vertexIndex][0]);

            // Loop through all the edges in shapeOne
            for (int edgeIndex = 0; edgeIndex < shapeOneEdges.length; edgeIndex++){
                Vector2[] edge = shapeOneEdges[edgeIndex];
                // Fetch the distance, and closet point
                Object[] distance = pointLineDistance(vertex, edge);
                float closestDistance = (float) distance[0];
                Vector2 closestPoint = (Vector2) distance[1];

                // If we find a distance equal to the current minimum distance
                // Note: This means we have found 2 points of contact since the vertices are equal
                // distance to the edge
                if (floatEqualityCheck(closestDistance, minimumDistance)){
                    if (!vectorEqualityCheck(closestPoint, contactPointOne) && !vectorEqualityCheck(closestPoint, contactPointTwo)){
                        contactPointTwo = closestPoint;
                        contactCount = 2;
                    }
                }
                // If we find a minimum distance between a vertex and an edge
                // Note: This means we have found the closest point of contact
                else if (closestDistance < minimumDistance){
                    minimumDistance = closestDistance;
                    contactPointOne = closestPoint;
                    contactCount = 1;
                }
            }
        }
        return new Object[] {contactCount, contactPointOne, contactPointTwo};
    }

    // Find one or two contact points between two given shapes
    public static Object[] findContactPoints(SoftPolygonShape shapeOne, SoftPolygonShape shapeTwo){
        // Fetch the edges of both polygon shapes
        Vector2[][] shapeOneEdges = shapeOne.getEdges();
        Vector2[][] shapeTwoEdges = shapeTwo.getEdges();

        // Declare the minimum distance
        float minimumDistance = Float.MAX_VALUE;

        // Return values
        Vector2 contactPointOne = new Vector2(0,0);
        Vector2 contactPointTwo = new Vector2(0,0);
        int contactCount = 0;

        // Loop through the vertices in shapeOne
        for (int vertexIndex = 0; vertexIndex < shapeOneEdges.length; vertexIndex++){
            // Only look at the first vertex in the edges list, so we don't run into duplicates
            Vector2 vertex = new Vector2(shapeOneEdges[vertexIndex][1]);

            // Loop through all the edges in shapeTwo
            for (int edgeIndex = 0; edgeIndex < shapeTwoEdges.length; edgeIndex++){
                Vector2[] edge = shapeTwoEdges[edgeIndex];
                // Fetch the distance, and closet point
                Object[] distance = pointLineDistance(vertex, edge);
                float closestDistance = (float) distance[0];
                Vector2 closestPoint = (Vector2) distance[1];

                // If we find a distance equal to the current minimum distance
                // Note: This means we have found 2 points of contact since the vertices are equal
                // distance to the edge
                if (floatEqualityCheck(closestDistance, minimumDistance)){
                    if (!vectorEqualityCheck(closestPoint, contactPointOne) && !vectorEqualityCheck(closestPoint, contactPointTwo)){
                        contactPointTwo.set(closestPoint);
                        contactCount = 2;
                    }
                }
                // If we find a minimum distance between a vertex and an edge
                // Note: This means we have found the closest point of contact
                else if (closestDistance < minimumDistance){
                    minimumDistance = closestDistance;
                    contactPointOne.set(closestPoint);
                    contactCount = 1;
                }
            }
        }

        // Loop through the vertices in shapeTwo
        for (int vertexIndex = 0; vertexIndex < shapeTwoEdges.length; vertexIndex++){
            // Only look at the first vertex in the edges list, so we don't run into duplicates
            Vector2 vertex = new Vector2(shapeTwoEdges[vertexIndex][0]);

            // Loop through all the edges in shapeOne
            for (int edgeIndex = 0; edgeIndex < shapeOneEdges.length; edgeIndex++){
                Vector2[] edge = shapeOneEdges[edgeIndex];
                // Fetch the distance, and closet point
                Object[] distance = pointLineDistance(vertex, edge);
                float closestDistance = (float) distance[0];
                Vector2 closestPoint = (Vector2) distance[1];

                // If we find a distance equal to the current minimum distance
                // Note: This means we have found 2 points of contact since the vertices are equal
                // distance to the edge
                if (floatEqualityCheck(closestDistance, minimumDistance)){
                    if (!vectorEqualityCheck(closestPoint, contactPointOne) && !vectorEqualityCheck(closestPoint, contactPointTwo)){
                        contactPointTwo = closestPoint;
                        contactCount = 2;
                    }
                }
                // If we find a minimum distance between a vertex and an edge
                // Note: This means we have found the closest point of contact
                else if (closestDistance < minimumDistance){
                    minimumDistance = closestDistance;
                    contactPointOne = closestPoint;
                    contactCount = 1;
                }
            }
        }
        return new Object[] {contactCount, contactPointOne, contactPointTwo};
    }

    // Return the distance, closet contact point between a vertex and an edge, and the edge normal
    public static Object[] pointLineDistance(Vector2 vertex, Vector2[] edge){
        Vector2 closestPoint;
        // Find the vector of the given edge
        Vector2 edgeVector = edge[1].cpy().sub(edge[0]);
        // Edge normal
        Vector2 edgeNormal = new Vector2(-edgeVector.y, edgeVector.x);
        // Find the vector of the vertex and the first edge end point
        Vector2 pointVector = vertex.cpy().sub(edge[0]);
        // Project the point vector onto the edge vector
        float projection = pointVector.dot(edgeVector);
        // Extra math
        float edgeVectorLengthSquared = edgeVector.len2();
        projection /= edgeVectorLengthSquared;

        // Clamp the closest point to be within the edge
        if (projection <= 0){
            closestPoint = edge[0];
        }
        else if (projection >= 1){
            closestPoint = edge[1];
        }
        else{
            closestPoint = edge[0].cpy().add(edgeVector.scl(projection));
        }
        return new Object[] {vertex.dst2(closestPoint), closestPoint, edgeNormal};
    }

    // Check for float equality within epsilon
    public static boolean floatEqualityCheck(float a, float b){
        return Math.abs(a - b) <= EQUALITY_EPSILON_VALUE;
    }

    // Check for vector equality within epsilon
    public static boolean vectorEqualityCheck(Vector2 a, Vector2 b){
        return a.dst2(b) < EQUALITY_EPSILON_VALUE * EQUALITY_EPSILON_VALUE;
    }
}
