package guess.domain.source.cms.contentful.eventtype;

import guess.domain.source.cms.contentful.ContentfulEntity;

public class ContentfulEventType extends ContentfulEntity {
    private ContentfulEventTypeFields fields;

    public ContentfulEventTypeFields getFields() {
        return fields;
    }

    public void setFields(ContentfulEventTypeFields fields) {
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
