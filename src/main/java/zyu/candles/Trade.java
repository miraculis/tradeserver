package zyu.candles;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by miraculis on 27.07.2016.
 */
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Trade {
    private final String ticker;
    private final double price;
    private final int volume;
    private final long ts;
}
