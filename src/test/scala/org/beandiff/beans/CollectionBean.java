/**
 * Copyright (c) 2012, Tomasz Kaczmarzyk.
 *
 * This file is part of BeanDiff.
 *
 * BeanDiff is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * BeanDiff is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BeanDiff; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
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
