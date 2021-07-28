package com.amido.stacks.menu.handlers;

import com.amido.stacks.menu.commands.DeleteCategoryCommand;
import com.amido.stacks.menu.domain.Category;
import com.amido.stacks.menu.domain.Menu;
import com.amido.stacks.menu.events.CategoryDeletedEvent;
import com.amido.stacks.menu.events.MenuEvent;
import com.amido.stacks.menu.events.MenuUpdatedEvent;
import com.amido.stacks.menu.exception.CategoryDoesNotExistException;
import com.amido.stacks.menu.repository.MenuRepository;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/** @author ArathyKrishna */
@Component
public class DeleteCategoryHandler extends MenuBaseCommandHandler<DeleteCategoryCommand> {

  public DeleteCategoryHandler(MenuRepository menuRepository) {
    super(menuRepository);
  }

  Optional<UUID> handleCommand(Menu menu, DeleteCategoryCommand command) {
    Category category = getCategory(menu, command);
    List<Category> collect =
        menu.getCategories().stream()
            .filter(t -> !Objects.equals(t, category))
            .collect(Collectors.toList());
    menu.setCategories(!collect.isEmpty() ? collect : Collections.emptyList());
    menuRepository.save(menu);
    return Optional.empty();
  }

  List<MenuEvent> raiseApplicationEvents(Menu menu, DeleteCategoryCommand command) {
    return Arrays.asList(
        new MenuUpdatedEvent(command), new CategoryDeletedEvent(command, command.getCategoryId()));
  }

  Category getCategory(Menu menu, DeleteCategoryCommand command) {
    return findCategory(menu, command.getCategoryId())
        .orElseThrow(() -> new CategoryDoesNotExistException(command, command.getCategoryId()));
  }
}
