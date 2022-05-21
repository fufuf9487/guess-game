package guess.util.load;

import guess.domain.Language;
import guess.domain.source.LocaleItem;
import guess.domain.source.cms.jrgcms.JrgLinks;
import guess.domain.source.cms.jrgcms.JrgPhoto;
import guess.domain.source.cms.jrgcms.speaker.JrgCmsSpeaker;
import guess.domain.source.cms.jrgcms.speaker.JrgContact;
import guess.domain.source.cms.jrgcms.talk.JrgTalkPresentation;
import guess.domain.source.cms.jrgcms.talk.JrgTalkPresentationFile;
import guess.domain.source.image.UrlDates;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("JrgCmsDataLoader class tests")
class JrgCmsDataLoaderTest {
    @Test
    void getImageWidthParameterName() {
        assertEquals("width", new JrgCmsDataLoader().getImageWidthParameterName());
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("getSpeakerName method tests")
    class GetSpeakerNameTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, null, null, null),
                    arguments(
                            List.of(new LocaleItem(Language.ENGLISH.getCode(), "Last")),
                            List.of(new LocaleItem(Language.ENGLISH.getCode(), "First")),
                            Language.ENGLISH,
                            "First Last")
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void getSpeakerName(List<LocaleItem> lastName, List<LocaleItem> firstName, Language language, String expected) {
            assertEquals(expected, JrgCmsDataLoader.getSpeakerName(lastName, firstName, language));
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

            JrgLinks jrgLinks0 = new JrgLinks();
            jrgLinks0.setContent(CONTENT0);

            JrgLinks jrgLinks1 = new JrgLinks();
            jrgLinks1.setContent(CONTENT1);

            JrgTalkPresentationFile jrgTalkPresentationFile0 = new JrgTalkPresentationFile();
            jrgTalkPresentationFile0.setLinks(jrgLinks0);

            JrgTalkPresentationFile jrgTalkPresentationFile1 = new JrgTalkPresentationFile();
            jrgTalkPresentationFile1.setLinks(jrgLinks1);

            JrgTalkPresentation jrgTalkPresentation0 = new JrgTalkPresentation();
            jrgTalkPresentation0.setFiles(List.of(jrgTalkPresentationFile0));

            JrgTalkPresentation jrgTalkPresentation1 = new JrgTalkPresentation();
            jrgTalkPresentation1.setFiles(List.of(jrgTalkPresentationFile0, jrgTalkPresentationFile1));

            return Stream.of(
                    arguments(null, Collections.emptyList()),
                    arguments(jrgTalkPresentation0, List.of(CONTENT0)),
                    arguments(jrgTalkPresentation1, List.of(CONTENT0, CONTENT1))
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

            JrgLinks jrgLinks0 = new JrgLinks();
            jrgLinks0.setContent(CONTENT);

            JrgPhoto jrgPhoto0 = new JrgPhoto();
            jrgPhoto0.setLinks(jrgLinks0);
            jrgPhoto0.setCreated(DATE0);
            jrgPhoto0.setLastModified(DATE1);

            JrgPhoto jrgPhoto1 = new JrgPhoto();
            jrgPhoto1.setLinks(jrgLinks0);
            jrgPhoto1.setCreated(DATE0);
            jrgPhoto1.setLastModified(DATE2);

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
            jrgCmsSpeaker2.setPhoto(List.of(jrgPhoto0));

            JrgCmsSpeaker jrgCmsSpeaker3 = new JrgCmsSpeaker();
            jrgCmsSpeaker3.setLastName(Collections.emptyMap());
            jrgCmsSpeaker3.setFirstName(Collections.emptyMap());
            jrgCmsSpeaker3.setPhoto(List.of(jrgPhoto0, jrgPhoto1));

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
