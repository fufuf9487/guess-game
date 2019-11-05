package guess.domain.question;

import guess.domain.source.LocaleItem;

import java.util.List;

public class QuestionSet {
    private long id;
    private List<LocaleItem> name;
    private String logoFileName;
    private List<SpeakerQuestion> speakerQuestions;
    private List<TalkQuestion> talkQuestions;

    public QuestionSet(long id, List<LocaleItem> name, String logoFileName, List<SpeakerQuestion> speakerQuestions, List<TalkQuestion> talkQuestions) {
        this.id = id;
        this.name = name;
        this.logoFileName = logoFileName;
        this.speakerQuestions = speakerQuestions;
        this.talkQuestions = talkQuestions;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<LocaleItem> getName() {
        return name;
    }

    public void setName(List<LocaleItem> name) {
        this.name = name;
    }

    public String getLogoFileName() {
        return logoFileName;
    }

    public void setLogoFileName(String logoFileName) {
        this.logoFileName = logoFileName;
    }

    public List<SpeakerQuestion> getSpeakerQuestions() {
        return speakerQuestions;
    }

    public void setSpeakerQuestions(List<SpeakerQuestion> speakerQuestions) {
        this.speakerQuestions = speakerQuestions;
    }

    public List<TalkQuestion> getTalkQuestions() {
        return talkQuestions;
    }

}
