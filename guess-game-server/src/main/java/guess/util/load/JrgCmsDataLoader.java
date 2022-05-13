package guess.util.load;

import guess.domain.Conference;
import guess.domain.Language;
import guess.domain.source.*;
import guess.domain.source.cms.jrgcms.talk.JrgCmsActivity;
import guess.domain.source.cms.jrgcms.talk.JrgCmsTalk;
import guess.domain.source.cms.jrgcms.talk.JrgCmsTalkResponse;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static guess.util.load.ContentfulDataLoader.getRestTemplate;

/**
 * Data loader of JUG Ru Group CMS.
 */
public class JrgCmsDataLoader extends CmsDataLoader {
    private static final String BASE_URL = "https://speakers.jugru.org/api/v1/public/{entityName}";
    private static final String EVENTS_VARIABLE_VALUE = "events";
    private static final String UNDECIDED_ACTIVITY_TYPE = "UNDECIDED";

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

        //TODO: implement
        var uri = builder
                .buildAndExpand(EVENTS_VARIABLE_VALUE, 100130)
                .encode()
                .toUri();
        JrgCmsTalkResponse response = getRestTemplate().getForObject(uri, JrgCmsTalkResponse.class);
        var talkId = new AtomicLong(-1);

        return Objects.requireNonNull(response)
                .getData().stream()
                .filter(JrgCmsDataLoader::isValidActivity)
                .map(JrgCmsActivity::getData)
                .filter(t -> JrgCmsDataLoader.isValidTalk(t, ignoreDemoStage))
                .map(t -> JrgCmsDataLoader.createTalk(t, talkId))
                .toList();

    }

    static boolean isValidActivity(JrgCmsActivity activity) {
        return !UNDECIDED_ACTIVITY_TYPE.equals(activity.getType());
    }

    static boolean isValidTalk(JrgCmsTalk jrgCmsTalk, boolean ignoreDemoStage) {
        return !ignoreDemoStage ||
                ((jrgCmsTalk.getOptions().getDemoStage() == null) || !jrgCmsTalk.getOptions().getDemoStage());
    }

    static Talk createTalk(JrgCmsTalk jrgCmsTalk, AtomicLong talkId) {
        //TODO: implement
        List<Speaker> speakers = new ArrayList<>();

        return new Talk(
                new Descriptionable(
                        talkId.getAndDecrement(),
                        extractLocaleItems(jrgCmsTalk.getTitle()),
                        extractLocaleItems(jrgCmsTalk.getShortDescription()),
                        extractLocaleItems(jrgCmsTalk.getFullDescription())
                ),
                1L,  //TODO: change
                LocalTime.of(0, 0), //TODO: change
                1L,  //TODO: change
                extractLanguage(jrgCmsTalk.getLanguage()),
                new Talk.TalkLinks(
                        new ArrayList<>(),    //TODO: fill (playlist?)
                        new ArrayList<>(),    //TODO: fill (extra?)
                        new ArrayList<>()     //TODO: fill (video?)
                ),
                speakers);
    }

    static List<LocaleItem> extractLocaleItems(Map<String, String> texts) {
        return extractLocaleItems(texts.get("en"), texts.get("ru"));
    }

    static String extractLanguage(String language) {
        if (Language.ENGLISH.getCode().equalsIgnoreCase(language)) {
            return Language.ENGLISH.getCode();
        } else if (Language.RUSSIAN.getCode().equalsIgnoreCase(language)) {
            return Language.RUSSIAN.getCode();
        } else {
            return null;
        }
    }
}
