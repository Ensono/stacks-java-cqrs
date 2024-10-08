package com.amido.stacks.workloads.menu.api.v1.dto.response;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Unit")
class ItemDTOTest {

  @Test
  void equalsContract() {
    EqualsVerifier.simple().forClass(ItemDTO.class).verify();
  }
}
