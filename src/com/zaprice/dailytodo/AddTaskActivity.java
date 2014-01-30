package com.zaprice.dailytodo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddTaskActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		
		final EditText taskName = (EditText) findViewById(R.id.editText1);
		
		final Button addTask = (Button) findViewById(R.id.button1);
        addTask.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	if(taskName.getText().toString() == "") {
            		setResult(RESULT_CANCELED);
                	finish();
            	}
            	getIntent().putExtra("task name", taskName.getText().toString());
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
