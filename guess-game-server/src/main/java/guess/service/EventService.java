package guess.service;

import guess.domain.source.Event;
import guess.domain.source.EventDays;
import guess.domain.source.EventPart;
import guess.domain.source.Talk;

import java.util.List;

/**
 * Event service.
 */
public interface EventService {
    Event getEventById(long id);

    List<Event> getEvents(boolean isConferences, boolean isMeetups, Long organizerId, Long eventTypeId);

    Event getDefaultEvent(boolean isConferences, boolean isMeetups);

    EventPart getDefaultEventPart(boolean isConferences, boolean isMeetups);

    Event getEventByTalk(Talk talk);

    EventPart createEventPart(Event event, EventDays eventDays);

    List<EventPart> convertEventToEventParts(Event event);

    List<EventPart> convertEventsToEventParts(List<Event> events);
}
