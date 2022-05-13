package guess.domain.source.cms.jrgcms.talk;

import java.util.Map;

public class JrgCmsTalk {
    private Map<String, String> title;
    private Map<String, String> shortDescription;
    private Map<String, String> fullDescription;
    private String language;
    private JrgTalkOptions options;

    public Map<String, String> getTitle() {
        return title;
    }

    public void setTitle(Map<String, String> title) {
        this.title = title;
    }

    public Map<String, String> getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(Map<String, String> shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Map<String, String> getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(Map<String, String> fullDescription) {
        this.fullDescription = fullDescription;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public JrgTalkOptions getOptions() {
        return options;
    }

    public void setOptions(JrgTalkOptions options) {
        this.options = options;
    }
}
