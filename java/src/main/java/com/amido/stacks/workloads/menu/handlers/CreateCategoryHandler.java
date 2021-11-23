package com.amido.stacks.workloads.menu.handlers;

import com.amido.stacks.workloads.menu.commands.CreateCategoryCommand;
import com.amido.stacks.workloads.menu.domain.Category;
import com.amido.stacks.workloads.menu.domain.Menu;
import com.amido.stacks.workloads.menu.exception.CategoryAlreadyExistsException;
import com.amido.stacks.workloads.menu.repository.MenuRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CreateCategoryHandler extends MenuBaseCommandHandler<CreateCategoryCommand> {

  public CreateCategoryHandler(MenuRepository menuRepository) {
    super(menuRepository);
  }

  @Override
  Optional<UUID> handleCommand(Menu menu, CreateCategoryCommand command) {
    command.setCategoryId(UUID.randomUUID());
    menu.setCategories(addCategory(menu, command));
    menuRepository.save(menu);
    return Optional.of(command.getCategoryId());
  }

  List<Category> addCategory(Menu menu, CreateCategoryCommand command) {
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
}
