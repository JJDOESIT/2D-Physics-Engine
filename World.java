package it.jjdoes.PhysicsEngine;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import it.jjdoes.PhysicsEngine.PolygonShapeTypes.Hexagon;
import it.jjdoes.PhysicsEngine.PolygonShapeTypes.Octagon;
import it.jjdoes.PhysicsEngine.PolygonShapeTypes.Pentagon;
import it.jjdoes.PhysicsEngine.PolygonShapeTypes.Square;
import it.jjdoes.PhysicsEngine.PolygonShapeTypes.Triangle;
import it.jjdoes.PhysicsEngine.RigidBody.RigidCircleShape;
import it.jjdoes.PhysicsEngine.RigidBody.RigidPolygonShape;
import it.jjdoes.PhysicsEngine.RigidBody.Quadtree;
import it.jjdoes.PhysicsEngine.RigidBody.RigidShape;
import it.jjdoes.PhysicsEngine.RigidBody.SAT;
import it.jjdoes.PhysicsEngine.SoftBody.SoftCollisionDetection;
import it.jjdoes.PhysicsEngine.SoftBody.SoftPoint;
import it.jjdoes.PhysicsEngine.SoftBody.SoftPolygonShape;
import it.jjdoes.PhysicsEngine.SoftBody.SoftPolygonShapeTypes.SoftCircle;
import it.jjdoes.PhysicsEngine.SoftBody.SoftPolygonShapeTypes.SoftOctagon;
import it.jjdoes.PhysicsEngine.SoftBody.SoftPolygonShapeTypes.SoftSquare;
import it.jjdoes.PhysicsEngine.SoftBody.Spring;

public class World implements WorldPhysics {
    private static float screenWidth;
    private static float screenHeight;
    private static float headerHeight;
    public static ArrayList<RigidShape> rigidShapes;
    public static ArrayList<SoftPolygonShape> softShapes;
    public static int mode = 0;
    private static ArrayList<TextureRegion> textures;
    private static ArrayList<Color> colors;
    public static ArrayList<Vector2> allContactPoints;
    private static int playerShapeIndex;
    private static TextButton selectedShape;
    private static float selectedGravity;
    private static float selectedMass;
    private static float selectedSideLength;
    private static float selectedCreationRotation;
    private static boolean selectedStaticOption;
    public static Quadtree quadtree;
    private static boolean usingQuadtree;
    private static final float e = 0.1f;
    private static final float gravity = -60f;
    private static final float resistance = 0.999f;
    private static final float staticFriction = 0.2f;
    private static final float dynamicFriction = 0.1f;
    private static final float springStrength = 1f;
    // Constructor
    public static void initialize(float width, float height, float hHeight, boolean quadtreeBackend){
        screenWidth = width;
        screenHeight = height;
        headerHeight = hHeight;
        rigidShapes = new ArrayList<>();
        softShapes = new ArrayList<>();
        textures = new ArrayList<>();
        colors = new ArrayList<>();
        allContactPoints = new ArrayList<>();
        usingQuadtree = quadtreeBackend;
        quadtree = new Quadtree(new Vector2(screenWidth / 2, screenHeight / 2 - headerHeight / 2), (screenWidth / 2), (screenHeight / 2) - (headerHeight / 2),8, 0);
        Quadtree.scaleFont(1.5f);
        initializeTextures();
        initializeColors();
        initializeRigidWalls();
        initializeRigidPlayer();
    }

    // Function to initialize the textures
    private static void initializeTextures(){
        textures.add(new TextureRegion(new Texture("white.png")));
    }

    // Function to initialize the colors
    private static void initializeColors(){
        colors.add(new Color(255 / 255f, 255 / 255f, 255 / 255f, 1)); // White
        colors.add(new Color(178 / 255f, 102 / 255f, 255 / 255f, 1)); // Purple
        colors.add(new Color(255 / 255f, 178 / 255f, 102 / 255f, 1)); // Orange
        colors.add(new Color(255 / 255f, 102 / 255f, 178 / 255f, 1)); // Dark pink
        colors.add(new Color(102 / 255f, 178 / 255f, 255 / 255f, 1)); // Blue
        colors.add(new Color(255 / 255f, 102 / 255f, 102 / 255f, 1)); // Red
        colors.add(new Color(102 / 255f, 255 / 255f, 102 / 255f, 1)); // Green
        colors.add(new Color(255 / 255f, 255 / 255f, 102 / 255f, 1)); // Yellow
        colors.add(new Color(255 / 255f, 102 / 255f, 255 / 255f, 1)); // Magenta
        colors.add(new Color(102 / 255f, 255 / 255f, 255 / 255f, 1)); // Sky blue
        colors.add(new Color(102 / 255f, 255 / 255f, 178 / 255f, 1)); // Mint
        colors.add(new Color(178 / 255f, 255 / 255f, 102 / 255f, 1)); // Lime
    }

