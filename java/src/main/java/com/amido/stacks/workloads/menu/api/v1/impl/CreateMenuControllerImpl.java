package com.amido.stacks.workloads.menu.api.v1.impl;

import com.amido.stacks.workloads.menu.api.v1.CreateMenuController;
import com.amido.stacks.workloads.menu.api.v1.dto.request.CreateMenuRequest;
import com.amido.stacks.workloads.menu.api.v1.dto.response.ResourceCreatedResponse;
import com.amido.stacks.workloads.menu.handlers.CreateMenuHandler;
import com.amido.stacks.workloads.menu.mappers.RequestToCommandMapper;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreateMenuControllerImpl implements CreateMenuController {

  private CreateMenuHandler createMenuHandler;

  @Autowired private RequestToCommandMapper requestToCommandMapper;

  public CreateMenuControllerImpl(CreateMenuHandler createMenuHandler) {
    this.createMenuHandler = createMenuHandler;
  }

  @Override
  public ResponseEntity<ResourceCreatedResponse> createMenu(
      @Valid CreateMenuRequest body, String correlationId) {
    return new ResponseEntity<>(
        new ResourceCreatedResponse(
            createMenuHandler
                .handle(requestToCommandMapper.map(correlationId, body))
                .orElseThrow()),
        HttpStatus.CREATED);
  }
}
