package guess.util.load;

import guess.domain.source.EventType;

import java.util.List;

/**
 * CMS data loader.
 */
public interface CmsDataLoader {
    List<EventType> getEventTypes();
}
