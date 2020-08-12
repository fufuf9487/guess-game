package guess.domain.answer;

import guess.domain.source.Speaker;

/**
 * Answer about speaker.
 */
public class SpeakerAnswer extends Answer<Speaker> {
    public SpeakerAnswer(Speaker speaker) {
        super(speaker);
    }

    public Speaker getSpeaker() {
        return getEntity();
    }
}
