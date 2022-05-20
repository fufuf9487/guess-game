package guess.util.load;

import guess.domain.Conference;
import guess.domain.Language;
import guess.domain.source.*;
import guess.domain.source.cms.jrgcms.JrgPhoto;
import guess.domain.source.cms.jrgcms.speaker.JrgCmsParticipant;
import guess.domain.source.cms.jrgcms.speaker.JrgCmsSpeaker;
import guess.domain.source.cms.jrgcms.speaker.JrgContact;
import guess.domain.source.cms.jrgcms.talk.JrgCmsActivity;
import guess.domain.source.cms.jrgcms.talk.JrgCmsActivityResponse;
import guess.domain.source.cms.jrgcms.talk.JrgCmsTalk;
import guess.domain.source.cms.jrgcms.talk.JrgTalkPresentation;
import guess.domain.source.image.UrlDates;
import guess.util.LocalizationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static guess.util.load.ContentfulDataLoader.getRestTemplate;

/**
 * Data loader of JUG Ru Group CMS.
 */
public class JrgCmsDataLoader extends CmsDataLoader {
    private static final Logger log = LoggerFactory.getLogger(JrgCmsDataLoader.class);

    private static final String BASE_URL = "https://speakers.jugru.org/api/v1/public/{entityName}";
    private static final String EVENTS_VARIABLE_VALUE = "events";
    private static final String SPEAKER_ROLE = "SPEAKER";
    private static final String JAVA_CHAMPION_TITULUS = "Java Champion";

    private static final String TWITTER_CONTACT_TYPE = "twitter";
    private static final String GITHUB_CONTACT_TYPE = "github";

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
        List<JrgCmsActivity> validJrgCmsActivities = Objects.requireNonNull(response)
                .getData().stream()
                .filter(a -> JrgCmsDataLoader.isValidTalk(a, ignoreDemoStage))
                .toList();
        Map<String, Speaker> speakerMap = getSpeakerMap(validJrgCmsActivities);

