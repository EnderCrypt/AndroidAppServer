package com.endercrypt.com.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class CountingHashMap<T> implements Serializable, Iterable<Map.Entry<T, Integer>>
{
	private static final long serialVersionUID = 3087900552101191596L;
	/**
	 * 
	 */

	private Map<T, Integer> map = new HashMap<>();
	private Integer defaultValue;

	public CountingHashMap()
	{
		this(0);
	}

	public CountingHashMap(Integer defaultValue)
	{
		this.defaultValue = defaultValue;
	}

	public Integer set(T key, Integer value)
	{
		return map.put(key, value);
	}

	public Integer get(T key)
	{
		return map.getOrDefault(key, defaultValue);
	}

	public Integer inc(T key)
	{
		return inc(key, 1);
	}

	public Integer inc(T key, Integer by)
	{
		int value = get(key);
		return set(key, value + by);
	}

	public Integer dec(T key)
	{
		return dec(key, 1);
	}

	public Integer dec(T key, Integer by)
	{
		int value = get(key);
		return set(key, value - by);
	}

	public boolean contains(T key)
	{
		return map.containsKey(key);
	}

	public int size()
	{
		return map.size();
	}

	public void clear()
	{
		map.clear();
	}

	public Integer remove(T key)
	{
		return map.remove(key);
	}

	public T lowest()
	{
		T currentObject = null;
		int currentValue = Integer.MAX_VALUE;

		for (Entry<T, Integer> entry : map.entrySet())
		{
			int value = entry.getValue();
			T object = entry.getKey();
			if (value < currentValue)
			{
				currentValue = value;
				currentObject = object;
			}
		}

		return currentObject;
	}

	public T highest()
	{
		T currentObject = null;
		int currentValue = Integer.MIN_VALUE;

		for (Entry<T, Integer> entry : this)
		{
			int value = entry.getValue();
			T object = entry.getKey();
			if (value > currentValue)
			{
				currentValue = value;
				currentObject = object;
			}
		}

		return currentObject;
	}

	@Override
	public Iterator<Map.Entry<T, Integer>> iterator()
	{
		return map.entrySet().iterator();
	}

	public Set<T> keys()
	{
		return map.keySet();
	}

	public Collection<Integer> values()
	{
		return map.values();
	}
}
