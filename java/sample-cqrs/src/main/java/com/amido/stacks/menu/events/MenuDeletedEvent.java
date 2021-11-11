package com.amido.stacks.menu.events;

import com.amido.stacks.menu.commands.MenuCommand;

/** @author ArathyKrishna */
public class MenuDeletedEvent extends MenuEvent {

  private static final int EVENT_CODE = 103;

  public MenuDeletedEvent(MenuCommand command) {
    super(command);
  }

  @Override
  public int getEventCode() {
    return EVENT_CODE;
  }
}
