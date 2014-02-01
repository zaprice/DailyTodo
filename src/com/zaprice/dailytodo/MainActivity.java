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
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	ArrayList<String> tasks;
	ArrayList<Integer> done;
	ListView taskList;
	ArrayAdapter<String> taskListAdapter;
	final int ADD_TASK = 0, TRUE = 1, FALSE = 0;
	final String TAG = "DEBUG";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Called when the app is first started; next call in the lifecycle is onStart
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tasks = new ArrayList<String>();
		done = new ArrayList<Integer>();
		taskList = (ListView) findViewById(R.id.taskList);
		taskListAdapter = new ArrayAdapter<String>(this, R.layout.list_item);
		taskList.setAdapter(taskListAdapter);
		
		taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			//Listener used to detect when user touches a task
			//On touch, a task will be marked done and given a strikethrough decoration
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Log.i(TAG, ((TextView) view).getText().toString());
				strikeText((TextView) view);
				//TODO: start here; this doesn't actually work
			}
		});
		
		loadTasks();
	}
	
	@Override
	protected void onStart() {
		//Called whenever MainActivity is resumed
		super.onStart();		
		assert(tasks.size() == done.size());		//To make sure nothing silly happens when adding/removing tasks
		drawTasks();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds the "add task" button to the menu bar
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Called back from AddTaskActivity when it ends
		//Passes a Bundle containing the task name
		//Task is added to memory; next call in the lifecycle is onStart
		if(resultCode == RESULT_OK) {
			Bundle taskBundle = data.getExtras();
			tasks.add(taskBundle.getString("task name"));
			done.add(Integer.valueOf(0));
			Log.i(TAG, tasks.get(tasks.size()- 1));
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
		Iterator<String> tasksIt = tasks.iterator();
		Iterator<Integer> doneIt = done.iterator();
		taskListAdapter.clear();
		String next;
		while(tasksIt.hasNext() && doneIt.hasNext()) {
			next = tasksIt.next();
			taskListAdapter.add(next);
			if(doneIt.next() == Integer.valueOf(TRUE)) {
				strikeText(next);
			} else {
				taskListAdapter.add(tasksIt.next());
			}
		}
	}
	
	private void saveTasks() {
		//Saves task list and done flags to SavedPreferences
		//Called onPause
		SharedPreferences data = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = data.edit();
		Iterator<String> tasksIt = tasks.iterator();
		Iterator<Integer> doneIt = done.iterator();
		while(tasksIt.hasNext() && doneIt.hasNext()) {
			editor.putInt(tasksIt.next(), doneIt.next().intValue());
		}
		editor.apply();
	}
	
	private void loadTasks() {
		//Loads task list and done flags from SavedPreferences
		//Called onCreate
		SharedPreferences data = getPreferences(MODE_PRIVATE);
		Map<String, ?> dataMap = data.getAll();
		for(Map.Entry<String, ?> entry : dataMap.entrySet()) {
			tasks.add(entry.getKey());
			done.add(Integer.valueOf(entry.getValue().toString()));
		}
	}
	
	private void strikeText(String task) {
		//Adds strikethrough decoration to completed task
		//Called in drawTasks
		TextView t = (TextView) taskList.findViewById((int) taskListAdapter.getItemId(taskListAdapter.getPosition(task)));
		t.setPaintFlags(t.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
	}
	
	private void strikeText(TextView t) {
		//Adds strikethrough decoration to completed task
		//Called onItemClick
		t.setPaintFlags(t.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
	}
	
	private void unstrikeText(String task) {
		//Removes strikethrough decoration from task
		//TODO: Called onItemClick
		TextView t = (TextView) taskList.findViewById((int) taskListAdapter.getItemId(taskListAdapter.getPosition(task)));
		t.setPaintFlags(t.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG);
	}
	
}
