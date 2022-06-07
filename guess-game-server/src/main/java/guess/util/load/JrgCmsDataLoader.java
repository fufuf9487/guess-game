package guess.util.load;

import guess.domain.Conference;
import guess.domain.Language;
import guess.domain.source.*;
import guess.domain.source.cms.jrgcms.JrgCmsPhoto;
import guess.domain.source.cms.jrgcms.JrgCmsTokenResponse;
import guess.domain.source.cms.jrgcms.event.JrgCmsConferenceSiteContent;
import guess.domain.source.cms.jrgcms.event.JrgCmsConferenceSiteContentResponse;
import guess.domain.source.cms.jrgcms.event.JrgCmsEvent;
import guess.domain.source.cms.jrgcms.event.JrgCmsEventComparator;
import guess.domain.source.cms.jrgcms.speaker.JrgCmsParticipant;
import guess.domain.source.cms.jrgcms.speaker.JrgCmsSpeaker;
import guess.domain.source.cms.jrgcms.speaker.JrgContact;
import guess.domain.source.cms.jrgcms.talk.JrgCmsActivity;
import guess.domain.source.cms.jrgcms.talk.JrgCmsActivityResponse;
import guess.domain.source.cms.jrgcms.talk.JrgCmsTalk;
import guess.domain.source.cms.jrgcms.talk.JrgTalkPresentation;
import guess.domain.source.cms.jrgcms.talk.schedule.*;
import guess.domain.source.image.UrlDates;
import guess.util.LocalizationUtils;
import guess.util.yaml.YamlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Data loader of JUG Ru Group CMS.
 */
public class JrgCmsDataLoader extends CmsDataLoader {
    private static final Logger log = LoggerFactory.getLogger(JrgCmsDataLoader.class);

    private record ScheduleInfo(List<JrgCmsDay> sortedDays, Set<LocalDate> startDateSet) {
    }

    private static final String CONFERENCE_SITE_CONTENT_URL = "https://squidex.jugru.team/api/content/sites/conf-site-content";
    private static final String TALKS_BASE_URL = "https://speakers.jugru.org/api/v1/public/events/{eventId}/activities";
    private static final String SCHEDULE_BASE_URL = "https://core.jugru.team/api/v1/public/events/{eventId}/schedule";

    private static final String FILTER_PARAM_NAME = "$filter";
    private static final String ORDERBY_PARAM_NAME = "$orderby";
    private static final String SPEAKER_ROLE = "SPEAKER";
    private static final String JAVA_CHAMPION_TITULUS = "Java Champion";
    private static final String TWITTER_CONTACT_TYPE = "twitter";
    private static final String GITHUB_CONTACT_TYPE = "github";

    private static final String ENGLISH_TEXT_KEY = "en";
    private static final String RUSSIAN_TEXT_KEY = "ru";

    private static final EnumMap<Conference, String> CONFERENCE_EVENT_PROJECT_MAP = new EnumMap<>(Conference.class);
    private static final Map<String, Conference> EVENT_PROJECT_CONFERENCE_MAP;

    private static final RestTemplate restTemplate;

    private static final String optionsDirectoryName = String.format("%s/%s", System.getProperty("user.home"), ".guess-game");
    private static final String TOKEN_FILENAME = "token.yml";

    private Long eventId;

