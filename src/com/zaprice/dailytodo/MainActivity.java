package com.zaprice.dailytodo;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @author zaprice
 *
 **/
public class MainActivity extends Activity {
	
	/**
	 * TODO list:
	 * Keyboard should open when AddTaskActivity starts
	 * Spacing between list items is not quite uniform
	 * Trim off trailing spaces; makes strikethrough look ugly
	 */
	
	//Constants
	final int ADD_TASK = 0;
	final String TAG = "DEBUG";
	
	//Data
	private ArrayList<Task> tasks;
	private ListView taskList;
	private TaskListAdapter taskListAdapter;
	private GregorianCalendar lastUsed;

	/**
	 * Called when the app is first started; next call in the lifecycle is onStart
	**/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tasks = new ArrayList<Task>();
		taskList = (ListView) findViewById(R.id.taskList);
		taskListAdapter = new TaskListAdapter(this, R.layout.list_item, tasks);
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
				Log.i(TAG, Long.toString(id));
			}
		});
		
		loadTasks();
		registerForContextMenu(taskList);
		if(lastUsed == null) {
			lastUsed = new GregorianCalendar();
		}
	}
	
	/**
	 * Called when app is resumed
	 * Used to compare dates
	 */
	@Override
	protected void onStart() {
		super.onStart();
		GregorianCalendar today = new GregorianCalendar(); 
		if(dayHasPassed(lastUsed, today)) {
			resetTasks();
		}
	}

	/**
	* Inflate the menu; this adds the "add task" button to the menu bar
	**/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	* Inflate ContextMenu
	* Called when user long presses a list item
	**/
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.context_menu, menu);
	}
	
	/**
	* Called when the add task menu button is pressed
	* Starts an AddTaskActivity
	**/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.add_task:
				Intent addTaskIntent = new Intent(this, AddTaskActivity.class);
				//Calls back to onActivityResult to pass data
				startActivityForResult(addTaskIntent, ADD_TASK);
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Called when delete is selected from the context menu
	 * Deletes a task
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	    	case R.id.delete:
	    		Log.i(TAG, Long.toString(info.id));
	    		delete(info.id);
	    		return true;
	    	default:
	    		return super.onContextItemSelected(item);
	    }
	}
	
	/**
	* Called back from AddTaskActivity when it ends
	* Passes a Bundle containing the task name
	* Task is added to memory; next call in the lifecycle is onStart
	**/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			Bundle taskBundle = data.getExtras();
			taskListAdapter.add(new Task(taskBundle.getString("task name")));
			
			Log.i(TAG, tasks.get(tasks.size()- 1).toString()); //For debugging
		}
	}
	
	/**
	 * Called when this Activity is no longer in focus
	* Calls saveTasks to store tasks in SavedPreferences
	**/
	@Override
	public void onPause() {
		super.onPause();
		saveTasks();
	}
	
	/**
	* Saves tasks and last used date to SavedPreferences
	* Called onPause
	**/
	private void saveTasks() {
		SharedPreferences data = getPreferences(MODE_PRIVATE);
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
		SharedPreferences data = getPreferences(MODE_PRIVATE);
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
	* Deletes an item from the task list
	* Called onContextItemSelected
	**/
	private void delete(long id) {
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
	 */
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
	 */
	private void resetTasks() {
		Iterator<Task> taskIt = tasks.iterator();
		Task t;
		while(taskIt.hasNext()) {
			t = taskIt.next();
			t.markDone();
		}
		taskListAdapter.notifyDataSetChanged();
		//TODO: make sure this resets strikethroughs
	}
}
