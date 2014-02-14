package com.zaprice.dailytodo;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * 
 * @author zaprice
 *
**/
public class MainActivity extends Activity {
	
	/**
	 * TODO list:
	 * Spacing between list items is not quite uniform, maybe?
	 * Add support for one-off tasks
	**/
	
	//Constants
	final int ADD_TASK = 0;
	final String TAG = "DEBUG";
	
	//Data
	private TaskListHandler taskListHandler;

	/**
	 * Called when the app is first started; next call in the lifecycle is onStart
	**/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		taskListHandler = new TaskListHandler(this);
	}
	
	/**
	 * Called when app is resumed
	 * Used to compare dates
	**/
	@Override
	protected void onStart() {
		super.onStart();
		taskListHandler.checkDate();
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
	    		taskListHandler.delete(info.id);
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
			taskListHandler.add(taskBundle.getString("task name"));
		}
	}
	
	/**
	 * Called when this Activity is no longer in focus
	 * Calls saveTasks to store tasks in SavedPreferences
	**/
	@Override
	public void onPause() {
		super.onPause();
		taskListHandler.saveTasks();
	}
}
