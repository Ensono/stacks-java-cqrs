package com.amido.workloads.menu.handlers;

import com.amido.workloads.menu.commands.DeleteItemCommand;
import com.amido.workloads.menu.domain.Category;
import com.amido.workloads.menu.domain.Item;
import com.amido.workloads.menu.domain.Menu;
import com.amido.workloads.menu.exception.CategoryDoesNotExistException;
import com.amido.workloads.menu.exception.ItemDoesNotExistsException;
import com.amido.workloads.menu.repository.MenuRepository;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** @author ArathyKrishna */
@Component
public class DeleteItemHandler extends MenuBaseCommandHandler<DeleteItemCommand> {

  public DeleteItemHandler(MenuRepository menuRepository) {
    super(menuRepository);
  }

  @Override
  Optional<UUID> handleCommand(Menu menu, DeleteItemCommand command) {
    Category category = getCategory(menu, command);
    Item item = getItem(category, command);

    List<Item> itemList =
        category.getItems().stream()
            .filter(t -> !Objects.equals(t, item))
            .collect(Collectors.toList());
    category.setItems(!itemList.isEmpty() ? itemList : Collections.emptyList());

    menuRepository.save(menu.addOrUpdateCategory(category));

    return Optional.empty();
  }

  Category getCategory(Menu menu, DeleteItemCommand command) {
    return findCategory(menu, command.getCategoryId())
        .orElseThrow(() -> new CategoryDoesNotExistException(command, command.getCategoryId()));
  }

  Item getItem(Category category, DeleteItemCommand command) {
    return findItem(category, command.getItemId())
        .orElseThrow(
            () ->
                new ItemDoesNotExistsException(
                    command, command.getCategoryId(), command.getItemId()));
  }
}
