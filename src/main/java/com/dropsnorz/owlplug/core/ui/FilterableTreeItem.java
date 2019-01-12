package com.dropsnorz.owlplug.core.ui;

import java.lang.reflect.Field;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilterableTreeItem<T> extends TreeItem<T> {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private final ObservableList<TreeItem<T>> sourceList;
  private ObjectProperty<TreeItemPredicate<T>> predicate = new SimpleObjectProperty<>();

  public FilterableTreeItem(T value) {
    super(value);
    this.sourceList = FXCollections.observableArrayList();
    FilteredList<TreeItem<T>> filteredList = new FilteredList<>(this.sourceList);
    filteredList.predicateProperty().bind(Bindings.createObjectBinding(() -> {
      return child -> {
        // Set the predicate of child items to force filtering
        if (child instanceof FilterableTreeItem) {
          FilterableTreeItem<T> filterableChild = (FilterableTreeItem<T>) child;
          filterableChild.setPredicate(this.predicate.get());
        }
        // If there is no predicate, keep this tree item
        if (this.predicate.get() == null) {
          return true;

        }
        // If there are children, keep this tree item
        if (child.getChildren().size() > 0) {
          return true;

        }
        // Otherwise ask the TreeItemPredicate
        return this.predicate.get().test(this, child.getValue());
      };
    }, this.predicate));
    setHiddenFieldChildren(filteredList);
  }

  protected void setHiddenFieldChildren(ObservableList<TreeItem<T>> list) {
    try {
      Field childrenField = TreeItem.class.getDeclaredField("children"); //$NON-NLS-1$
      childrenField.setAccessible(true);
      childrenField.set(this, list);

      Field declaredField = TreeItem.class.getDeclaredField("childrenListener"); //$NON-NLS-1$
      declaredField.setAccessible(true);
      list.addListener((ListChangeListener<? super TreeItem<T>>) declaredField.get(this));
    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
      log.error("Error creating Filetrable Tree Item", e);
    }
  }

  public ObservableList<TreeItem<T>> getInternalChildren() {
    return this.sourceList;
  }

  public void setPredicate(TreeItemPredicate<T> predicate) {
    this.predicate.set(predicate);
  }

  public TreeItemPredicate getPredicate() {
    return predicate.get();
  }

  public ObjectProperty<TreeItemPredicate<T>> predicateProperty() {
    return predicate;
  }
}