    static {
        CONFERENCE_EVENT_PROJECT_MAP.put(Conference.CPP_RUSSIA, "CPP");
        CONFERENCE_EVENT_PROJECT_MAP.put(Conference.DEV_OOPS, "DEVOOPS");
        CONFERENCE_EVENT_PROJECT_MAP.put(Conference.DOT_NEXT, "DOTNEXT");
        CONFERENCE_EVENT_PROJECT_MAP.put(Conference.HEISENBUG, "HEISENBUG");
        CONFERENCE_EVENT_PROJECT_MAP.put(Conference.HOLY_JS, "HOLYJS");
        CONFERENCE_EVENT_PROJECT_MAP.put(Conference.HYDRA, "HYDRA");
        CONFERENCE_EVENT_PROJECT_MAP.put(Conference.JBREAK, "JBREAK");
        CONFERENCE_EVENT_PROJECT_MAP.put(Conference.JOKER, "JOKER");
        CONFERENCE_EVENT_PROJECT_MAP.put(Conference.JPOINT, "JPOINT");
        CONFERENCE_EVENT_PROJECT_MAP.put(Conference.MOBIUS, "MOBIUS");
        CONFERENCE_EVENT_PROJECT_MAP.put(Conference.TECH_TRAIN, "TT");
        CONFERENCE_EVENT_PROJECT_MAP.put(Conference.SMART_DATA, "SMARTDATA");
        CONFERENCE_EVENT_PROJECT_MAP.put(Conference.SPTDC, "SPTDC");
        CONFERENCE_EVENT_PROJECT_MAP.put(Conference.VIDEO_TECH, "VIDEOTECH");

        EVENT_PROJECT_CONFERENCE_MAP = CONFERENCE_EVENT_PROJECT_MAP.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        restTemplate = createRestTemplate();
    }

    static RestTemplate getRestTemplate() {
        return restTemplate;
    }

    static String getOptionsDirectoryName() {
        return optionsDirectoryName;
    }

    /**
     * Stores token in cache.
     *
     * @param jrgCmsTokenResponse token response
     * @throws IOException          if saving error occurs
     * @throws NoSuchFieldException if field name is invalid
     */
    static void storeTokenInCache(JrgCmsTokenResponse jrgCmsTokenResponse) throws IOException, NoSuchFieldException {
        YamlUtils.save(jrgCmsTokenResponse, getOptionsDirectoryName(), TOKEN_FILENAME);
    }

    /**
     * Gets token from cache.
     *
     * @return token
     * @throws IOException if resource files could not be opened
     */
    static String getTokenFromCache() throws IOException {
        var resolver = new PathMatchingResourcePatternResolver();
        var tokenResponseResource = resolver.getResource(String.format("file:%s/%s", getOptionsDirectoryName(), TOKEN_FILENAME));
        var tokenResponseYaml = new Yaml(new Constructor(JrgCmsTokenResponse.class));

        try {
            var jrgCmsTokenResponse = (JrgCmsTokenResponse) tokenResponseYaml.load(tokenResponseResource.getInputStream());

            return Objects.requireNonNull(jrgCmsTokenResponse, "Token response is empty").getAccessToken();
        } catch (FileNotFoundException e) {
            log.warn("Token response file {} not found", TOKEN_FILENAME);

            return null;
        }
    }

    String getToken() {
        //TODO: implement
        return null;
    }

