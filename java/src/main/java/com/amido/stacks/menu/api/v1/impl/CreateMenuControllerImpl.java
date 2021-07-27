package com.amido.stacks.menu.api.v1.impl;

import com.amido.stacks.menu.api.v1.CreateMenuController;
import com.amido.stacks.menu.api.v1.dto.request.CreateMenuRequest;
import com.amido.stacks.menu.api.v1.dto.response.ResourceCreatedResponse;
import com.amido.stacks.menu.handlers.CreateMenuHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.amido.stacks.menu.mappers.RequestToCommandMapper.map;

@RestController
public class CreateMenuControllerImpl implements CreateMenuController {

  private CreateMenuHandler createMenuHandler;

  public CreateMenuControllerImpl(CreateMenuHandler createMenuHandler) {
    this.createMenuHandler = createMenuHandler;
  }

  @Override
  public ResponseEntity<ResourceCreatedResponse> createMenu(
      @Valid CreateMenuRequest body, String correlationId) {
    return new ResponseEntity<>(
        new ResourceCreatedResponse(createMenuHandler.handle(map(correlationId, body)).get()),
        HttpStatus.CREATED);
  }
}
