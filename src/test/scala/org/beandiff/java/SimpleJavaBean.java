package org.beandiff.java;

public class SimpleJavaBean {

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
}
