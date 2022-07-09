package guess.domain.source.cms.contentful.city;

import guess.domain.source.cms.contentful.ContentfulEntity;

public class ContentfulCity extends ContentfulEntity {
    private ContentfulCityFields fields;

    public ContentfulCityFields getFields() {
        return fields;
    }

    public void setFields(ContentfulCityFields fields) {
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
