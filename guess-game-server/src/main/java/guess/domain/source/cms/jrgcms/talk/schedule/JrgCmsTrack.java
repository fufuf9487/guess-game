package guess.domain.source.cms.jrgcms.talk.schedule;

import java.util.List;

public class JrgCmsTrack {
    private Long trackNumber;
    private List<JrgCmsSlot> slots;

    public Long getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(Long trackNumber) {
        this.trackNumber = trackNumber;
    }

    public List<JrgCmsSlot> getSlots() {
        return slots;
    }

    public void setSlots(List<JrgCmsSlot> slots) {
        this.slots = slots;
    }
}
