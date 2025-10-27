package io.github.maze11.messages;

import java.util.ArrayList;
import java.util.List;

public class MessagePublisher {
    List<MessageListener> listeners = new ArrayList<MessageListener>();

    public void addListener(MessageListener messageListener){
        listeners.add(messageListener);
    }
    public boolean removeListener(MessageListener messageListener){
        return listeners.remove(messageListener);
    }
    public void publish(Message message){
        for(MessageListener listener : listeners){
            listener.receive(message);
        }
    }
}
