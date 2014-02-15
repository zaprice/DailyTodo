package com.zaprice.dailytodo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
/**
 * 
 * @author zaprice
 *
**/
public class AddTaskActivity extends Activity{
	
	private boolean everyDay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		
		everyDay = true;
		
		//UI elements; EditText lets the user type in a name for their task
		final EditText taskName = (EditText) findViewById(R.id.editText1);
		final Button addTask = (Button) findViewById(R.id.button1);
		final Spinner days = (Spinner) findViewById(R.id.spinner1);
		
		days.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			/**
			 * Listener used to detect Spinner selection
			**/
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				everyDay = (pos == 0);		
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				return;
			}
		});
		
        addTask.setOnClickListener(new View.OnClickListener() {
        	/**
        	 * Listener used to detect when a user is finished specifying their task
        	**/
            public void onClick(View v) {
            	String task = taskName.getText().toString().trim();
            	if(task.equals("")) {
            		//Cancels adding a task if the name is empty
            		setResult(RESULT_CANCELED);
            		finish();
            	}
            	//Sends task name back to MainActivity by calling back to onActivityResult
            	getIntent().putExtra("task name", task);
            	getIntent().putExtra("every day", everyDay);
            	setResult(RESULT_OK, getIntent());
            	finish();
            }
        });
	}
	

 	
	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		super.onBackPressed();
	}
}
