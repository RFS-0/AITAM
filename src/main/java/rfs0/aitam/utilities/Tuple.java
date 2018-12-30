package rfs0.aitam.utilities;

public class Tuple<K, V> {
	
	private final K m_key;
	private final V m_value;
	
	public Tuple(K key, V value) {
		m_key = key;
		m_value = value;
	}

	public K getKey() {
		return m_key;
	}

	public V getValue() {
		return m_value;
	}
}
