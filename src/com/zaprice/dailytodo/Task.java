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
	private boolean everyDay;
	
	public Task(String taskName) {
		this.taskName = taskName;
		isDone = false;
		everyDay = true;
	}
	
	public Task(String taskName, boolean isDone, boolean everyDay) {
		this.taskName = taskName;
		this.isDone = isDone;
		this.everyDay = everyDay;
	}
	
	public boolean isDone() {
		return isDone;
	}
	
	public void markDone() {
		isDone = !isDone;
	}
	
	public boolean everyDay() {
		return everyDay;
	}
	
	public void setEveryDay(boolean everyDay) {
		this.everyDay = everyDay;
	}
	
	public long getId() {
		return taskName.hashCode();
	}
	
	public String toString() {
		return taskName;
	}
}
