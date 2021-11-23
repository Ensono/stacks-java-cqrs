package com.amido.stacks.workloads.menu.handlers;

import com.amido.stacks.workloads.menu.commands.DeleteCategoryCommand;
import com.amido.stacks.workloads.menu.domain.Category;
import com.amido.stacks.workloads.menu.domain.Menu;
import com.amido.stacks.workloads.menu.exception.CategoryDoesNotExistException;
import com.amido.stacks.workloads.menu.repository.MenuRepository;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

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

  Category getCategory(Menu menu, DeleteCategoryCommand command) {
    return findCategory(menu, command.getCategoryId())
        .orElseThrow(() -> new CategoryDoesNotExistException(command, command.getCategoryId()));
  }
}
