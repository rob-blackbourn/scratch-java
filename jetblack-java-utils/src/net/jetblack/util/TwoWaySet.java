package net.jetblack.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TwoWaySet<TFirst, TSecond> {

    private final Map<TFirst, Set<TSecond>> _firstToSeconds = new HashMap<TFirst, Set<TSecond>>();
    private final Map<TSecond, Set<TFirst>> _secondToFirsts = new HashMap<TSecond, Set<TFirst>>();

    public void addFirstAndSecond(TFirst first, TSecond second) {
        Set<TSecond> seconds = _firstToSeconds.get(first);
        if (seconds == null) {
            _firstToSeconds.put(first, seconds = new HashSet<TSecond>());
        }
        seconds.add(second);

        Set<TFirst> firsts = _secondToFirsts.get(second);
        if (firsts == null) {
            _secondToFirsts.put(second, firsts = new HashSet<TFirst>());
        }
        firsts.add(first);
    }

    public void addSecondAndFirst(TSecond second, TFirst first) {
        addFirstAndSecond(first, second);
    }

    public boolean containsFirstKey(TFirst first) {
        return _firstToSeconds.containsKey(first);
    }

    public boolean containsSecondKey(TSecond second) {
        return _secondToFirsts.containsKey(second);
    }

    public Set<TSecond> getSecondFromFirst(TFirst first) {
        return _firstToSeconds.get(first);
    }

    public Set<TFirst> getFirstFromSecond(TSecond second, Set<TFirst> firsts) {
        return _secondToFirsts.get(second);
    }

    public Set<TSecond> removeFirst(TFirst first) {
        Set<TSecond> seconds = _firstToSeconds.get(first);
        if (seconds == null) {
            return null;
        }

        Set<TSecond> secondsWithoutFirsts = new HashSet<TSecond>();

        for (TSecond second : seconds) {
            Set<TFirst> firsts = _secondToFirsts.get(second);
            firsts.remove(first);
            if (firsts.size() == 0) {
                _secondToFirsts.remove(second);
                secondsWithoutFirsts.add(second);
            }
        }

        _firstToSeconds.remove(first);

        return secondsWithoutFirsts;
    }

    public Set<TFirst> removeSecond(TSecond second)
    {
        Set<TFirst> firsts = _secondToFirsts.get(second);
        if (firsts == null) {
            return null;
        }

        Set<TFirst> firstsWithoutSeconds = new HashSet<TFirst>();

        for (TFirst first : firsts) {
            Set<TSecond> seconds = _firstToSeconds.get(first);
            seconds.remove(second);
            if (seconds.size() == 0) {
                _firstToSeconds.remove(first);
                firstsWithoutSeconds.add(first);
            }
        }

        _secondToFirsts.remove(second);

        return firstsWithoutSeconds;
    }

    public Set<TFirst> getFirsts() {
    	return _firstToSeconds.keySet();
    }

    public Set<TSecond> getSeconds() {
    	return _secondToFirsts.keySet();
    }
}
