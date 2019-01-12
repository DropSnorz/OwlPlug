package com.owlplug.core.ui;

import java.util.function.Predicate;

import javafx.scene.control.TreeItem;

@FunctionalInterface
public interface TreeItemPredicate<T> {
  /**
   * Utility method to create a TreeItemPredicate from a given {@link Predicate}
   */
  static <T> TreeItemPredicate<T> create(Predicate<T> predicate) {
    return (parent, value) -> predicate.test(value);
  }

  /**
   * Evaluates this predicate on the given argument.
   *
   * @param parent the parent tree item of the element or null if there is no
   *               parent
   * @param value  the value to be tested
   * @return {@code true} if the input argument matches the predicate,otherwise
   *         {@code false}
   */
  boolean test(TreeItem<T> parent, T value);
}