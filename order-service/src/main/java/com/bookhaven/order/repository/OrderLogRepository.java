package com.bookhaven.order.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bookhaven.order.entity.OrderLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderLogRepository extends BaseMapper<OrderLog> {
}
