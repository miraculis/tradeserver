package zyu.candles;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created by miraculis on 27.07.2016.
 */
@Getter
@Setter
@ToString
public class Candle {
    private final String symbol;
    private final double open;
    private final double close;
    private final double high;
    private final double low;
    private final LocalDateTime ts;
    private final long period;
    private final int volume;

    public Candle(String symbol, long period, double open, double close, double high, double low, long ts, int volume) {
        this.symbol = symbol;
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.ts = LocalDateTime.ofInstant(Instant.ofEpochMilli(ts), ZoneId.systemDefault());
        this.volume = volume;
        this.period = period;
    }
}
