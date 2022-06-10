package guess.domain.source;

import java.time.LocalDate;

/**
 * Event days.
 */
public class EventDays {
    private LocalDate startDate;
    private LocalDate endDate;

    private long placeId;
    private Place place;

    public EventDays() {
    }

    public EventDays(LocalDate startDate, LocalDate endDate, Place place) {
        this.startDate = startDate;
        this.endDate = endDate;

        this.place = place;
        this.placeId = place.getId();
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(long placeId) {
        this.placeId = placeId;
    }
}
