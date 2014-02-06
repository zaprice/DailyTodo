package com.zaprice.dailytodo;

/**
 * 
 * @author zaprice
 * Internal representation of the user's tasks
 * 
 */
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
	
	public long getId() {
		return taskName.hashCode();
	}
	
	public String toString() {
		return taskName;
	}
}
