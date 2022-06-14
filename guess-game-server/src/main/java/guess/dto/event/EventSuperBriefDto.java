package guess.dto.event;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.util.LocalizationUtils;

import java.util.List;

/**
 * Event DTO (super brief).
 */
public class EventSuperBriefDto {
    private final long id;
    private final long eventTypeId;
    private final long organizerId;
    private final String name;
    private final List<EventDaysDto> days;

    public EventSuperBriefDto(long id, long eventTypeId, long organizerId, String name, List<EventDaysDto> days) {
        this.id = id;
        this.eventTypeId = eventTypeId;
        this.organizerId = organizerId;
        this.name = name;
        this.days = days;
    }

    public long getId() {
        return id;
    }

    public long getEventTypeId() {
        return eventTypeId;
    }

    public long getOrganizerId() {
        return organizerId;
    }

    public String getName() {
        return name;
    }

    public List<EventDaysDto> getDays() {
        return days;
    }

    public static EventSuperBriefDto convertToSuperBriefDto(Event event, Language language) {
        return new EventSuperBriefDto(
                event.getId(),
                event.getEventType().getId(),
                event.getEventType().getOrganizer().getId(),
                LocalizationUtils.getString(event.getName(), language),
                EventDaysDto.convertToDto(event.getDays(), language));
    }

    public static List<EventSuperBriefDto> convertToSuperBriefDto(List<Event> events, Language language) {
        return events.stream()
                .map(e -> convertToSuperBriefDto(e, language))
                .toList();
    }
}
