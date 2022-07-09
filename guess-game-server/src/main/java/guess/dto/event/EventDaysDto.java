package guess.dto.event;

import guess.domain.Language;
import guess.domain.source.EventDays;
import guess.util.LocalizationUtils;

import java.time.LocalDate;
import java.util.List;

public class EventDaysDto {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String placeCity;
    private final String placeVenueAddress;
    private final String mapCoordinates;

    public EventDaysDto(LocalDate startDate, LocalDate endDate, String placeCity, String placeVenueAddress, String mapCoordinates) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.placeCity = placeCity;
        this.placeVenueAddress = placeVenueAddress;
        this.mapCoordinates = mapCoordinates;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getPlaceCity() {
        return placeCity;
    }

    public String getPlaceVenueAddress() {
        return placeVenueAddress;
    }

    public String getMapCoordinates() {
        return mapCoordinates;
    }

    public static EventDaysDto convertToDto(EventDays eventDays, Language language) {
        var place = eventDays.getPlace();
        String placeCity = (place != null) ? LocalizationUtils.getString(place.getCity(), language) : null;
        String placeVenueAddress = (place != null) ? LocalizationUtils.getString(place.getVenueAddress(), language) : null;
        String mapCoordinates = (place != null) ? place.getMapCoordinates() : null;

        return new EventDaysDto(
                eventDays.getStartDate(),
                eventDays.getEndDate(),
                placeCity,
                placeVenueAddress,
                mapCoordinates);
    }

    public static List<EventDaysDto> convertToDto(List<EventDays> eventDaysList, Language language) {
        return eventDaysList.stream()
                .map(ed -> convertToDto(ed, language))
                .toList();
    }
}
