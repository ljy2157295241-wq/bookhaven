package com.bookhaven.cart.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bookhaven.cart.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartRepository extends BaseMapper<CartItem> {
}
