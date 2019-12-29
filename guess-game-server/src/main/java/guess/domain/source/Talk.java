package guess.domain.source;

import guess.domain.Identifiable;

import java.util.ArrayList;
import java.util.List;

/**
 * Talk.
 */
public class Talk implements Identifiable {
    private long id;
    private List<LocaleItem> name;

    private List<Long> speakerIds;
    private List<Speaker> speakers = new ArrayList<>();

    public Talk() {
    }

    public Talk(long id, List<LocaleItem> name, List<Speaker> speakers) {
        this.id = id;
        this.name = name;
        this.speakers = speakers;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<LocaleItem> getName() {
        return name;
    }

    public void setName(List<LocaleItem> name) {
        this.name = name;
    }

    public List<Long> getSpeakerIds() {
        return speakerIds;
    }

    public void setSpeakerIds(List<Long> speakerIds) {
        this.speakerIds = speakerIds;
    }

    public List<Speaker> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(List<Speaker> speakers) {
        this.speakers = speakers;
    }

    @Override
    public String toString() {
        return "Talk{" +
                "id=" + id +
                ", name=" + name +
                ", speakers=" + speakers +
                '}';
    }
}
