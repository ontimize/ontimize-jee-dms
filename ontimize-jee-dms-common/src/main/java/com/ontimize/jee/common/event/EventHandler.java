package com.ontimize.jee.common.event;

import com.ontimize.jee.common.event.data.IEventData;
import com.ontimize.jee.common.event.listener.IEventListener;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;


/**
 * Implementation of IEventHandler
 * {@inheritDoc}
 */
@NoArgsConstructor
public class EventHandler implements IEventHandler{

    /** Logger */
    private static final Logger LOGGER = Logger.getLogger( "com.imatia.platform.hr.model.core.dms.service.core.event.handler.EventHandler" );

    /** Map with the active Event Listeners */
    private final Map<Enum, Set<IEventListener>> events = new HashMap<>();



    @Override
    public void addEventListener ( final IEventListener... listeners ) {
        //Browse all Event Listeners
        for ( final IEventListener listener : listeners ) {
            this.addEventListenerForEachEvent ( listener );
        }
    }


    /**
     * Method that registers an eventListener in its corresponding event for each of the events registered in the event listener
     *
     * @param listener The Event Listener
     */
    private void addEventListenerForEachEvent ( final IEventListener listener ) {
        //Browse all events in Event Listener
        for ( final Object event : listener.getEvents() ) {

            //Check if the event isn't registered in the event map of the handler
            this.events.computeIfAbsent( (Enum) event, key -> new HashSet<>() );

            //Register event listener for this event
            this.events.get( event ).add( listener );
        }
    }


    @Override
    public void removeEventListener ( final IEventListener... listeners ) {
        //Browse all Event Listeners
        for ( final IEventListener listener : listeners ) {
            this.removeEventListenerForEachEvent ( listener );
        }
    }


    /**
     * Method that deregisters an eventListener to its corresponding event for each of the events registered in the event listener
     *
     * @param listener The Event Listener
     */
    private void removeEventListenerForEachEvent ( final IEventListener listener ) {
        //Browse all events in Event Listener
        for ( final Object event : listener.getEvents() ) {

            //Get all EventListeners in event map
            final Set<IEventListener> registerListeners = this.events.get( event );

            //Get Event listeners to must deregisters
            final Set<IEventListener> resultOfListenerSearch = registerListeners.stream().filter(target -> target == listener ).collect(Collectors.toSet() );

            //Deregister Event Listeners
            resultOfListenerSearch.stream().forEach( registerListeners::remove );
        }
    }


    @Override
    public void clearEvent ( final Enum... events ) {
        //Browse events to remove
        for ( final Enum event : events ) {
            this.events.remove( event );
        }
    }


    @Override
    public void trigger ( final Enum event, final IEventData data ) {
        //Check if event listeners are registered in the triggered event
        if( !this.events.isEmpty() && this.events.containsKey( event ) ) { // If event listeners are registered
            //Set triggered event in data
            data.setEvent( event );

            //Run all event listeners in triggered event
            this.events.get( event ).stream().forEach( target -> target.run( data ));
        }
    }


    @Override
    public void trigger ( final List<Enum> events, final IEventData data ) {
        //Trigger event for each triggered events
        events.stream().forEach( target -> this.trigger( target, data ) );
    }
}

