package org.rboaop.sample;

import java.util.List;

public class Sample {
 
	private String firstName;
	private boolean result;
 
	public void hello() {
		System.out.println("hello from former hello() method");
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public boolean isResult() {
		return result;
	}
}
