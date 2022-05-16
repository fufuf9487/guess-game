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
    private static final Logger log = LoggerFactory.getLogger(CmsDataLoader.class);

    /**
     * Gets tags by conference code prefix.
     *
     * @param conferenceCodePrefix conference code prefix
     * @return tags
     */
    abstract Map<ContentfulDataLoader.ConferenceSpaceInfo, List<String>> getTags(String conferenceCodePrefix);

    /**
     * Gets event types.
     *
     * @return event types
     */
    abstract List<EventType> getEventTypes();

    /**
     * Gets event.
     *
     * @param conference conference
     * @param startDate  start date
     * @return event
     */
    abstract Event getEvent(Conference conference, LocalDate startDate);

    /**
     * Gets talks
     *
     * @param conference      conference
     * @param conferenceCode  conference code
     * @param ignoreDemoStage ignore demo stage talks
     * @return talks
     */
    abstract List<Talk> getTalks(Conference conference, String conferenceCode, boolean ignoreDemoStage);

    /**
     * Gets name of image width parameter.
     * 
     * @return name of image width parameter
     */
    abstract String getImageWidthParameterName();

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

    /**
     * Gets fixed name.
     *
     * @param name name
     * @return fixed name
     */
    static String getSpeakerFixedName(String name) {
        Map<String, String> fixedLastNames = Map.of("Аксенов", "Аксёнов", "Богачев", "Богачёв", "Горбачев", "Горбачёв",
                "Королев", "Королёв", "Плетнев", "Плетнёв", "Пономарев", "Пономарёв",
                "Толкачев", "Толкачёв", "Усачев", "Усачёв", "Федоров", "Фёдоров", "Шипилев", "Шипилёв");
        Map<String, String> fixedFirstNames = Map.of("Алена", "Алёна", "Артем", "Артём",
                "Петр", "Пётр", "Семен", "Семён", "Федор", "Фёдор");

        if ((name == null) || name.isEmpty()) {
            return name;
        }

        // Change last names
        for (var fixedLastName : fixedLastNames.entrySet()) {
            String nameWithFixedLastName = name.replaceAll(String.format("\\b%s$", fixedLastName.getKey()), fixedLastName.getValue());

            if (!name.equals(nameWithFixedLastName)) {
                log.warn("Speaker last name is changed; original: {}, changed: {}", name, nameWithFixedLastName);
                name = nameWithFixedLastName;

                break;
            }
        }

        // Change first names
        for (var fixedFirstName : fixedFirstNames.entrySet()) {
            String nameWithFixedFirstName = name.replaceAll(String.format("^%s\\b", fixedFirstName.getKey()), fixedFirstName.getValue());

            if (!name.equals(nameWithFixedFirstName)) {
                log.warn("Speaker first name is changed; original: {}, changed: {}", name, nameWithFixedFirstName);
                name = nameWithFixedFirstName;

                break;
            }
        }

        return name;
    }
}
