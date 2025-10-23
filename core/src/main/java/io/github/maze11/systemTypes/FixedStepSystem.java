package io.github.maze11.systemTypes;

import com.badlogic.ashley.core.EntitySystem;

public abstract class FixedStepSystem extends EntitySystem implements FixedUpdateListener {

    public FixedStepSystem(FixedStepper stepper) {
        stepper.addListener(this);
    }
}
