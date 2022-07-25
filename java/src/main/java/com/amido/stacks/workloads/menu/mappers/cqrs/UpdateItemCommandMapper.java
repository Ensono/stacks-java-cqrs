package com.amido.stacks.workloads.menu.mappers.cqrs;

import com.amido.stacks.core.mapping.BaseMapper;
import com.amido.stacks.core.mapping.MapperUtils;
import com.amido.stacks.workloads.menu.commands.UpdateItemCommand;
import com.amido.stacks.workloads.menu.domain.Item;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(
    componentModel = "spring",
    uses = {MapperUtils.class},
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UpdateItemCommandMapper extends BaseMapper<UpdateItemCommand, Item> {}
