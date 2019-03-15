package com.owlplug.auth.ui;

/**
 * Account menu objects.
 *
 */
public class AccountMenuItem implements AccountItem {

  private String text;

  public AccountMenuItem(String text) {
    super();
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public Long getId() {
    return (long) -1;
  }

}
