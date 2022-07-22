package com.amido.stacks.workloads.menu.service.v1;

import com.amido.stacks.workloads.menu.commands.CreateItemCommand;
import com.amido.stacks.workloads.menu.commands.DeleteItemCommand;
import com.amido.stacks.workloads.menu.commands.UpdateItemCommand;
import com.amido.stacks.workloads.menu.domain.Category;
import com.amido.stacks.workloads.menu.domain.Item;
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
public class ItemService {

  protected final MenuRepository menuRepository;
  private final MenuHelperService menuHelperService;

  public void create(Menu menu, CreateItemCommand createItemCommand) {
    createItemCommand.setItemId(UUID.randomUUID());
    Category category =
        menuHelperService.addItem(
            menuHelperService.getCategory(menu, createItemCommand), createItemCommand);
    menuRepository.save(menu.addOrUpdateCategory(category));
  }

  public void delete(Menu menu, DeleteItemCommand command) {

    Category category = menuHelperService.getCategory(menu, command);
    Item item = menuHelperService.getItem(category, command);

    List<Item> itemList =
        category.getItems().stream()
            .filter(t -> !Objects.equals(t, item))
            .collect(Collectors.toList());
    category.setItems(!itemList.isEmpty() ? itemList : Collections.emptyList());
    menuRepository.save(menu.addOrUpdateCategory(category));
  }

  public void update(Menu menu, UpdateItemCommand command) {
    Category category = menuHelperService.getCategory(menu, command);
    Item updated = menuHelperService.updateItem(command, category);
    menu.addOrUpdateCategory(category.addOrUpdateItem(updated));
  }
}