    // Function to create the bounding rigid walls
    private static void initializeRigidWalls(){
        // Create the bounding walls
        RigidPolygonShape topWall = new Square(new Vector2(Math.max(screenWidth, screenHeight) / 2,(screenHeight + Math.max(screenWidth, screenHeight) / 2) - headerHeight), colors.get(0),  Math.max(screenWidth, screenHeight), 45, 0, false);
        RigidPolygonShape rightWall = new Square(new Vector2(screenWidth + Math.max(screenWidth, screenHeight) / 2,Math.max(screenWidth, screenHeight) / 2), colors.get(0), Math.max(screenWidth, screenHeight), 45, 0, false);
        RigidPolygonShape bottomWall = new Square(new Vector2(Math.max(screenWidth, screenHeight) / 2,-Math.max(screenWidth, screenHeight) / 2), colors.get(0), Math.max(screenWidth, screenHeight), 45, 0, false);
        RigidPolygonShape leftWall = new Square(new Vector2(-Math.max(screenWidth, screenHeight) / 2,Math.max(screenWidth, screenHeight) / 2), colors.get(0), Math.max(screenWidth, screenHeight), 45, 0, false);
        // Add the walls to the rigidShapes list
        rigidShapes.add(topWall);
        rigidShapes.add(rightWall);
        rigidShapes.add(bottomWall);
        rigidShapes.add(leftWall);

        // If using a quadtree
        if (usingQuadtree){
            quadtree.insert(topWall);
            quadtree.insert(rightWall);
            quadtree.insert(bottomWall);
            quadtree.insert(leftWall);
        }
    }

    // Function to create the bounding soft walls
    private static void initializeSoftWalls(){
        // Create the bounding walls
        SoftPolygonShape topWall = new SoftSquare(new Vector2(Math.max(screenWidth, screenHeight) / 2,(screenHeight + Math.max(screenWidth, screenHeight) / 2) - headerHeight), colors.get(0),  Math.max(screenWidth, screenHeight), 45, 0, false, 1, 1);
        SoftPolygonShape rightWall = new SoftSquare(new Vector2(screenWidth + Math.max(screenWidth, screenHeight) / 2,Math.max(screenWidth, screenHeight) / 2), colors.get(0), Math.max(screenWidth, screenHeight), 45, 0, false, 1, 1);
        SoftPolygonShape bottomWall = new SoftSquare(new Vector2(Math.max(screenWidth, screenHeight) / 2,-Math.max(screenWidth, screenHeight) / 2), colors.get(2), Math.max(screenWidth, screenHeight), 45, 0, false, 1, 1);
        SoftPolygonShape leftWall = new SoftSquare(new Vector2(-Math.max(screenWidth, screenHeight) / 2,Math.max(screenWidth, screenHeight) / 2), colors.get(0), Math.max(screenWidth, screenHeight), 45, 0, false, 1, 1);
        // Add the walls to the softShapes list
        softShapes.add(topWall);
        softShapes.add(rightWall);
        softShapes.add(bottomWall);
        softShapes.add(leftWall);
    }

    // Function to create an initial rigid player character
    private static void initializeRigidPlayer(){
        RigidPolygonShape playerShape = new Square(new Vector2(250,450), colors.get(2), selectedSideLength * 2, selectedCreationRotation, selectedMass * 2, true);
        rigidShapes.add(playerShape);
        playerShapeIndex = rigidShapes.size() - 1;
    }

