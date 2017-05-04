package zyu.impl.cache;

import org.junit.Assert;
import org.junit.Test;
import zyu.candles.Trade;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miraculis on 04.05.2017.
 */
public class BufTest {
    @Test
    public void testAddition() {
        Buffer b = new Buffer(16);
        List<Trade> ts = Arrays.asList(
            new Trade("A", 1d, 1, 1),
            new Trade("A", 2d, 1, 3),
            new Trade("A", 3d, 1, 2)
        );
        ts.forEach(b::add);
        for (int i = 0; i < b.getLength(); i++)
            System.out.println(b.get(i));
        Assert.assertEquals(2, b.getLength());
    }

    @Test
    public void testOverflow() {
        Buffer b = new Buffer(2);
        b.add(new Trade("A", 1d, 1, 1));
        b.add(new Trade("A", 1d, 1, 2));
        b.add(new Trade("A", 1d, 1, 3));
        Assert.assertEquals(2, b.getLength());
        Assert.assertEquals(2, b.get(0).getTs());
        Assert.assertEquals(3, b.get(1).getTs());
    }

    @Test
    public void testExtraction() {
        Buffer b = new Buffer(4);
        b.add(new Trade("A", 1d, 1, 1));
        b.add(new Trade("A", 1d, 1, 3));
        b.add(new Trade("A", 1d, 1, 5));
        b.add(new Trade("A", 1d, 1, 7));
        Assert.assertEquals(1, b.getIndex(2));
    }

    @Test
    public void testExtractionWithOverflow() {
        Buffer b = new Buffer(4);
        b.add(new Trade("A", 1d, 1, 1));
        b.add(new Trade("A", 1d, 1, 3));
        b.add(new Trade("A", 1d, 1, 5));
        b.add(new Trade("A", 1d, 1, 7));
        b.add(new Trade("A", 1d, 1, 9));
        Assert.assertEquals(1, b.getIndex(4));
    }
}
