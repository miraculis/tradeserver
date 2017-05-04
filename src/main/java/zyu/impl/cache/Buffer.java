package zyu.impl.cache;

import zyu.candles.Trade;

/**
 * Created by miraculis on 04.05.2017.
 */
class Buffer {
    private final Trade[] data;
    private int tail = -1;
    private int len = 0;

    public Buffer(int c) {
        if (c % 2 == 1)
            c++;
        data = new Trade[c];
    }

    void add(Trade t) {
        Trade last = len > 0 ? data[tail] : null;
        if (last != null && (last.getTs() > t.getTs() || !last.getTicker().equals(t.getTicker())))
            return;
        tail++;
        if(tail > data.length - 1) {
            tail = 0;
        }
        data[tail] = t;
        len++;
        if (len > data.length) {
            len = data.length;
        }
    }

    Trade get(int index) {
        if (len == 0)
            return null;
        int i = tail - len + 1 + index;
        if (i < 0)
            i += data.length;
        return data[i];
    }

    int getIndex(long time) {
        int lo = 0, hi = len - 1;

        while (hi > lo) {
            int mid = lo + (hi - lo) / 2;
            Trade t = get(mid);
            if (t == null) {
                hi = mid;
                continue;
            }
            if (t.getTs() < time) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return lo;
    }

    int getLength() {
        return len;
    }
}
