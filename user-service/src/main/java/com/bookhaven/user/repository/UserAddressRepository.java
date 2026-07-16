package com.bookhaven.user.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bookhaven.user.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserAddressRepository extends BaseMapper<UserAddress> {
}
