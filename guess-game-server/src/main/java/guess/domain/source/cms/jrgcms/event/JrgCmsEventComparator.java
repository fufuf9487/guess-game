package guess.domain.source.cms.jrgcms.event;

import java.util.Comparator;

public class JrgCmsEventComparator implements Comparator<JrgCmsEvent> {
    private record EventVersionPair(int year, int seasonOrderNumber) {
    }
    
    private EventVersionPair createEventVersionPair(String eventVersion) {
        String[] strings = eventVersion.split(" ");
        
        if (strings.length < 1) {
            throw new IllegalArgumentException(String.format("Invalid event version: %s", eventVersion));
        }
        
        int year = Integer.parseInt(strings[0]);
        String season = (strings.length > 1) ? strings[1] : null;
        int seasonOrderNumber = "Autumn".equals(season) ? 2 : ("Spring".equals(season) ? 1 : 0);
        
        return new EventVersionPair(year, seasonOrderNumber);
    }

    @Override
    public int compare(JrgCmsEvent event1, JrgCmsEvent event2) {
        EventVersionPair eventVersionPair1 = createEventVersionPair(event1.getEventVersion().getIv());
        EventVersionPair eventVersionPair2 = createEventVersionPair(event2.getEventVersion().getIv());
        
        if (eventVersionPair1.year() == eventVersionPair2.year()) {
            return Integer.compare(eventVersionPair1.seasonOrderNumber(), eventVersionPair2.seasonOrderNumber());
        } else {
            return Integer.compare(eventVersionPair1.year(), eventVersionPair2.year());
        }
    }
}
