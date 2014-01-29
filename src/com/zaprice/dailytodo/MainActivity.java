package com.zaprice.dailytodo;

import java.util.ArrayList;
import java.util.Iterator;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
	
	ArrayList<String> tasks;
	ArrayList<Boolean> done;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		assert(tasks.size() == done.size());
		Iterator<String> tasksIt = tasks.iterator();
		Iterator<Boolean> doneIt = done.iterator();
		while(tasksIt.hasNext() && doneIt.hasNext()) {
			if(doneIt.next()) {
				tasksIt.next();
			} else {
				String write = tasksIt.next();
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
				startActivity(addTaskIntent);
				return true;
			default:
				return false;
		}
	}
}
