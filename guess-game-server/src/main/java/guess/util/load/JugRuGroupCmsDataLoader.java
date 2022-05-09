package guess.util.load;

import guess.domain.source.EventType;

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
}
