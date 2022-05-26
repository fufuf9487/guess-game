package guess.domain.source.cms.jrgcms.talk.schedule;

import java.util.Map;

public class ScheduleInfo {
    private final Map<String, DayTrackTime> currentDayTrackTimeMap;
    private final Map<String, DayTrackTime> totalDayTrackTimeMap;

    public ScheduleInfo(Map<String, DayTrackTime> currentDayTrackTimeMap, Map<String, DayTrackTime> totalDayTrackTimeMap) {
        this.currentDayTrackTimeMap = currentDayTrackTimeMap;
        this.totalDayTrackTimeMap = totalDayTrackTimeMap;
    }

    public Map<String, DayTrackTime> getCurrentDayTrackTimeMap() {
        return currentDayTrackTimeMap;
    }

    public Map<String, DayTrackTime> getTotalDayTrackTimeMap() {
        return totalDayTrackTimeMap;
    }
}