    @Override
    public Map<String, List<String>> getTags(String conferenceCodePrefix) {
        // https://squidex.jugru.team/api/content/sites/conf-site-content?$filter=startswith(data/eventVersion/iv, '{conferenceCode}')&$orderby=data/eventProject/iv
        var builder = UriComponentsBuilder
                .fromUriString(CONFERENCE_SITE_CONTENT_URL)
                .queryParam(FILTER_PARAM_NAME, String.format("startswith(data/eventVersion/iv, '%s')", conferenceCodePrefix))
                .queryParam(ORDERBY_PARAM_NAME, "data/eventProject/iv");
        var uri = builder
                .build()
                .encode()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(System.getProperty("token")); //TODO: change

        JrgCmsConferenceSiteContentResponse response = getRestTemplate().exchange(
                        uri,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        JrgCmsConferenceSiteContentResponse.class)
                .getBody();
        Map<String, List<String>> tags = Objects.requireNonNull(response).getItems().stream()
                .map(JrgCmsConferenceSiteContent::getData)
                .collect(Collectors.groupingBy(
                                e -> e.getEventProject().getIv(),
                                Collectors.mapping(
                                        e -> e.getEventVersion().getIv(),
                                        Collectors.toList()
                                )
                        )
                );

        return tags.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    @Override
    public List<EventType> getEventTypes() {
        // https://squidex.jugru.team/api/content/sites/conf-site-content?$filter=data/eventProject/iv in ('MOBIUS', 'CPP')&$orderby=data/eventProject/iv
        var eventVersions = CONFERENCE_EVENT_PROJECT_MAP.values().stream()
                .filter(Objects::nonNull)
                .map(s -> "'" + s + "'")
                .collect(Collectors.joining(","));
        var builder = UriComponentsBuilder
                .fromUriString(CONFERENCE_SITE_CONTENT_URL)
                .queryParam(FILTER_PARAM_NAME, String.format("data/eventProject/iv in (%s)", eventVersions))
                .queryParam(ORDERBY_PARAM_NAME, "data/eventProject/iv");
        var uri = builder
                .build()
                .encode()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(System.getProperty("token")); //TODO: change

        JrgCmsConferenceSiteContentResponse response = getRestTemplate().exchange(
                        uri,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        JrgCmsConferenceSiteContentResponse.class)
                .getBody();
        Map<String, List<JrgCmsEvent>> eventProjectEventsMap = Objects.requireNonNull(response).getItems().stream()
                .map(JrgCmsConferenceSiteContent::getData)
                .collect(Collectors.groupingBy(
                                e -> e.getEventProject().getIv(),
                                Collectors.mapping(
                                        e -> e,
                                        Collectors.toList()
                                )
                        )
                );
        Map<Conference, JrgCmsEvent> eventProjectEventMap = eventProjectEventsMap.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> EVENT_PROJECT_CONFERENCE_MAP.get(e.getKey()),
                        e -> e.getValue().stream()
                                .max(new JrgCmsEventComparator())
                                .orElseThrow()
                ));
        var id = new AtomicLong(-1);

