package com.zaprice.dailytodo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
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
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tasks = new ArrayList<String>();
		done = new ArrayList<Integer>();
		taskList = (ListView) findViewById(R.id.taskList);
		taskListAdapter = new ArrayAdapter<String>(this, R.layout.list_item);
		taskList.setAdapter(taskListAdapter);
		loadTasks();
	}
	
	@Override
	protected void onStart() {
		super.onStart();		
		assert(tasks.size() == done.size());
		Iterator<String> tasksIt = tasks.iterator();
		Iterator<Integer> doneIt = done.iterator();
		taskListAdapter.clear();
		while(tasksIt.hasNext() && doneIt.hasNext()) {
			if(doneIt.next() == Integer.valueOf(TRUE)) {
				tasksIt.next();
			} else {
				taskListAdapter.add(tasksIt.next());
				//TextView t = new TextView(this);
				//t.setText(tasksIt.next());
				//t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
				//taskList.addView(t);
				//TODO: make checkable task from string write
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.add_task:
				Intent addTaskIntent = new Intent(this, AddTaskActivity.class);
				startActivityForResult(addTaskIntent, ADD_TASK);
				return true;
			default:
				return false;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
		super.onPause();
		saveTasks();
	}
	
	private void saveTasks() {
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
		SharedPreferences data = getPreferences(MODE_PRIVATE);
		Map<String, ?> dataMap = data.getAll();
		for(Map.Entry<String, ?> entry : dataMap.entrySet()) {
			tasks.add(entry.getKey());
			done.add(Integer.valueOf(entry.getValue().toString()));
		}
	}
}
