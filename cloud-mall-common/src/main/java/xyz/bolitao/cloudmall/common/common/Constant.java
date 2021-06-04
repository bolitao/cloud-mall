package xyz.bolitao.cloudmall.common.common;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import xyz.bolitao.cloudmall.common.exception.ImoocMallException;
import xyz.bolitao.cloudmall.common.exception.ImoocMallExceptionEnum;

import java.util.Set;

/**
 * @author boli.tao
 */
@Component
public class Constant {
    public static final String SALT = "asdzxc8324asdrf[]werfgnmfh/.fghfghi32d";

    public static final String IMOOC_MALL_USER = "imooc_mall_user";

    public interface ProductListOrderBy {
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price desc", "price asc");
    }

    public interface SaleStatus {
        int NOT_SALE = 0;
        int SALE = 1;
    }

    public interface Cart {
        int UN_SELECTED = 0;
        int SELECTED = 1;
        Set<Integer> CART_CHECK_OR_NOT = Sets.newHashSet(UN_SELECTED, SELECTED);
    }

    public enum OrderStatusEnum {
        CANCELED(0, "用户已取消"),
        NOT_PAID(10, "未付款"),
        PAID(20, "已付款"),
        DELIVERED(30, "已发货"),
        FINISHED(40, "交易完成");


        /**
         * 业务码
         */
        private Integer code;

        /**
         * 业务说明
         */
        private String value;

        public static OrderStatusEnum codeOf(int code) {
            // TODO: com
            for (OrderStatusEnum orderStatusEnum : values()) {
                if (orderStatusEnum.getCode() == code) {
                    return orderStatusEnum;
                }
            }
            throw new ImoocMallException(ImoocMallExceptionEnum.NO_SUCH_ENUM);
        }

        OrderStatusEnum(Integer code, String value) {
            this.code = code;
            this.value = value;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
