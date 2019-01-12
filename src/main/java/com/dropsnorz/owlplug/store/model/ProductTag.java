package com.dropsnorz.owlplug.store.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ProductTag {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String name;
  @ManyToOne
  private StoreProduct product;

  public ProductTag() {

  }

  public ProductTag(String name) {
    super();
    this.name = name;
  }

  /**
   * Creates a ProductTag for a given product.
   * 
   * @param name    - name of the tag
   * @param product - related product
   */
  public ProductTag(String name, StoreProduct product) {
    super();
    this.name = name;
    this.product = product;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
