package guess.domain.source;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Event.
 */
public class Event extends AbstractEvent {
    private List<EventDays> days;

    private long placeId;           //TODO: delete
    private Place place;            //TODO: delete

    public Event() {
    }

    public Event(Nameable nameable, EventType eventType, List<EventDays> days, EventLinks links, Place place,
                 String timeZone, List<Talk> talks) {
        super(nameable, eventType, links, timeZone, talks);

        this.days = days;

        this.place = place;                 //TODO: delete
        this.placeId = place.getId();       //TODO: delete
    }

    public List<EventDays> getDays() {
        return days;
    }

    public void setDays(List<EventDays> days) {
        this.days = days;
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

    public LocalDate getFirstStartDate() {
        if ((days != null) && !days.isEmpty()) {
            return days.get(0).getStartDate();
        } else {
            return null;
        }
    }

    public LocalDate getLastEndDate() {
        if ((days != null) && !days.isEmpty()) {
            return days.get(days.size() - 1).getEndDate();
        } else {
            return null;
        }
    }

    public long getDuration() {
        return days.stream()
                .mapToLong(ed -> ChronoUnit.DAYS.between(ed.getStartDate(), ed.getEndDate()) + 1)
                .sum();
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + getId() +
                ", eventType=" + getEventType() +
                ", name=" + getName() +
                ", place=" + place +
                ", talks=" + getTalks() +
                '}';
    }
}