    // Function to create an initial soft player character
    private static void initializeSoftPlayer(){
        SoftPolygonShape playerShape = new SoftOctagon(new Vector2(250,450), colors.get(2), 100, 0, 1, true, 100, springStrength);
        playerShape.initializeMatcher();

        softShapes.add(playerShape);
        playerShapeIndex = softShapes.size() - 1;
    }

    // Function to swap the mode from soft to rigid or from rigid to soft
    public static void swapMode(){
        // Swap from rigid to soft
        if (mode == 0){
            rigidShapes.clear();
            initializeSoftWalls();
            initializeSoftPlayer();
            mode = 1;
        }
        // Swap from soft to rigid
        else if (mode == 1){
            softShapes.clear();
            initializeRigidWalls();
            initializeRigidPlayer();
            mode = 0;
        }


    }

    // Function to handle user input
    public static void handleInput(Viewport viewport){
        handleMouseInput(viewport);
        handleKeyboardInput();
    }

    // Function to handle keyboard input from the user
    private static void handleKeyboardInput(){
        if (mode == 0) {
            // If the user presses tab
            if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
                // If a player shape is selected
                if (playerShapeIndex != -1) {
                    // Delete the player shape
                    rigidShapes.remove(playerShapeIndex);
                    playerShapeIndex = -1;
                }
            }

            // If no player shape is selected
            if (playerShapeIndex == -1) {
                return;
            }
            // If the user presses WASD
            float shapeSpeed = 25.0f; // Player speed
            Vector2 shapeDelta = new Vector2(0f, 0f);
            // Up
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                shapeDelta.y += shapeSpeed;
            }
            // Down
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                shapeDelta.y -= shapeSpeed;
            }
            // Left
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                shapeDelta.x -= shapeSpeed;
            }
            // Right
            if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
                shapeDelta.x += shapeSpeed;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.O)) {
                World.swapMode();
                return;
            }
            // Fetch the player shape
            RigidShape playerShape = rigidShapes.get(playerShapeIndex);
            // Update velocity
            playerShape.setVelocity(playerShape.getVelocity().add(shapeDelta));
        }
        else if (mode == 1){
            float shapeSpeed = 25f; // Player speed
            Vector2 shapeDelta = new Vector2(0f, 0f);
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                shapeDelta.y += shapeSpeed;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                shapeDelta.y -= shapeSpeed;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                shapeDelta.x -= shapeSpeed;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                shapeDelta.x += shapeSpeed;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.R)) {
                softShapes.get(playerShapeIndex).rotateAll(-1);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
                World.swapMode();
                return;
            }
            softShapes.get(playerShapeIndex).offsetVelocity(shapeDelta);
        }
    }

    // Function to handle mouse input from the user
    private static void handleMouseInput(Viewport viewport){
        // Rigid mode
        if (mode == 0) {
            // If the user left clicks to spawn a shape
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                // Fetch the mouse position
                Vector3 mousePosition3D = viewport.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0.0f));
                // Convert to a 2D vector
                Vector2 mousePosition2D = new Vector2(mousePosition3D.x, mousePosition3D.y);
                // If the click is out of bounds, exit early
                if (mousePosition2D.x < 0 || mousePosition2D.x > screenWidth || mousePosition2D.y < 0 || mousePosition2D.y > screenHeight - headerHeight) {
                    return;
                }

                // Spawn a small circle where the user clicks
                RigidShape mouseShape = new RigidCircleShape(mousePosition2D, colors.get(0), 1, 1f, false);
                // Loop through each shape
                for (int index = 0; index < rigidShapes.size(); index++) {
                    // If the small circle intersects another shape
                    if (SAT.checkCollision(mouseShape, rigidShapes.get(index)) != null) {
                        // If the player shape is selected
                        if (playerShapeIndex != -1) {
                            // Set the old selected shape to a random color
                            rigidShapes.get(playerShapeIndex).setColor(colors.get(random.nextInt(3, colors.size())));
                        }
                        // Set the new selected shape to orange
                        playerShapeIndex = index;
                        rigidShapes.get(playerShapeIndex).setColor(colors.get(2));
                        return;
                    }
                }

                // Create an empty shape
                RigidShape shape = null;
                // Fetch selected shape
                String shapeType = selectedShape.getText().toString();
                // Set the color (purple if static / random if not static)
                Color color = selectedStaticOption ? colors.get(random.nextInt(3, colors.size())) : colors.get(1);

                // Triangle
                if (shapeType.equals("Triangle")) {
                    shape = new Triangle(mousePosition2D, color, selectedSideLength, selectedCreationRotation, selectedMass, selectedStaticOption);
                }
                // Square
                else if (shapeType.equals("Square")) {
                    shape = new Square(mousePosition2D, color, selectedSideLength, selectedCreationRotation, selectedMass, selectedStaticOption);
                }
                // Pentagon
                else if (shapeType.equals("Pentagon")) {
                    shape = new Pentagon(mousePosition2D, color, selectedSideLength, selectedCreationRotation, selectedMass, selectedStaticOption);
                }
                // Hexagon
                else if (shapeType.equals("Hexagon")) {
                    shape = new Hexagon(mousePosition2D, color, selectedSideLength, selectedCreationRotation, selectedMass, selectedStaticOption);
                }
                // Octagon
                else if (shapeType.equals("Octagon")) {
                    shape = new Octagon(mousePosition2D, color, selectedSideLength, selectedCreationRotation, selectedMass, selectedStaticOption);
                }
                // Circle
                else if (shapeType.equals("Circle")) {
                    shape = new RigidCircleShape(mousePosition2D, color, selectedSideLength, selectedMass, selectedStaticOption);
                }
                // If the shape was successfully created
                if (shape != null) {
                    // Add the shape to the list
                    rigidShapes.add(shape);
                    // If quadtree backend is being used
                    if (usingQuadtree) {
                        // Insert the shape into the quadtree
                        quadtree.insert(shape);
                    }
                }
            }
        }
        // Soft mode
        else if (mode == 1){
            // If the user left clicks to spawn a shape
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                // Fetch the mouse position
                Vector3 mousePosition3D = viewport.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0.0f));
                // Convert to a 2D vector
                Vector2 mousePosition2D = new Vector2(mousePosition3D.x, mousePosition3D.y);
                // If the click is out of bounds, exit early
                if (mousePosition2D.x < 0 || mousePosition2D.x > screenWidth || mousePosition2D.y < 0 || mousePosition2D.y > screenHeight - headerHeight) {
                    return;
                }

                // Create an empty shape
                SoftPolygonShape shape = null;
                // Fetch selected shape
                String shapeType = selectedShape.getText().toString();
                // Set the color (purple if static / random if not static)
                Color color = selectedStaticOption ? colors.get(random.nextInt(3, colors.size())) : colors.get(1);
                // Square
                if (shapeType.equals("Square")) {
                    shape = new SoftSquare(mousePosition2D, color, selectedSideLength, selectedCreationRotation, selectedMass, selectedStaticOption, selectedSideLength, springStrength);
                    shape.initializeMatcher();
                }
                else if (shapeType.equals("Octagon")){
                    shape = new SoftOctagon(mousePosition2D, color, selectedSideLength, selectedCreationRotation, selectedMass, selectedStaticOption, selectedSideLength, springStrength);
                    shape.initializeMatcher();
                }
                else if (shapeType.equals("Circle")){
                    shape = new SoftCircle(mousePosition2D, color, selectedSideLength, selectedCreationRotation, selectedMass, selectedStaticOption, selectedSideLength, springStrength);
                    shape.initializeMatcher();
                }

                // If the shape was successfully created
                if (shape != null) {
                    // Add the shape to the list
                    softShapes.add(shape);
                }
            }
        }
    }

    // Logic functionality
    public static void logic(float time, int iterations){
        // If the mode is rigid
        if (mode == 0){
            // If using quadtree
            if (usingQuadtree){
                stepWithQuadtree(time, iterations);
            }
            // Else if NOT using quadtree
            else{
                step(time, iterations);
            }
        }
        // If the mode is soft
        else if (mode == 1){
            softStep(time, iterations);
        }
    }

    // Step through the simulation with rigid bodies
    private static void step(float time, int iterations){
        // Clear contact points
        allContactPoints.clear();

        // Loop through all rigidShapes
        for (RigidShape shape: rigidShapes) {
            // If the shape is not static
            if (shape.isMoveable()) {
                // Apply resistance (simulated wind/friction)
                shape.addResistance(resistance);
            }
        }
        // Split the move, collide, separate process into iterations
        for (int iteration = 0; iteration < iterations; iteration++){
            // Loop through the polygon rigidShapes
            for (int outer = 0; outer < rigidShapes.size() - 1; outer++){
                for (int inner = outer + 1; inner < rigidShapes.size(); inner++) {
                    // Declare shapeOne and shapeTwo
                    RigidShape shapeOne = rigidShapes.get(outer);
                    RigidShape shapeTwo = rigidShapes.get(inner);

                    // If there is no overlap on AABB
                    if (!SAT.checkCollision(shapeOne.getAABB(), shapeTwo.getAABB())){
                        continue;
                    }

                    // Check if two objects are overlapping using SAT
                    Object[] collisionData = SAT.checkCollision(rigidShapes.get(outer), rigidShapes.get(inner));
                    // If there is no overlap
                    if (collisionData == null) {
                        continue;
                    }
                    // Get the contact normal and the depth
                    Vector2 contactNormal = (Vector2) collisionData[0];
                    float depth = (float) collisionData[1];

                    // Displace the rigidShapes
                    WorldPhysics.displaceShapes(rigidShapes.get(outer), rigidShapes.get(inner), contactNormal, depth);

                    // Find the points of contact between the rigidShapes (guaranteed one, maybe two)
                    Object[] contactPointsData = WorldPhysics.findContactPoints(rigidShapes.get(outer), rigidShapes.get(inner));

                    // Fetch the necessary data (contact count, and contact points)
                    int contactCount = (int) contactPointsData[0];
                    Vector2[] contactPoints = new Vector2[2];
                    for (int index = 0; index < contactCount; index++){
                        contactPoints[index] = (Vector2) contactPointsData[index + 1];
                        allContactPoints.add((Vector2) contactPointsData[index + 1]);
                    }

                    // Apply the impulse
                    WorldPhysics.applyImpulseWithRotation(contactNormal, rigidShapes.get(outer), rigidShapes.get(inner), contactPoints, contactCount, e, staticFriction, dynamicFriction);
                }
            }
            // Loop through all rigidShapes
            for (RigidShape shape: rigidShapes) {
                // If the shape is not static
                if (shape.isMoveable()) {
                    // Apply gravity, velocity, and angular velocity
                    shape.addGravity(gravity * selectedGravity, time/ iterations);
                    shape.applyVelocity(time / iterations);
                    shape.applyAngularVelocity(time / iterations);
                }
            }
        }
    }

    // Step through the simulation using a quadtree
    private static void stepWithQuadtree(float time, int iterations) {
        // Clear quadtree and contact points
        quadtree.clear();
        allContactPoints.clear();
        // Create a hashmap
        Set<AbstractMap.SimpleEntry<RigidShape, RigidShape>> checkedPairs = new HashSet<>();

        // Loop through all rigidShapes
        for (RigidShape shape : rigidShapes) {
            // If the shape is not static
            if (shape.isMoveable()) {
                // Apply resistance
                shape.addResistance(resistance);
            }
            // Insert the shape into the quadtree
            quadtree.insert(shape);
        }

        // Split the move, collide, separate process into iterations
        for (int iteration = 0; iteration < iterations; iteration++) {
            // Clear checked pairs for each iteration
            checkedPairs.clear();

            // Loop through the polygon rigidShapes
            for (int outer = 0; outer < rigidShapes.size(); outer++) {
                // Find rigidShapes near shapeOne in the quadtree
                RigidShape shapeOne = rigidShapes.get(outer);
                ArrayList<RigidShape> foundShapes = new ArrayList<>();
                quadtree.query(shapeOne, foundShapes);

                // Loop through the found rigidShapes near shapeOne
                for (int inner = 0; inner < foundShapes.size(); inner++) {
                    RigidShape shapeTwo = foundShapes.get(inner);
                    // Skip if the pair has already been checked
                    // Note: This compares the hashcode values of the shapes to determine the order.
                    // That way we can always guarantee that one shape will be placed before the other.
                    AbstractMap.SimpleEntry<RigidShape, RigidShape> pair = shapeOne.hashCode() < shapeTwo.hashCode() ?
                        new AbstractMap.SimpleEntry<>(shapeOne, shapeTwo) :
                        new AbstractMap.SimpleEntry<>(shapeTwo, shapeOne);

                    // If the rigidShapes have already been resolved
                    if (checkedPairs.contains(pair)) {
                        continue;
                    }
                    // Add the pair to the hashmap
                    checkedPairs.add(pair);

                    // Perform AABB collision check
                    if (!SAT.checkCollision(shapeOne.getAABB(), shapeTwo.getAABB())) {
                        continue;
                    }

                    // Perform SAT collision check
                    Object[] collisionData = SAT.checkCollision(shapeOne, shapeTwo);
                    if (collisionData == null) {
                        continue;
                    }

                    // Get the contact normal and the depth
                    Vector2 contactNormal = (Vector2) collisionData[0];
                    float depth = (float) collisionData[1];

                    // Displace the rigidShapes
                    WorldPhysics.displaceShapes(shapeOne, shapeTwo, contactNormal, depth);

                    // Find the points of contact between the rigidShapes
                    Object[] contactPointsData = WorldPhysics.findContactPoints(shapeOne, shapeTwo);
                    int contactCount = (int) contactPointsData[0];
                    Vector2[] contactPoints = new Vector2[2];
                    for (int index = 0; index < contactCount; index++) {
                        contactPoints[index] = (Vector2) contactPointsData[index + 1];
                        allContactPoints.add(contactPoints[index]);
                    }

                    // Apply the impulse
                    WorldPhysics.applyImpulseWithRotation(contactNormal, shapeOne, shapeTwo, contactPoints, contactCount, e, staticFriction, dynamicFriction);
                }
            }

            // Loop through all rigidShapes
            for (RigidShape shape : rigidShapes) {
                // If the shape is not static
                if (shape.isMoveable()) {
                    shape.addGravity(gravity * selectedGravity, time / iterations);
                    shape.applyVelocity(time / iterations);
                    shape.applyAngularVelocity(time / iterations);
                }
            }
        }
    }

    // Step through the simulation with soft bodies
    private static void softStep(float time, int iterations){
        // Clear contact points
        allContactPoints.clear();

        // Split the move, collide, separate process into iterations
        for (int iteration = 0; iteration < iterations; iteration++){
            // Loop through all softShapes
            for (SoftPolygonShape shape: softShapes) {
                // If the shape is not static
                if (shape.isMoveable()) {
                    // Set the time step
                    float timeStep = 0.01f;
                    // Calculate matcher forces
                    shape.getMatcher().moveAll(shape.getOrigin().sub(shape.getMatcher().getOrigin()));
                    shape.getMatcher().rotateAll(shape.getAngle(shape.getMatcher()) * -1);
                    shape.getMatcher().calculateSpringForce(2500, 0);
                    // Calculate shape forces
                    shape.calculateSpringForce(2500, 35);
                    shape.calculatePressure(700000);

                    // Part one integration
                    Object[] savedIntegrationData = shape.integratePartOne(timeStep);
                    ArrayList<Vector2> savedForces = (ArrayList<Vector2>) savedIntegrationData[0];
                    ArrayList<Vector2> savedVelocities = (ArrayList<Vector2>) savedIntegrationData[1];

                    // Recalculate forces
                    shape.calculateSpringForce(2500, 35);
                    shape.calculatePressure(700000);

                    // Part two integration
                    shape.integratePartTwo(savedForces, savedVelocities, timeStep);
                }
            }
            // Loop through the polygon softShapes
            for (int outer = 0; outer < softShapes.size() - 1; outer++){
                for (int inner = outer + 1; inner < softShapes.size(); inner++) {
                    // Declare shapeOne and shapeTwo
                    SoftPolygonShape shapeOne = softShapes.get(outer);
                    SoftPolygonShape shapeTwo = softShapes.get(inner);

//                    // If there is no overlap on AABB
//                    if (!SAT.checkCollision(shapeOne.getAABB(), shapeTwo.getAABB())){
//                        continue;
//                    }

                    // Check if two objects are overlapping
                    Object[] collisionData = SoftCollisionDetection.checkCollision(shapeOne, shapeTwo);
                    if (collisionData == null){
                        continue;
                    }
                    boolean collision = (boolean) collisionData[0];
                    // If there is no overlap
                    if (!collision) {
                        continue;
                    }
                    // Displace the softShapes
                    //WorldPhysics.resolveCollision(shapeOne, shapeTwo, (ArrayList<Float>) collisionData[1], (Vector2) collisionData[2], (ArrayList<Integer>) collisionData[3], (ArrayList<Integer>) collisionData[4], (ArrayList<Integer>) collisionData[5], (ArrayList<Integer>) collisionData[6], (float) collisionData[7]);
                }
            }
        }
    }

    // Return the screen width
    public static float getScreenWidth(){
        return screenWidth;
    }

    // Return the screen height
    public static float getScreenHeight(){
        return screenHeight;
    }

    // Return the selected shape (TextButton)
    public static TextButton getSelectedShape(){
        return selectedShape;
    }

    // Set the selected shape (TextButton)
    public static void setSelectedShape(TextButton button){
        selectedShape = button;
    }

    // Set the selected gravity
    public static void setSelectedGravity(float gravity){
        selectedGravity = gravity;
    }

    // Set the selected mass
    public static void setSelectedMass(float mass){
        selectedMass = mass;
    }

    // Set the selected side length
    public static void setSelectedSideLength(float sideLength){
        selectedSideLength = sideLength;
    }

    // Set the selected creation rotation
    public static void setSelectedCreationRotation(float creationRotation){
        selectedCreationRotation = creationRotation;
    }

    // Set the selected static option (can a shape be moved)
    public static void setSelectedStaticOption(boolean staticOption){
        selectedStaticOption = staticOption;
    }

    // Draw function
    public static void draw(Viewport viewport, ShapeRenderer shapeRenderer, PolygonSpriteBatch polygonSpriteBatch){
        // Re-position the camera
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        // Clear the screen
        ScreenUtils.clear(Color.BLACK);

        // Add a white background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(0, 0, screenWidth, screenHeight);
        shapeRenderer.end();

        // Rigid mode
        if (mode == 0){
            // Loop through all the rigidShapes
            for (RigidShape shape: rigidShapes) {
                // If the shape is a polygon
                if (shape.getType() == 0){
                    polygonSpriteBatch.begin();
                    RigidPolygonShape polygonShape = (RigidPolygonShape) shape;
                    polygonShape.draw(polygonSpriteBatch, textures.get(0));
                    polygonSpriteBatch.end();
                }
                // Else if the shape is a circle
                else if (shape.getType() == 1){
                    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    RigidCircleShape circleShape = (RigidCircleShape) shape;
                    circleShape.draw(shapeRenderer);
                    shapeRenderer.end();
                }

            }

            // Draw a black outline around the rigidShapes
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.BLACK);
            // Loop through all the rigidShapes
            for (RigidShape shape: rigidShapes) {
                // If the shape is a polygon
                if (shape.getType() == 0){
                    RigidPolygonShape polygonShape = (RigidPolygonShape) shape;
                    shapeRenderer.polygon(polygonShape.getVerticesArray());
                }
                // Else if the shape is a circle
                else if (shape.getType() == 1){
                    RigidCircleShape circleShape = (RigidCircleShape) shape;
                    Vector2 origin = circleShape.getOrigin();
                    shapeRenderer.circle(origin.x, origin.y, circleShape.getRadius());
                }
            }
            shapeRenderer.end();
        }
        else if (mode == 1){
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            for (SoftPolygonShape shape : softShapes){
                shapeRenderer.setColor(Color.BLACK);
                Vector2[][] springs = shape.getSpringVectors();
                for (Vector2[] spring : springs){
                    shapeRenderer.line(spring[0], spring[1]);
                }

                if (shape.getMatcher() == null){
                    continue;
                }
//                for (SoftPoint softPoint : shape.getMatcher().getPoints()){
//                    shapeRenderer.circle(softPoint.getOrigin().x, softPoint.getOrigin().y, 5);
//                }
//                for (Vector2[] matcherSprings : shape.getMatcher().getSpringVectors()){
//                    shapeRenderer.line(matcherSprings[0], matcherSprings[1]);
//                }

            }
            shapeRenderer.end();
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        // Draw the contact points
        shapeRenderer.setColor(Color.BLUE);
        for (Vector2 cp: allContactPoints){
            shapeRenderer.circle(cp.x, cp.y, 5);
        }
        shapeRenderer.end();
    }
}
