package io.github.maze11.messages;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to send messages to all subscribed listeners
 */
public class MessagePublisher {
    public List<MessageListener> listeners = new ArrayList<MessageListener>();

    /** Adds a listener to be able to recieve messages from this publisher */
    public void addListener(MessageListener messageListener){
        listeners.add(messageListener);
    }

    /** Causes the listener to become unsubscribed and no longer receive messages */
    public boolean removeListener(MessageListener messageListener){
        return listeners.remove(messageListener);
    }

    /** Sends a message to all subscribed listeners */
    public void publish(Message message){
        for (MessageListener listener : listeners) {
            listener.receive(message);
        }
    }
}
