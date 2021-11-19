package com.amido.workloads.menu.handlers;

import com.amido.workloads.menu.commands.CreateItemCommand;
import com.amido.workloads.menu.domain.Category;
import com.amido.workloads.menu.domain.Item;
import com.amido.workloads.menu.domain.Menu;
import com.amido.workloads.menu.exception.CategoryDoesNotExistException;
import com.amido.workloads.menu.exception.ItemAlreadyExistsException;
import com.amido.workloads.menu.repository.MenuRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

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
