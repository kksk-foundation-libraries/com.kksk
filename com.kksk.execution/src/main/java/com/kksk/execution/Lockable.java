package com.kksk.execution;

public final class Lockable<Input, Output> {
	public final Input input;
	public final Object lock;
	private Output output;

	public Lockable(Input input) {
		this.input = input;
		this.lock = new Object();
	}

	public Output getOutput() {
		return output;
	}

	public void setOutput(Output output) {
		this.output = output;
	}
}
