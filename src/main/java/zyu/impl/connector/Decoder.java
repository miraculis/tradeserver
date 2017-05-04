package zyu.impl.connector;

import zyu.candles.Trade;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by miraculis on 27.07.2016.
 */
public class Decoder {
    public Trade decode(DataInputStream input) throws IOException {
        short packSize = input.readShort();
        long ts = input.readLong();
        short tickerLen = input.readShort();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tickerLen; i++) {
            sb.append((char) input.readByte());
        }
        String ticker = sb.toString();
        double price = input.readDouble();
        int volume = input.readInt();

        return new Trade(ticker, price, volume, ts);
    }
}
