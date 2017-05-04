package zyu.impl.connector;

import zyu.candles.Trade;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.function.ToIntFunction;

/**
 * Created by miraculis on 27.07.2016.
 */
public class Connector implements Runnable {
    private final String host;
    private final int port;
    private final Decoder decoder = new Decoder();
    private final ToIntFunction<Trade> aggregator;

    public Connector(String host, int port, ToIntFunction<Trade> aggregator) {
        this.host = host;
        this.port = port;
        this.aggregator = aggregator;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Socket socket = new Socket(host, port);
                System.out.println("Connector connected to " + host + ":" + port);

                DataInputStream input = new DataInputStream(socket.getInputStream());
                Trade trade = null;
                while (true) {
                    trade = decoder.decode(input);
                    aggregator.applyAsInt(trade);
                }
            } catch (IOException e) {
                try {
                    Thread.sleep(10000l);
                    System.out.println("IO exception, reconnecting....");
                } catch (InterruptedException e1) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
