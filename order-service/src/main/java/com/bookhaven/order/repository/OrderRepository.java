package com.bookhaven.order.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bookhaven.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderRepository extends BaseMapper<Order> {
}
