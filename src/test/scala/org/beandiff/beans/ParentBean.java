package org.beandiff.beans;

public class ParentBean {

	private String name;
	private SimpleJavaBean child;
	
	
	public ParentBean(SimpleJavaBean child) {
		this.child = child;
	}
	
	public ParentBean(String name, SimpleJavaBean child) {
		this(child);
		this.name = name;
	}
	
	public SimpleJavaBean getChild() {
		return child;
	}
	
	public String getName() {
		return name;
	}
}
