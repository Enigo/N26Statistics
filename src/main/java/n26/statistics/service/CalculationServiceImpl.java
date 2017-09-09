package n26.statistics.service;

import n26.statistics.dto.StatisticsResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.TreeSet;

@Service
public class CalculationServiceImpl implements CalculationService {

    @Override
    public StatisticsResponse calculate(double inputAmount, StatisticsResponse statisticsResponse) {
        final StatisticsResponse.StatisticsResponseBuilder builder = StatisticsResponse.builder();
        final long count = statisticsResponse.getCount() + 1;
        final double amount = BigDecimal.valueOf(inputAmount).setScale(2, RoundingMode.HALF_UP).doubleValue();
        final double sum = BigDecimal.valueOf(statisticsResponse.getSum())
                .add(BigDecimal.valueOf(amount)).doubleValue();
        builder.sum(sum)
                .count(count)
                .avg(BigDecimal.valueOf(sum).divide(
                        BigDecimal.valueOf(count), RoundingMode.HALF_UP).doubleValue());

        final TreeSet<Double> maxHistory = statisticsResponse.getMaxHistory();
        if (maxHistory.isEmpty()) {
            builder.max(amount);
        } else {
            final double last = maxHistory.last();
            builder.max(amount > last ? amount : last);
        }
        maxHistory.add(amount);
        builder.maxHistory(maxHistory);

        final TreeSet<Double> minHistory = statisticsResponse.getMinHistory();
        if (minHistory.isEmpty()) {
            builder.min(amount);
        } else {
            final double last = minHistory.last();
            builder.min(last > amount ? amount : last);
        }
        minHistory.add(amount);
        builder.minHistory(minHistory);

        return builder.build();
    }

    @Override
    public StatisticsResponse remove(double inputAmount, StatisticsResponse statisticsResponse) {
        final StatisticsResponse.StatisticsResponseBuilder builder = StatisticsResponse.builder();
        final long count = statisticsResponse.getCount() - 1;
        final BigDecimal amount = BigDecimal.valueOf(inputAmount).setScale(2, RoundingMode.HALF_UP);
        final double sum = BigDecimal.valueOf(statisticsResponse.getSum())
                .subtract(amount).doubleValue();
        builder.sum(sum)
                .count(count)
                .avg(count == 0 ? 0 : BigDecimal.valueOf(sum).divide(
                        BigDecimal.valueOf(count), RoundingMode.HALF_UP).doubleValue());
        
        final TreeSet<Double> maxHistory = statisticsResponse.getMaxHistory();
        if (maxHistory.isEmpty()) {
            builder.max(0);
        } else {
            final BigDecimal last = BigDecimal.valueOf(maxHistory.last()).setScale(2, RoundingMode.HALF_UP);
            if (amount.equals(last)) {
                maxHistory.pollLast();
                builder.max(maxHistory.isEmpty() ? 0 : maxHistory.last());
            } else {
                builder.max(last.doubleValue());
            }
        }
        maxHistory.remove(amount.doubleValue());
        builder.maxHistory(maxHistory);

        final TreeSet<Double> minHistory = statisticsResponse.getMinHistory();
        if (minHistory.isEmpty()) {
            builder.min(0);
        } else {
            final BigDecimal last = BigDecimal.valueOf(minHistory.last()).setScale(2, RoundingMode.HALF_UP);
            if (amount.equals(last)) {
                minHistory.pollLast();
                builder.min(minHistory.isEmpty() ? 0 : minHistory.last());
            } else {
                builder.min(last.doubleValue());
            }
        }
        minHistory.remove(amount.doubleValue());
        builder.minHistory(minHistory);

        return builder.build();
    }

}