        return validJrgCmsActivities.stream()
                .map(a -> JrgCmsDataLoader.createTalk(a, speakerMap, talkId))
                .toList();

    }

    @Override
    String getImageWidthParameterName() {
        return "width";
    }

    /**
     * Checks talk validity.
     *
     * @param activity        activity
     * @param ignoreDemoStage {@code true} if ignore demo stage, otherwise {@code false}
     * @return talk validity
     */
    static boolean isValidTalk(JrgCmsActivity activity, boolean ignoreDemoStage) {
        return !ignoreDemoStage ||
                ((activity.getData().getOptions().getDemoStage() == null) || !activity.getData().getOptions().getDemoStage());
    }

    /**
     * Checks speaker validity.
     *
     * @param jrgCmsParticipant participant
     * @return speaker validity
     */
    static boolean isValidSpeaker(JrgCmsParticipant jrgCmsParticipant) {
        return SPEAKER_ROLE.equals(jrgCmsParticipant.getParticipation().getRole());
    }

    /**
     * Gets map id/speaker.
     *
     * @param validJrgCmsActivities activities
     * @return map id/speaker
     */
    static Map<String, Speaker> getSpeakerMap(List<JrgCmsActivity> validJrgCmsActivities) {
        var speakerId = new AtomicLong(-1);
        var companyId = new AtomicLong(-1);

        return validJrgCmsActivities.stream()
                .flatMap(a -> a.getParticipants().stream())
                .filter(JrgCmsDataLoader::isValidSpeaker)
                .map(JrgCmsParticipant::getData)
                .distinct()
                .collect(Collectors.toMap(
                        JrgCmsSpeaker::getId,
                        s -> createSpeaker(s, speakerId, companyId, false)
                ));
    }

    /**
     * Creates speaker from JUG Ru Group CMS information.
     *
     * @param jrgCmsSpeaker        JUG Ru Group CMS speaker
     * @param speakerId            atomic speaker identifier
     * @param companyId            atomic company identifier
     * @param checkEnTextExistence {@code true} if need to check English text existence, {@code false} otherwise
     * @return speaker
     */
    static Speaker createSpeaker(JrgCmsSpeaker jrgCmsSpeaker, AtomicLong speakerId, AtomicLong companyId, boolean checkEnTextExistence) {
        var urlDates = extractPhoto(jrgCmsSpeaker);
        List<LocaleItem> lastName = extractLocaleItems(jrgCmsSpeaker.getLastName());
        List<LocaleItem> firstName = extractLocaleItems(jrgCmsSpeaker.getFirstName());
        String enSpeakerName = getSpeakerName(lastName, firstName, Language.ENGLISH);
        String ruSpeakerName = getSpeakerFixedName(getSpeakerName(lastName, firstName, Language.RUSSIAN));

        List<LocaleItem> name = extractLocaleItems(jrgCmsSpeaker.getCompany());
        String enName = LocalizationUtils.getString(name, Language.ENGLISH);
        String ruName = LocalizationUtils.getString(name, Language.RUSSIAN);

        Map<String, JrgContact> contactMap = jrgCmsSpeaker.getContacts().stream()
                .collect(Collectors.toMap(
                        JrgContact::getType,
                        c -> c
                ));

        return new Speaker(
                speakerId.getAndDecrement(),
                new Speaker.SpeakerPhoto(
                        urlDates.getUrl(),
                        urlDates.getUpdatedAt()
                ),
                extractLocaleItems(enSpeakerName, ruSpeakerName, checkEnTextExistence, true),
                createCompanies(enName, ruName, companyId, checkEnTextExistence),
                extractLocaleItems(jrgCmsSpeaker.getDescription(), checkEnTextExistence),
                new Speaker.SpeakerSocials(
                        extractContactValue(contactMap, TWITTER_CONTACT_TYPE, CmsDataLoader::extractTwitter),
                        extractContactValue(contactMap, GITHUB_CONTACT_TYPE, CmsDataLoader::extractGitHub),
                        null
                ),
                new Speaker.SpeakerDegrees(
                        JAVA_CHAMPION_TITULUS.equals(jrgCmsSpeaker.getTitulus()),
                        false,
                        false
                )
        );
    }

    /**
     * Creates talk from JUG Ru Group CMS information.
     *
     * @param jrgCmsActivity JUG Ru Group CMS activity
     * @param speakerMap     speaker map
     * @param talkId         atomic talk identifier
     * @return talk
     */
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
                        new ArrayList<>(),
                        new ArrayList<>()
                ),
                speakers);
    }

    /**
     * Extracts local items.
     *
     * @param texts                text map
     * @param checkEnTextExistence {@code true} if need to check English text existence, {@code false} otherwise
     * @return local items
     */
    static List<LocaleItem> extractLocaleItems(Map<String, String> texts, boolean checkEnTextExistence) {
        return extractLocaleItems(texts.get(ENGLISH_TEXT_KEY), texts.get(RUSSIAN_TEXT_KEY), checkEnTextExistence);
    }

    /**
     * Extracts local items.
     *
     * @param texts text map
     * @return local items
     */
    static List<LocaleItem> extractLocaleItems(Map<String, String> texts) {
        return extractLocaleItems(texts, true);
    }

    /**
     * Gets speaker name.
     *
     * @param lastName  last name
     * @param firstName first name
     * @return speaker name
     */
    static String getSpeakerName(String lastName, String firstName) {
        String name = null;

        if ((firstName != null) && !firstName.isEmpty()) {
            name = firstName;
        }

        if ((lastName != null) && !lastName.isEmpty()) {
            if (name != null) {
                name += " ";
                name += lastName;
            } else {
                name = lastName;
            }
        }

        return name;
    }

    /**
     * Gets speaker name.
     *
     * @param lastName  last name
     * @param firstName first name
     * @param language  language
     * @return speaker name
     */
    static String getSpeakerName(List<LocaleItem> lastName, List<LocaleItem> firstName, Language language) {
        String localLastName = LocalizationUtils.getString(lastName, language);
        String localFirstName = LocalizationUtils.getString(firstName, language);

        return getSpeakerName(localLastName, localFirstName);
    }

    /**
     * Extracts talk language.
     *
     * @param language language string
     * @return talk language
     */
    static String extractLanguage(String language) {
        if (Language.ENGLISH.getCode().equalsIgnoreCase(language)) {
            return Language.ENGLISH.getCode();
        } else if (Language.RUSSIAN.getCode().equalsIgnoreCase(language)) {
            return Language.RUSSIAN.getCode();
        } else {
            return null;
        }
    }

    /**
     * Extracts presentation links.
     *
     * @param presentation presentation
     * @return presentation links
     */
    static List<String> extractPresentationLinks(JrgTalkPresentation presentation) {
        if (presentation == null) {
            return new ArrayList<>();
        }

        return presentation.getFiles().stream()
                .map(f -> f.getLinks().getContent())
                .toList();
    }

    /**
     * Extracts photo.
     *
     * @param jrgCmsSpeaker speaker
     * @return photo URL and dates
     */
    static UrlDates extractPhoto(JrgCmsSpeaker jrgCmsSpeaker) {
        List<JrgPhoto> photos = jrgCmsSpeaker.getPhoto();
        String speakerName = getSpeakerName(jrgCmsSpeaker.getLastName().get(ENGLISH_TEXT_KEY), jrgCmsSpeaker.getFirstName().get(ENGLISH_TEXT_KEY));

        if (photos == null) {
            log.warn("Photos is null for '{}' speaker", speakerName);
            return new UrlDates(null, null, null);
        }

        if (photos.isEmpty()) {
            log.warn("Photos is empty for '{}' speaker", speakerName);
            return new UrlDates(null, null, null);
        }

        JrgPhoto jrgPhoto = photos.get(0);

        if (photos.size() > 1) {
            log.warn("There are many photos ({}) for '{}' speaker", photos.size(), speakerName);
        }

        return new UrlDates(jrgPhoto.getLinks().getContent(), jrgPhoto.getCreated(), jrgPhoto.getLastModified());
    }

    /**
     * Extracts contact value.
     *
     * @param contactMap         contact map
     * @param type               contact type
     * @param extractionOperator extraction operation
     * @return contact value
     */
    static String extractContactValue(Map<String, JrgContact> contactMap, String type, UnaryOperator<String> extractionOperator) {
        JrgContact jrgContact = contactMap.get(type);

        return ((jrgContact != null) && (jrgContact.getValue() != null) && !jrgContact.getValue().isEmpty()) ?
                extractionOperator.apply(jrgContact.getValue()) : null;
    }
}
