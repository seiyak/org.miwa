package org.rboaop.sample;

public class NewSample {

	private String firstName;
	private String lastName; 
	private Sample2 sample;
 
	public NewSample(){ 
		this.sample = new Sample2(); 
	}
	   
	public void say(){
		System.out.println("say hello------ from New sample");
	}  
	 
	public String getFirsName() {
		this.sample.setFirstName("google");
		this.sample.getFirstName();
		this.sample.hello();
		this.sample.say();
		this.sample.helloWorld("hello");
		return firstName;
	}

	public void setFirstName(String firstName) {
		try{
		this.firstName = firstName;     
		}catch(RuntimeException ex){
			System.out.println("");   
		}
	} 
 
	public String getLastName() {
		return lastName;
	}  

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setSample(Sample2 sample) {
		this.sample = sample;
	}

	public Sample2 getSample() {
		return sample;
	}
}
