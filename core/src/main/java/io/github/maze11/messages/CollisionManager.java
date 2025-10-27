package io.github.maze11.messages;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class CollisionManager implements ContactListener {

    MessagePublisher messagePublisher;

    public CollisionManager(MessagePublisher messagePublisher)
    {
        this.messagePublisher = messagePublisher;
    }

    @Override
    public void beginContact(Contact contact) {
        // TODO: send collision event
        System.out.println("beginContact");
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
