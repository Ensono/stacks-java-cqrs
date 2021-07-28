package com.amido.stacks.menu.handlers;

import com.amido.stacks.menu.commands.CreateItemCommand;
import com.amido.stacks.menu.domain.Category;
import com.amido.stacks.menu.domain.Item;
import com.amido.stacks.menu.domain.Menu;
import com.amido.stacks.menu.events.CategoryUpdatedEvent;
import com.amido.stacks.menu.events.MenuEvent;
import com.amido.stacks.menu.events.MenuItemCreatedEvent;
import com.amido.stacks.menu.events.MenuUpdatedEvent;
import com.amido.stacks.menu.exception.CategoryDoesNotExistException;
import com.amido.stacks.menu.exception.ItemAlreadyExistsException;
import com.amido.stacks.menu.repository.MenuRepository;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CreateItemHandler extends MenuBaseCommandHandler<CreateItemCommand> {

  public CreateItemHandler(MenuRepository menuRepository) {
    super(menuRepository);
  }

  @Override
  Optional<UUID> handleCommand(Menu menu, CreateItemCommand command) {
    command.setItemId(UUID.randomUUID());
    Category category = addItem(getCategory(menu, command), command);
    menuRepository.save(menu.addOrUpdateCategory(category));
    return Optional.of(command.getItemId());
  }

  @Override
  List<MenuEvent> raiseApplicationEvents(Menu menu, CreateItemCommand command) {
    return Arrays.asList(
        new MenuUpdatedEvent(command),
        new CategoryUpdatedEvent(command, command.getCategoryId()),
        new MenuItemCreatedEvent(command, command.getCategoryId(), command.getItemId()));
  }

  Category getCategory(Menu menu, CreateItemCommand command) {
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

  Category addItem(Category category, CreateItemCommand command) {
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
}
