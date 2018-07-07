package com.dropsnorz.owlplug.core.engine.tasks;

import javafx.concurrent.Task;
import javafx.concurrent.Worker;

public abstract class AbstractTask extends Task<TaskResult> {
	
	private String name = "OwlPlug task" ;
	
	protected TaskResult success() {
		return new TaskResult();
	}
	

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		String prefix = "W";
		
		if (this.isRunning()) prefix = "R";
		if (this.isDone()) prefix = "D";
		if (this.getState().equals(State.FAILED)) prefix = "F";
		return prefix + " - " + this.getName();
		

	}
	
	

}
