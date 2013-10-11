package se.inera.statistics.service.report.repository;

import java.util.HashMap;

public final class DefaultHashMap<K,V> extends HashMap<K,V> {

    private V defaultValue;

    public DefaultHashMap(V defaultValue) {
      this.defaultValue = defaultValue;
    }

    @Override
    public V get(Object k) {
      V v = super.get(k);
      return ((v == null) && !this.containsKey(k)) ? this.defaultValue : v;
    }
}
