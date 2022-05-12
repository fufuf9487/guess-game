package guess.util.load;

import guess.domain.Conference;
import guess.domain.Language;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.LocaleItem;
import guess.domain.source.Talk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * CMS data loader.
 */
public abstract class CmsDataLoader {
    private static final Logger log = LoggerFactory.getLogger(ContentfulDataLoader.class);

    abstract Map<ContentfulDataLoader.ConferenceSpaceInfo, List<String>> getTags(String conferenceCodePrefix);

    abstract List<EventType> getEventTypes();

    abstract Event getEvent(Conference conference, LocalDate startDate);

    abstract List<Talk> getTalks(Conference conference, String conferenceCode, boolean ignoreDemoStage);

    /**
     * Extracts string, i.e. trims not null string.
     *
     * @param value                      source value
     * @param removeDuplicateWhiteSpaces {@code true} if need to remove duplicate white spaces, {@code false} otherwise
     * @return extracted string
     */
    static String extractString(String value, boolean removeDuplicateWhiteSpaces) {
        if (value != null) {
            String trimmedValue = value.trim();

            if (removeDuplicateWhiteSpaces) {
                return trimmedValue.replaceAll("\\s+", " ");
            } else {
                return trimmedValue;
            }
        } else {
            return null;
        }
    }

    /**
     * Extracts string, i.e. trims not null string.
     *
     * @param value source value
     * @return extracted string
     */
    static String extractString(String value) {
        return extractString(value, false);
    }

    /**
     * Extracts local items.
     *
     * @param enText                     English text
     * @param ruText                     Russian text
     * @param checkEnTextExistence       {@code true} if need to check English text existence, {@code false} otherwise
     * @param removeDuplicateWhiteSpaces {@code true} if need to remove duplicate white spaces, {@code false} otherwise
     * @return local items
     */
    static List<LocaleItem> extractLocaleItems(String enText, String ruText, boolean checkEnTextExistence, boolean removeDuplicateWhiteSpaces) {
        enText = extractString(enText, removeDuplicateWhiteSpaces);
        ruText = extractString(ruText, removeDuplicateWhiteSpaces);

        if (checkEnTextExistence &&
                ((enText == null) || enText.isEmpty()) &&
                ((ruText != null) && !ruText.isEmpty())) {
            log.warn("Invalid arguments: enText is empty, ruText is not empty ('{}')", ruText);
        }

        if (Objects.equals(enText, ruText)) {
            ruText = null;
        }

        List<LocaleItem> localeItems = new ArrayList<>();

        if ((enText != null) && !enText.isEmpty()) {
            localeItems.add(new LocaleItem(
                    Language.ENGLISH.getCode(),
                    enText));
        }

        if ((ruText != null) && !ruText.isEmpty()) {
            localeItems.add(new LocaleItem(
                    Language.RUSSIAN.getCode(),
                    ruText));
        }

        return localeItems;
    }

    /**
     * Extracts local items.
     *
     * @param enText               English text
     * @param ruText               Russian text
     * @param checkEnTextExistence {@code true} if need to check English text existence, {@code false} otherwise
     * @return local items
     */
    static List<LocaleItem> extractLocaleItems(String enText, String ruText, boolean checkEnTextExistence) {
        return extractLocaleItems(enText, ruText, checkEnTextExistence, false);
    }

    /**
     * Extracts local items.
     *
     * @param enText English text
     * @param ruText Russian text
     * @return local items
     */
    static List<LocaleItem> extractLocaleItems(String enText, String ruText) {
        return extractLocaleItems(enText, ruText, true);
    }
}
