package org.irods.scotty;

import java.io.Serializable;
 
/**
 * * Manipulate the Tasks menu
 * 
 * @author Lisa Stillwell
 *
 */
public class AdminTasks implements Serializable {
	
	private static final long serialVersionUID = -1527533636119933180L;
	private String selectedTask;
	
	public AdminTasks() {	
	}
	
	public String getSelectedTask() {
		return this.selectedTask;
	}
	
	public String changeTask(String newTask) {
		this.selectedTask = newTask;
		return this.selectedTask;
	}

}
