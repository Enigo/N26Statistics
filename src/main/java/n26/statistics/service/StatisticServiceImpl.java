package n26.statistics.service;

import n26.statistics.dto.StatisticsResponse;
import n26.statistics.dto.TransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StatisticServiceImpl implements StatisticService {

    private static final ZoneId TIME_ZONE = ZoneId.of("UTC");
    private static final int SECONDS_TO_KEEP = 60;

    private final Map<LocalTime, TransactionRequest> transactions = new ConcurrentHashMap<>();
    private final CalculationService calculationService;
    
    private volatile StatisticsResponse response = new StatisticsResponse();

    @Autowired
    public StatisticServiceImpl(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    @Override
    public StatisticsResponse getStatistics() {
        return response;
    }

    @Override
    public boolean saveTransaction(TransactionRequest request) {
        final LocalTime time = Instant.ofEpochMilli(request.getTimestamp()).atZone(TIME_ZONE).toLocalTime();
        final boolean result = time.isAfter(LocalTime.now(TIME_ZONE).minusSeconds(SECONDS_TO_KEEP));

        if (result) {
            final double amount = request.getAmount();
            synchronized (this) {
                if (transactions.containsKey(time)) {
                    System.err.println("Transaction for this time " + time + " already exists!");
                    return false;
                }
                response = calculationService.calculate(amount, response);
                transactions.put(time, request);
            }
        }

        return result;
    }

    @Scheduled(fixedRate = 1000)
    @Override
    public void recalculateStatistics() {
        transactions.entrySet().stream()
                .filter(entry -> !entry.getKey().isAfter(LocalTime.now(TIME_ZONE).minusSeconds(SECONDS_TO_KEEP)))
                .forEach(entry -> {
                    synchronized (this) {
                        response = calculationService.remove(entry.getValue().getAmount(), response);
                        transactions.remove(entry.getKey());
                    }
                });
    }

}
