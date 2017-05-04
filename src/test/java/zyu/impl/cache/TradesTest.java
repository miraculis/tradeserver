package zyu.impl.cache;

import org.junit.Assert;
import org.junit.Test;
import zyu.candles.Candle;
import zyu.candles.Trade;

import java.util.List;

/**
 * Created by miraculis on 04.05.2017.
 */
public class TradesTest {
    @Test
    public void testDrain() {
        Trades t = new Trades(10);
        t.add(new Trade("A", 1d, 1, 1));
        t.add(new Trade("A", 2d, 1, 5));
        t.add(new Trade("A", 3d, 1, 7));
        t.add(new Trade("A", 4d, 1, 13));
        List<Candle> c = t.drainCandles(0);
        Assert.assertEquals(2, c.size());

        Assert.assertEquals(1d, c.get(0).getOpen(), 0.00001d);
        Assert.assertEquals(3d, c.get(0).getHigh(), 0.00001d);
        Assert.assertEquals(1d, c.get(0).getLow(), 0.00001d);
        Assert.assertEquals(3d, c.get(0).getClose(), 0.00001d);
        Assert.assertEquals(3, c.get(0).getVolume());
    }
}
