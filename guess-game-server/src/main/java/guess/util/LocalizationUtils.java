package guess.util;

import guess.domain.Language;
import guess.domain.source.LocaleItem;

import java.util.List;
import java.util.Optional;

/**
 * Localization utility methods.
 */
public class LocalizationUtils {
    public static final String ENGLISH_LANGUAGE = "en";
    public static final String RUSSIAN_LANGUAGE = "ru";
    private static final LocaleItem DEFAULT_LOCALE_ITEM = new LocaleItem(ENGLISH_LANGUAGE, "");

    /**
     * Gets english name.
     *
     * @param localeItems locale items
     * @return english name
     */
    public static String getEnglishName(List<LocaleItem> localeItems) {
        return localeItems.stream()
                .filter(et -> et.getLanguage().equals(ENGLISH_LANGUAGE))
                .findFirst().orElse(DEFAULT_LOCALE_ITEM).getText();
    }

    /**
     * Gets name for language.
     *
     * @param localeItems     locale items
     * @param language        language
     * @param defaultLanguage default language
     * @return name
     */
    public static String getName(List<LocaleItem> localeItems, Language language, Language defaultLanguage) {
        Language finalLanguage = (language != null) ? language : defaultLanguage;

        Optional<LocaleItem> currentLanguageOptional = localeItems.stream()
                .filter(et -> et.getLanguage().equals(finalLanguage.getCode()))
                .findFirst();

        if (currentLanguageOptional.isPresent()) {
            return currentLanguageOptional.get().getText();
        } else {
            Optional<LocaleItem> defaultLanguageOptional = localeItems.stream()
                    .filter(et -> et.getLanguage().equals(defaultLanguage.getCode()))
                    .findFirst();

            if (defaultLanguageOptional.isPresent()) {
                return defaultLanguageOptional.get().getText();
            } else {
                return "";
            }
        }
    }

    /**
     * Gets name for language.
     *
     * @param localeItems locale items
     * @param language    language
     * @return name
     */
    public static String getName(List<LocaleItem> localeItems, Language language) {
        return getName(localeItems, language, Language.ENGLISH);
    }
}
