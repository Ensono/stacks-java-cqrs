package com.amido.stacks.core.messaging.event;

import static java.time.ZonedDateTime.now;

import com.amido.stacks.core.operations.OperationContext;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

public abstract class ApplicationEvent extends OperationContext implements Serializable {

  private UUID id;
  private int eventCode;
  private ZonedDateTime creationDate;

  public ApplicationEvent(int operationCode, String correlationId, int eventCode) {
    super(operationCode, correlationId);
    this.eventCode = eventCode;
    this.id = UUID.randomUUID();
    this.creationDate = now();
  }

  public UUID getId() {
    return id;
  }

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSZ")
  public ZonedDateTime getCreationDate() {
    return creationDate;
  }

  public int getEventCode() {
    return eventCode;
  }

  @Override
  public String toString() {
    return "ApplicationEvent{"
        + "id="
        + id
        + ", eventCode="
        + eventCode
        + ", creationDate="
        + creationDate
        + "} "
        + super.toString();
  }
}
