package guess.service;

import guess.dao.exception.QuestionSetNotExistsException;
import guess.domain.GuessMode;
import guess.domain.question.QuestionSet;
import guess.domain.source.Event;
import guess.domain.source.EventType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Question service.
 */
public interface QuestionService {
    List<QuestionSet> getQuestionSets();

    Long getDefaultQuestionSetId(LocalDate date);

    List<EventType> getEventTypes();

    List<Event> getEvents(List<Long> eventTypeIds);

    Event getDefaultEvent(LocalDateTime dateTime);

    List<Integer> getQuantities(List<Long> questionSetIds, GuessMode guessMode) throws QuestionSetNotExistsException;
}
