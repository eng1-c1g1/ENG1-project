package io.github.maze11.messages;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Listens to messages sent out by a MessagePublisher. Any messages received are added to a queue, and can be
 * queried in the order they are received. Queue representation is used so that systems that use message listeners
 * can dequeue and process events whenever the system wakes up, as opposed to immediately.
 */
public class MessageListener {
    private final Queue<Message> queue = new LinkedList<Message>();
    public MessagePublisher publisher;

    public MessageListener(MessagePublisher publisher) {
        this.publisher = publisher;
        publisher.addListener(this);
    }

    // package private since this should only be called by the publisher
    void receive(Message message){
        queue.add(message);
    }
    public boolean hasNext(){
        return !queue.isEmpty();
    }
    public Message next(){
        return queue.poll();
    }

    /**
     * Removes the listener from its associated publisher. It will no longer receive messages and can be removed by
     * The garbage collector if all other references are removed
     */
    public void dispose(){
        if (publisher != null) {
            publisher.removeListener(this);
            publisher = null;
        }
    }

    /** Returns true if dispose() has been called, false otherwise
     */
    public boolean isDisposed(){
        return publisher == null;
    }
}
