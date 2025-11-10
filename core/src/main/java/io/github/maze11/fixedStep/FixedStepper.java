package io.github.maze11.fixedStep;

import java.util.ArrayList;

/**
 * Steps the simulation forward.
 * Ensures that a fixedUpdate is fired at intervals of TIME_STEP,
 * regardless of the frame rate the game is running at.
 */
public class FixedStepper {

    public static final float TIME_STEP = 1/60f;
    private final ArrayList<FixedUpdateListener> listeners = new ArrayList<>();
    private float accumulator = 0f;

    // Default visibility so that it is visible from the other members of this package, but not outside
    void addListener(FixedUpdateListener listener) {
        listeners.add(listener);
    }
    void removeListener(FixedUpdateListener listener) {
        listeners.remove(listener);
    }

    private void fireFixedUpdate() {
        for (FixedUpdateListener listener : listeners) {
            listener.fixedUpdate(TIME_STEP);
        }
    }

    public void advanceSimulation(float deltaTime) {
        accumulator += deltaTime;
        // step the physics world in fixed increments
        while (accumulator >= TIME_STEP) {
            // 6 velocity, 2 positions iterations (standard values)
            fireFixedUpdate();
            accumulator -= TIME_STEP;
        }
    }
}
