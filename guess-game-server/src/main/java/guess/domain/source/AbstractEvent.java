package guess.domain.source;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract event.
 */
public abstract class AbstractEvent extends Nameable {
    public record EventLinks(List<LocaleItem> siteLink, String youtubeLink) {
    }

    private long eventTypeId;
    private EventType eventType;

    private List<LocaleItem> siteLink;
    private String youtubeLink;

    private String timeZone;
    private ZoneId timeZoneId;

    private List<Long> talkIds;
    private List<Talk> talks = new ArrayList<>();

    protected AbstractEvent() {
    }

    protected AbstractEvent(Nameable nameable, EventType eventType, EventLinks links, String timeZone, List<Talk> talks) {
        super(nameable.getId(), nameable.getName());

        this.eventType = eventType;
        this.siteLink = links.siteLink;
        this.youtubeLink = links.youtubeLink;

        this.timeZone = timeZone;
        this.timeZoneId = (timeZone != null) ? ZoneId.of(timeZone) : null;

        this.talks = talks;
        this.talkIds = talks.stream()
                .map(Talk::getId)
                .toList();
    }

    public long getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(long eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public List<LocaleItem> getSiteLink() {
        return siteLink;
    }

    public void setSiteLink(List<LocaleItem> siteLink) {
        this.siteLink = siteLink;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public void setYoutubeLink(String youtubeLink) {
        this.youtubeLink = youtubeLink;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public ZoneId getTimeZoneId() {
        return timeZoneId;
    }

    public void setTimeZoneId(ZoneId timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public List<Long> getTalkIds() {
        return talkIds;
    }

    public void setTalkIds(List<Long> talkIds) {
        this.talkIds = talkIds;
    }

    public List<Talk> getTalks() {
        return talks;
    }

    public void setTalks(List<Talk> talks) {
        this.talks = talks;
    }

    public ZoneId getFinalTimeZoneId() {
        return (timeZoneId != null) ? timeZoneId : eventType.getTimeZoneId();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
