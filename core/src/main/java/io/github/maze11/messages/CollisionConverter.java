package io.github.maze11.messages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * Converts collision callbacks into messages which it sends via a publisher. This allows other systems to easily
 * access collision data
 */
public class CollisionConverter implements ContactListener {

    MessagePublisher messagePublisher;

    public CollisionConverter(MessagePublisher messagePublisher)
    {
        this.messagePublisher = messagePublisher;
    }

    @Override
    public void beginContact(Contact contact) {
        var entityA = (Entity) contact.getFixtureA().getUserData();
        var entityB = (Entity) contact.getFixtureB().getUserData();

        messagePublisher.publish(new CollisionMessage(entityA, entityB));
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
