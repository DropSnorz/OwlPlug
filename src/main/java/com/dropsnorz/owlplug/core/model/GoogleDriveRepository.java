package com.dropsnorz.owlplug.core.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.dropsnorz.owlplug.auth.model.UserAccount;

@Entity
public class GoogleDriveRepository extends PluginRepository {
	
	private String remoteRessourceId;
	@ManyToOne
	private UserAccount userAccount;
	
	public GoogleDriveRepository() {}
	
	public GoogleDriveRepository(String name, String id, UserAccount userAccount) {
		super(name);
		this.remoteRessourceId = id;
		this.userAccount = userAccount;
	}
	
	
	public String getRemoteRessourceId() {
		return remoteRessourceId;
	}
	public void setRemoteRessourceId(String remoteRessourceId) {
		this.remoteRessourceId = remoteRessourceId;
	}
	public UserAccount getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(UserAccount userAccount) {
		this.userAccount = userAccount;
	}
	
	
	

}
