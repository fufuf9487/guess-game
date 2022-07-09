package guess.domain.source.cms.contentful.asset;

import guess.domain.source.cms.contentful.ContentfulEntity;

public class ContentfulAsset extends ContentfulEntity {
    private ContentfulAssetFields fields;

    public ContentfulAssetFields getFields() {
        return fields;
    }

    public void setFields(ContentfulAssetFields fields) {
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
