package com.dropsnorz.owlplug.core.tasks;

import java.util.ArrayList;
import javafx.concurrent.Task;

public abstract class AbstractTask extends Task<TaskResult> {
	
	private String name = "OwlPlug task";
	
	private double maxProgress = 1;
	private double commitedProgress = 0;
	
	private ArrayList<String> warnings = new ArrayList<>();
	
	public AbstractTask() {}
	
	public AbstractTask(String name) {
		this.name = name;
	}
	
	
	protected void commitProgress(double progress) {
		commitedProgress = commitedProgress + progress;
		this.updateProgress(commitedProgress, getMaxProgress());

	}
	
	protected double getCommitedProgress() {
		return commitedProgress;
	}


	protected void setCommitedProgress(double commitedProgress) {
		this.commitedProgress = commitedProgress;

	}
	
	protected double getMaxProgress() {
		return maxProgress;
	}

	protected void setMaxProgress(double maxProgress) {
		this.maxProgress = maxProgress;
	}
	
	protected void computeTotalProgress(double progress) {
		updateProgress(commitedProgress + progress, maxProgress);
	}
	
	protected TaskResult success() {
		return new TaskResult();
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	protected ArrayList<String> getWarnings() {
		return warnings;
	}
	

	@Override
	public String toString() {
		String prefix = "W";
		if (this.isRunning()) {
			prefix = "R";
		}
		if (this.isDone()) {
			prefix = "D";
		}
		if (this.getState().equals(State.FAILED)) {
			prefix = "F";
		}
		return prefix + " - " + this.getName();

	}
}
