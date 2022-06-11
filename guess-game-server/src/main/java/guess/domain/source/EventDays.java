package guess.domain.source;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Event days.
 */
public class EventDays implements Serializable {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventDays)) return false;
        EventDays eventDays = (EventDays) o;
        return Objects.equals(getStartDate(), eventDays.getStartDate()) && Objects.equals(getEndDate(), eventDays.getEndDate()) && Objects.equals(getPlace(), eventDays.getPlace());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStartDate(), getEndDate(), getPlace());
    }

    @Override
    public String toString() {
        return "EventDays{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", placeId=" + placeId +
                ", place=" + place +
                '}';
    }
}
