package n26.statistics.service;

import n26.statistics.dto.StatisticsResponse;
import n26.statistics.dto.TransactionRequest;

public interface StatisticService {

    StatisticsResponse getStatistics();

    boolean saveTransaction(TransactionRequest request);

    void recalculateStatistics();

}
