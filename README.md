# 2D Physics Simulation  
**A powerful and versatile 2D physics simulation engine supporting both rigid and soft bodies.**

---

## How to Use  

1. **Download** the [release version](https://github.com/JJDOESIT/2D-Physics-Engine/releases/tag/v1.0.0) for your operating system.  
2. **Extract** the downloaded folder.  
3. **Run the executable** based on your OS:  
   - **Windows X64**:  
     ```bash
     ./PhysicsEngine-1.0.0-winX64/PhysicsEngine.exe
     ```  
   - **Mac X64**:  
     ```bash
     ./PhysicsEngine-1.0.0-macX64/PhysicsEngine.app/Contents/MacOS/PhysicsEngine
     ```  
   - **Linux X64**:  
     ```bash
     ./PhysicsEngine-1.0.0-linuxX64/PhysicsEngine
     ```  
4. If you encounter a "Permission Denied" error, run the following command to set the correct permissions:  
   ```bash
   chmod 700 <path to file>
---

## Features  
- **Support for Multiple Shapes**  
  - Triangle, Square, Hexagon, Octagon, and Circle  
- **Interactive Gravity Slider**  
- **Customizable Properties**  
  - Adjust mass, size, and rotation  
- **Static Objects**  
  - Non-moveable objects for dynamic interactions  

---

# Controls  

### General  
- **Choose a Shape**: Select one of the six available options.  
- **Spawn a Shape**: Left-click in an open area of the field.  
- **Move a Shape**: Use the **WASD** keys.  
- **Select a Shape**: Left-click on the shape you want to control.  
- **Delete a Shape**: Select a shape and press **Tab**.  

### Modifying Shape Properties  
- **Change Mass**:  
  - Enter a positive floating-point value in the input field.  
- **Change Side Length**:  
  - Enter a positive integer in the input field.  
- **Set Creation Rotation**:  
  - Enter a positive or negative integer in the input field to define the spawn angle.  

### Additional Features  
- **Spawn Static Shapes**:  
  - Enable the static option to spawn non-movable shapes.  
- **Switch to Soft Bodies**:  
  - Enable the soft body option to use soft-body physics.  
  - **Note**: Circles are made up of 20 sides, so it’s recommended to choose a low side length (1-100).  
  - **Switch Models**: Press **M** to toggle between the spring-pressure model and the spring-shape matcher model.

---

## Known Issues with Soft Bodies  
- **Triangle Stacking**:  
  - Triangles do not stack well due to pixel-perfect edge collisions, leading to infinite displacement.  
- **Mass Discrepancy Collisions**:  
  - Collisions between very heavy objects and significantly lighter ones can result in unpredictable behavior.  

---

## Rigid Bodies  
Explore the simulation of rigid bodies with realistic physics interactions.

| **Example 1** | **Example 2** |  
|---------------|---------------|  
| ![Rigid Body Example 1](https://github.com/user-attachments/assets/3c7a8986-45b0-4e73-bc25-f4d9f0846b44) | ![Rigid Body Example 2](https://github.com/user-attachments/assets/04ddcbfd-ab7f-4ae3-9a8d-90d1c8566d02) |  

---

## Soft Bodies  
Simulate soft bodies with flexible and dynamic behavior.

| **Example 1** | **Example 2** |  
|---------------|---------------|  
| ![Soft Body Example 1](https://github.com/user-attachments/assets/fc9da1ac-e474-4585-a333-66a7387243ee) | ![Soft Body Example 2](https://github.com/user-attachments/assets/eed22bf8-0171-4d6c-83ab-a68569c5e566) |  
