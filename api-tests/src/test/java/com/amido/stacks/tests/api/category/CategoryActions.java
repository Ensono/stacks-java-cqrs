package com.amido.stacks.tests.api.category;

import com.amido.stacks.tests.api.models.Category;
import com.amido.stacks.tests.api.models.Item;
import java.util.ArrayList;
import java.util.Map;

public class CategoryActions {

  public static Category mapToCategory(Map<String, String> properties, String id) {
    return new Category(
        id, properties.get("name"), properties.get("description"), new ArrayList<Item>());
  }
}
