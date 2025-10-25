package io.github.maze11.systemTypes;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

/**
 * An iterating system that subscribes to fixedUpdate callbacks
 */
public abstract class IteratingFixedStepSystem extends IteratingSystem implements FixedUpdateListener {

    public IteratingFixedStepSystem(FixedStepper stepper, Family family) {
        super(family);
        stepper.addListener(this);
    }

    public void fixedUpdate(float deltaTime) {
        for (Entity entity : getEntities()) {
            fixedStepProcessEntity(entity, deltaTime);
        }
    }

    protected abstract void fixedStepProcessEntity(Entity entity, float deltaTime);
}
