package com.amido.stacks.workloads.menu.domain.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.amido.stacks.workloads.menu.domain.Category;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Unit")
class CategoryHelperTest {

  @Test
  void shouldCreateMenus() {

    List<Category> categories = CategoryHelper.createCategories(5);

    assertEquals(5, categories.size());
  }
}
