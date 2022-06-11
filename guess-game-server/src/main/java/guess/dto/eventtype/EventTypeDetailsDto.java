package guess.dto.eventtype;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.dto.event.EventPartBriefDto;

import java.util.List;

/**
 * Event type details DTO.
 */
public record EventTypeDetailsDto(EventTypeDto eventType, List<EventPartBriefDto> eventParts) {
    public static EventTypeDetailsDto convertToDto(EventType eventType, List<Event> events, Language language) {
        return new EventTypeDetailsDto(
                EventTypeDto.convertToDto(eventType, language),
                EventPartBriefDto.convertToBriefDto(events, language));
    }
}
