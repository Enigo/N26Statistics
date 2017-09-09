package n26.statistics.rest;

import n26.statistics.dto.StatisticsResponse;
import n26.statistics.dto.TransactionRequest;
import n26.statistics.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticController {

    private final StatisticService statisticService;

    @Autowired
    StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @GetMapping(path = "/statistics")
    public StatisticsResponse getStatistics() {
        return statisticService.getStatistics();
    }

    @PostMapping(path = "/transactions")
    public ResponseEntity saveTransaction(@RequestBody TransactionRequest request) {
        final boolean result = statisticService.saveTransaction(request);

        return ResponseEntity.status(result ? HttpStatus.CREATED : HttpStatus.NO_CONTENT).build();
    }

}
