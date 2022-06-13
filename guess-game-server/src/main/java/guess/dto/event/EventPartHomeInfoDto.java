package guess.dto.event;

import guess.domain.Language;
import guess.domain.source.EventPart;
import guess.domain.source.Place;
import guess.util.LocalizationUtils;

import java.time.LocalDate;

/**
 * Event part home info DTO.
 */
public record EventPartHomeInfoDto(long id, String name, LocalDate startDate, LocalDate endDate, String placeCity,
                                   String eventTypeLogoFileName) {
    public static EventPartHomeInfoDto convertToDto(EventPart eventPart, Language language) {
        String logoFileName = (eventPart.getEventType() != null) ? eventPart.getEventType().getLogoFileName() : null;
        Place place = eventPart.getPlace();
        String placeCity = (place != null) ? LocalizationUtils.getString(place.getCity(), language) : null;

        return new EventPartHomeInfoDto(
                eventPart.getId(),
                LocalizationUtils.getString(eventPart.getName(), language),
                eventPart.getStartDate(),
                eventPart.getEndDate(),
                placeCity,
                logoFileName);
    }
}
