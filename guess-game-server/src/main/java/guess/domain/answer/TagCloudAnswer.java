package guess.domain.answer;

import com.kennycason.kumo.WordFrequency;
import guess.domain.Language;
import guess.domain.QuestionAnswer;
import guess.domain.source.Speaker;
import guess.util.tagcloud.TagCloudUtils;

import java.util.List;
import java.util.Map;

public class TagCloudAnswer extends QuestionAnswer<Speaker> implements Answer {
    private final Map<Language, List<WordFrequency>> languageWordFrequenciesMap;
    private Map<Language, byte[]> languageImageMap;

    public TagCloudAnswer(Speaker speaker, Map<Language, List<WordFrequency>> languageWordFrequenciesMap) {
        super(speaker);

        this.languageWordFrequenciesMap = languageWordFrequenciesMap;
    }

    public Map<Language, byte[]> getLanguageImageMap() {
        if (languageImageMap == null) {
            languageImageMap = TagCloudUtils.createLanguageImageMap(languageWordFrequenciesMap);
        }

        return languageImageMap;
    }

    public Speaker getSpeaker() {
        return getEntity();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
