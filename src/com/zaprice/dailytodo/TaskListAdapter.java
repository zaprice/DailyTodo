package com.zaprice.dailytodo;

import java.util.List;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * 
 * @author zaprice
 * Subclass of ArrayAdapter<Task>
 * Used to handle crossing off tasks when view is changed
 *
 */
public class TaskListAdapter extends ArrayAdapter<Task> {

	public TaskListAdapter(Context context, int resource, List<Task> objects) {
		super(context, resource, objects);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		Task task = getItem(position);
		TextView t = (TextView) super.getView(position, convertView, parent);
		if(task.isDone()) {
			t.setPaintFlags(t.getPaintFlags() ^ Paint.STRIKE_THRU_TEXT_FLAG);
		}
		return t;		
	}
}
