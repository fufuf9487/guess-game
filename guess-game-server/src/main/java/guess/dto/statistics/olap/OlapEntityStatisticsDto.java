package guess.dto.statistics.olap;

import java.util.List;

/**
 * OLAP entity statistics DTO.
 */
public abstract class OlapEntityStatisticsDto<T, S extends OlapEntityMetricsDto> {
    private final List<T> dimensionValues;
    private final List<S> metricsList;
    private final OlapEntityMetricsDto totals;

    public OlapEntityStatisticsDto(List<T> dimensionValues, List<S> metricsList, OlapEntityMetricsDto totals) {
        this.dimensionValues = dimensionValues;
        this.metricsList = metricsList;
        this.totals = totals;
    }

    public List<T> getDimensionValues() {
        return dimensionValues;
    }

    public List<S> getMetricsList() {
        return metricsList;
    }

    public OlapEntityMetricsDto getTotals() {
        return totals;
    }
}
