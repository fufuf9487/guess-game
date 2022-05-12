package guess.domain.source.cms.jrgcms.talk;

import java.util.List;

public class JrgCmsTalkResponseData {
    private JrgCmsTalk data;
    private List<JrgCmsParticipant> participants;

    public JrgCmsTalk getData() {
        return data;
    }

    public void setData(JrgCmsTalk data) {
        this.data = data;
    }

    public List<JrgCmsParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<JrgCmsParticipant> participants) {
        this.participants = participants;
    }
}
