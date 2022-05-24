package guess.domain.source.cms.jrgcms.talk.schedule;

import java.util.List;

public class JrgCmsDay {
    private Long dayNumber;
    private List<JrgCmsTrack> tracks;

    public Long getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(Long dayNumber) {
        this.dayNumber = dayNumber;
    }

    public List<JrgCmsTrack> getTracks() {
        return tracks;
    }

    public void setTracks(List<JrgCmsTrack> tracks) {
        this.tracks = tracks;
    }
}
