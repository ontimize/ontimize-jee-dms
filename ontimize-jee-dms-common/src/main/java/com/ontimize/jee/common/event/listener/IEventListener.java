package com.ontimize.jee.common.event.listener;


import com.ontimize.jee.common.event.data.IEventData;

import java.util.EventListener;
import java.util.List;

/**
 * Interface representing an Event Listener
 * {@inheritDoc}
 */
public interface IEventListener<T extends IEventData> extends EventListener {

    /**
     * Method that gets the events from the EventListener
     *
     * @return The Event list
     */
    List<Enum> getEvents();


    /**
     * Method to be executed when the corresponding event is triggered
     *
     * @param data The Event data
     */
    void run ( final T data );
}
