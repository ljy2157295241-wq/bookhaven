package com.bookhaven.order.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bookhaven.order.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemRepository extends BaseMapper<OrderItem> {
}