        return eventProjectEventMap.entrySet().stream()
                .map(e -> createEventType(e.getValue(), id, e.getKey()))
                .toList();
    }

    /**
     * Creates event type.
     *
     * @param et         CMS event type
     * @param id         identifier
     * @param conference conference
     * @return event type
     */
    static EventType createEventType(JrgCmsEvent et, AtomicLong id, Conference conference) {
        return new EventType(
                new Descriptionable(
                        id.getAndDecrement(),
                        null,
                        null,
                        extractLocaleItems(
                                extractString(et.getAboutPage().get(ENGLISH_TEXT_KEY).getMain()),
                                extractString(et.getAboutPage().get(RUSSIAN_TEXT_KEY).getMain()))
                ),
                conference,
                null,
                new EventType.EventTypeLinks(
                        null,
                        null,
                        null,
                        new EventType.EventTypeSocialLinks(
                                null,
                                null,
                                null,
                                null,
                                null
                        )
                ),
                Collections.emptyList(),
                new Organizer(JUG_RU_GROUP_ORGANIZER_ID, Collections.emptyList()),
                new EventType.EventTypeAttributes(false, null)
        );
    }

    @Override
    public Event getEvent(Conference conference, LocalDate startDate, String conferenceCode, Event eventTemplate) {
        eventId = getEventId(conference, conferenceCode);
        LocalDate endDate = getEventEndDate(eventId, startDate);

        return new Event(
                new Nameable(
                        -1L,
                        eventTemplate.getName()
                ),
                null,
                new Event.EventDates(
                        startDate,
                        endDate
                ),
                new Event.EventLinks(
                        Collections.emptyList(),
                        null
                ),
                new Place(
                        eventTemplate.getPlace().getId(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        null
                ),
                null,
                Collections.emptyList());
    }

    @Override
    public List<Talk> getTalks(Conference conference, LocalDate startDate, String conferenceCode, boolean ignoreDemoStage) {
        if (eventId == null) {
            eventId = getEventId(conference, conferenceCode);
        }

        Map<String, DayTrackTime> dayTrackTimeMap = getDayTrackTimeMap(eventId, startDate);

        return getTalks(eventId, ignoreDemoStage, dayTrackTimeMap);
    }

    /**
     * Gets event identifier.
     *
     * @param conference     conference
     * @param conferenceCode conference code
     * @return event identifier
     */
    long getEventId(Conference conference, String conferenceCode) {
        // https://squidex.jugru.team/api/content/sites/conf-site-content?$filter=data/eventProject/iv eq '{eventProject}' and data/eventVersion/iv eq '{eventVersion}'
        String eventProject = CONFERENCE_EVENT_PROJECT_MAP.get(conference);
        var builder = UriComponentsBuilder
                .fromUriString(CONFERENCE_SITE_CONTENT_URL)
                .queryParam(FILTER_PARAM_NAME, String.format("data/eventProject/iv eq '%s' and data/eventVersion/iv eq '%s'", eventProject, conferenceCode));
        var uri = builder
                .build()
                .encode()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(System.getProperty("token")); //TODO: change

        JrgCmsConferenceSiteContentResponse response = getRestTemplate().exchange(
                        uri,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        JrgCmsConferenceSiteContentResponse.class)
                .getBody();
        List<JrgCmsConferenceSiteContent> items = Objects.requireNonNull(response).getItems();

        if (items.isEmpty()) {
            throw new IllegalStateException(String.format("No events found for conference %s and event version '%s' (change conference and/or conference code and rerun)",
                    conference, conferenceCode));
        }

        if (items.size() > 1) {
            throw new IllegalStateException(String.format("Too much events found for conference %s and event version '%s', events: %d (change conference and/or conference code and rerun)",
                    conference, conferenceCode, items.size()));
        }

        return items.get(0).getData().getEventId().getIv();
    }

    /**
     * Gets schedule information.
     *
     * @param eventId   event identifier
     * @param startDate start date
     * @return schedule information
     */
    ScheduleInfo getScheduleInfo(long eventId, LocalDate startDate) {
        // https://core.jugru.team/api/v1/public/events/{eventId}/schedule
        var builder = UriComponentsBuilder
                .fromUriString(SCHEDULE_BASE_URL);
        var uri = builder
                .buildAndExpand(eventId)
                .encode()
                .toUri();
        JrgCmsScheduleResponse response = getRestTemplate().getForObject(uri, JrgCmsScheduleResponse.class);
        List<JrgCmsDay> sortedDays = Objects.requireNonNull(response).getData().getDays().stream()
                .sorted(Comparator.comparing(d -> CmsDataLoader.createEventLocalDate(d.getDayStartsAt())))
                .toList();
        Map<LocalDate, Set<LocalDate>> dayDateMap = new HashMap<>();
        LocalDate currentDate = null;
        Set<LocalDate> currentDateSet = new HashSet<>();

        // Create day date map
        for (JrgCmsDay day : sortedDays) {
            LocalDate dayDate = CmsDataLoader.createEventLocalDate(day.getDayStartsAt());

            if ((currentDate == null) || !currentDate.plusDays(1).equals(dayDate)) {
                currentDateSet = new HashSet<>();
            }

            currentDate = dayDate;
            currentDateSet.add(currentDate);
            dayDateMap.put(currentDate, currentDateSet);
        }

        Set<LocalDate> startDateSet = Objects.requireNonNull(dayDateMap.get(startDate),
                () -> String.format("Start date %s not found in conference dates", startDate));

        return new ScheduleInfo(sortedDays, startDateSet);
    }

    /**
     * Gets event end date.
     *
     * @param eventId   event identifier
     * @param startDate start date
     * @return event end date
     */
    LocalDate getEventEndDate(long eventId, LocalDate startDate) {
        ScheduleInfo scheduleInfo = getScheduleInfo(eventId, startDate);

        return Collections.max(scheduleInfo.startDateSet);
    }

    /**
     * Gets activity identifier/day number, track number, start time map.
     *
     * @param eventId   event identifier
     * @param startDate start date
     * @return day, track, start time map
     */
    Map<String, DayTrackTime> getDayTrackTimeMap(long eventId, LocalDate startDate) {
        ScheduleInfo scheduleInfo = getScheduleInfo(eventId, startDate);

        // Fill DayTrackTime maps
        long dayNumber = 0;
        Map<String, DayTrackTime> currentDayTrackTimeMap = new HashMap<>();

        for (JrgCmsDay day : scheduleInfo.sortedDays) {
            LocalDate dayDate = CmsDataLoader.createEventLocalDate(day.getDayStartsAt());

            if (scheduleInfo.startDateSet.contains(dayDate)) {
                dayNumber++;

                for (JrgCmsTrack track : day.getTracks()) {
                    for (JrgCmsSlot slot : track.getSlots()) {
                        currentDayTrackTimeMap.put(slot.getActivity().getId(), new DayTrackTime(
                                dayNumber,
                                track.getTrackNumber(),
                                CmsDataLoader.createEventLocalTime(slot.getSlotStartTime())));
                    }
                }
            }
        }

        return currentDayTrackTimeMap;
    }

    /**
     * Gets talks
     *
     * @param eventId         event identifier
     * @param ignoreDemoStage ignore demo stage talks
     * @param dayTrackTimeMap day, track, start time map
     * @return talks
     */
    List<Talk> getTalks(long eventId, boolean ignoreDemoStage, Map<String, DayTrackTime> dayTrackTimeMap) {
        // https://speakers.jugru.org/api/v1/public/events/{eventId}/activities
        var builder = UriComponentsBuilder
                .fromUriString(TALKS_BASE_URL);
        var uri = builder
                .buildAndExpand(eventId)
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
                .filter(a -> dayTrackTimeMap.containsKey(a.getId()))
                .map(a -> JrgCmsDataLoader.createTalk(a, speakerMap, talkId, dayTrackTimeMap))
                .sorted(Comparator.comparing(Talk::getTalkDay).thenComparing(Talk::getTrackTime).thenComparing(Talk::getTrack))
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
                ((activity.getData().getOptions() == null) || (activity.getData().getOptions().getDemoStage() == null) || !activity.getData().getOptions().getDemoStage());
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
                        c -> c,
                        (c1, c2) -> c1
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
     * @param jrgCmsActivity  JUG Ru Group CMS activity
     * @param speakerMap      speaker map
     * @param talkId          atomic talk identifier
     * @param dayTrackTimeMap day, track, start time map
     * @return talk
     */
    static Talk createTalk(JrgCmsActivity jrgCmsActivity, Map<String, Speaker> speakerMap, AtomicLong talkId,
                           Map<String, DayTrackTime> dayTrackTimeMap) {
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
        DayTrackTime dayTrackTime = Objects.requireNonNull(dayTrackTimeMap.get(jrgCmsActivity.getId()),
                () -> String.format("Activity id %s not found for '%s' talk", jrgCmsActivity.getId(), jrgCmsTalk.getTitle().get(ENGLISH_TEXT_KEY)));

        return new Talk(
                new Descriptionable(
                        talkId.getAndDecrement(),
                        extractLocaleItems(jrgCmsTalk.getTitle()),
                        extractLocaleItems(jrgCmsTalk.getShortDescription()),
                        extractLocaleItems(jrgCmsTalk.getFullDescription())
                ),
                dayTrackTime.dayNumber(),
                dayTrackTime.startTime(),
                dayTrackTime.trackNumber(),
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
                name += (" " + lastName);
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
        if ((presentation == null) || (presentation.getFiles() == null)) {
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
        List<JrgCmsPhoto> photos = jrgCmsSpeaker.getPhoto();
        String speakerName = getSpeakerName(jrgCmsSpeaker.getLastName().get(ENGLISH_TEXT_KEY), jrgCmsSpeaker.getFirstName().get(ENGLISH_TEXT_KEY));

        if (photos == null) {
            log.warn("Photos is null for '{}' speaker", speakerName);
            return new UrlDates(null, null, null);
        }

        if (photos.isEmpty()) {
            log.warn("Photos is empty for '{}' speaker", speakerName);
            return new UrlDates(null, null, null);
        }

        JrgCmsPhoto jrgCmsPhoto = photos.get(0);

        if (photos.size() > 1) {
            log.warn("There are many photos ({}) for '{}' speaker", photos.size(), speakerName);
        }

        return new UrlDates(jrgCmsPhoto.getLinks().getContent(), jrgCmsPhoto.getCreated(), jrgCmsPhoto.getLastModified());
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
