package io.github.maze11.systemTypes;

import java.util.ArrayList;

/**
 * Steps the simulation forward when fireFixedUpdate is called
 */
public class FixedStepper {

    private ArrayList<FixedUpdateListener> listeners = new ArrayList<>();

    public void addListener(FixedUpdateListener listener) {
        listeners.add(listener);
    }
    public void removeListener(FixedUpdateListener listener) {
        listeners.remove(listener);
    }

    public void fireFixedUpdate(float deltaTime) {
        for (FixedUpdateListener listener : listeners) {
            listener.fixedUpdate(deltaTime);
        }
    }
}
