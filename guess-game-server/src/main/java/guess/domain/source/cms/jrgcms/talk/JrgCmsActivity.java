package guess.domain.source.cms.jrgcms.talk;

import guess.domain.source.cms.jrgcms.speaker.JrgCmsParticipant;

import java.util.List;

public class JrgCmsActivity {
    private String id;
    private JrgCmsTalk data;
    private List<JrgCmsParticipant> participants;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
