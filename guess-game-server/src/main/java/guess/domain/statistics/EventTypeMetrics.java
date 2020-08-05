package guess.domain.statistics;

import guess.domain.source.EventType;

import java.time.LocalDate;

/**
 * Event type metrics.
 */
public class EventTypeMetrics extends AbstractEventTypeMetrics {
    private final EventType eventType;

    public EventTypeMetrics(EventType eventType, LocalDate startDate, long age, long duration, long eventsQuantity,
                            long talksQuantity, long speakersQuantity, long javaChampionsQuantity, long mvpsQuantity) {
        super(startDate, age, duration, eventsQuantity, talksQuantity, speakersQuantity, javaChampionsQuantity, mvpsQuantity);

        this.eventType = eventType;
    }

    public EventType getEventType() {
        return eventType;
    }
}
