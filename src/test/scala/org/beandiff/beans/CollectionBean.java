package org.beandiff.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


public class CollectionBean<T> {

	public Collection<T> collection;
	
	public CollectionBean(Collection<T> collection) {
		this.collection = collection;
	}
	
	public static <T> CollectionBean<T> listBean(T... elems) {
		return new CollectionBean<T>(new ArrayList<T>(Arrays.asList(elems)));
	}
}
