package guess.domain.source.cms.contentful.event;

import guess.domain.source.cms.contentful.ContentfulEntity;

public class ContentfulEvent extends ContentfulEntity {
    private ContentfulEventFields fields;

    public ContentfulEventFields getFields() {
        return fields;
    }

    public void setFields(ContentfulEventFields fields) {
        this.fields = fields;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
