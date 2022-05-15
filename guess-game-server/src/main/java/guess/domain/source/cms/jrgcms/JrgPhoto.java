package guess.domain.source.cms.jrgcms;

import java.time.ZonedDateTime;

public class JrgPhoto {
    private ZonedDateTime created;
    private ZonedDateTime lastModified;
    private JrgLinks links;

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

    public JrgLinks getLinks() {
        return links;
    }

    public void setLinks(JrgLinks links) {
        this.links = links;
    }
}
