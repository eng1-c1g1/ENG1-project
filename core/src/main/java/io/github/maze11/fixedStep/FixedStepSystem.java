package io.github.maze11.fixedStep;

import com.badlogic.ashley.core.EntitySystem;

/**
 * An entity system that subscribes to fixedUpdate callbacks
 */
public abstract class FixedStepSystem extends EntitySystem implements FixedUpdateListener {

    public FixedStepSystem(FixedStepper stepper) {
        stepper.addListener(this);
    }
}
