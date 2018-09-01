package com.dropsnorz.owlplug.auth.model;

import com.dropsnorz.owlplug.auth.ui.AccountItem;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * OwlPlug JPA Entity class to handle users accounts informations.
 */
@Entity
public class UserAccount implements AccountItem {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;
	private String iconUrl;
	private UserAccountProvider accountProvider;
	@OneToOne(cascade = CascadeType.REMOVE, optional = true)
	private GoogleCredential credential;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public GoogleCredential getCredential() {
		return credential;
	}

	public void setCredential(GoogleCredential credential) {
		this.credential = credential;
	}



}
