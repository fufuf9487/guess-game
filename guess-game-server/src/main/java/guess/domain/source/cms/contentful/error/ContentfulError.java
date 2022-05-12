package guess.domain.source.cms.contentful.error;

import guess.domain.source.cms.contentful.ContentfulEntity;

public class ContentfulError extends ContentfulEntity {
    private ContentfulErrorDetails details;

    public ContentfulErrorDetails getDetails() {
        return details;
    }

    public void setDetails(ContentfulErrorDetails details) {
        this.details = details;
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
