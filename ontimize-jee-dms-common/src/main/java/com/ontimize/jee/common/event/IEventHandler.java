package com.ontimize.jee.common.event;

import com.ontimize.jee.common.event.data.IEventData;
import com.ontimize.jee.common.event.listener.IEventListener;

import java.util.List;


/**
 * Class that handles the events of an application
 */
public interface IEventHandler {

    /**
     * Method registering EventListners in the Even Handler
     *
     * @param listeners The Event Listeners
     */
    void addEventListener ( final IEventListener... listeners );


    /**
     * Method that removes the EventListners record in the Even Handler
     *
     * @param listeners The Event Listeners
     */
    void removeEventListener ( final IEventListener... listeners );


    /**
     * Method that deletes all Event Listeners registered to a given event
     *
     * @param events The events to delete all Event Listeners registered
     */
    void clearEvent ( final Enum... events );


    /**
     * Method that triggers Event Listeners registered to an event
     *
     * @param event The event
     * @param data The event data
     */
    void trigger ( final Enum event, final IEventData data );


    /**
     * Method that triggers Event Listeners registered to various events
     *
     * @param events The List of events
     * @param data The event data
     */
    void trigger ( final List<Enum> events, final IEventData data );
}

