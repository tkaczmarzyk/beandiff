package org.beandiff.beans;

public class ParentBean {

	private String name;
	private Object child;
	
	
	public ParentBean(String name) {
		this.name = name;
	}
	
	public ParentBean(Object child) {
		this.child = child;
	}
	
	public ParentBean(String name, Object child) {
		this(child);
		this.name = name;
	}
	
	public Object getChild() {
		return child;
	}
	
	public void setChild(Object child) {
		this.child = child;
	}
	
	public String getName() {
		return name;
	}
}
