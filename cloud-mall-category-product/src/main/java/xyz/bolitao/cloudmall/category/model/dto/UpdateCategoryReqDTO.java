package xyz.bolitao.cloudmall.category.model.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author boli.tao
 */
public class UpdateCategoryReqDTO {
    @NotNull(message = "ID 不能为 null")
    private Integer id;

    @Size(min = 2, max = 5)
    private String name;

    @Max(3)
    private Integer type;

    private Integer parentId;

    private Integer orderNum;

    @Override
    public String toString() {
        return "UpdateCategoryReqDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", parentId=" + parentId +
                ", orderNum=" + orderNum +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }
}
