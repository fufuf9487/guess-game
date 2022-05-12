package guess.domain.source.cms.contentful.talk.fields;

import com.fasterxml.jackson.annotation.JsonSetter;
import guess.domain.source.cms.contentful.ContentfulLink;

public class ContentfulTalkFieldsHeisenbug extends ContentfulTalkFields {
    @Override
    @JsonSetter("talksPresentation")
    public void setPresentation(ContentfulLink presentation) {
        super.setPresentation(presentation);
    }

    @Override
    @JsonSetter("talksPresentationLink")
    public void setMaterial(String material) {
        super.setMaterial(material);
    }
}
