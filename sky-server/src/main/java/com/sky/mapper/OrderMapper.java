package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Orders;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {


    void insert(Orders orders);

    /**
     * 用于替换微信支付更新数据库状态的问题
     * @param orderStatus
     * @param orderPaidStatus
     */

    @Update("update orders set status = #{orderStatus},pay_status = #{orderPaidStatus} ,checkout_time = #{check_out_time} where id = #{id}")
    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime check_out_time, Long id);

    /**
     * 根据订单号查询订单
     * @param outTradeNo
     */
    @Select("select * from orders where number = #{outTraceNo}")
    Orders getByNumber(String outTradeNo);

    void update(Orders orders);
}
