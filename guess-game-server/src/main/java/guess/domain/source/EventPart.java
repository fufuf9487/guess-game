package guess.domain.source;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

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

    public long getDuration() {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
    
    public static EventPart of(Event event, EventDays eventDays) {
        return new EventPart(
                new Nameable(
                        event.getId(),
                        event.getName()
                ),
                event.getEventType(),
                new EventPart.EventDates(
                        eventDays.getStartDate(),
                        eventDays.getEndDate()
                ),
                new AbstractEvent.EventLinks(
                        event.getSiteLink(),
                        event.getYoutubeLink()
                ),
                eventDays.getPlace(),
                event.getTimeZone(),
                event.getTalks()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventPart)) return false;
        if (!super.equals(o)) return false;
        EventPart eventPart = (EventPart) o;
        return Objects.equals(getStartDate(), eventPart.getStartDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getStartDate());
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
