package guess.util.load;

import guess.domain.Conference;
import guess.domain.Language;
import guess.domain.source.*;
import guess.domain.source.cms.jrgcms.JrgCmsLinks;
import guess.domain.source.cms.jrgcms.JrgCmsObject;
import guess.domain.source.cms.jrgcms.JrgCmsPhoto;
import guess.domain.source.cms.jrgcms.JrgCmsTokenResponse;
import guess.domain.source.cms.jrgcms.event.JrgCmsAboutPage;
import guess.domain.source.cms.jrgcms.event.JrgCmsConferenceSiteContent;
import guess.domain.source.cms.jrgcms.event.JrgCmsConferenceSiteContentResponse;
import guess.domain.source.cms.jrgcms.event.JrgCmsEvent;
import guess.domain.source.cms.jrgcms.speaker.JrgCmsSpeaker;
import guess.domain.source.cms.jrgcms.speaker.JrgContact;
import guess.domain.source.cms.jrgcms.talk.JrgTalkPresentation;
import guess.domain.source.cms.jrgcms.talk.JrgTalkPresentationFile;
import guess.domain.source.image.UrlDates;
import guess.util.FileUtils;
import guess.util.LocalizationUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.stubbing.Answer;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("JrgCmsDataLoader class tests")
class JrgCmsDataLoaderTest {
    private static final String OPTIONS_DIRECTORY_NAME = "options";

    @BeforeEach
    void setUp() throws IOException {
        FileUtils.deleteDirectory(OPTIONS_DIRECTORY_NAME);
    }

    @AfterEach
    void tearDown() throws IOException {
        FileUtils.deleteDirectory(OPTIONS_DIRECTORY_NAME);
    }

    @Test
    void getRestTemplate() {
        assertNotNull(JrgCmsDataLoader.getRestTemplate());
    }

    @Test
    void getOptionsDirectoryName() {
        assertNotNull(JrgCmsDataLoader.getOptionsDirectoryName());
    }

