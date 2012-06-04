package org.rboaop.refactor.engine.generic;

public class InstructionRange {

	private int start;
	private int end;

	public InstructionRange() {
		this.start = -1;
		this.end = -1;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	@Override
	public String toString() {

		return "instruction start: " + this.start + " end: " + this.end;
	}
}
