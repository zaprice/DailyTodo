package com.zaprice.dailytodo;

import java.util.ArrayList;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	//Constants
	final int ADD_TASK = 0, TRUE = 1, FALSE = 0;
	final String TAG = "DEBUG";
	
	//Data
	private ArrayList<Task> tasks;
	private ListView taskList;
	private ArrayAdapter<Task> taskListAdapter;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Called when the app is first started; next call in the lifecycle is onStart
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tasks = new ArrayList<Task>();
		taskList = (ListView) findViewById(R.id.taskList);
		taskListAdapter = new ArrayAdapter<Task>(this, R.layout.list_item, tasks);
		taskList.setAdapter(taskListAdapter);
		taskListAdapter.setNotifyOnChange(true); //TODO: maybe have adapter add/remove itself
		
		taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			//Listener used to detect when user touches a task
			//On touch, a task will be marked done and given a strikethrough decoration
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView t = (TextView) view;
				strikeText(t);
				Log.i(TAG, Long.toString(id));
			}
		});
		
		loadTasks();
		registerForContextMenu(taskList);
	}
	
	@Override
	protected void onStart() {
		//Called whenever MainActivity is resumed
		super.onStart();		
		drawTasks();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds the "add task" button to the menu bar
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		//Inflate ContextMenu
		//Called when user long presses a list item
		super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.context_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Called when the add task menu button is pressed
		//Starts an AddTaskActivity
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Called back from AddTaskActivity when it ends
		//Passes a Bundle containing the task name
		//Task is added to memory; next call in the lifecycle is onStart
		if(resultCode == RESULT_OK) {
			Bundle taskBundle = data.getExtras();
			tasks.add(new Task(taskBundle.getString("task name")));
			taskListAdapter.notifyDataSetChanged(); //TODO: maybe have adapter add/remove itself
			Log.i(TAG, tasks.get(tasks.size()- 1).toString());
			return;
		}
	}
	
	@Override
	public void onPause() {
		//Called when this Activity is no longer in focus
		//Calls saveTasks to store tasks in SavedPreferences
		super.onPause();
		saveTasks();
	}
	
	private void drawTasks() {
		//Adds tasks in memory to the list on-screen
		//Called onStart
		//TODO: alter this so it only strikes tasks
		/*Iterator<String> tasksIt = tasks.iterator();
		Iterator<Integer> doneIt = done.iterator();
		taskListAdapter.clear();
		String next;
		while(tasksIt.hasNext() && doneIt.hasNext()) {
			next = tasksIt.next();
			taskListAdapter.add(next);
			if(doneIt.next() == Integer.valueOf(TRUE)) {
				strikeText(next);
			}
		}*/
	}
	
	private void saveTasks() {
		//Saves task list and done flags to SavedPreferences
		//Called onPause
		SharedPreferences data = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = data.edit();
		editor.clear();
		Iterator<Task> tasksIt = tasks.iterator();
		Task t;
		int i;
		while(tasksIt.hasNext()) {
			t = tasksIt.next();
			i = t.isDone() ? TRUE : FALSE;
			editor.putInt(t.toString(), i);
		}
		editor.apply();
	}
	
	private void loadTasks() {
		//Loads task list and done flags from SavedPreferences
		//Called onCreate
		SharedPreferences data = getPreferences(MODE_PRIVATE);
		Map<String, ?> dataMap = data.getAll();
		
		for(Map.Entry<String, ?> entry : dataMap.entrySet()) {
			//TODO: map does not guarantee stable ordering, may not be preserved across instances; maybe should fix that
			tasks.add(new Task(entry.getKey(), Integer.parseInt(entry.getValue().toString()) == TRUE));
		}
		taskListAdapter.notifyDataSetChanged(); //TODO: maybe have adapter add/remove itself
	}
	
	private void delete(long id) {
		//TODO: doesn't actually work; need to add ids to TextViews 
		Task t = taskListAdapter.getItem((int)id);
		tasks.remove(t);
		taskListAdapter.notifyDataSetChanged(); //TODO: maybe have adapter add/remove itself
	}
	
	private void strikeText(String task) {
		//Adds strikethrough decoration to completed task, or removes decoration if it already has one
		//Called in drawTasks
		//strikeText((TextView) taskList.findViewById((int) taskListAdapter.getItemId(taskListAdapter.getPosition(task))));
		//TODO: this is broken; can't look up a TextView by its String
	}
	
	private void strikeText(TextView t) {
		//Adds strikethrough decoration to completed task, or removes decoration if it already has one
		//Called onItemClick, strikeText(String)
		t.setPaintFlags(t.getPaintFlags() ^ Paint.STRIKE_THRU_TEXT_FLAG);
		setDoneFlag(t.getText().toString());
	}
	
	private void setDoneFlag(String taskName) {
		//Flips the done flag when task is crossed/uncrossed
		//Called in strikeText
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
}
