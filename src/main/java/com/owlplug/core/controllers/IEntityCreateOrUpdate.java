package com.owlplug.core.controllers;

public interface IEntityCreateOrUpdate<T> {

  public void startCreateSequence();

  public void startUpdateSequence(T entity);

}
