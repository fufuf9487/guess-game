package guess.dao;

import guess.dao.exception.QuestionSetNotExistsException;
import guess.dao.exception.SpeakerDuplicatedException;
import guess.domain.GuessType;
import guess.domain.question.Question;
import guess.domain.question.QuestionSet;
import guess.domain.question.SpeakerQuestion;
import guess.domain.question.TalkQuestion;
import guess.domain.source.Event;
import guess.util.QuestionUtils;
import guess.util.YamlUtils;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Question DAO implementation.
 */
@Repository
public class QuestionDaoImpl implements QuestionDao {
    private final List<QuestionSet> questionSets;
    private final List<Event> events;

    public QuestionDaoImpl() throws IOException, SpeakerDuplicatedException {
        this.questionSets = YamlUtils.readQuestionSets();
        this.events = YamlUtils.readEvents().stream()
                .filter(e -> (e.getStartDate() != null) && (e.getEndDate() != null) && !e.getStartDate().isAfter(e.getEndDate()))
                .sorted(Comparator.comparing(Event::getStartDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionSet> getQuestionSets() {
        return questionSets;
    }

    @Override
    public Long getDefaultQuestionSetId(LocalDate date) {
        return events.stream()
                .filter(e -> !date.isAfter(e.getEndDate()))
                .findFirst()
                .map(e -> e.getEventType().getId())
                .orElse(0L);
    }

    @Override
    public QuestionSet getQuestionSetById(long id) throws QuestionSetNotExistsException {
        Optional<QuestionSet> optional = questionSets.stream()
                .filter(q -> q.getId() == id)
                .findFirst();

        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new QuestionSetNotExistsException();
        }
    }

    @Override
    public List<Question> getQuestionByIds(List<Long> questionSetIds, GuessType guessType) throws QuestionSetNotExistsException {
        List<Question> questions;

        if (GuessType.GUESS_NAME_TYPE.equals(guessType) || GuessType.GUESS_PICTURE_TYPE.equals(guessType)) {
            // Guess name by picture or picture by name
            List<SpeakerQuestion> speakerQuestions = new ArrayList<>();

            for (Long questionSetId : questionSetIds) {
                speakerQuestions.addAll(getQuestionSetById(questionSetId).getSpeakerQuestions());
            }

            questions = new ArrayList<>(QuestionUtils.removeDuplicatesById(speakerQuestions));
        } else if (GuessType.GUESS_TALK_TYPE.equals(guessType) || GuessType.GUESS_SPEAKER_TYPE.equals(guessType)) {
            // Guess talk by speaker or speaker by talk
            List<TalkQuestion> talkQuestions = new ArrayList<>();

            for (Long questionSetId : questionSetIds) {
                talkQuestions.addAll(getQuestionSetById(questionSetId).getTalkQuestions());
            }

            questions = new ArrayList<>(QuestionUtils.removeDuplicatesById(talkQuestions));
        } else {
            throw new IllegalArgumentException(String.format("Unknown guess type: %s", guessType));
        }

        return questions;
    }
}
