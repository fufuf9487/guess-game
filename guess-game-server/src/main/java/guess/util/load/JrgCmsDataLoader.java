package guess.util.load;

import guess.domain.Conference;
import guess.domain.source.*;
import guess.domain.source.cms.jrgcms.talk.JrgCmsTalkResponse;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static guess.util.load.ContentfulDataLoader.getRestTemplate;

/**
 * Data loader of JUG Ru Group CMS.
 */
public class JrgCmsDataLoader extends CmsDataLoader {
    private static final String BASE_URL = "https://speakers.jugru.org/api/v1/public/{entityName}";
    private static final String EVENTS_VARIABLE_VALUE = "events";
    
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
        return new Event(
                new Nameable(
                        -1L,
                        extractLocaleItems(
                                "TechTrain 2022 Spring",
                                null
                        )
                ),
                null,
                new Event.EventDates(
                        LocalDate.of(2022, 5, 14),
                        LocalDate.of(2022, 5, 14)
                ),
                new Event.EventLinks(
                        extractLocaleItems(
                                "https://techtrain.ru/",
                                null
                        ),
                        "https://www.youtube.com/channel/UCJoerW5eDOz5qu7I2CYi7xg"
                ),
                new Place(
                        -1,
                        extractLocaleItems(
                                "Online",
                                "Онлайн"),
                        Collections.emptyList(),
                        null
                ),
                null,
                Collections.emptyList());
    }

    @Override
    public List<Talk> getTalks(Conference conference, String conferenceCode, boolean ignoreDemoStage) {
        // https://speakers.jugru.org/api/v1/public/events/{eventId}/activities
        var talksBaseUrl = String.format("%s/{eventId}/activities", BASE_URL);
        var builder = UriComponentsBuilder
                .fromUriString(talksBaseUrl);

        var uri = builder
                .buildAndExpand(EVENTS_VARIABLE_VALUE, 100130)
                .encode()
                .toUri();
        JrgCmsTalkResponse response = getRestTemplate().getForObject(uri, JrgCmsTalkResponse.class);

        //TODO: implement
        return Collections.emptyList();
    }
}
