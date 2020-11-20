package cn.scut.mall.order.vo;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 订单确认页需要用的数据
 */

public class OrderConfirmVo {
    //收货地址`ums_member_receive_address`
    List<MemberAddressVo> address;

    //所有选中的购物项
    List<OrderItemVo> items;

    //发票信息---这里不写了
    //优惠卷信息---》当前会员可用的优惠信息
    Integer integration;

    BigDecimal total;//订单总额
    BigDecimal payPrice;//应付金额

    Integer count;//一共有几件

    //防重令牌
    String orderToken;

    //库存信息
    Map<Long,Boolean> stocks;

    public void setCount(Integer count) {
        this.count = count;
    }

    public Map<Long, Boolean> getStocks() {
        return stocks;
    }

    public void setStocks(Map<Long, Boolean> stocks) {
        this.stocks = stocks;
    }

    public Integer getCount() {
        Integer sum = 0;
        if(!CollectionUtils.isEmpty(items)){
            for (OrderItemVo item : items) {
                sum += item.getCount();
            }
        }
        return sum;
    }



    public String getOrderToken() {
        return orderToken;
    }

    public void setOrderToken(String orderToken) {
        this.orderToken = orderToken;
    }


    public List<MemberAddressVo> getAddress() {
        return address;
    }

    public void setAddress(List<MemberAddressVo> address) {
        this.address = address;
    }

    public List<OrderItemVo> getItems() {
        return items;
    }

    public void setItems(List<OrderItemVo> items) {
        this.items = items;
    }

    public Integer getIntegration() {
        return integration;
    }

    public void setIntegration(Integer integration) {
        this.integration = integration;
    }

    public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal("0");
        if (items != null) {
            for (OrderItemVo item : items) {
                BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount()));
                sum = sum.add(multiply);
            }
        }
        return sum;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getPayPrice() {
        return getTotal();
    }

    public void setPayPrice(BigDecimal payPrice) {
        this.payPrice = payPrice;
    }
}