    @Test
    void getTokenFromCacheAndStoreTokenInCache() throws IOException {
        try (MockedStatic<JrgCmsDataLoader> mockedStatic = Mockito.mockStatic(JrgCmsDataLoader.class)) {
            mockedStatic.when(JrgCmsDataLoader::getOptionsDirectoryName)
                    .thenReturn(OPTIONS_DIRECTORY_NAME);
            mockedStatic.when(() -> JrgCmsDataLoader.storeTokenInCache(Mockito.any(JrgCmsTokenResponse.class)))
                    .thenCallRealMethod();
            mockedStatic.when(JrgCmsDataLoader::getTokenFromCache)
                    .thenCallRealMethod();

            final String ACCESS_TOKEN = "ACCESS_TOKEN";

            JrgCmsTokenResponse tokenResponse = JrgCmsDataLoader.getTokenFromCache();

            assertNull(tokenResponse);

            JrgCmsTokenResponse jrgCmsTokenResponse = new JrgCmsTokenResponse();
            jrgCmsTokenResponse.setAccessToken(ACCESS_TOKEN);

            assertDoesNotThrow(() -> JrgCmsDataLoader.storeTokenInCache(jrgCmsTokenResponse));

            tokenResponse = JrgCmsDataLoader.getTokenFromCache();

            assertNotNull(tokenResponse);
            assertEquals(ACCESS_TOKEN, tokenResponse.getAccessToken());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getToken method tests")
    class GetTokenTest {
        private Stream<Arguments> data() {
            final String CLIENT_ID = "clientId";
            final String CLIENT_SECRET = "clientSecret";

            return Stream.of(
                    arguments(null, null, IllegalArgumentException.class),
                    arguments(null, CLIENT_SECRET, IllegalArgumentException.class),
                    arguments(CLIENT_ID, null, IllegalArgumentException.class),
                    arguments(CLIENT_ID, CLIENT_SECRET, null)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getToken(String clientId, String clientSecret, Class<? extends Exception> expectedException) {
            try (MockedStatic<JrgCmsDataLoader> mockedStatic = Mockito.mockStatic(JrgCmsDataLoader.class)) {
                mockedStatic.when(() -> JrgCmsDataLoader.getToken(Mockito.nullable(String.class), Mockito.nullable(String.class)))
                        .thenCallRealMethod();

                if (expectedException == null) {
                    final String ACCESS_TOKEN = "accessToken";

                    JrgCmsTokenResponse tokenResponse = new JrgCmsTokenResponse();
                    tokenResponse.setAccessToken(ACCESS_TOKEN);

                    ResponseEntity<JrgCmsTokenResponse> responseEntity = new ResponseEntity<>(tokenResponse, HttpStatus.OK);

                    RestTemplate restTemplateMock = Mockito.mock(RestTemplate.class);
                    Mockito.when(restTemplateMock.exchange(Mockito.any(URI.class), Mockito.eq(HttpMethod.POST),
                                    Mockito.nullable(HttpEntity.class), Mockito.eq(JrgCmsTokenResponse.class)))
                            .thenReturn(responseEntity);

                    mockedStatic.when(JrgCmsDataLoader::getRestTemplate)
                            .thenReturn(restTemplateMock);

                    JrgCmsTokenResponse actual = JrgCmsDataLoader.getToken(clientId, clientSecret);

                    assertEquals(ACCESS_TOKEN, actual.getAccessToken());
                } else {
                    assertThrows(expectedException, () -> JrgCmsDataLoader.getToken(clientId, clientSecret));
                }
            }
        }
    }

    @Test
    void getToken() {
        try (MockedStatic<JrgCmsDataLoader> mockedStatic = Mockito.mockStatic(JrgCmsDataLoader.class)) {
            mockedStatic.when(JrgCmsDataLoader::getToken)
                    .thenCallRealMethod();

            JrgCmsDataLoader.getToken();

            mockedStatic.verify(JrgCmsDataLoader::getToken);
            mockedStatic.verify(() -> JrgCmsDataLoader.getToken(Mockito.nullable(String.class), Mockito.nullable(String.class)));
            mockedStatic.verifyNoMoreInteractions();
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("makeRequest method tests")
    class MakeRequestTest {
        private Stream<Arguments> data() {
            final int RESULT = 42;
            final String ACCESS_TOKEN0 = "accessToken0";
            final String ACCESS_TOKEN1 = "accessToken1";
            final String ACCESS_TOKEN2 = "accessToken2";
            final String ACCESS_TOKEN3 = "accessToken3";

            Function<String, Integer> requestFunction0 = s -> RESULT;

            Function<String, Integer> requestFunction1 = s -> {
                return switch (s) {
                    case ACCESS_TOKEN0, ACCESS_TOKEN2 ->
                            throw HttpClientErrorException.create(null, HttpStatus.UNAUTHORIZED, "", new HttpHeaders(), null, null);
                    case ACCESS_TOKEN1 -> RESULT;
                    default ->
                            throw HttpClientErrorException.create(null, HttpStatus.BAD_REQUEST, "", new HttpHeaders(), null, null);
                };
            };

            JrgCmsTokenResponse tokenResponse0 = new JrgCmsTokenResponse();

            JrgCmsTokenResponse tokenResponse1 = new JrgCmsTokenResponse();
            tokenResponse1.setAccessToken(ACCESS_TOKEN0);

            JrgCmsTokenResponse tokenResponse2 = new JrgCmsTokenResponse();
            tokenResponse2.setAccessToken(ACCESS_TOKEN1);

            JrgCmsTokenResponse tokenResponse3 = new JrgCmsTokenResponse();
            tokenResponse3.setAccessToken(ACCESS_TOKEN2);

            JrgCmsTokenResponse tokenResponse4 = new JrgCmsTokenResponse();
            tokenResponse4.setAccessToken(ACCESS_TOKEN3);

            return Stream.of(
                    arguments(requestFunction0, null, tokenResponse0, null, RESULT),
                    arguments(requestFunction0, tokenResponse0, tokenResponse0, null, RESULT),
                    arguments(requestFunction0, tokenResponse1, null, null, RESULT),
                    arguments(requestFunction1, tokenResponse1, tokenResponse2, null, RESULT),
                    arguments(requestFunction1, tokenResponse1, tokenResponse3, HttpClientErrorException.Unauthorized.class, null),
                    arguments(requestFunction1, tokenResponse1, tokenResponse4, HttpClientErrorException.BadRequest.class, null)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        @SuppressWarnings("unchecked")
        void makeRequest(Function<String, Integer> requestFunction, JrgCmsTokenResponse getTokenFromCacheResult,
                         JrgCmsTokenResponse getTokenResult, Class<? extends Exception> expectedException, Integer expectedValue)
                throws IOException, NoSuchFieldException {
            try (MockedStatic<JrgCmsDataLoader> mockedStatic = Mockito.mockStatic(JrgCmsDataLoader.class)) {
                mockedStatic.when(() -> JrgCmsDataLoader.makeRequest(Mockito.any(Function.class)))
                        .thenCallRealMethod();
                mockedStatic.when(JrgCmsDataLoader::getTokenFromCache)
                        .thenReturn(getTokenFromCacheResult);
                mockedStatic.when(JrgCmsDataLoader::getToken)
                        .thenReturn(getTokenResult);

                if (expectedException == null) {
                    assertEquals(expectedValue, JrgCmsDataLoader.makeRequest(requestFunction));
                } else {
                    assertThrowsExactly(expectedException, () -> JrgCmsDataLoader.makeRequest(requestFunction));
                }
            }
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void getTags() throws IOException, NoSuchFieldException {
        try (MockedStatic<JrgCmsDataLoader> mockedStatic = Mockito.mockStatic(JrgCmsDataLoader.class)) {
            final String MOBIUS_EVENT_PROJECT = "MOBIUS";
            final String HEISENBUG_EVENT_PROJECT = "HEISENBUG";
            final String SPRING_EVENT_VERSION = "2022 Spring";
            final String AUTUMN_EVENT_VERSION = "2022 Autumn";

            // Event projects
            JrgCmsObject<String> eventProjectObject0 = new JrgCmsObject<>();
            eventProjectObject0.setIv(MOBIUS_EVENT_PROJECT);

            JrgCmsObject<String> eventProjectObject1 = new JrgCmsObject<>();
            eventProjectObject1.setIv(HEISENBUG_EVENT_PROJECT);

            // Event versions
            JrgCmsObject<String> eventVersion0 = new JrgCmsObject<>();
            eventVersion0.setIv(SPRING_EVENT_VERSION);

            JrgCmsObject<String> eventVersion1 = new JrgCmsObject<>();
            eventVersion1.setIv(AUTUMN_EVENT_VERSION);

            // Events
            JrgCmsEvent jrgCmsEvent0 = new JrgCmsEvent();
            jrgCmsEvent0.setEventProject(eventProjectObject0);
            jrgCmsEvent0.setEventVersion(eventVersion0);

            JrgCmsEvent jrgCmsEvent1 = new JrgCmsEvent();
            jrgCmsEvent1.setEventProject(eventProjectObject1);
            jrgCmsEvent1.setEventVersion(eventVersion0);

            JrgCmsEvent jrgCmsEvent2 = new JrgCmsEvent();
            jrgCmsEvent2.setEventProject(eventProjectObject1);
            jrgCmsEvent2.setEventVersion(eventVersion1);

            // Conference sile contents
            JrgCmsConferenceSiteContent jrgCmsConferenceSiteContent0 = new JrgCmsConferenceSiteContent();
            jrgCmsConferenceSiteContent0.setData(jrgCmsEvent0);

            JrgCmsConferenceSiteContent jrgCmsConferenceSiteContent1 = new JrgCmsConferenceSiteContent();
            jrgCmsConferenceSiteContent1.setData(jrgCmsEvent1);

            JrgCmsConferenceSiteContent jrgCmsConferenceSiteContent2 = new JrgCmsConferenceSiteContent();
            jrgCmsConferenceSiteContent2.setData(jrgCmsEvent2);

            // Conference sile content list
            JrgCmsConferenceSiteContentResponse response = new JrgCmsConferenceSiteContentResponse();
            response.setItems(List.of(jrgCmsConferenceSiteContent0, jrgCmsConferenceSiteContent1, jrgCmsConferenceSiteContent2));

            ResponseEntity<JrgCmsConferenceSiteContentResponse> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);

            RestTemplate restTemplateMock = Mockito.mock(RestTemplate.class);
            Mockito.when(restTemplateMock.exchange(Mockito.any(URI.class), Mockito.eq(HttpMethod.GET),
                            Mockito.nullable(HttpEntity.class), Mockito.eq(JrgCmsConferenceSiteContentResponse.class)))
                    .thenReturn(responseEntity);

            // Mock methods
            mockedStatic.when(() -> JrgCmsDataLoader.makeRequest(Mockito.any(Function.class)))
                    .thenAnswer(
                            (Answer<Map<String, List<String>>>) invocation -> {
                                Object[] args = invocation.getArguments();
                                Function<String, Map<String, List<String>>> requestFunction = (Function<String, Map<String, List<String>>>) args[0];

                                return requestFunction.apply("");
                            }
                    );
            mockedStatic.when(JrgCmsDataLoader::getRestTemplate)
                    .thenReturn(restTemplateMock);

            // Mock method under test
            JrgCmsDataLoader jrgCmsDataLoader = Mockito.mock(JrgCmsDataLoader.class);
            Mockito.when(jrgCmsDataLoader.getTags(Mockito.anyString()))
                    .thenCallRealMethod();

            // Expected result
            Map<String, List<String>> expected = new LinkedHashMap<>();
            expected.put(HEISENBUG_EVENT_PROJECT, List.of(AUTUMN_EVENT_VERSION, SPRING_EVENT_VERSION));
            expected.put(MOBIUS_EVENT_PROJECT, List.of(SPRING_EVENT_VERSION));

            // Actual result
            Map<String, List<String>> actual = jrgCmsDataLoader.getTags("2022");

            assertEquals(expected, actual);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void getEventTypes() throws IOException, NoSuchFieldException {
        try (MockedStatic<JrgCmsDataLoader> mockedStatic = Mockito.mockStatic(JrgCmsDataLoader.class)) {
            final String MOBIUS_EVENT_PROJECT = "MOBIUS";
            final String HEISENBUG_EVENT_PROJECT = "HEISENBUG";
            final String SPRING_EVENT_VERSION = "2022 Spring";
            final String AUTUMN_EVENT_VERSION = "2022 Autumn";

            // Event projects
            JrgCmsObject<String> eventProjectObject0 = new JrgCmsObject<>();
            eventProjectObject0.setIv(MOBIUS_EVENT_PROJECT);

            JrgCmsObject<String> eventProjectObject1 = new JrgCmsObject<>();
            eventProjectObject1.setIv(HEISENBUG_EVENT_PROJECT);

            // Event versions
            JrgCmsObject<String> eventVersion0 = new JrgCmsObject<>();
            eventVersion0.setIv(SPRING_EVENT_VERSION);

            JrgCmsObject<String> eventVersion1 = new JrgCmsObject<>();
            eventVersion1.setIv(AUTUMN_EVENT_VERSION);

            // Event about pages
            JrgCmsAboutPage jrgCmsAboutPage0 = new JrgCmsAboutPage();
            jrgCmsAboutPage0.setMain("");

            JrgCmsAboutPage jrgCmsAboutPage1 = new JrgCmsAboutPage();
            jrgCmsAboutPage1.setMain("");

            Map<String, JrgCmsAboutPage> aboutPage = new HashMap<>();
            aboutPage.put(JrgCmsDataLoader.ENGLISH_TEXT_KEY, jrgCmsAboutPage0);
            aboutPage.put(JrgCmsDataLoader.RUSSIAN_TEXT_KEY, jrgCmsAboutPage1);

            // Events
            JrgCmsEvent jrgCmsEvent0 = new JrgCmsEvent();
            jrgCmsEvent0.setEventProject(eventProjectObject0);
            jrgCmsEvent0.setEventVersion(eventVersion0);
            jrgCmsEvent0.setAboutPage(aboutPage);

            JrgCmsEvent jrgCmsEvent1 = new JrgCmsEvent();
            jrgCmsEvent1.setEventProject(eventProjectObject1);
            jrgCmsEvent1.setEventVersion(eventVersion0);
            jrgCmsEvent1.setAboutPage(aboutPage);

            JrgCmsEvent jrgCmsEvent2 = new JrgCmsEvent();
            jrgCmsEvent2.setEventProject(eventProjectObject1);
            jrgCmsEvent2.setEventVersion(eventVersion1);
            jrgCmsEvent2.setAboutPage(aboutPage);

            // Conference sile contents
            JrgCmsConferenceSiteContent jrgCmsConferenceSiteContent0 = new JrgCmsConferenceSiteContent();
            jrgCmsConferenceSiteContent0.setData(jrgCmsEvent0);

            JrgCmsConferenceSiteContent jrgCmsConferenceSiteContent1 = new JrgCmsConferenceSiteContent();
            jrgCmsConferenceSiteContent1.setData(jrgCmsEvent1);

            JrgCmsConferenceSiteContent jrgCmsConferenceSiteContent2 = new JrgCmsConferenceSiteContent();
            jrgCmsConferenceSiteContent2.setData(jrgCmsEvent2);

            // Conference sile content list
            JrgCmsConferenceSiteContentResponse response = new JrgCmsConferenceSiteContentResponse();
            response.setItems(List.of(jrgCmsConferenceSiteContent0, jrgCmsConferenceSiteContent1, jrgCmsConferenceSiteContent2));

            ResponseEntity<JrgCmsConferenceSiteContentResponse> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);

            RestTemplate restTemplateMock = Mockito.mock(RestTemplate.class);
            Mockito.when(restTemplateMock.exchange(Mockito.any(URI.class), Mockito.eq(HttpMethod.GET),
                            Mockito.nullable(HttpEntity.class), Mockito.eq(JrgCmsConferenceSiteContentResponse.class)))
                    .thenReturn(responseEntity);

            // Mock methods
            mockedStatic.when(() -> JrgCmsDataLoader.makeRequest(Mockito.any(Function.class)))
                    .thenAnswer(
                            (Answer<List<EventType>>) invocation -> {
                                Object[] args = invocation.getArguments();
                                Function<String, List<EventType>> requestFunction = (Function<String, List<EventType>>) args[0];

                                return requestFunction.apply("");
                            }
                    );
            mockedStatic.when(JrgCmsDataLoader::getRestTemplate)
                    .thenReturn(restTemplateMock);
            mockedStatic.when(() -> JrgCmsDataLoader.createEventType(Mockito.any(JrgCmsEvent.class), Mockito.any(AtomicLong.class), Mockito.any(Conference.class)))
                    .thenAnswer(
                            (Answer<EventType>) invocation -> {
                                Object[] args = invocation.getArguments();
                                Conference conference = (Conference) args[2];

                                EventType eventType = new EventType();
                                eventType.setConference(conference);

                                return eventType;
                            }
                    );

            // Mock method under test
            JrgCmsDataLoader jrgCmsDataLoader = Mockito.mock(JrgCmsDataLoader.class);
            Mockito.when(jrgCmsDataLoader.getEventTypes())
                    .thenCallRealMethod();

            // Actual result
            List<EventType> actual = jrgCmsDataLoader.getEventTypes();

            assertEquals(2, actual.size());
            assertEquals(1, actual.stream()
                    .filter(et -> Conference.MOBIUS.equals(et.getConference()))
                    .count());
            assertEquals(1, actual.stream()
                    .filter(et -> Conference.HEISENBUG.equals(et.getConference()))
                    .count());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("createEventType method tests")
    class CreateEventTypeTest {
        private JrgCmsEvent createJrgCmsEvent(String enLongDescription, String ruLongDescription) {
            JrgCmsAboutPage jrgCmsAboutPage0 = new JrgCmsAboutPage();
            jrgCmsAboutPage0.setMain(enLongDescription);

            JrgCmsAboutPage jrgCmsAboutPage1 = new JrgCmsAboutPage();
            jrgCmsAboutPage1.setMain(ruLongDescription);

            Map<String, JrgCmsAboutPage> aboutPage0 = new HashMap<>();
            aboutPage0.put(JrgCmsDataLoader.ENGLISH_TEXT_KEY, jrgCmsAboutPage0);
            aboutPage0.put(JrgCmsDataLoader.RUSSIAN_TEXT_KEY, jrgCmsAboutPage1);

            JrgCmsEvent jrgCmsEvent = new JrgCmsEvent();
            jrgCmsEvent.setAboutPage(aboutPage0);

            return jrgCmsEvent;
        }

        private EventType createEventType(long id, Conference conference, String enLongDescription, String ruLongDescription) {
            EventType eventType = new EventType();

            eventType.setId(id);
            eventType.setConference(conference);
            eventType.setLongDescription(List.of(
                    new LocaleItem(Language.ENGLISH.getCode(), enLongDescription),
                    new LocaleItem(Language.RUSSIAN.getCode(), ruLongDescription)
            ));

            return eventType;
        }

        private Stream<Arguments> data() {
            final long ID0 = 42;
            final long ID1 = 43;
            final String ENGLISH_LONG_DESCRIPTION0 = "LongDescription0";
            final String ENGLISH_LONG_DESCRIPTION1 = "LongDescription1";
            final String RUSSIAN_LONG_DESCRIPTION0 = "ДлинноеОписание0";
            final String RUSSIAN_LONG_DESCRIPTION1 = "ДлинноеОписание1";
            final Conference CONFERENCE0 = Conference.HEISENBUG;
            final Conference CONFERENCE1 = Conference.MOBIUS;

            JrgCmsEvent jrgCmsEvent0 = createJrgCmsEvent(ENGLISH_LONG_DESCRIPTION0, RUSSIAN_LONG_DESCRIPTION0);
            JrgCmsEvent jrgCmsEvent1 = createJrgCmsEvent(ENGLISH_LONG_DESCRIPTION1, RUSSIAN_LONG_DESCRIPTION1);

            EventType eventType0 = createEventType(ID0, CONFERENCE0, ENGLISH_LONG_DESCRIPTION0, RUSSIAN_LONG_DESCRIPTION0);
            EventType eventType1 = createEventType(ID1, CONFERENCE1, ENGLISH_LONG_DESCRIPTION1, RUSSIAN_LONG_DESCRIPTION1);

            return Stream.of(
                    arguments(jrgCmsEvent0, new AtomicLong(ID0), CONFERENCE0, eventType0),
                    arguments(jrgCmsEvent1, new AtomicLong(ID1), CONFERENCE1, eventType1)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void createEventType(JrgCmsEvent et, AtomicLong id, Conference conference, EventType expected) {
            EventType actual = JrgCmsDataLoader.createEventType(et, id, conference);

            assertEquals(expected, actual);
            assertEquals(expected.getConference(), actual.getConference());
            assertEquals(expected.getLongDescription(), actual.getLongDescription());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getEvent method tests")
    class GetEventTest {
        private Stream<Arguments> data() {
            final String EN_NAME0 = "Name0";
            final String RU_NAME0 = "Наименование0";
            final String EN_NAME1 = "Name1";
            final String RU_NAME1 = "Наименование1";
            final String EN_NAME2 = "Name2";
            final String RU_NAME2 = "Наименование2";
            final LocalDate START_DATE0 = LocalDate.of(2022, 6, 29);
            final LocalDate END_DATE0 = LocalDate.of(2022, 6, 30);
            final LocalDate START_DATE1 = LocalDate.of(2022, 7, 1);
            final LocalDate END_DATE1 = LocalDate.of(2022, 7, 2);

            // Names
            List<LocaleItem> name0 = List.of(
                    new LocaleItem(Language.ENGLISH.getCode(), EN_NAME0),
                    new LocaleItem(Language.RUSSIAN.getCode(), RU_NAME0)
            );

            List<LocaleItem> name1 = List.of(
                    new LocaleItem(Language.ENGLISH.getCode(), EN_NAME1),
                    new LocaleItem(Language.RUSSIAN.getCode(), RU_NAME1)
            );

            List<LocaleItem> name2 = List.of(
                    new LocaleItem(Language.ENGLISH.getCode(), EN_NAME2),
                    new LocaleItem(Language.RUSSIAN.getCode(), RU_NAME2)
            );

            // Places
            Place place0 = new Place();
            place0.setId(0);

            Place place1 = new Place();
            place1.setId(1);

            // Event templates
            Event eventTemplate0 = new Event();
            eventTemplate0.setName(name0);
            eventTemplate0.setDays(Collections.emptyList());

            Event eventTemplate1 = new Event();
            eventTemplate1.setName(name1);
            eventTemplate1.setDays(List.of(new EventDays(null, null, place0)));

            Event eventTemplate2 = new Event();
            eventTemplate2.setName(name2);
            eventTemplate2.setDays(List.of(new EventDays(null, null, place1)));

            // Events
            Event event0 = new Event();
            event0.setId(-1L);
            event0.setName(name1);
            event0.setDays(List.of(new EventDays(START_DATE0, END_DATE0, place0)));

            Event event1 = new Event();
            event1.setId(-1L);
            event1.setName(name2);
            event1.setDays(List.of(new EventDays(START_DATE1, END_DATE1, place1)));

            // Results of getEventDatesList method
            List<JrgCmsDataLoader.EventDates> getEventDatesListResult0 = List.of(
                    new JrgCmsDataLoader.EventDates(START_DATE0, END_DATE0)
            );

            List<JrgCmsDataLoader.EventDates> getEventDatesListResult1 = List.of(
                    new JrgCmsDataLoader.EventDates(START_DATE1, END_DATE1)
            );

            return Stream.of(
                    arguments(null, null, null, eventTemplate0, getEventDatesListResult0, IllegalArgumentException.class, null),
                    arguments(null, null, null, eventTemplate1, getEventDatesListResult0, null, event0),
                    arguments(null, null, null, eventTemplate2, getEventDatesListResult1, null, event1)
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getEvent(Conference conference, LocalDate startDate, String conferenceCode, Event eventTemplate,
                      List<JrgCmsDataLoader.EventDates> getEventDatesListResult, Class<? extends Exception> expectedException,
                      Event expectedValue) throws IOException, NoSuchFieldException {
            JrgCmsDataLoader jrgCmsDataLoader = Mockito.mock(JrgCmsDataLoader.class);

            Mockito.when(jrgCmsDataLoader.getEvent(Mockito.nullable(Conference.class), Mockito.nullable(LocalDate.class), Mockito.nullable(String.class), Mockito.any(Event.class)))
                    .thenCallRealMethod();
            Mockito.when(jrgCmsDataLoader.getEventId(Mockito.any(Conference.class), Mockito.any(String.class)))
                    .thenReturn(42L);
            Mockito.when(jrgCmsDataLoader.getEventDatesList(Mockito.anyLong()))
                    .thenReturn(getEventDatesListResult);

            if (expectedException == null) {
                Event actual = jrgCmsDataLoader.getEvent(conference, startDate, conferenceCode, eventTemplate);

                assertEquals(expectedValue, actual);
                assertEquals(expectedValue.getName(), actual.getName());
                assertEquals(expectedValue.getDays(), actual.getDays());
            } else {
                assertThrows(expectedException, () -> jrgCmsDataLoader.getEvent(conference, startDate, conferenceCode, eventTemplate));
            }
        }
    }

    @Test
    void getImageWidthParameterName() {
        assertEquals("width", new JrgCmsDataLoader().getImageWidthParameterName());
    }

    @Test
    void extractLocaleItems() {
        try (MockedStatic<JrgCmsDataLoader> jrgCmsDataLoaderMockedStatic = Mockito.mockStatic(JrgCmsDataLoader.class);
             MockedStatic<CmsDataLoader> cmsDataLoaderMockedStatic = Mockito.mockStatic(CmsDataLoader.class)) {
            jrgCmsDataLoaderMockedStatic.when(() -> JrgCmsDataLoader.extractLocaleItems(Mockito.anyMap(), Mockito.anyBoolean()))
                    .thenCallRealMethod();
            cmsDataLoaderMockedStatic.when(() -> CmsDataLoader.extractLocaleItems(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
                    .thenReturn(Collections.emptyList());

            assertDoesNotThrow(() -> JrgCmsDataLoader.extractLocaleItems(Map.of(), true));
            cmsDataLoaderMockedStatic.verify(() -> CmsDataLoader.extractLocaleItems(Mockito.nullable(String.class), Mockito.nullable(String.class), Mockito.anyBoolean()),
                    VerificationModeFactory.times(1));
        }
    }

    @Test
    void testExtractLocaleItems() {
        try (MockedStatic<JrgCmsDataLoader> mockedStatic = Mockito.mockStatic(JrgCmsDataLoader.class)) {
            mockedStatic.when(() -> JrgCmsDataLoader.extractLocaleItems(Mockito.anyMap()))
                    .thenCallRealMethod();
            mockedStatic.when(() -> JrgCmsDataLoader.extractLocaleItems(Mockito.anyMap(), Mockito.anyBoolean()))
                    .thenReturn(Collections.emptyList());

            assertDoesNotThrow(() -> JrgCmsDataLoader.extractLocaleItems(Map.of()));
            mockedStatic.verify(() -> JrgCmsDataLoader.extractLocaleItems(Mockito.anyMap(), Mockito.anyBoolean()),
                    VerificationModeFactory.times(1));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getSpeakerName method tests")
    class GetSpeakerNameTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, null, null),
                    arguments("", null, null),
                    arguments(null, "", null),
                    arguments("", "", null),
                    arguments(null, "Last", "Last"),
                    arguments("First", null, "First"),
                    arguments("Last", "First", "First Last")
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getSpeakerName(String lastName, String firstName, String expected) {
            assertEquals(expected, JrgCmsDataLoader.getSpeakerName(lastName, firstName));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void getSpeakerName() {
        try (MockedStatic<LocalizationUtils> localizationUtilsMockedStatic = Mockito.mockStatic(LocalizationUtils.class);
             MockedStatic<JrgCmsDataLoader> jrgCmsDataLoaderMockedStatic = Mockito.mockStatic(JrgCmsDataLoader.class)) {
            jrgCmsDataLoaderMockedStatic.when(() -> JrgCmsDataLoader.getSpeakerName(Mockito.anyList(), Mockito.anyList(), Mockito.nullable(Language.class)))
                    .thenCallRealMethod();
            jrgCmsDataLoaderMockedStatic.when(() -> JrgCmsDataLoader.getSpeakerName(Mockito.nullable(String.class), Mockito.nullable(String.class)))
                    .thenReturn("");

            final String FIRST_NAME = "First";
            final String LAST_NAME = "Last";
            localizationUtilsMockedStatic.when(() -> LocalizationUtils.getString(Mockito.anyList(), Mockito.nullable(Language.class)))
                    .thenAnswer(
                            (Answer<String>) invocation -> {
                                Object[] args = invocation.getArguments();
                                List<LocaleItem> localeItems = (List<LocaleItem>) args[0];

                                if (localeItems.isEmpty()) {
                                    return LAST_NAME;
                                } else if (localeItems.size() == 1) {
                                    return FIRST_NAME;
                                } else {
                                    return null;
                                }
                            }
                    );

            assertDoesNotThrow(() -> JrgCmsDataLoader.getSpeakerName(Collections.emptyList(), List.of(new LocaleItem()), Language.ENGLISH));
            localizationUtilsMockedStatic.verify(() -> LocalizationUtils.getString(Mockito.anyList(), Mockito.nullable(Language.class)),
                    VerificationModeFactory.times(2));
            jrgCmsDataLoaderMockedStatic.verify(() -> JrgCmsDataLoader.getSpeakerName(LAST_NAME, FIRST_NAME),
                    VerificationModeFactory.times(1));
            localizationUtilsMockedStatic.verifyNoMoreInteractions();
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("extractLanguage method tests")
    class ExtractLanguageTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, null),
                    arguments("", null),
                    arguments("un", null),
                    arguments("en", "en"),
                    arguments("EN", "en"),
                    arguments("ru", "ru"),
                    arguments("RU", "ru")
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractLanguage(String language, String expected) {
            assertEquals(expected, JrgCmsDataLoader.extractLanguage(language));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("extractPresentationLinks method tests")
    class ExtractPresentationLinksTest {
        private Stream<Arguments> data() {
            final String CONTENT0 = "https://valid.com/fileName0.pdf";
            final String CONTENT1 = "https://valid.com/fileName1.pdf";

            JrgCmsLinks jrgCmsLinks0 = new JrgCmsLinks();
            jrgCmsLinks0.setContent(CONTENT0);

            JrgCmsLinks jrgCmsLinks1 = new JrgCmsLinks();
            jrgCmsLinks1.setContent(CONTENT1);

            JrgTalkPresentationFile jrgTalkPresentationFile0 = new JrgTalkPresentationFile();
            jrgTalkPresentationFile0.setLinks(jrgCmsLinks0);

            JrgTalkPresentationFile jrgTalkPresentationFile1 = new JrgTalkPresentationFile();
            jrgTalkPresentationFile1.setLinks(jrgCmsLinks1);

            JrgTalkPresentation jrgTalkPresentation0 = new JrgTalkPresentation();

            JrgTalkPresentation jrgTalkPresentation1 = new JrgTalkPresentation();
            jrgTalkPresentation1.setFiles(List.of(jrgTalkPresentationFile0));

            JrgTalkPresentation jrgTalkPresentation2 = new JrgTalkPresentation();
            jrgTalkPresentation2.setFiles(List.of(jrgTalkPresentationFile0, jrgTalkPresentationFile1));

            return Stream.of(
                    arguments(null, Collections.emptyList()),
                    arguments(jrgTalkPresentation0, Collections.emptyList()),
                    arguments(jrgTalkPresentation1, List.of(CONTENT0)),
                    arguments(jrgTalkPresentation2, List.of(CONTENT0, CONTENT1))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractPresentationLinks(JrgTalkPresentation presentation, List<String> expected) {
            assertEquals(expected, JrgCmsDataLoader.extractPresentationLinks(presentation));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("extractPhoto method tests")
    class ExtractPhotoTest {
        private Stream<Arguments> data() {
            final UrlDates EMPTY_URL_DATES = new UrlDates(null, null, null);
            final String CONTENT = "https://valid.com/fileName0.jpg";
            final ZonedDateTime DATE0 = ZonedDateTime.of(2022, 5, 20, 21, 0, 0, 0, ZoneId.of("UTC"));
            final ZonedDateTime DATE1 = ZonedDateTime.of(2022, 5, 20, 22, 0, 0, 0, ZoneId.of("UTC"));
            final ZonedDateTime DATE2 = ZonedDateTime.of(2022, 5, 20, 22, 30, 0, 0, ZoneId.of("UTC"));

            JrgCmsLinks jrgCmsLinks0 = new JrgCmsLinks();
            jrgCmsLinks0.setContent(CONTENT);

            JrgCmsPhoto jrgCmsPhoto0 = new JrgCmsPhoto();
            jrgCmsPhoto0.setLinks(jrgCmsLinks0);
            jrgCmsPhoto0.setCreated(DATE0);
            jrgCmsPhoto0.setLastModified(DATE1);

            JrgCmsPhoto jrgCmsPhoto1 = new JrgCmsPhoto();
            jrgCmsPhoto1.setLinks(jrgCmsLinks0);
            jrgCmsPhoto1.setCreated(DATE0);
            jrgCmsPhoto1.setLastModified(DATE2);

            JrgCmsSpeaker jrgCmsSpeaker0 = new JrgCmsSpeaker();
            jrgCmsSpeaker0.setLastName(Collections.emptyMap());
            jrgCmsSpeaker0.setFirstName(Collections.emptyMap());

            JrgCmsSpeaker jrgCmsSpeaker1 = new JrgCmsSpeaker();
            jrgCmsSpeaker1.setLastName(Collections.emptyMap());
            jrgCmsSpeaker1.setFirstName(Collections.emptyMap());
            jrgCmsSpeaker1.setPhoto(Collections.emptyList());

            JrgCmsSpeaker jrgCmsSpeaker2 = new JrgCmsSpeaker();
            jrgCmsSpeaker2.setLastName(Collections.emptyMap());
            jrgCmsSpeaker2.setFirstName(Collections.emptyMap());
            jrgCmsSpeaker2.setPhoto(List.of(jrgCmsPhoto0));

            JrgCmsSpeaker jrgCmsSpeaker3 = new JrgCmsSpeaker();
            jrgCmsSpeaker3.setLastName(Collections.emptyMap());
            jrgCmsSpeaker3.setFirstName(Collections.emptyMap());
            jrgCmsSpeaker3.setPhoto(List.of(jrgCmsPhoto0, jrgCmsPhoto1));

            return Stream.of(
                    arguments(jrgCmsSpeaker0, EMPTY_URL_DATES),
                    arguments(jrgCmsSpeaker1, EMPTY_URL_DATES),
                    arguments(jrgCmsSpeaker2, new UrlDates(CONTENT, DATE0, DATE1)),
                    arguments(jrgCmsSpeaker3, new UrlDates(CONTENT, DATE0, DATE1))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractPhoto(JrgCmsSpeaker jrgCmsSpeaker, UrlDates expected) {
            try (MockedStatic<JrgCmsDataLoader> jrgCmsDataLoaderMockedStatic = Mockito.mockStatic(JrgCmsDataLoader.class)) {
                jrgCmsDataLoaderMockedStatic.when(() -> JrgCmsDataLoader.extractPhoto(Mockito.any(JrgCmsSpeaker.class)))
                        .thenCallRealMethod();
                jrgCmsDataLoaderMockedStatic.when(() -> JrgCmsDataLoader.getSpeakerName(Mockito.any(), Mockito.any()))
                        .thenReturn("");

                assertEquals(expected, JrgCmsDataLoader.extractPhoto(jrgCmsSpeaker));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("extractContactValue method tests")
    class ExtractContactValueTest {
        private Stream<Arguments> data() {
            final String TWITTER_CONTACT_TYPE = "twitter";
            final String FACEBOOK_CONTACT_TYPE = "facebook";
            final String GITHUB_CONTACT_TYPE = "github";
            final String HABR_CONTACT_TYPE = "habr";
            final String UNKNOWN_CONTACT_TYPE = "unknown";

            final String TWITTER_CONTACT_VALUE = "user";
            final String HABR_CONTACT_VALUE = "";

            final JrgContact jrgContact0 = new JrgContact();
            jrgContact0.setType(TWITTER_CONTACT_TYPE);
            jrgContact0.setValue(TWITTER_CONTACT_VALUE);

            final JrgContact jrgContact1 = new JrgContact();
            jrgContact1.setType(FACEBOOK_CONTACT_TYPE);
            jrgContact1.setValue(null);

            final JrgContact jrgContact2 = new JrgContact();
            jrgContact2.setType(HABR_CONTACT_TYPE);
            jrgContact2.setValue(HABR_CONTACT_VALUE);

            UnaryOperator<String> extractionOperator = v -> v;

            Map<String, JrgContact> contactMap = new HashMap<>();
            contactMap.put(TWITTER_CONTACT_TYPE, jrgContact0);
            contactMap.put(GITHUB_CONTACT_TYPE, null);
            contactMap.put(FACEBOOK_CONTACT_TYPE, jrgContact1);
            contactMap.put(HABR_CONTACT_TYPE, jrgContact2);

            return Stream.of(
                    arguments(contactMap, UNKNOWN_CONTACT_TYPE, null, null),
                    arguments(contactMap, GITHUB_CONTACT_TYPE, null, null),
                    arguments(contactMap, FACEBOOK_CONTACT_TYPE, null, null),
                    arguments(contactMap, HABR_CONTACT_TYPE, null, null),
                    arguments(contactMap, TWITTER_CONTACT_TYPE, extractionOperator, extractionOperator.apply(TWITTER_CONTACT_VALUE))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractContactValue(Map<String, JrgContact> contactMap, String type, UnaryOperator<String> extractionOperator,
                                 String expected) {
            assertEquals(expected, JrgCmsDataLoader.extractContactValue(contactMap, type, extractionOperator));
        }
    }
}
