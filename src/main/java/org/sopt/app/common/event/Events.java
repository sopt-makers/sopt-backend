package org.sopt.app.common.event;

import static java.util.Objects.nonNull;

import org.springframework.context.ApplicationEventPublisher;

public class Events {

    private static ApplicationEventPublisher publisher;

    public static void setPublisher(ApplicationEventPublisher publisher) {
        Events.publisher = publisher;
    }

    public static void raise(Object event) {
        if (nonNull(publisher)) {
            publisher.publishEvent(event);
        }
    }
}
