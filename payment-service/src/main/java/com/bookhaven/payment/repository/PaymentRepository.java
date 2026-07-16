package com.bookhaven.payment.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bookhaven.payment.entity.Payment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentRepository extends BaseMapper<Payment> {
}
