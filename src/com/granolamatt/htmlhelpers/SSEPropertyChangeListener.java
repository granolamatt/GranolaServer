/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.htmlhelpers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;

/**
 *
 * @author root
 */
public class SSEPropertyChangeListener implements PropertyChangeListener {

    private final PropertyChangeSupport mySupport;
    private final EventOutput eventOutput = new EventOutput();
    private final ExecutorService threadPool = Executors.newSingleThreadExecutor();
    private final PropertyChangeListener listener = this;

    public SSEPropertyChangeListener(PropertyChangeSupport change) {
        mySupport = change;
    }

    public EventOutput register() {
        mySupport.addPropertyChangeListener(this);
        return eventOutput;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        
        Runnable r = new Runnable() {
            @Override
            public void run() {
                System.out.println("Sending event " + evt);
                try {
                    final OutboundEvent.Builder eventBuilder
                            = new OutboundEvent.Builder();
                    eventBuilder.name(evt.getPropertyName());
                    eventBuilder.data(evt.getNewValue());
                    final OutboundEvent event = eventBuilder.build();
                    eventOutput.write(event);
                } catch (IOException e) {
                    System.out.println("Error with fire");
                    mySupport.removePropertyChangeListener(listener);
                    try {
                        eventOutput.close();
                    } catch (IOException ioClose) {
                        throw new RuntimeException(
                                "Error when closing the event output.", ioClose);
                    }
                }
            }
        };
        threadPool.submit(r);
    }

}
