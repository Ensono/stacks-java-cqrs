package com.amido.stacks.menu.handlers;

import com.amido.stacks.menu.commands.UpdateItemCommand;
import com.amido.stacks.menu.domain.Category;
import com.amido.stacks.menu.domain.Item;
import com.amido.stacks.menu.domain.Menu;
import com.amido.stacks.menu.events.CategoryUpdatedEvent;
import com.amido.stacks.menu.events.MenuEvent;
import com.amido.stacks.menu.events.MenuItemUpdatedEvent;
import com.amido.stacks.menu.events.MenuUpdatedEvent;
import com.amido.stacks.menu.exception.CategoryDoesNotExistException;
import com.amido.stacks.menu.exception.ItemAlreadyExistsException;
import com.amido.stacks.menu.exception.ItemDoesNotExistsException;
import com.amido.stacks.menu.repository.MenuRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

/** @author ArathyKrishna */
@Component
public class UpdateItemHandler extends MenuBaseCommandHandler<UpdateItemCommand> {

  public UpdateItemHandler(MenuRepository repository) {
    super(repository);
  }

  @Override
  Optional<UUID> handleCommand(Menu menu, UpdateItemCommand command) {
    Category category = getCategory(menu, command);
    Item updated = updateItem(command, category);
    menu.addOrUpdateCategory(category.addOrUpdateItem(updated));
    menuRepository.save(menu);
    return Optional.of(command.getItemId());
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
  Item updateItem(UpdateItemCommand command, Category category) {
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

  @Override
  List<MenuEvent> raiseApplicationEvents(Menu menu, UpdateItemCommand command) {
    return Arrays.asList(
        new MenuItemUpdatedEvent(command, command.getCategoryId(), command.getItemId()),
        new CategoryUpdatedEvent(command, command.getCategoryId()),
        new MenuUpdatedEvent(command));
  }

  Category getCategory(Menu menu, UpdateItemCommand command) {
    return findCategory(menu, command.getCategoryId())
        .orElseThrow(() -> new CategoryDoesNotExistException(command, command.getCategoryId()));
  }

  Item getItem(Category category, UpdateItemCommand command) {
    return findItem(category, command.getItemId())
        .orElseThrow(
            () ->
                new ItemDoesNotExistsException(
                    command, command.getCategoryId(), command.getItemId()));
  }
}
