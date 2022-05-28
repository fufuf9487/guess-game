package guess.domain.source.cms.jrgcms.event;

import guess.domain.source.cms.jrgcms.JrgCmsObject;

public class JrgCmsEvent {
    private JrgCmsObject<Long> eventId;

    public JrgCmsObject<Long> getEventId() {
        return eventId;
    }

    public void setEventId(JrgCmsObject<Long> eventId) {
        this.eventId = eventId;
    }
}
