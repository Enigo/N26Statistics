package n26.statistics.service;

import n26.statistics.dto.StatisticsResponse;

public interface CalculationService {

    StatisticsResponse calculate(double amount, StatisticsResponse statisticsResponse);

    StatisticsResponse remove(double amount, StatisticsResponse statisticsResponse);
}
