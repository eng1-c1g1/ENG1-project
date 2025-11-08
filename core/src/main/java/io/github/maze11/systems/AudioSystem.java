package io.github.maze11.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.math.Vector2;
import io.github.maze11.components.AudioListenerComponent;
import io.github.maze11.components.TransformComponent;
import io.github.maze11.messages.*;

/**
 * Plays any sound effects from their corresponding messages
 */
public class AudioSystem extends EntitySystem {

    private final Engine engine;
    private final MessageListener messageListener;
    private final ComponentMapper<AudioListenerComponent> listenerMapper = ComponentMapper.getFor(AudioListenerComponent.class);
    private final ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);

    public AudioSystem(Engine engine, MessagePublisher messagePublisher) {
        this.engine = engine;
        this.messageListener = new MessageListener(messagePublisher);
    }

    @Override
    public void update(float deltaTime) {

        // Find the position of the listener
        var listeners = engine.getEntitiesFor(Family.all(AudioListenerComponent.class).get());
        if (listeners.size() != 1) {
            throw new RuntimeException("There must be exactly one AudioListenerComponent");
        }
        Entity listener = listeners.get(0);
        Vector2 listenPosition = new Vector2(transformMapper.get(listener).position);
        listenPosition.add(listenerMapper.get(listener).offset);

        // Play all the newly arrived sound effects
        while (messageListener.hasNext()){
            Message message = messageListener.next();
            if (message.type == MessageType.SOUND_EFFECT) {
                playSound((SoundMessage)  message, listenPosition);
            }
        }
    }

    /**
     * Plays the sound in the message specified at the appropriate volume
     */
    private void playSound(SoundMessage message, Vector2 listenerPosition){

        float volume = message.getEffectiveVolume(listenerPosition);
        message.sound.play(volume);
    }

}
