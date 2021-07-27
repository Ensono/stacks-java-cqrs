package com.amido.stacks.menu.api.v1.impl;

import com.amido.stacks.menu.api.v1.CreateCategoryController;
import com.amido.stacks.menu.api.v1.dto.request.CreateCategoryRequest;
import com.amido.stacks.menu.api.v1.dto.response.ResourceCreatedResponse;
import com.amido.stacks.menu.handlers.CreateCategoryHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

import static com.amido.stacks.menu.mappers.RequestToCommandMapper.map;

@RestController
public class CreateCategoryControllerImpl implements CreateCategoryController {

  private CreateCategoryHandler createCategoryHandler;

  public CreateCategoryControllerImpl(CreateCategoryHandler createCategoryHandler) {
    this.createCategoryHandler = createCategoryHandler;
  }

  @Override
  public ResponseEntity<ResourceCreatedResponse> addMenuCategory(
      UUID menuId, @Valid CreateCategoryRequest body, String correlationId) {

    return new ResponseEntity<>(
        new ResourceCreatedResponse(
            createCategoryHandler.handle(map(correlationId, menuId, body)).get()),
        HttpStatus.CREATED);
  }
}
