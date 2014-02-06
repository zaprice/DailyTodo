package com.zaprice.dailytodo;

public class Task {
	
	private String taskName;
	private boolean isDone;
	
	public Task(String taskName) {
		this.taskName = taskName;
		isDone = false;
	}
	public Task(String taskName, boolean isDone) {
		this.taskName = taskName;
		this.isDone = isDone;
	}
	
	public boolean isDone() {
		return isDone;
	}
	
	public void markDone() {
		isDone = !isDone;
	}
	
	public String toString() {
		return taskName;
	}
}
