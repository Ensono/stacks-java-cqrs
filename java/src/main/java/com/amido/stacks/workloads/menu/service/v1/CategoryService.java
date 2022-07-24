package com.amido.stacks.workloads.menu.service.v1;

import com.amido.stacks.workloads.menu.commands.CreateCategoryCommand;
import com.amido.stacks.workloads.menu.commands.DeleteCategoryCommand;
import com.amido.stacks.workloads.menu.commands.UpdateCategoryCommand;
import com.amido.stacks.workloads.menu.domain.Category;
import com.amido.stacks.workloads.menu.domain.Menu;
import com.amido.stacks.workloads.menu.exception.CategoryDoesNotExistException;
import com.amido.stacks.workloads.menu.mappers.cqrs.CreateCategoryCommandMapper;
import com.amido.stacks.workloads.menu.mappers.cqrs.UpdateCategoryCommandMapper;
import com.amido.stacks.workloads.menu.repository.MenuRepository;
import com.amido.stacks.workloads.menu.service.v1.utility.MenuHelperService;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final MenuHelperService menuHelperServiceV1;
  protected final MenuRepository menuRepository;
  private CreateCategoryCommandMapper createCategoryCommandMapper;
  private UpdateCategoryCommandMapper updateCategoryCommandMapper;

  public void create(Menu menu, CreateCategoryCommand command) {
    Category category = createCategoryCommandMapper.fromDto(command);
    category.setId(UUID.randomUUID().toString());
    menuHelperServiceV1.addOrUpdateCategory(menu, category);
    menuRepository.save(menu);
  }

  public void delete(Menu menu, DeleteCategoryCommand command) {
    Optional<Category> optCategory =
        menu.getCategories().stream()
            .filter(c -> command.getCategoryId().toString().equals(c.getId()))
            .findFirst();

    if (optCategory.isEmpty()) {
      throw new CategoryDoesNotExistException(command, command.getCategoryId());
    }

    menuHelperServiceV1.removeCategory(menu, command.getCategoryId());
    menuRepository.save(menu);

  }

  public void update(Menu menu, UpdateCategoryCommand command) {

//check by Id
    Category category = menuHelperServiceV1.checkCategoryExistsById(command, menu,
        command.getCategoryId());
    //Check By name
    menuHelperServiceV1.verifyCategoryNameNotAlreadyExisting(command, menu, command.getCategoryId(),
        command.getName());

    Category updatedCategory = updateCategoryCommandMapper.fromDto(command);
    updatedCategory.setItems(category.getItems());
    menuHelperServiceV1.addOrUpdateCategory(menu, updatedCategory);
    menuRepository.save(menu);

  }
}
