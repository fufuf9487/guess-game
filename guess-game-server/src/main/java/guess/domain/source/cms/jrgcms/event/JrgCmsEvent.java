package guess.domain.source.cms.jrgcms.event;

import guess.domain.source.cms.jrgcms.JrgCmsObject;

public class JrgCmsEvent {
    private JrgCmsObject<Long> eventId;
    private JrgCmsObject<String> eventProject;
    private JrgCmsObject<String> eventVersion;

    public JrgCmsObject<Long> getEventId() {
        return eventId;
    }

    public void setEventId(JrgCmsObject<Long> eventId) {
        this.eventId = eventId;
    }

    public JrgCmsObject<String> getEventProject() {
        return eventProject;
    }

    public void setEventProject(JrgCmsObject<String> eventProject) {
        this.eventProject = eventProject;
    }

    public JrgCmsObject<String> getEventVersion() {
        return eventVersion;
    }

    public void setEventVersion(JrgCmsObject<String> eventVersion) {
        this.eventVersion = eventVersion;
    }
}
