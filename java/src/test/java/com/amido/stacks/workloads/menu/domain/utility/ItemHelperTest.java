package com.amido.stacks.workloads.menu.domain.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.amido.stacks.workloads.menu.domain.Item;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Unit")
class ItemHelperTest {

  @Test
  void shouldCreateItems() {

    List<Item> items = ItemHelper.createItems(5);

    assertEquals(5, items.size());
  }
}
