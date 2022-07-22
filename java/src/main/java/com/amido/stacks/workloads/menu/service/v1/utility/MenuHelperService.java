package com.amido.stacks.workloads.menu.service.v1.utility;

import com.amido.stacks.workloads.menu.commands.CreateCategoryCommand;
import com.amido.stacks.workloads.menu.commands.CreateItemCommand;
import com.amido.stacks.workloads.menu.commands.DeleteCategoryCommand;
import com.amido.stacks.workloads.menu.commands.DeleteItemCommand;
import com.amido.stacks.workloads.menu.commands.UpdateCategoryCommand;
import com.amido.stacks.workloads.menu.commands.UpdateItemCommand;
import com.amido.stacks.workloads.menu.domain.Category;
import com.amido.stacks.workloads.menu.domain.Item;
import com.amido.stacks.workloads.menu.domain.Menu;
import com.amido.stacks.workloads.menu.exception.CategoryAlreadyExistsException;
import com.amido.stacks.workloads.menu.exception.CategoryDoesNotExistException;
import com.amido.stacks.workloads.menu.exception.ItemAlreadyExistsException;
import com.amido.stacks.workloads.menu.exception.ItemDoesNotExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuHelperService {

  public Optional<Category> findCategory(Menu menu, UUID categoryId) {
    Optional<Category> existing = Optional.empty();
    if (menu.getCategories() != null && !menu.getCategories().isEmpty()) {
      existing =
          menu.getCategories().stream()
              .filter(c -> c.getId().equals(categoryId.toString()))
              .findFirst();
    }
    return existing;
  }

  public Optional<Item> findItem(Category category, UUID itemId) {
    Optional<Item> existing = Optional.empty();

    if (category.getItems() != null && !category.getItems().isEmpty()) {
      existing =
          category.getItems().stream().filter(t -> t.getId().equals(itemId.toString())).findFirst();
    }
    return existing;
  }

  public Category getCategory(Menu menu, CreateItemCommand command) {
    Optional<Category> existing = Optional.empty();

    if (menu.getCategories() != null && !menu.getCategories().isEmpty()) {
      existing =
          menu.getCategories().stream()
              .filter(c -> c.getId().equals(command.getCategoryId().toString()))
              .findFirst();
    }
    return existing.orElseThrow(
        () -> new CategoryDoesNotExistException(command, command.getCategoryId()));
  }

  public Category getCategory(Menu menu, DeleteCategoryCommand command) {
    return findCategory(menu, command.getCategoryId())
        .orElseThrow(() -> new CategoryDoesNotExistException(command, command.getCategoryId()));
  }

  public Category addItem(Category category, CreateItemCommand command) {
    List<Item> items = category.getItems() == null ? new ArrayList<>() : category.getItems();

    if (items.stream().anyMatch(c -> c.getName().equalsIgnoreCase(command.getName()))) {
      throw new ItemAlreadyExistsException(command, command.getCategoryId(), command.getName());
    } else {
      Item item =
          new Item(
              command.getItemId().toString(),
              command.getName(),
              command.getDescription(),
              command.getPrice(),
              command.getAvailable());
      category.getItems().add(item);
      return category;
    }
  }

  public List<Category> addCategory(Menu menu, CreateCategoryCommand command) {
    List<Category> categories =
        menu.getCategories() == null ? new ArrayList<>() : menu.getCategories();

    if (categories.stream().anyMatch(c -> c.getName().equalsIgnoreCase(command.getName()))) {
      throw new CategoryAlreadyExistsException(command, command.getName());
    } else {
      categories.add(
          new Category(
              command.getCategoryId().toString(),
              command.getName(),
              command.getDescription(),
              new ArrayList<>()));
      return categories;
    }
  }

  /**
   * if the request is to update the name and description of a category If there is a category with
   * the same name but only updating the description then allow that else throw a category already
   * exists com.amido.core.api.exception if a category with the same name doesn't exits then update
   * the requested category.
   *
   * @param menu menu
   * @param command update category request
   * @return category
   */
  public Category updateCategory(Menu menu, UpdateCategoryCommand command) {
    Category category = getCategory(menu, command);
    menu.getCategories()
        .forEach(
            t -> {
              if (t.getName().equalsIgnoreCase(command.getName())) {
                if (t.getId().equalsIgnoreCase(command.getCategoryId().toString())) {
                  category.setDescription(command.getDescription());
                } else {
                  throw new CategoryAlreadyExistsException(command, command.getName());
                }
              } else {
                category.setDescription(command.getDescription());
                category.setName(command.getName());
              }
            });

    return category;
  }

  public Category getCategory(Menu menu, UpdateCategoryCommand command) {
    return findCategory(menu, command.getCategoryId())
        .orElseThrow(() -> new CategoryDoesNotExistException(command, command.getCategoryId()));
  }

  public Category getCategory(Menu menu, DeleteItemCommand command) {
    return findCategory(menu, command.getCategoryId())
        .orElseThrow(() -> new CategoryDoesNotExistException(command, command.getCategoryId()));
  }

  public Item getItem(Category category, DeleteItemCommand command) {
    return findItem(category, command.getItemId())
        .orElseThrow(
            () ->
                new ItemDoesNotExistsException(
                    command, command.getCategoryId(), command.getItemId()));
  }

  public Category getCategory(Menu menu, UpdateItemCommand command) {
    return findCategory(menu, command.getCategoryId())
        .orElseThrow(() -> new CategoryDoesNotExistException(command, command.getCategoryId()));
  }

  public Item getItem(Category category, UpdateItemCommand command) {
    return findItem(category, command.getItemId())
        .orElseThrow(
            () ->
                new ItemDoesNotExistsException(
                    command, command.getCategoryId(), command.getItemId()));
  }

  /**
   * If the request is to update the description/available/price of an existing item then allow that
   * if the request is to update an item but an item with that name already exists then throw an
   * com.amido.core.api.exception if there are no item with the same name then allow that
   *
   * @param command update item request
   * @param category category
   * @return item
   */
  public Item updateItem(UpdateItemCommand command, Category category) {
    Item item = getItem(category, command);

    category
        .getItems()
        .forEach(
            t -> {
              if (t.getName().equalsIgnoreCase(command.getName())) {
                if (t.getId().equalsIgnoreCase(command.getItemId().toString())) {
                  item.setAvailable(command.getAvailable());
                  item.setDescription(command.getDescription());
                  item.setPrice(command.getPrice());
                } else {
                  throw new ItemAlreadyExistsException(
                      command, command.getCategoryId(), command.getName());
                }
              } else {
                item.setAvailable(command.getAvailable());
                item.setDescription(command.getDescription());
                item.setName(command.getName());
                item.setPrice(command.getPrice());
              }
            });
    return item;
  }
}
