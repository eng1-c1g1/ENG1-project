package io.github.maze11.fixedStep;

/**
 * Interface used by FixedStepper to send fixedUpdate callbacks
 */
interface FixedUpdateListener {
    public void fixedUpdate(float deltaTime);
}
