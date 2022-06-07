package cn.stylefeng.guns.pay.modular.wxpay.enums;

import cn.stylefeng.guns.core.annotion.ExpEnumType;
import cn.stylefeng.guns.core.exception.enums.abs.AbstractBaseExceptionEnum;
import cn.stylefeng.guns.core.factory.ExpEnumCodeFactory;
import cn.stylefeng.guns.pay.core.consts.PayExpEnumConstant;

/**
 * 微信支付相关异常枚举
 *
 * @author xuyuxiang
 * @date 2020/8/11 16:49
 **/
@ExpEnumType(module = PayExpEnumConstant.GUNS_PAY_MODULE_EXP_CODE, kind = PayExpEnumConstant.WXPAY_EXCEPTION_ENUM)
public enum WxPayExceptionEnum implements AbstractBaseExceptionEnum {

    /**
     * 控制器需要继承AbstractWxPayApiController
     */
    CONTROLLER_ERROR(1, "控制器需要继承AbstractWxPayApiController");

    private final Integer code;

    private final String message;

    WxPayExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return ExpEnumCodeFactory.getExpEnumCode(this.getClass(), code);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
