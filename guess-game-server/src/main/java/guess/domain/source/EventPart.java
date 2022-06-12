package guess.domain.source;

import java.time.LocalDate;
import java.util.List;

/**
 * Event part.
 */
public class EventPart extends AbstractEvent {
    public record EventDates(LocalDate startDate, LocalDate endDate) {
    }

    private LocalDate startDate;
    private LocalDate endDate;

    private long placeId;
    private Place place;

    public EventPart() {
    }

    public EventPart(Nameable nameable, EventType eventType, EventDates dates, EventLinks links, Place place,
                     String timeZone, List<Talk> talks) {
        super(nameable, eventType, links, timeZone, talks);

        this.startDate = dates.startDate;
        this.endDate = dates.endDate;

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

    public long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(long placeId) {
        this.placeId = placeId;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "EventPart{" +
                "id=" + getId() +
                ", eventType=" + getEventType() +
                ", name=" + getName() +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", place=" + place +
                ", talks=" + getTalks() +
                '}';
    }
}
