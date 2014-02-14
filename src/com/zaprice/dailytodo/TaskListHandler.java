package com.zaprice.dailytodo;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @author zaprice
 * 
 * Class handles access to task list and UI objects
 *
**/
public class TaskListHandler {
	
	//Data
	private MainActivity mainActivity;
	private ArrayList<Task> tasks;
	private ListView taskList;
	private TaskListAdapter taskListAdapter;
	private GregorianCalendar lastUsed;
	
	/**
	 * Constructor
	**/
	public TaskListHandler(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
		
		tasks = new ArrayList<Task>();
		taskList = (ListView) mainActivity.findViewById(R.id.taskList);
		taskListAdapter = new TaskListAdapter(mainActivity, R.layout.list_item, tasks);
		taskList.setAdapter(taskListAdapter);
		taskListAdapter.setNotifyOnChange(true);
		
		taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			/**
			 * Listener used to detect when user touches a task
			 * On touch, a task will be marked done and given a strikethrough decoration
			**/
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				strikeText((TextView) view);
			}
		});
		
		loadTasks();
		mainActivity.registerForContextMenu(taskList);
		if(lastUsed == null) {
			lastUsed = new GregorianCalendar();
		}
	}
	
	/**
	 * Saves tasks and last used date to SavedPreferences
	 * Called onPause
	**/
	void saveTasks() {
		SharedPreferences data = mainActivity.getPreferences(Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = data.edit();
		editor.clear();
		Iterator<Task> tasksIt = tasks.iterator();
		Task t;
		while(tasksIt.hasNext()) {
			t = tasksIt.next();
			editor.putBoolean(t.toString(), t.isDone());
		}
		editor.putLong("date", lastUsed.getTimeInMillis());
		editor.apply();
		
	}
	
	/**
	 * Loads tasks and last used date from SavedPreferences
	 * Called onCreate
	**/
	private void loadTasks() {
		SharedPreferences data = mainActivity.getPreferences(Activity.MODE_PRIVATE);
		Map<String, ?> dataMap = data.getAll();
		
		for(Map.Entry<String, ?> entry : dataMap.entrySet()) {
			//TODO: map does not guarantee stable ordering, may not be preserved across instances; maybe should fix that
			if(entry.getKey().equals("date")) {
				lastUsed = new GregorianCalendar();
				lastUsed.setTimeInMillis(Long.parseLong(entry.getValue().toString()));
			}else {
				taskListAdapter.add(new Task(entry.getKey(), Boolean.parseBoolean(entry.getValue().toString())));
			}
		}
	}
	
	/**
	 * Checks to see if a day has passed
	 * Calls resetTasks if necessary
	**/
	void checkDate() {
		GregorianCalendar today = new GregorianCalendar(); 
		if(dayHasPassed(lastUsed, today)) {
			resetTasks();
			lastUsed = today;
		}
	}
	
	void add(String taskName) {
		taskListAdapter.add(new Task(taskName));
	}
	
	/**
	 * Deletes an item from the task list
	 * Called onContextItemSelected
	**/
	void delete(long id) {
		Task t = taskListAdapter.getItem((int)id);
		taskListAdapter.remove(t);
	}
	
	/**
	 * Adds strikethrough decoration to completed task, or removes decoration if it already has one
	 * Called onItemClick, strikeText(String)
	**/
	private void strikeText(TextView t) {
		t.setPaintFlags(t.getPaintFlags() ^ Paint.STRIKE_THRU_TEXT_FLAG);
		setDoneFlag(t.getText().toString());
	}
	
	/**
	 * Flips the done flag when task is crossed/uncrossed
	 * Called in strikeText
	**/
	private void setDoneFlag(String taskName) {
		Iterator<Task> taskIt = tasks.iterator();
		Task t;
		while(taskIt.hasNext()) {
			t = taskIt.next();
			if(t.toString().equals(taskName)) {
				t.markDone();
				break;
			}
		}
	}
	
	/**
	 * Used to determine if tasks should be reset
	 * Called onStart
	**/
	private boolean dayHasPassed(GregorianCalendar lastUsed, GregorianCalendar today) {
		GregorianCalendar dayLastUsed = new GregorianCalendar(lastUsed.get(GregorianCalendar.YEAR), lastUsed.get(GregorianCalendar.MONTH), lastUsed.get(GregorianCalendar.DAY_OF_MONTH));
		GregorianCalendar dayOfToday = new GregorianCalendar(today.get(GregorianCalendar.YEAR), today.get(GregorianCalendar.MONTH), today.get(GregorianCalendar.DAY_OF_MONTH));
		if(dayOfToday.after(dayLastUsed)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Used to reset done flags on tasks
	 * Called onStart
	**/
	private void resetTasks() {
		Iterator<Task> taskIt = tasks.iterator();
		Task t;
		while(taskIt.hasNext()) {
			t = taskIt.next();
			if(t.isDone()) {
				t.markDone();
			}
		}
		taskListAdapter.notifyDataSetChanged();
	}
}