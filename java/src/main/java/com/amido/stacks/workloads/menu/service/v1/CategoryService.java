package com.amido.stacks.workloads.menu.service.v1;

import com.amido.stacks.workloads.menu.commands.CreateCategoryCommand;
import com.amido.stacks.workloads.menu.commands.DeleteCategoryCommand;
import com.amido.stacks.workloads.menu.commands.UpdateCategoryCommand;
import com.amido.stacks.workloads.menu.domain.Category;
import com.amido.stacks.workloads.menu.domain.Menu;
import com.amido.stacks.workloads.menu.repository.MenuRepository;
import com.amido.stacks.workloads.menu.service.v1.utility.MenuHelperService;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final MenuHelperService menuHelperService;
  protected final MenuRepository menuRepository;

  public void create(Menu menu, CreateCategoryCommand command) {

    command.setCategoryId(UUID.randomUUID());
    menu.setCategories(menuHelperService.addCategory(menu, command));
    menuRepository.save(menu);
  }

  public void delete(Menu menu, DeleteCategoryCommand command) {
    Category category = menuHelperService.getCategory(menu, command);
    List<Category> collect =
        menu.getCategories().stream()
            .filter(t -> !Objects.equals(t, category))
            .collect(Collectors.toList());
    menu.setCategories(!collect.isEmpty() ? collect : Collections.emptyList());
    menuRepository.save(menu);
  }

  public void update(Menu menu, UpdateCategoryCommand command) {
    menu.addOrUpdateCategory(menuHelperService.updateCategory(menu, command));
    menuRepository.save(menu);
  }
}
