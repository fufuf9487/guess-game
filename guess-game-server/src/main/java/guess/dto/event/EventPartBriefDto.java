package guess.dto.event;

import guess.domain.Language;
import guess.domain.source.Event;
import guess.domain.source.EventDays;
import guess.util.LocalizationUtils;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Event part DTO (brief).
 */
public class EventPartBriefDto extends EventPartSuperBriefDto {
    private final long duration;
    private final String placeCity;
    private final String placeVenueAddress;
    private final String eventTypeLogoFileName;

    public EventPartBriefDto(EventPartSuperBriefDto eventPartSuperBriefDto, long duration, String placeCity, String placeVenueAddress,
                             String eventTypeLogoFileName) {
        super(eventPartSuperBriefDto.getId(), eventPartSuperBriefDto.getEventTypeId(), eventPartSuperBriefDto.getOrganizerId(),
                eventPartSuperBriefDto.getName(), eventPartSuperBriefDto.getStartDate(), eventPartSuperBriefDto.getEndDate());
        this.duration = duration;
        this.placeCity = placeCity;
        this.placeVenueAddress = placeVenueAddress;
        this.eventTypeLogoFileName = eventTypeLogoFileName;
    }

    public long getDuration() {
        return duration;
    }

    public String getPlaceCity() {
        return placeCity;
    }

    public String getPlaceVenueAddress() {
        return placeVenueAddress;
    }

    public String getEventTypeLogoFileName() {
        return eventTypeLogoFileName;
    }

    public static EventPartBriefDto convertToBriefDto(EventPartSuperBriefDto eventPartSuperBriefDto, Event event, EventDays eventDays, Language language) {
        long duration = (ChronoUnit.DAYS.between(eventPartSuperBriefDto.getStartDate(), eventPartSuperBriefDto.getEndDate()) + 1);
        var place = eventDays.getPlace();
        String placeCity = (place != null) ? LocalizationUtils.getString(place.getCity(), language) : null;
        String placeVenueAddress = (place != null) ? LocalizationUtils.getString(place.getVenueAddress(), language) : null;
        String logoFileName = (event.getEventType() != null) ? event.getEventType().getLogoFileName() : null;

        return new EventPartBriefDto(
                eventPartSuperBriefDto,
                duration,
                placeCity,
                placeVenueAddress,
                logoFileName);
    }

    public static EventPartBriefDto convertToBriefDto(Event event, EventDays eventDays, Language language) {
        return convertToBriefDto(convertToSuperBriefDto(event, eventDays, language), event, eventDays, language);
    }

    public static List<EventPartBriefDto> convertToBriefDto(List<Event> events, Language language) {
        List<EventPartBriefDto> eventPartBriefDtos = new ArrayList<>();

        for (Event event : events) {
            for (EventDays eventDays : event.getDays()) {
                eventPartBriefDtos.add(convertToBriefDto(event, eventDays, language));
            }
        }

        return eventPartBriefDtos;
    }
}
