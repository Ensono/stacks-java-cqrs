package com.amido.stacks.workloads.menu.domain.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.amido.stacks.workloads.menu.domain.Menu;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Unit")
class MenuHelperTest {

  @Test
  void shouldCreateMenus() {

    List<Menu> menus = MenuHelper.createMenus(5);

    assertEquals(5, menus.size());
  }
}
