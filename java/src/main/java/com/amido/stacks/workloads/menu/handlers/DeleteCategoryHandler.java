package com.amido.stacks.workloads.menu.handlers;

import com.amido.stacks.workloads.menu.commands.DeleteCategoryCommand;
import com.amido.stacks.workloads.menu.domain.Menu;
import com.amido.stacks.workloads.menu.service.v1.CategoryService;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author ArathyKrishna
 */
@RequiredArgsConstructor
@Component
public class DeleteCategoryHandler extends MenuBaseCommandHandler<DeleteCategoryCommand> {

  protected CategoryService categoryService;

  public DeleteCategoryHandler(
      CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  Optional<UUID> handleCommand(Menu menu, DeleteCategoryCommand command) {

    categoryService.delete(menu, command);
    return Optional.empty();
  }


  @Override
  public Optional<UUID> handle(DeleteCategoryCommand deleteCategoryCommand) {
    return Optional.empty();
  }
}
