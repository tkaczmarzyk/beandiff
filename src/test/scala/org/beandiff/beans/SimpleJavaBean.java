/**
 * Copyright (c) 2012-2013, Tomasz Kaczmarzyk.
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

import java.util.Comparator;

public class SimpleJavaBean {

	public static Comparator<SimpleJavaBean> orderByName = new Comparator<SimpleJavaBean>() {
		public int compare(SimpleJavaBean o1, SimpleJavaBean o2) {
			return o1.name.compareTo(o2.name);
		}
	};
	
	private String name;
	
	private int value;
	
	
	public SimpleJavaBean(String name, int value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public int getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return "Bean[" + name + ", " + value + "]";
	}
	
}
