package guess.domain.source.cms.jrgcms.speaker;

public class JrgCmsParticipant {
    private JrgCmsParticipation participation;
    private JrgCmsSpeaker data;

    public JrgCmsParticipation getParticipation() {
        return participation;
    }

    public void setParticipation(JrgCmsParticipation participation) {
        this.participation = participation;
    }

    public JrgCmsSpeaker getData() {
        return data;
    }

    public void setData(JrgCmsSpeaker data) {
        this.data = data;
    }
}
