package com.dropsnorz.owlplug.core.model.platform;

import java.util.ArrayList;
import java.util.List;

public class RuntimePlatform {
	
	private String tag;
	private OperatingSystem operatingSystem;
	private String arch;
	
	private ArrayList<RuntimePlatform> compatiblePlatforms;

	public RuntimePlatform(String tag, OperatingSystem operatingSystem, String arch) {
		super();
		this.tag = tag;
		this.operatingSystem = operatingSystem;
		this.arch = arch;
		
		this.compatiblePlatforms = new ArrayList<>();
		this.compatiblePlatforms.add(this);
	}
	
	public List<String> getCompatiblePlatformsTags() {
		List<String> platforms = new ArrayList<>();
		for (RuntimePlatform platform : compatiblePlatforms) {
			platforms.add(platform.getTag());
		}
		return platforms;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public OperatingSystem getOperatingSystem() {
		return operatingSystem;
	}

	public void setOperatingSystem(OperatingSystem operatingSystem) {
		this.operatingSystem = operatingSystem;
	}

	public String getArch() {
		return arch;
	}

	public void setArch(String arch) {
		this.arch = arch;
	}

	public ArrayList<RuntimePlatform> getCompatiblePlatforms() {
		return compatiblePlatforms;
	}

	protected void setCompatiblePlatforms(ArrayList<RuntimePlatform> compatibleEnvironments) {
		this.compatiblePlatforms = compatibleEnvironments;
	}

	@Override
	public String toString() {
		return "RuntimePlatform [tag=" + tag + ", operatingSystem=" + operatingSystem + ", arch=" + arch + "]";
	}
	

}
