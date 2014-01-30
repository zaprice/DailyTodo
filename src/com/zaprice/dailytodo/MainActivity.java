package com.zaprice.dailytodo;

import java.util.ArrayList;
import java.util.Iterator;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
	
	ArrayList<String> tasks;
	ArrayList<Boolean> done;
	final int ADD_TASK = 0;
	final String TAG = "DEBUG";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tasks = new ArrayList<String>();
		done = new ArrayList<Boolean>();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		/*assert(tasks.size() == done.size());
		Iterator<String> tasksIt = tasks.iterator();
		Iterator<Boolean> doneIt = done.iterator();
		while(tasksIt.hasNext() && doneIt.hasNext()) {
			if(doneIt.next()) {
				tasksIt.next();
			} else {
				String write = tasksIt.next();
				//TODO: make checkable task from string write
			}
		}*/
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
			done.add(Boolean.valueOf(false));
			Log.i(TAG, tasks.get(tasks.size()- 1));
			return;
		}
	}
}
