package guess.domain.source.cms.jrgcms;

import java.time.ZonedDateTime;

public class JrgCmsPhoto {
    private ZonedDateTime created;
    private ZonedDateTime lastModified;
    private JrgCmsLinks links;

    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public ZonedDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(ZonedDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public JrgCmsLinks getLinks() {
        return links;
    }

    public void setLinks(JrgCmsLinks links) {
        this.links = links;
    }
}
