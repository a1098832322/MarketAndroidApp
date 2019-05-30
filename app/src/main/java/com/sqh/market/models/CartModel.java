package com.sqh.market.models;


/**
 * 购物车或已购买商品Model
 *
 * @author 郑龙
 */
public class CartModel {
    /**
     * 商品id
     */
    private Long id;

    /**
     * 商品名称
     */
    private String commodityName;

    /**
     * 商品类别id
     */
    private Integer commodityType;

    /**
     * 商品价格
     */
    private Double commodityPrice;

    /**
     * 商品总量
     */
    private Long commodityTotal;

    /**
     * 商品剩余
     */
    private Long commoditySurplus;

    /**
     * 数据是否被删除
     */
    private String isDeleted;

    /**
     * 商品描述
     */
    private String commodityInfo;

    /**
     * 商品图片(Base64编码)
     */
    private String commodityImg;

    /**
     * 购买数量
     */
    private Integer number;

    /**
     * 总花费
     */
    private Double totalPrice;

    /**
     * 是否选中
     */
    private boolean choosed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public Integer getCommodityType() {
        return commodityType;
    }

    public void setCommodityType(Integer commodityType) {
        this.commodityType = commodityType;
    }

    public Double getCommodityPrice() {
        return commodityPrice;
    }

    public void setCommodityPrice(Double commodityPrice) {
        this.commodityPrice = commodityPrice;
    }

    public Long getCommodityTotal() {
        return commodityTotal;
    }

    public void setCommodityTotal(Long commodityTotal) {
        this.commodityTotal = commodityTotal;
    }

    public Long getCommoditySurplus() {
        return commoditySurplus;
    }

    public void setCommoditySurplus(Long commoditySurplus) {
        this.commoditySurplus = commoditySurplus;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getCommodityInfo() {
        return commodityInfo;
    }

    public void setCommodityInfo(String commodityInfo) {
        this.commodityInfo = commodityInfo;
    }

    public String getCommodityImg() {
        return commodityImg;
    }

    public void setCommodityImg(String commodityImg) {
        this.commodityImg = commodityImg;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean isChoosed() {
        return choosed;
    }

    public void setChoosed(boolean choosed) {
        this.choosed = choosed;
    }
}