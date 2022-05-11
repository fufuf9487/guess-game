package guess.util.load;

import guess.domain.Conference;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.Talk;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * CMS data loader.
 */
public interface CmsDataLoader {
    Map<ContentfulDataLoader.ConferenceSpaceInfo, List<String>> getTags(String conferenceCodePrefix);

    List<EventType> getEventTypes();

    Event getEvent(Conference conference, LocalDate startDate);

    List<Talk> getTalks(Conference conference, String conferenceCode, boolean ignoreDemoStage);
}
