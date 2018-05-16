package com.dropsnorz.owlplug.auth.dao;

import org.springframework.data.repository.CrudRepository;

import com.dropsnorz.owlplug.auth.model.UserAccount;

public interface UserAccountDAO extends CrudRepository<UserAccount, Long> {

}
