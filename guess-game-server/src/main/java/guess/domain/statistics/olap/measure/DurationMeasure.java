package guess.domain.statistics.olap.measure;

import guess.domain.source.EventPart;

import java.util.Set;

/**
 * Duration measure.
 */
public class DurationMeasure extends Measure<EventPart> {
    public DurationMeasure(Set<Object> entities) {
        super(EventPart.class, entities);
    }

    @Override
    public long calculateValue() {
        return entities.stream()
                .mapToLong(EventPart::getDuration)
                .sum();
    }
}
