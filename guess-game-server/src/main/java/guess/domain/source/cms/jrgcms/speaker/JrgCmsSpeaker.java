package guess.domain.source.cms.jrgcms.speaker;

import guess.domain.source.cms.jrgcms.JrgPhoto;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class JrgCmsSpeaker {
    private String id;
    private List<JrgPhoto> photo;
    private Map<String, String> firstName;
    private Map<String, String> lastName;
    private Map<String, String> company;
    private Map<String, String> description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<JrgPhoto> getPhoto() {
        return photo;
    }

    public void setPhoto(List<JrgPhoto> photo) {
        this.photo = photo;
    }

    public Map<String, String> getFirstName() {
        return firstName;
    }

    public void setFirstName(Map<String, String> firstName) {
        this.firstName = firstName;
    }

    public Map<String, String> getLastName() {
        return lastName;
    }

    public void setLastName(Map<String, String> lastName) {
        this.lastName = lastName;
    }

    public Map<String, String> getCompany() {
        return company;
    }

    public void setCompany(Map<String, String> company) {
        this.company = company;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public void setDescription(Map<String, String> description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JrgCmsSpeaker)) return false;
        JrgCmsSpeaker that = (JrgCmsSpeaker) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
