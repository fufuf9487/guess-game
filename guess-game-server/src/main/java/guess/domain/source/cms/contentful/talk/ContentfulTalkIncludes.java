package guess.domain.source.cms.contentful.talk;

import com.fasterxml.jackson.annotation.JsonProperty;
import guess.domain.source.cms.contentful.ContentfulIncludes;
import guess.domain.source.cms.contentful.speaker.ContentfulSpeaker;

import java.util.List;

public class ContentfulTalkIncludes extends ContentfulIncludes {
    @JsonProperty("Entry")
    private List<ContentfulSpeaker> entry;

    public List<ContentfulSpeaker> getEntry() {
        return entry;
    }

    public void setEntry(List<ContentfulSpeaker> entry) {
        this.entry = entry;
    }
}
