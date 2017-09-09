package n26.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

    @Getter
    private double amount;
    @Getter
    private long timestamp;
}
