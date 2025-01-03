package it.jjdoes.PhysicsEngine.SoftBody;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GodCode {

    //Frames per second
    final int    FPS			= 120;

    //Number of points
    final int    PTS            = 20;        //20 by default, 50 works also

    //Number of springs
    final int    SPRS           = PTS;

    final int    LENGTH         = 75;

    final double MASS           = 1.0;
    final double BALL_RADIUS    = 70;    //0.516 by default

    //Spring constants
    final double KS             = 755.0;    //755 by default
    final double KD             = 35.0;        //35.0 by default

    //Gravity force and user applied force
    final double GY             = 110.0;     //110.0 by default
    final double FAPP           = -1100.0;

    //Time interval for numeric integration
    final double DT             = 0.01;    //0.005 by default

    //Pressure to be reached before ball is at full capacity
    final double FINAL_PRESSURE = 70000;     //70000 by default

    //Tangential and normal damping factors
    final double TDF = 0.99;                         //0.95 by default, 1.0 works, 1.01 is cool
    //A TDF of 1.0 means frictionless boundaries.
    //If some energy were not lost due to the ball's
    //spring-damping, the ball could continue
    //traveling forever without any force.

    final double NDF = 0.1;                   //0.1  by default

    double pressure;

    JPoint2d myPoints;
    JSpring2d mySprings;


    /**************************************************
     * Initialize the applet by declaring new objects
     * of type JPoint2d and JSpring2d.
     *
     * Also, set things up for an animation.
     **************************************************/
    public void init() {
        myPoints = new JPoint2d(PTS);
        mySprings = new JSpring2d(SPRS);

        pressure = 0;

        createBall();
    }

    /**************************************************
     * Function to set up the springs.
     **************************************************/
    public void addSpring(int i, int j, int k) {
        mySprings.spr1[i] = j;
        mySprings.spr2[i] = k;

        mySprings.length[i] = Math.sqrt( (myPoints.x[j] - myPoints.x[k]) * (myPoints.x[j] - myPoints.x[k])
            + (myPoints.y[j] - myPoints.y[k]) * (myPoints.y[j] - myPoints.y[k]));
    }

    /**************************************************
     * Simple function to lay out the points of the
     * ball in a circle, then create springs between
     * these points.
     **************************************************/
    public void createBall() {
        for (int i = 0; i < PTS; i++) {
            myPoints.x[i] = BALL_RADIUS * Math.cos(i * 2 * Math.PI / PTS) + 190;
            myPoints.y[i] = BALL_RADIUS * Math.sin(i * 2 * Math.PI / PTS) + 95;
        }

        for (int i = 0; i < PTS - 1; i++) {
            addSpring(i, i, i + 1);
        }
        addSpring(PTS - 1, PTS - 1, 0);
    }

    /**************************************************
     * This function does a large part of the physics
     * calculations.  It starts by adding gravity and
     * checking for inputs, and then by taking into
     * account spring force and pressure force.
     **************************************************/
    public void accumulateForces() {
        double x1, x2, y1, y2;
        double r12d;
        double vx12;
        double vy12;
        double f;
        double fx0, fy0;
        double volume = 0;
        double pressurev;

        /**************************************************
         * Check for keyboard inputs and add gravitational
         * force.
         **************************************************/
        for (int i = 0; i < PTS; i++) {
            myPoints.fx[i] = 0;
            myPoints.fy[i] = (pressure - FINAL_PRESSURE) >= 0 ? GY*MASS : 0;

            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                myPoints.fy[i] = -FAPP*MASS;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                myPoints.fx[i] = FAPP*MASS;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                myPoints.fx[i] = -FAPP*MASS;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                myPoints.fy[i] = FAPP*MASS;
            }
        }


        /**************************************************
         * Calculate force due to each spring.
         **************************************************/
        for (int i = 0; i < SPRS; i++) {
            x1 = myPoints.x[mySprings.spr1[i]];
            x2 = myPoints.x[mySprings.spr2[i]];
            y1 = myPoints.y[mySprings.spr1[i]];
            y2 = myPoints.y[mySprings.spr2[i]];

            //Find the distance between each spring:
            r12d = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));

            //Accumulate spring forces:
            if (r12d != 0) {
                vx12 = myPoints.vx[mySprings.spr1[i]] - myPoints.vx[mySprings.spr2[i]];
                vy12 = myPoints.vy[mySprings.spr1[i]] - myPoints.vy[mySprings.spr2[i]];

                f = (r12d - mySprings.length[i]) * KS + (vx12 * (x1 - x2) + vy12 * (y1 - y2)) * KD / r12d;

                fx0 = ((x1 - x2) / r12d ) * f;
                fy0 = ((y1 - y2) / r12d ) * f;

                myPoints.fx[mySprings.spr1[i]] -= fx0;
                myPoints.fy[mySprings.spr1[i]] -= fy0;

                myPoints.fx[mySprings.spr2[i]] += fx0;
                myPoints.fy[mySprings.spr2[i]] += fy0;
            }
            //Calculate normal vectors for use with finding pressure force:
            mySprings.nx[i] = -(y1 - y2) / r12d;
            mySprings.ny[i] = (x1 - x2) / r12d;
        }

        /**************************************************
         * This uses the divergence theorem (2d version)
         * to calculate the volume (area) of the body (which is
         * why we needed to calculate the normal vectors
         * previously), and then uses that to calculate
         * pressure (since P*V = constant?).
         *
         * TODO: rewrite this using Green's theorem /
         * surveyor's formula for area
         **************************************************/
        for (int i = 0; i < SPRS; i++) {
            x1 = myPoints.x[mySprings.spr1[i]];
            x2 = myPoints.x[mySprings.spr2[i]];
            y1 = myPoints.y[mySprings.spr1[i]];
            y2 = myPoints.y[mySprings.spr2[i]];

            r12d = Math.sqrt((x1 - x2) *(x1 - x2)  +  (y1 - y2) * (y1 - y2));

            volume += 0.5 * Math.abs(x1 - x2) * Math.abs(mySprings.nx[i]) * (r12d);
        }

        for (int i = 0; i < SPRS; i++) {
            x1 = myPoints.x[mySprings.spr1[i]];
            x2 = myPoints.x[mySprings.spr2[i]];
            y1 = myPoints.y[mySprings.spr1[i]];
            y2 = myPoints.y[mySprings.spr2[i]];

            r12d = Math.sqrt((x1 - x2) * (x1 - x2)  +  (y1 - y2) * (y1 - y2));

            pressurev = r12d * pressure * (1.0/volume);

            myPoints.fx[mySprings.spr1[i]] += mySprings.nx[i]*pressurev;
            myPoints.fy[mySprings.spr1[i]] += mySprings.ny[i]*pressurev;
            myPoints.fx[mySprings.spr2[i]] += mySprings.nx[i]*pressurev;
            myPoints.fy[mySprings.spr2[i]] += mySprings.ny[i]*pressurev;
        }
    }

    public void draw(ShapeRenderer shapeRenderer){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLUE);
        for (int i = 0; i < PTS - 1; i++) {
            shapeRenderer.line((float) myPoints.x[i], (float) myPoints.y[i], (float) myPoints.x[i+1], (float) myPoints.y[i+1]);
        }
        shapeRenderer.line((float) myPoints.x[PTS - 1], (float) myPoints.y[PTS - 1], (float) myPoints.x[0], (float) myPoints.y[0]);
        shapeRenderer.end();
    }

    /**************************************************
     * Heun Predictor-Corrector Integration
     * (with bounds checking).
     **************************************************/
    public void integrateHeun() {
        double drx, dry;

        double fxsaved[] = new double[PTS];
        double fysaved[] = new double[PTS];

        double vxsaved[] = new double[PTS];
        double vysaved[] = new double[PTS];

        for (int i = 0; i < PTS; i++) {
            fxsaved[i] = myPoints.fx[i];
            fysaved[i] = myPoints.fy[i];

            vxsaved[i] = myPoints.vx[i];
            vysaved[i] = myPoints.vy[i];

            myPoints.vx[i] += myPoints.fx[i] / MASS * DT;
            drx = myPoints.vx[i] * DT;

            myPoints.x[i] += drx;

            myPoints.vy[i] += myPoints.fy[i] / MASS * DT;
            dry = myPoints.vy[i] * DT;

            myPoints.y[i] += dry;
        }

        accumulateForces();

        for (int i=0; i<PTS; i++) {
            myPoints.vx[i] = vxsaved[i] + (myPoints.fx[i] + fxsaved[i]) / MASS * DT/2;
            drx = myPoints.vx[i] * DT;

            myPoints.x[i] += drx;

            myPoints.vy[i] = vysaved[i] + (myPoints.fy[i] + fysaved[i]) / MASS * DT/2;
            dry = myPoints.vy[i] * DT;

            myPoints.y[i] += dry;

            /**************************************************
             * From here, the rest of the method is devoted to
             * boundary checking.
             **************************************************/
            if (myPoints.x[i] > 380)
                myPoints.x[i] = 380;
            if (myPoints.y[i] < 0)
                myPoints.y[i] = 0;
            if (myPoints.x[i] < 0)
                myPoints.x[i] = 0;
            if (myPoints.y[i] > 380)
                myPoints.y[i] = 380;

            if (myPoints.x[i] + drx >  Math.sqrt(36100 - Math.pow(myPoints.y[i] - 190, 2)) + 190 ||
                myPoints.x[i] + drx < -Math.sqrt(36100 - Math.pow(myPoints.y[i] - 190, 2)) + 190)
            {
                drx *= -1;                           //These are temporary until I do
                dry *= -1;                           //the math to get more exact values.

                double vx0 = myPoints.vx[i];
                double vy0 = myPoints.vy[i];

                double sinTheta = (myPoints.y[i] - 190.0) / 190.0;
                double cosTheta = (myPoints.x[i] - 190.0) / 190.0;

                myPoints.vx[i] = -vx0;
                myPoints.vy[i] = -vy0;
                myPoints.vx[i] = vy0 * (-TDF * sinTheta * cosTheta - NDF * sinTheta * cosTheta) + vx0 * (TDF * sinTheta * sinTheta - NDF * cosTheta * cosTheta);
                myPoints.vy[i] = vy0 * (TDF * cosTheta * cosTheta - NDF * sinTheta * sinTheta) + vx0 * (-TDF * sinTheta * cosTheta - NDF * sinTheta * cosTheta);
            }

            if ((myPoints.y[i] > 250 || myPoints.y[i] < 130) && myPoints.y[i] > Math.sqrt(36100 - Math.pow(myPoints.x[i] - 190, 2)) + 190)
                myPoints.y[i] = Math.sqrt(Math.abs(36100 - Math.pow(myPoints.x[i] - 190, 2))) + 190;
            if ((myPoints.y[i] > 250 || myPoints.y[i] < 130) && myPoints.y[i] < -Math.sqrt(36100 - Math.pow(myPoints.x[i] - 190, 2)) + 190)
                myPoints.y[i] = -Math.sqrt(Math.abs(36100 - Math.pow(myPoints.x[i] - 190, 2))) + 190;

            if ((myPoints.x[i] > 250 || myPoints.x[i] < 130) && myPoints.x[i] > Math.sqrt(36100 - Math.pow(myPoints.y[i] - 190, 2)) + 190)
                myPoints.x[i] = Math.sqrt(Math.abs(36100 - Math.pow(myPoints.y[i] - 190, 2))) + 190;
            if ((myPoints.y[i] > 250 || myPoints.x[i] < 130) && myPoints.x[i] < -Math.sqrt(36100 - Math.pow(myPoints.y[i] - 190, 2)) + 190)
                myPoints.x[i] = -Math.sqrt(Math.abs(36100 - Math.pow(myPoints.y[i] - 190, 2))) + 190;

        }
    }

    /**************************************************
     * Idle function that runs all of the physics and
     * math calculations.  At the start of the program,
     * it simulates blowing the ball up by incrementing
     * the total pressure until it reaches a specified
     * value.
     **************************************************/
    public void idle() {
        accumulateForces();
        integrateHeun();

        if (pressure < FINAL_PRESSURE) {
            pressure += FINAL_PRESSURE / 300;
        }
    }


    /**************************************************
     * Class for the points object.  It includes arrays
     * to describe forces, velocities, and positions
     * of the points.
     **************************************************/
    public class JPoint2d
    {
        public double[] x;
        public double[] y;
        public double[] vx, vy;
        public double[] fx, fy;

        public JPoint2d(int i) {
            x = new double[i];
            y = new double[i];
            vx = new double[i];
            vy = new double[i];
            fx = new double[i];
            fy = new double[i];
        }

        public int[] getArrX() {
            int[] arrX = new int[PTS];
            for (int i = 0; i < PTS; i++) {
                arrX[i] = (int)x[i];
            }
            return arrX;
        }
        public int[] getArrY() {
            int[] arrY = new int[PTS];
            for (int i = 0; i < PTS; i++) {
                arrY[i] = (int)y[i];
            }
            return arrY;
        }
    }

    /**************************************************
     *Class for the springs object.  It includes arrays
     * to describe point-indexes, length, and normal
     * forces of the springs.
     **************************************************/
    public class JSpring2d {
        private int[] spr1, spr2;
        private double[] length;
        private double[] nx, ny;

        public JSpring2d(int k) {
            spr1 = new int[k];
            spr2 = new int[k];
            length = new double[k];
            nx = new double[k];
            ny = new double[k];
        }
    }
}
