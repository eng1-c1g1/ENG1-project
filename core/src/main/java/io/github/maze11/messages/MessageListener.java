package io.github.maze11.messages;

import java.util.LinkedList;
import java.util.Queue;

public class MessageListener {
    private final Queue<Message> queue = new LinkedList<Message>();
    public final MessagePublisher publisher;

    public MessageListener(MessagePublisher publisher) {
        this.publisher = publisher;
    }

    public void receive(Message message){
        queue.add(message);
    }
    public boolean hasNext(){
        return !queue.isEmpty();
    }
    public Message next(){
        return queue.poll();
    }
}
