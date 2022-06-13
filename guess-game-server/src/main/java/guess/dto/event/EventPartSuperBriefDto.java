package guess.dto.event;

import guess.domain.Language;
import guess.domain.source.EventPart;
import guess.util.LocalizationUtils;

import java.time.LocalDate;
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

    public static EventPartSuperBriefDto convertToSuperBriefDto(EventPart eventPart, Language language) {
        return new EventPartSuperBriefDto(
                eventPart.getId(),
                eventPart.getEventType().getId(),
                eventPart.getEventType().getOrganizer().getId(),
                LocalizationUtils.getString(eventPart.getName(), language),
                eventPart.getStartDate(),
                eventPart.getEndDate());
    }

    public static List<EventPartSuperBriefDto> convertToSuperBriefDto(List<EventPart> eventParts, Language language) {
        return eventParts.stream()
                .map(ep -> convertToSuperBriefDto(ep, language))
                .toList();
    }
}
