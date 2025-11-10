package io.github.maze11.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import io.github.maze11.components.HiddenWallComponent;
import io.github.maze11.fixedStep.FixedStepper;
import io.github.maze11.fixedStep.IteratingFixedStepSystem;
import io.github.maze11.messages.MessageListener;
import io.github.maze11.messages.MessagePublisher;
import io.github.maze11.messages.MessageType;
import io.github.maze11.messages.PressurePlateTriggerMessage;

/**
 * Listens for pressure plates that are pressed, removes hidden walls when a corresponding pressure plate is pressed
 */
public class HiddenWallSystem extends IteratingFixedStepSystem {

    private final ComponentMapper<HiddenWallComponent> wallMapper = ComponentMapper.getFor(HiddenWallComponent.class);

    private final MessageListener messageListener;

    public HiddenWallSystem(FixedStepper stepper, MessagePublisher publisher) {
        super(stepper, Family.all(HiddenWallComponent.class).get());
        this.messageListener = new MessageListener(publisher);
    }

    @Override
    protected void fixedStepProcessEntity(Entity entity, float deltaTime) {
        HiddenWallComponent wall = wallMapper.get(entity);
        while (messageListener.hasNext()) {
            var message = messageListener.next();

            if (message.type == MessageType.PRESSURE_PLATE_TRIGGER) {
                PressurePlateTriggerMessage pressureMessage = (PressurePlateTriggerMessage) message;
                if (pressureMessage.triggers.equals(wall.triggeredBy)) {
                    getEngine().removeEntity(entity);
                }
            }
        }
    }
}
