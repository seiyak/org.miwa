package org.rboaop.sample;

import java.util.List;

public class Sample2 {

	private String firstName;
	private String lastName;
	private boolean done;
	private List<String> list;
	private static String STR = "String";
	private static final String ST = "STRING";
	public static final String STTT = "STTT";
	private String toBeConstant;
	private byte tobeByteConstant;
	private char toBeCharConstant;
	private double toBeDoubleConstant;
	private float toBeFloatConstant;
	private int toBeIntConstant;
	private long toBeLongConstant;
	private short toBeShortConstant;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public void hello() {
		System.out.println("hello from Sample2");
	}

	public void say() {
		System.out.println("say hello---------");
	}

	public void helloWorld(String hello) {
		System.out.println("hello world");
		this.toBeConstant = "constant";
		System.out.println(this.toBeConstant);
		this.setToBeConstant("toBeConstant");
		System.out.println(this.getToBeConstant());
	}

	public void setToBeConstant(String toBeConstant) {
		this.toBeConstant = toBeConstant;
	}

	public String getToBeConstant() {
		return toBeConstant;
	}

	public void setTobeByteConstant(byte tobeByteConstant) {
		this.tobeByteConstant = tobeByteConstant;
	}

	public byte getTobeByteConstant() {
		return tobeByteConstant;
	}

	public void setToBeCharConstant(char toBeCharConstant) {
		this.toBeCharConstant = toBeCharConstant;
	}

	public char getToBeCharConstant() {
		return toBeCharConstant;
	}

	public void setToBeDoubleConstant(double toBeDoubleConstant) {
		this.toBeDoubleConstant = toBeDoubleConstant;
	}

	public double getToBeDoubleConstant() {
		return toBeDoubleConstant;
	}

	public void setToBeFloatConstant(float toBeFloatConstant) {
		this.toBeFloatConstant = toBeFloatConstant;
	}

	public float getToBeFloatConstant() {
		return toBeFloatConstant;
	}

	public void setToBeIntConstant(int toBeIntConstant) {
		this.toBeIntConstant = toBeIntConstant;
	}

	public int getToBeIntConstant() {
		return toBeIntConstant;
	}

	public void setToBeLongConstant(long toBeLongConstant) {
		this.toBeLongConstant = toBeLongConstant;
	}

	public long getToBeLongConstant() {
		return toBeLongConstant;
	}

	public void setToBeShortConstant(short toBeShortConstant) {
		this.toBeShortConstant = toBeShortConstant;
	}

	public short getToBeShortConstant() {
		return toBeShortConstant;
	}
}
