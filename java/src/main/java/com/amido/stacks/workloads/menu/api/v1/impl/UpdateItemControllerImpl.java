package com.amido.stacks.workloads.menu.api.v1.impl;

import com.amido.stacks.core.api.dto.response.ResourceUpdatedResponse;
import com.amido.stacks.workloads.menu.api.v1.UpdateItemController;
import com.amido.stacks.workloads.menu.api.v1.dto.request.UpdateItemRequest;
import com.amido.stacks.workloads.menu.handlers.UpdateItemHandler;
import com.amido.stacks.workloads.menu.mappers.RequestToCommandMapper;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UpdateItemControllerImpl implements UpdateItemController {

  private UpdateItemHandler handler;

  @Autowired private RequestToCommandMapper requestToCommandMapper;

  public UpdateItemControllerImpl(UpdateItemHandler handler) {
    this.handler = handler;
  }

  @Override
  public ResponseEntity<ResourceUpdatedResponse> updateItem(
      UUID menuId,
      UUID categoryId,
      UUID itemId,
      @Valid UpdateItemRequest body,
      String correlationId) {
    return new ResponseEntity<>(
        new ResourceUpdatedResponse(
            handler
                .handle(requestToCommandMapper.map(correlationId, menuId, categoryId, itemId, body))
                .orElseThrow()),
        HttpStatus.OK);
  }
}
