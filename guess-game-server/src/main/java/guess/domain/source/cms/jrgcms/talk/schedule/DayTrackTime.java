package guess.domain.source.cms.jrgcms.talk.schedule;

import java.time.LocalTime;

public class DayTrackTime {
    private final Long dayNumber;
    private final Long trackNumber;
    private final LocalTime startTime;

    public DayTrackTime(Long dayNumber, Long trackNumber, LocalTime startTime) {
        this.dayNumber = dayNumber;
        this.trackNumber = trackNumber;
        this.startTime = startTime;
    }

    public Long getDayNumber() {
        return dayNumber;
    }

    public Long getTrackNumber() {
        return trackNumber;
    }

    public LocalTime getStartTime() {
        return startTime;
    }
}
