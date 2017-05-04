package zyu.impl.cache;

import zyu.candles.*;
import zyu.candles.Trade;

import java.util.*;
import java.util.function.ToIntFunction;

/**
 * Created by miraculis on 27.07.2016.
 */
public class Trades {
    private final long candlePeriod;
    private long lastDistributionTime = 0;

    private final Map<String, Buffer> trades = new HashMap<>();
    private final Collection<ToIntFunction<Collection<Candle>>> listeners = new ArrayList<>();

    private final Timer timer = new Timer();

    public Trades(long candlePeriod) {
        this.candlePeriod = candlePeriod;
    }

    public int addCandlesListener(ToIntFunction<Collection<Candle>> listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
        Collection<Candle> candles = drainCandles(lastDistributionTime);
        listener.applyAsInt(candles);
        return 0;
    }

    public void start() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                distribute();
            }
        }, candlePeriod, candlePeriod);
    }

    public int add(Trade trade) {
        synchronized (trades) {
            Buffer buf = trades.get(trade.getTicker());
            if (buf == null) {
                trades.put(trade.getTicker(), buf = new Buffer(1024));
            }
            buf.add(trade);
            if (lastDistributionTime == 0)
                lastDistributionTime = trade.getTs();
        }
        return 0;
    }

    List<Candle> drainCandles(long time) {

        List<Candle> result = new ArrayList<>();

        synchronized (trades) {
            trades.values().forEach((x) -> {
                int startIndex = x.getIndex(time);
                double high = Double.MIN_VALUE, low = Double.MAX_VALUE;
                int volume = 0;
                long candleTime = time;
                Trade first = x.get(startIndex);
                Trade last = first;

                for (int i = startIndex; i < x.getLength(); i++) {
                    Trade t = x.get(i);
                    if (t.getTs() >= candleTime + candlePeriod) {
                        result.add(new Candle(t.getTicker(), candlePeriod, first.getPrice(),
                                last.getPrice(), high, low, candleTime, volume));
                        volume = 0;
                        first = t;
                        last = first;
                        high = Double.MIN_VALUE;
                        low = Double.MAX_VALUE;
                        candleTime = candleTime + candlePeriod;
                    }
                    volume += t.getVolume();
                    last = t;
                    if (t.getPrice() > high)
                        high = t.getPrice();
                    if (t.getPrice() < low)
                        low = t.getPrice();
                }
                result.add(new Candle(last.getTicker(), candlePeriod, first.getPrice(),
                        last.getPrice(), high, low, candleTime, volume));

            });
        }
        return result;
    }

    private void distribute() {
        Collection<Candle> unsent = drainCandles(lastDistributionTime);

        Collection<ToIntFunction<Collection<Candle>>> listenersCopy = null;
        synchronized (listeners) {
            listenersCopy = new ArrayList<>(listeners);
        }
        listenersCopy.forEach((c) -> c.applyAsInt(unsent));
        lastDistributionTime = System.currentTimeMillis();
    }
}
