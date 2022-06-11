package guess.dto.event;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.domain.source.EventDays;
import guess.util.LocalizationUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Event part DTO (super brief).
 */
public class EventPartSuperBriefDto {
    private final long id;
    private final long eventTypeId;
    private final long organizerId;
    private final String name;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public EventPartSuperBriefDto(long id, long eventTypeId, long organizerId, String name, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.eventTypeId = eventTypeId;
        this.organizerId = organizerId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public static EventPartSuperBriefDto convertToSuperBriefDto(Event event, EventDays eventDays, Language language) {
        return new EventPartSuperBriefDto(
                event.getId(),
                event.getEventType().getId(),
                event.getEventType().getOrganizer().getId(),
                LocalizationUtils.getString(event.getName(), language),
                eventDays.getStartDate(),
                eventDays.getEndDate());
    }

    public static List<EventPartSuperBriefDto> convertToSuperBriefDto(List<Event> events, Language language) {
        List<EventPartSuperBriefDto> eventPartSuperBriefDtos = new ArrayList<>();

        for (Event event : events) {
            for (EventDays eventDays : event.getDays()) {
                eventPartSuperBriefDtos.add(convertToSuperBriefDto(event, eventDays, language));
            }
        }

        return eventPartSuperBriefDtos;
    }
}
