package com.amido.stacks.menu.domain;

import com.microsoft.azure.spring.data.cosmosdb.core.mapping.Document;
import com.microsoft.azure.spring.data.cosmosdb.core.mapping.PartitionKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@Builder
@Document(collection = "Menu")
public class Menu {

  @Id @PartitionKey private String id;

  private String restaurantId;

  private String name;

  private String description;

  @Builder.Default private List<Category> categories = new ArrayList<>();

  private Boolean enabled;

  public Menu addOrUpdateCategory(Category category) {
    if (this.categories == null) {
      this.categories = new ArrayList<>();
    }
    this.categories =
        this.categories.stream()
            .filter(c -> !c.getId().equals(category.getId()))
            .collect(Collectors.toList());
    this.categories.add(category);
    return this;
  }
}
