package com.zaprice.dailytodo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
/**
 * 
 * @author zaprice
 *
 */
public class AddTaskActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		
		//UI elements; EditText lets the user type in a name for their task
		final EditText taskName = (EditText) findViewById(R.id.editText1);
		final Button addTask = (Button) findViewById(R.id.button1);
		
        addTask.setOnClickListener(new View.OnClickListener() {
        	/**
        	* Listener used to detect when a user is finished specifying their task
        	**/
            public void onClick(View v) {
            	if(taskName.getText().toString().equals("")) {
            		//Cancels adding a task if the name is empty
            		setResult(RESULT_CANCELED);
                	finish();
            	}
            	//Sends task name back to MainActivity by calling back to onActivityResult
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
