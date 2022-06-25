package guess.util.load;

import guess.domain.Language;
import guess.domain.source.LocaleItem;
import guess.domain.source.cms.jrgcms.JrgCmsLinks;
import guess.domain.source.cms.jrgcms.JrgCmsPhoto;
import guess.domain.source.cms.jrgcms.JrgCmsTokenResponse;
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

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
