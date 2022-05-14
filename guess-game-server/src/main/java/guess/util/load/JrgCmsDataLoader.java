package guess.util.load;

import guess.domain.Conference;
import guess.domain.Language;
import guess.domain.source.*;
import guess.domain.source.cms.jrgcms.speaker.JrgCmsParticipant;
import guess.domain.source.cms.jrgcms.talk.JrgCmsActivity;
import guess.domain.source.cms.jrgcms.talk.JrgCmsTalk;
import guess.domain.source.cms.jrgcms.talk.JrgCmsActivityResponse;
import guess.domain.source.cms.jrgcms.talk.JrgTalkPresentation;
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
    private static final String SPEAKER_ROLE = "SPEAKER";

    private static final String ENGLISH_TEXT_KEY = "en";
    private static final String RUSSIAN_TEXT_KEY = "ru";

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
        JrgCmsActivityResponse response = getRestTemplate().getForObject(uri, JrgCmsActivityResponse.class);
        var talkId = new AtomicLong(-1);
        Map<String, Speaker> speakerMap = getSpeakerMap(response, ignoreDemoStage);

        return Objects.requireNonNull(response)
                .getData().stream()
                .filter(a -> JrgCmsDataLoader.isValidTalk(a, ignoreDemoStage))
                .map(a -> JrgCmsDataLoader.createTalk(a, speakerMap, talkId))
                .toList();

    }

    static boolean isValidTalk(JrgCmsActivity jrgCmsActivity, boolean ignoreDemoStage) {
        return !ignoreDemoStage ||
                ((jrgCmsActivity.getData().getOptions().getDemoStage() == null) || !jrgCmsActivity.getData().getOptions().getDemoStage());
    }

    static boolean isValidSpeaker(JrgCmsParticipant jrgCmsParticipant) {
        return SPEAKER_ROLE.equals(jrgCmsParticipant.getParticipation().getRole());
    }

    static Map<String, Speaker> getSpeakerMap(JrgCmsActivityResponse response, boolean ignoreDemoStage) {
        var speakerId = new AtomicLong(-1);
        var companyId = new AtomicLong(-1);

        //TODO: implement
        response.getData().stream()
                .filter(a -> JrgCmsDataLoader.isValidTalk(a, ignoreDemoStage))
                .forEach(a -> {
                    a.getParticipants().stream()
                            .filter(JrgCmsDataLoader::isValidSpeaker)
                            .map(JrgCmsParticipant::getData)
                            .forEach(s -> System.out.printf("Speaker id: %s\n", s.getId()));
                });

        return Collections.emptyMap();
    }

    static Talk createTalk(JrgCmsActivity jrgCmsActivity, Map<String, Speaker> speakerMap, AtomicLong talkId) {
        JrgCmsTalk jrgCmsTalk = jrgCmsActivity.getData();
        List<Speaker> speakers = jrgCmsActivity.getParticipants().stream()
                .filter(JrgCmsDataLoader::isValidSpeaker)
                .map(p -> {
                    String speakerId = p.getData().getId();
                    var speaker = speakerMap.get(speakerId);
                    return Objects.requireNonNull(speaker,
                            () -> String.format("Speaker id %s not found for '%s' talk", speakerId, jrgCmsTalk.getTitle().get(ENGLISH_TEXT_KEY)));
                })
                .toList();

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
                        extractPresentationLinks(jrgCmsTalk.getPresentation()),
                        new ArrayList<>(),    //TODO: fill (extra?)
                        new ArrayList<>()     //TODO: fill (video?)
                ),
                speakers);
    }

    static List<LocaleItem> extractLocaleItems(Map<String, String> texts) {
        return extractLocaleItems(texts.get(ENGLISH_TEXT_KEY), texts.get(RUSSIAN_TEXT_KEY));
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

    static List<String> extractPresentationLinks(JrgTalkPresentation presentation) {
        if (presentation == null) {
            return new ArrayList<>();
        }

        return presentation.getFiles().stream()
                .map(f -> f.getLinks().getContent())
                .toList();
    }
}
