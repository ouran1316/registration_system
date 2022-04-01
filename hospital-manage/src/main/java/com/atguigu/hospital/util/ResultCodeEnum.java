package com.atguigu.hospital.util;
import lombok.Getter;

/**
 * 统一返回结果状态信息类
 *
 * @author qy
 */
@Getter
public enum ResultCodeEnum {

    SUCCESS(200,"成功"),
    FAIL(201, "失败"),
    SERVICE_ERROR(202, "服务异常"),
    DATA_ERROR(204, "数据异常"),

    SIGN_ERROR(300, "签名错误"),

    PAY_PASSWORD_ERROR(401, "支付密码错误"),
    REPEAT_ERROR(402, "重复提交"),

    INVEST_AMMOUNT_MORE_ERROR(501, "出借金额已经多余标的金额"),
    RETURN_AMMOUNT_MORE_ERROR(502, "还款金额不正确"),
    PROJECT_AMMOUNT_ERROR(503, "标的金额不一致"),

    VALIDATE_CODE_ERROR(601, "验证码错误"),
    VALIDATE_CODE_TIMEOUT(602, "验证码超时"),

    USER_LOGIN_ERROR(211, "账号或密码错误"),

    USER_NOT_LOGGER(801, "用户未登录"),
    USER_NOT_AUTHENTICATE(802, "权限不足"),

    ORDER_ERROR(901, "预约失败，场地数量不足，请重新预约")
    ;

    private Integer code;

    private String message;

    private ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 通过 code 获取枚举
     * @param code
     * @return
     */
    public static ResultCodeEnum getResultCodeEnum(Integer code) {
        for (ResultCodeEnum resultCodeEnum : ResultCodeEnum.values()) {
            if (resultCodeEnum.code.equals(code)) {
                return resultCodeEnum;
            }
        }
        return null;
    }
}
