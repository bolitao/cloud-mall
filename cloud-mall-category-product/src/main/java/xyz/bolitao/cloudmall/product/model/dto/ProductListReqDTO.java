package xyz.bolitao.cloudmall.product.model.dto;

/**
 * @author boli.tao
 */
public class ProductListReqDTO {
    private String keyword;

    private Integer categoryId;

    private Integer status;

    private String orderBy;

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    @Override
    public String toString() {
        return "ProductListReqDTO{" +
                "keyword='" + keyword + '\'' +
                ", categoryId=" + categoryId +
                ", status=" + status +
                ", orderBy='" + orderBy + '\'' +
                ", pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                '}';
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
