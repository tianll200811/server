package tcp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

//key--value һһӳ��
public class HashMapEx<K, V> extends HashMap<K, V> {
	public Set<V> valueSet() {
		Set<V> result = new HashSet<V>();
		for (K key : keySet()) {
			result.add(get(key));
		}
		return result;
	}
	
	public V put(K key, V value) {
		for (V val : valueSet()) {
			if (val.equals(value)
				&& val.hashCode() == value.hashCode()) {
				throw new RuntimeException("HashMapEx ����������ظ�value");
			}
		}
		return super.put(key, value);
	}
	
	public K getKeyByValue(V val) {
		for(K key : keySet()) {
			if (get(key).equals(val)
					&&(get(key) == val))
				return key;
		}
		return null;
	}
}
