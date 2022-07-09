package guess.domain.source.cms.jrgcms.talk.schedule;

import guess.domain.source.cms.jrgcms.talk.JrgCmsActivity;

public class JrgCmsSlot {
    private String slotStartTime;
    private JrgCmsActivity activity;

    public String getSlotStartTime() {
        return slotStartTime;
    }

    public void setSlotStartTime(String slotStartTime) {
        this.slotStartTime = slotStartTime;
    }

    public JrgCmsActivity getActivity() {
        return activity;
    }

    public void setActivity(JrgCmsActivity activity) {
        this.activity = activity;
    }
}
