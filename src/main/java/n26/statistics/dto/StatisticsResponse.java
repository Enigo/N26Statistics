package n26.statistics.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Comparator;
import java.util.TreeSet;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"maxHistory", "minHistory"})
public class StatisticsResponse {

    @Getter
    private double sum;
    @Getter
    private double avg;
    @Getter
    private double max;
    @Getter
    private double min;
    @Getter
    private long count;

    @JsonIgnore
    @Getter
    private TreeSet<Double> maxHistory = new TreeSet<>();
    @JsonIgnore
    @Getter
    private TreeSet<Double> minHistory = new TreeSet<>(Comparator.reverseOrder());

}
