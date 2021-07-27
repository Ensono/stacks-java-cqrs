package com.xxAMIDOxx.xxSTACKSxx.menu.api.v1.impl;

import static com.xxAMIDOxx.xxSTACKSxx.menu.mappers.RequestToCommandMapper.map;

import com.xxAMIDOxx.xxSTACKSxx.menu.api.v1.UpdateItemController;
import com.xxAMIDOxx.xxSTACKSxx.menu.api.v1.dto.request.UpdateItemRequest;
import com.xxAMIDOxx.xxSTACKSxx.menu.api.v1.dto.response.ResourceUpdatedResponse;
import com.xxAMIDOxx.xxSTACKSxx.menu.handlers.UpdateItemHandler;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/** @author ArathyKrishna */
@RestController
public class UpdateItemControllerImpl implements UpdateItemController {

  private UpdateItemHandler handler;

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
            handler.handle(map(correlationId, menuId, categoryId, itemId, body)).get()),
        HttpStatus.OK);
  }
}
