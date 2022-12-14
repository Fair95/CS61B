package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> com;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        com = c;
    }

    public T max() {
        if (isEmpty()) return null;
        T maxElem = get(0);
        for (T cur : this) {
            if (com.compare(cur, maxElem) > 0) maxElem = cur;
        }
        return maxElem;
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) return null;
        T maxElem = get(0);
        for (T cur : this) {
            if (c.compare(cur, maxElem) > 0) maxElem = cur;
        }
        return maxElem;
    }

}
