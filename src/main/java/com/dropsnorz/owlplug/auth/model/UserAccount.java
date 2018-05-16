package com.dropsnorz.owlplug.auth.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class UserAccount {
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private UserAccountProvider accountProvider;
	
	public UserAccount() {
		
	}
	
	public UserAccount(UserAccountProvider accountProvider) {
		
		this.accountProvider = accountProvider;
		
	}

	public Long getId() {
		return this.id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getKey() {
		return Long.toString(id);
	}

	public UserAccountProvider getAccountProvider() {
		return accountProvider;
	}

	public void setAccountProvider(UserAccountProvider accountProvider) {
		this.accountProvider = accountProvider;
	}
	

}
