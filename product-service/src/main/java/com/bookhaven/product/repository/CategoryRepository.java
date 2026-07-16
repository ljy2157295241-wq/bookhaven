package com.bookhaven.product.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bookhaven.product.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryRepository extends BaseMapper<Category> {
}
