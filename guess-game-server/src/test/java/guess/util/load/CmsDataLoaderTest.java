package guess.util.load;

import guess.domain.Language;
import guess.domain.source.LocaleItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("CmsDataLoaderTest class tests")
class CmsDataLoaderTest {
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("extractString method tests")
    class ExtractStringTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, false, null),
                    arguments(null, true, null),
                    arguments("", false, ""),
                    arguments("", true, ""),
                    arguments(" value0", false, "value0"),
                    arguments(" value0", true, "value0"),
                    arguments("value1 ", false, "value1"),
                    arguments("value1 ", true, "value1"),
                    arguments(" value2 ", false, "value2"),
                    arguments(" value2 ", true, "value2"),
                    arguments("value3  value4", false, "value3  value4"),
                    arguments("value3  value4", true, "value3 value4")
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractBoolean(String value, boolean removeDuplicateWhiteSpaces, String expected) {
            assertEquals(expected, CmsDataLoader.extractString(value, removeDuplicateWhiteSpaces));
        }
    }

    @Test
    void extractString() {
        final String SOURCE = "source";

        try (MockedStatic<CmsDataLoader> mockedStatic = Mockito.mockStatic(CmsDataLoader.class)) {
            mockedStatic.when(() -> CmsDataLoader.extractString(Mockito.anyString()))
                    .thenCallRealMethod();
            mockedStatic.when(() -> CmsDataLoader.extractString(Mockito.anyString(), Mockito.anyBoolean()))
                    .thenReturn("42");

            CmsDataLoader.extractString(SOURCE);

            mockedStatic.verify(() -> CmsDataLoader.extractString(SOURCE), VerificationModeFactory.times(1));
            mockedStatic.verify(() -> CmsDataLoader.extractString(SOURCE, false), VerificationModeFactory.times(1));
            mockedStatic.verifyNoMoreInteractions();
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("extractLocaleItems method tests")
    class ExtractLocaleItemsTest {
        private Stream<Arguments> data() {
            return Stream.of(
                    arguments(null, null, true, false, Collections.emptyList()),
                    arguments(null, "", true, false, Collections.emptyList()),
                    arguments("", null, true, false, Collections.emptyList()),
                    arguments("", "", true, false, Collections.emptyList()),
                    arguments("value0", null, true, false, List.of(
                            new LocaleItem(
                                    Language.ENGLISH.getCode(),
                                    "value0"))),
                    arguments("value0", "", true, false, List.of(
                            new LocaleItem(
                                    Language.ENGLISH.getCode(),
                                    "value0"))),
                    arguments("value0", "value0", true, false, List.of(
                            new LocaleItem(
                                    Language.ENGLISH.getCode(),
                                    "value0"))),
                    arguments("value0", "value1", true, false, List.of(
                            new LocaleItem(
                                    Language.ENGLISH.getCode(),
                                    "value0"),
                            new LocaleItem(
                                    Language.RUSSIAN.getCode(),
                                    "value1"))),
                    arguments(null, "value1", true, false, List.of(
                            new LocaleItem(
                                    Language.RUSSIAN.getCode(),
                                    "value1"))),
                    arguments("", "value1", true, false, List.of(
                            new LocaleItem(
                                    Language.RUSSIAN.getCode(),
                                    "value1"))),
                    arguments(null, null, false, false, Collections.emptyList()),
                    arguments(null, "", false, false, Collections.emptyList()),
                    arguments("", null, false, false, Collections.emptyList()),
                    arguments("", "", false, false, Collections.emptyList()),
                    arguments("value0", null, false, false, List.of(
                            new LocaleItem(
                                    Language.ENGLISH.getCode(),
                                    "value0"))),
                    arguments("value0", "", false, false, List.of(
                            new LocaleItem(
                                    Language.ENGLISH.getCode(),
                                    "value0"))),
                    arguments("value0", "value0", false, false, List.of(
                            new LocaleItem(
                                    Language.ENGLISH.getCode(),
                                    "value0"))),
                    arguments("value0", "value1", false, false, List.of(
                            new LocaleItem(
                                    Language.ENGLISH.getCode(),
                                    "value0"),
                            new LocaleItem(
                                    Language.RUSSIAN.getCode(),
                                    "value1"))),
                    arguments(null, "value1", false, false, List.of(
                            new LocaleItem(
                                    Language.RUSSIAN.getCode(),
                                    "value1"))),
                    arguments("", "value1", false, false, List.of(
                            new LocaleItem(
                                    Language.RUSSIAN.getCode(),
                                    "value1"))),
                    arguments("", "value1  value2", false, false, List.of(
                            new LocaleItem(
                                    Language.RUSSIAN.getCode(),
                                    "value1  value2"))),
                    arguments("", "value1  value2", false, true, List.of(
                            new LocaleItem(
                                    Language.RUSSIAN.getCode(),
                                    "value1 value2")))
            );
        }

        @ParameterizedTest
        @MethodSource("data")
        void extractLocaleItems(String enText, String ruText, boolean checkEnTextExistence, boolean removeDuplicateWhiteSpaces,
                                List<LocaleItem> expected) {
            assertEquals(expected, CmsDataLoader.extractLocaleItems(enText, ruText, checkEnTextExistence, removeDuplicateWhiteSpaces));

            if (!removeDuplicateWhiteSpaces) {
                assertEquals(expected, CmsDataLoader.extractLocaleItems(enText, ruText, checkEnTextExistence));
                assertEquals(expected, CmsDataLoader.extractLocaleItems(enText, ruText));
            }
        }
    }
}
