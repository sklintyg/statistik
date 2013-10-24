package se.inera.statistics.service.report.repository;

import java.util.TreeMap;

public final class DefaultTreeMap<K, V> extends TreeMap<K, V> {

    private V defaultValue;

    public DefaultTreeMap(V defaultValue) {
      this.defaultValue = defaultValue;
    }

    @Override
    public V get(Object k) {
      V v = super.get(k);
      return ((v == null) && !this.containsKey(k)) ? this.defaultValue : v;
    }
}
