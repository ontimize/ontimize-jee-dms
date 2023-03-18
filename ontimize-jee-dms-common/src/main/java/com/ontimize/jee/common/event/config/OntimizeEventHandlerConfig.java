package com.ontimize.jee.common.event.config;

import com.ontimize.jee.common.event.EventHandler;
import com.ontimize.jee.common.event.IEventHandler;
import com.ontimize.jee.common.event.annotation.OntimizeEventListener;
import com.ontimize.jee.common.event.listener.IEventListener;
import org.reflections.Reflections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration
public class OntimizeEventHandlerConfig {

    @Bean
    public IEventHandler ontimizeEventHandler(){
        final IEventHandler result = new EventHandler();
        List<IEventListener> observers = this.getAnnotedOntimzeEventListeners();
        result.addEventListener( observers.toArray( new IEventListener[0] ) );
        return result;
    }

    public List<IEventListener> getAnnotedOntimzeEventListeners() {
        List<IEventListener> result = new ArrayList<>();
        Reflections reflections = new Reflections( "com.imatia.platform" );
        Set<Class<?>> listAnnotatedClasses = reflections.getTypesAnnotatedWith( OntimizeEventListener.class );

        for( Class<?> clazz : listAnnotatedClasses ){
            try{
                IEventListener eventListener = (IEventListener) clazz.getDeclaredConstructor().newInstance();
                result.add( eventListener );
            }
            catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e ){
                e.printStackTrace();
            }
        }

        return result;
    }
}
