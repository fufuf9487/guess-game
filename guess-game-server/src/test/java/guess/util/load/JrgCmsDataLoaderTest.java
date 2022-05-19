package guess.util.load;

import guess.domain.source.cms.jrgcms.speaker.JrgContact;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
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
    @DisplayName("extractContactValue method tests")
    class ExtractContactValueTest {
        private Stream<Arguments> data() {
            final String TWITTER_CONTACT_TYPE = "twitter";
            final String GITHUB_CONTACT_TYPE = "github";
            final String HABR_CONTACT_TYPE = "habr";
            final String UNKNOWN_CONTACT_TYPE = "unknown";

            final String TWITTER_CONTACT_VALUE = "user";
            final String HABR_CONTACT_VALUE = "";

            final JrgContact jrgContact0 = new JrgContact();
            jrgContact0.setType(TWITTER_CONTACT_TYPE);
            jrgContact0.setValue(TWITTER_CONTACT_VALUE);

            final JrgContact jrgContact1 = new JrgContact();
            jrgContact1.setType(HABR_CONTACT_TYPE);
            jrgContact1.setValue(HABR_CONTACT_VALUE);

            UnaryOperator<String> extractionOperator = v -> v;
            
            Map<String, JrgContact> contactMap = new HashMap<>();
            contactMap.put(TWITTER_CONTACT_TYPE, jrgContact0);
            contactMap.put(GITHUB_CONTACT_TYPE, null);
            contactMap.put(HABR_CONTACT_TYPE, jrgContact1);

            return Stream.of(
                    arguments(contactMap, UNKNOWN_CONTACT_TYPE, null, null),
                    arguments(contactMap, GITHUB_CONTACT_TYPE, null, null),
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
