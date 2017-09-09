package n26.statistics.service;

import n26.statistics.dto.StatisticsResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class CalculationServiceImplTest {

    private static final int AMOUNT = 100;
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    private CalculationService calculationService;

    @Test
    public void testCalculateForTheFirst() {
        final StatisticsResponse result = calculationService.calculate(AMOUNT, new StatisticsResponse());
        final long expectedCount = 1;
        final double expectedSum = AMOUNT;
        assertEquals(expectedCount, result.getCount());
        assertEquals(AMOUNT, result.getMin(), 0);
        assertEquals(AMOUNT, result.getMax(), 0);
        assertEquals(expectedSum / expectedCount, result.getAvg(), 0);
        assertEquals(expectedSum, result.getSum(), 0);
    }

    @Test
    public void testCalculateWithPrevious() {
        final StatisticsResponse response =
                StatisticsResponse.builder()
                        .min(10)
                        .max(180)
                        .count(2)
                        .sum(200)
                        .avg(10)
                        .maxHistory(new TreeSet<>(Collections.singleton(180d)))
                        .minHistory(new TreeSet<>(Collections.singleton(10d)))
                        .build();
        final StatisticsResponse result = calculationService.calculate(AMOUNT, response);
        final long expectedCount = response.getCount() + 1;
        final double expectedSum = response.getSum() + AMOUNT;
        assertEquals(expectedCount, result.getCount());
        assertEquals(response.getMin(), result.getMin(), 0);
        assertEquals(response.getMax(), result.getMax(), 0);
        assertEquals(expectedSum / expectedCount, result.getAvg(), 0);
        assertEquals(expectedSum, result.getSum(), 0);
    }

    @Test
    public void testCalculateNewMaxAndMin() {
        final StatisticsResponse response =
                StatisticsResponse.builder()
                        .min(150)
                        .max(50)
                        .count(2)
                        .sum(200)
                        .avg(10)
                        .maxHistory(new TreeSet<>(Collections.singleton(50d)))
                        .minHistory(new TreeSet<>(Collections.singleton(150d)))
                        .build();
        final StatisticsResponse result = calculationService.calculate(AMOUNT, response);
        assertEquals(AMOUNT, result.getMin(), 0);
        assertEquals(AMOUNT, result.getMax(), 0);
    }

    @Test
    public void testRemove() {
        final StatisticsResponse response =
                StatisticsResponse.builder()
                        .min(10)
                        .max(180)
                        .count(2)
                        .sum(200)
                        .avg(10)
                        .maxHistory(new TreeSet<>(Collections.singleton(180d)))
                        .minHistory(new TreeSet<>(Collections.singleton(10d)))
                        .build();
        final StatisticsResponse result = calculationService.remove(AMOUNT, response);
        final long expectedCount = response.getCount() - 1;
        final double expectedSum = response.getSum() - AMOUNT;
        assertEquals(expectedCount, result.getCount());
        assertEquals(response.getMin(), result.getMin(), 0);
        assertEquals(response.getMax(), result.getMax(), 0);
        assertEquals(expectedSum / expectedCount, result.getAvg(), 0);
        assertEquals(expectedSum, result.getSum(), 0);
    }

    @Test
    public void testRemoveNewMaxAndMin() {
        final StatisticsResponse response =
                StatisticsResponse.builder()
                        .min(100)
                        .max(100)
                        .count(2)
                        .sum(200)
                        .avg(10)
                        .maxHistory(new TreeSet<>(Arrays.asList(50d, 100d)))
                        .minHistory(new TreeSet<>(Arrays.asList(50d, 100d)))
                        .build();
        final StatisticsResponse result = calculationService.remove(AMOUNT, response);
        assertEquals(50, result.getMin(), 0);
        assertEquals(50, result.getMax(), 0);
    }

    @Test
    public void testRemoveNewMaxAndMinLast() {
        final StatisticsResponse response =
                StatisticsResponse.builder()
                        .min(100)
                        .max(100)
                        .count(2)
                        .sum(200)
                        .avg(10)
                        .maxHistory(new TreeSet<>(Collections.singleton(100d)))
                        .minHistory(new TreeSet<>(Collections.singleton(100d)))
                        .build();
        final StatisticsResponse result = calculationService.remove(AMOUNT, response);
        assertEquals(0, result.getMin(), 0);
        assertEquals(0, result.getMax(), 0);
    }

    @Test
    public void testRemoveEmptyHistory() {
        final StatisticsResponse response =
                StatisticsResponse.builder()
                        .min(100)
                        .max(100)
                        .count(1)
                        .sum(200)
                        .avg(10)
                        .maxHistory(new TreeSet<>())
                        .minHistory(new TreeSet<>())
                        .build();
        final StatisticsResponse result = calculationService.remove(AMOUNT, response);
        assertEquals(0, result.getMin(), 0);
        assertEquals(0, result.getMax(), 0);
    }

    @TestConfiguration
    static class TestContextConfiguration {

        @Bean
        public CalculationService calculationService() {
            return new CalculationServiceImpl();
        }
    }

}
