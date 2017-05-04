package zyu;

import zyu.impl.cache.Trades;
import zyu.impl.connector.Connector;
import zyu.impl.server.NettyCandlesServer;

/**
 * Created by miraculis on 27.07.2016.
 */
public class Launcher {
    public static void main(String[] a) throws Exception {
        String providerHost = a[0];
        int providerPort = Integer.parseInt(a[1]);
        int serverPort = Integer.parseInt(a[2]);

        Trades cache = new Trades(60 * 1000);
        cache.start();

        Connector connector = new Connector(providerHost, providerPort, cache::add);
        Thread connectorThread = new Thread(connector);
        connectorThread.setDaemon(true);
        connectorThread.start();

        NettyCandlesServer server = new NettyCandlesServer(serverPort, cache::addCandlesListener);
        Thread serverThread = new Thread(server::run);
        serverThread.setDaemon(true);
        serverThread.start();
    }
}
