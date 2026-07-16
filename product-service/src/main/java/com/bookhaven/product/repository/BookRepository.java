package com.bookhaven.product.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bookhaven.product.entity.Book;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookRepository extends BaseMapper<Book> {
}
