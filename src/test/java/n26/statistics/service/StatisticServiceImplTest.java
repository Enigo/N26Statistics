package n26.statistics.service;

import n26.statistics.dto.StatisticsResponse;
import n26.statistics.dto.TransactionRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class StatisticServiceImplTest {

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    private StatisticService statisticService;

    @Test
    public void testGetStatistics() {
        assertNotNull(statisticService.getStatistics());
    }

    @Test
    public void testSaveTransactionSuccessful() {
        final TransactionRequest request = new TransactionRequest(100, System.currentTimeMillis());
        assertTrue(statisticService.saveTransaction(request));
    }

    @Test
    public void testSaveTransactionUnsuccessful() {
        final TransactionRequest request = new TransactionRequest(100, System.currentTimeMillis() - 61 * 1000);
        assertFalse(statisticService.saveTransaction(request));
    }

    @Test
    public void testSaveTransactionAlreadyExists() {
        final long currentTime = System.currentTimeMillis();
        final TransactionRequest request = new TransactionRequest(100, currentTime);
        assertTrue(statisticService.saveTransaction(request));
        assertFalse(statisticService.saveTransaction(request));
    }

    @Test
    public void testRecalculateStatistics() throws InterruptedException {
        final TransactionRequest request = new TransactionRequest(100, System.currentTimeMillis() - 59 * 1000);
        assertTrue(statisticService.saveTransaction(request));
        assertNotNull(statisticService.getStatistics());
        Thread.sleep(1100);
        statisticService.recalculateStatistics();
        // since mocked calculationService returns null as of result of remove
        // invocation it's valid result here
        assertNull(statisticService.getStatistics());
    }

    @TestConfiguration
    static class ContextConfiguration {

        @Bean
        public StatisticService statisticService() {
            return new StatisticServiceImpl(calculationService());
        }

        @Bean
        public CalculationService calculationService() {
            final CalculationService mock = mock(CalculationService.class);
            when(mock.calculate(anyDouble(), any())).thenReturn(new StatisticsResponse());
            return mock;
        }
    }

}
