package xyz.bolitao.cloudmall.common.exception;

/**
 * @author boli.tao
 */

public enum ImoocMallExceptionEnum {
    /**
     * 用户名不能为空
     */
    NEED_USERNAME(10001, "用户名不能为空"),

    /**
     * 密码不能为空
     */
    NEED_PASSWORD(10002, "用户名不能为空"),

    PASSWORD_TOO_SHORT(10003, "密码长度不得小于 8 位"),

    NAME_ALREADY_EXISTED(10004, "不允许重名"),

    DB_INSERT_FAILED(10005, "数据库插入异常，请重试"),

    WRONG_USERNAME_OR_PASSWORD(10006, "用户名或密码错误"),

    WRONG_ROLE(10007, "该用户无权限操作"),

    NEED_LOGIN(10008, "该操作需要登陆"),

    UPDATE_FAILED(10009, "更新失败"),

    PARAM_NOT_NULL(10010, "参数不能为空"),

    INSERT_FAILED(10011, "新增失败"),

    REQUEST_PARAM_ERROR(10012, "参数错误"),

    DELETE_FAILED(10013, "删除失败"),

    MKDIR_FAILED(10014, "文件夹创建失败"),

    ILLEGAL_ARGUMENT(10015, "参数有误"),

    RECORD_NOT_EXISTS(10016, "不存在此纪录"),

    PRODUCT_NOT_SELL(10017, "商品不为上架状态"),

    STOCK_NOT_ENOUGH(10018, "库存不足"),

    CART_EMPTY(10019, "购物车没有勾选的商品"),

    NO_SUCH_ENUM(10019, "该枚举不存在"),

    NO_SUCH_ORDER(10020, "订单不存在"),

    RESOURCE_NOT_PERMIT(10021, "用户无此资源访问权限"),

    WRONG_ORDER_STATUS(10022, "订单状态异常"),

    SYSTEM_EXCEPTION(20000, "系统异常");


    /**
     * code
     */
    int code;

    /**
     * msg
     */
    String msg;

    ImoocMallExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
