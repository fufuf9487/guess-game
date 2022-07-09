package guess.dto.event;

import guess.domain.Language;
import guess.domain.source.Event;

import java.util.List;

/**
 * Event DTO (brief).
 */
public class EventBriefDto extends EventSuperBriefDto {
    private final String eventTypeLogoFileName;

    public EventBriefDto(EventSuperBriefDto eventSuperBriefDto, String eventTypeLogoFileName) {
        super(eventSuperBriefDto.getId(), eventSuperBriefDto.getEventTypeId(), eventSuperBriefDto.getOrganizerId(),
                eventSuperBriefDto.getName(), eventSuperBriefDto.getDays());
        this.eventTypeLogoFileName = eventTypeLogoFileName;
    }

    public String getEventTypeLogoFileName() {
        return eventTypeLogoFileName;
    }

    public static EventBriefDto convertToBriefDto(EventSuperBriefDto eventSuperBriefDto, Event event) {
        String logoFileName = (event.getEventType() != null) ? event.getEventType().getLogoFileName() : null;

        return new EventBriefDto(
                eventSuperBriefDto,
                logoFileName);
    }

    public static EventBriefDto convertToBriefDto(Event event, Language language) {
        return convertToBriefDto(convertToSuperBriefDto(event, language), event);
    }

    public static List<EventBriefDto> convertToBriefDto(List<Event> events, Language language) {
        return events.stream()
                .map(e -> convertToBriefDto(e, language))
                .toList();
    }
}
