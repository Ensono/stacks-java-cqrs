package com.amido.workloads.menu.api.v1.impl;

import static org.springframework.http.HttpStatus.OK;

import com.amido.workloads.menu.api.v1.DeleteItemController;
import com.amido.workloads.menu.commands.DeleteItemCommand;
import com.amido.workloads.menu.handlers.DeleteItemHandler;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/** @author ArathyKrishna */
@RestController
public class DeleteItemControllerImpl implements DeleteItemController {

  private DeleteItemHandler handler;

  public DeleteItemControllerImpl(DeleteItemHandler handler) {
    this.handler = handler;
  }

  @Override
  public ResponseEntity<Void> deleteItem(
      UUID menuId, UUID categoryId, UUID itemId, String correlationId) {
    handler.handle(new DeleteItemCommand(correlationId, menuId, categoryId, itemId));
    return new ResponseEntity<>(OK);
  }
}
