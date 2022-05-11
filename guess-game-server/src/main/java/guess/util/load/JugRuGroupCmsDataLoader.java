package guess.util.load;

import guess.domain.Conference;
import guess.domain.source.Event;
import guess.domain.source.EventType;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Data loader of JUG Ru Group CMS.
 */
public class JugRuGroupCmsDataLoader implements CmsDataLoader {
    @Override
    public Map<ContentfulDataLoader.ConferenceSpaceInfo, List<String>> getTags(String conferenceCodePrefix) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<EventType> getEventTypes() {
        //TODO: implement
        return Collections.emptyList();
    }

    @Override
    public Event getEvent(Conference conference, LocalDate startDate) {
        //TODO: implement
        return null;
    }
}
