package cn.stylefeng.guns.pay.modular.alipay.controller;

import cn.stylefeng.guns.core.annotion.BusinessLog;
import cn.stylefeng.guns.core.annotion.Permission;
import cn.stylefeng.guns.core.enums.LogAnnotionOpTypeEnum;
import cn.stylefeng.guns.core.pojo.response.ResponseData;
import cn.stylefeng.guns.core.pojo.response.SuccessResponseData;
import cn.stylefeng.guns.pay.core.controller.AbstractAliPayApiController;
import cn.stylefeng.guns.pay.core.pojo.AliPayBean;
import cn.stylefeng.guns.pay.core.utils.AliPayApiConfigUtil;
import cn.stylefeng.guns.pay.modular.alipay.param.AliPayParam;
import cn.stylefeng.guns.pay.modular.alipay.param.AliPayTradeHistoryParam;
import cn.stylefeng.guns.pay.modular.alipay.service.AliPayMgrService;
import cn.stylefeng.guns.pay.modular.alipay.service.AliPayTradeHistoryService;
import com.alipay.api.AlipayApiException;
import com.ijpay.alipay.AliPayApiConfig;
import com.ijpay.alipay.AliPayApiConfigKit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 支付宝支付管理控制器，用于查询订单等
 *
 * @author xuyuxiang
 * @date 2020/8/12 12:25
 **/
@RestController
public class AliPayMgrController extends AbstractAliPayApiController {

    @Resource
    private AliPayBean aliPayBean;

    @Resource
    private AliPayMgrService aliPayMgrService;

    @Resource
    private AliPayTradeHistoryService aliPayTradeHistoryService;

    @Override
    public AliPayApiConfig getApiConfig() throws AlipayApiException {
        AliPayApiConfig aliPayApiConfig;
        try {
            //先从缓存中获取
            aliPayApiConfig = AliPayApiConfigKit.getApiConfig(aliPayBean.getAppId());
        } catch (Exception e) {
            //没有则构建，普通公钥模式
            //aliPayApiConfig = AliPayApiConfigUtil.genPublicKeyApiConfig(aliPayBean);
            //没有则构建，证书模式
            aliPayApiConfig = AliPayApiConfigUtil.genCertApiConfig(aliPayBean);
        }
        return aliPayApiConfig;
    }

    /**
     * 会员信息查询
     *
     * @author xuyuxiang
     * @date 2020/8/12 10:14
     **/
    @Permission
    @GetMapping("/aliPayMgr/userInfoQuery")
    @BusinessLog(title = "支付宝支付管理_会员信息查询", opType = LogAnnotionOpTypeEnum.OTHER)
    public ResponseData userInfoQuery() {
        return new SuccessResponseData(aliPayMgrService.userInfoQuery());
    }

    /**
     * 商户账户余额查询
     *
     * @author xuyuxiang
     * @date 2020/8/12 10:14
     **/
    @Permission
    @GetMapping("/aliPayMgr/accountQuery")
    @BusinessLog(title = "支付宝支付管理_余额查询", opType = LogAnnotionOpTypeEnum.OTHER)
    public ResponseData accountQuery() {
        return new SuccessResponseData(aliPayMgrService.accountQuery());
    }

    /**
     * 交易记录查询
     *
     * @author xuyuxiang
     * @date 2020/8/17 10:29
     **/
    @Permission
    @GetMapping("/aliPayMgr/tradeHisQuery")
    @BusinessLog(title = "支付宝支付管理_交易记录查询", opType = LogAnnotionOpTypeEnum.OTHER)
    public ResponseData tradeHisQuery(AliPayTradeHistoryParam aliPayTradeHistoryParam) {
        return new SuccessResponseData(aliPayTradeHistoryService.page(aliPayTradeHistoryParam));
    }

    /**
     * 交易查询
     *
     * @author xuyuxiang
     * @date 2020/8/17 10:29
     **/
    @Permission
    @GetMapping("/aliPayMgr/tradeQuery")
    @BusinessLog(title = "支付宝支付管理_交易查询", opType = LogAnnotionOpTypeEnum.OTHER)
    public ResponseData tradeQuery(@Validated(AliPayParam.trace.class) AliPayParam aliPayParam) {
        return new SuccessResponseData(aliPayMgrService.tradeQuery(aliPayParam));
    }

    /**
     * 退款查询
     *
     * @author xuyuxiang
     * @date 2020/8/17 10:29
     **/
    @Permission
    @GetMapping("/aliPayMgr/tradeRefundQuery")
    @BusinessLog(title = "支付宝支付管理_退款查询", opType = LogAnnotionOpTypeEnum.OTHER)
    public ResponseData tradeRefundQuery(@Validated(AliPayParam.trace.class) AliPayParam aliPayParam) {
        return new SuccessResponseData(aliPayMgrService.tradeRefundQuery(aliPayParam));
    }

    /**
     * 下载对账单
     *
     * @author xuyuxiang
     * @date 2020/8/17 14:14
     **/
    @Permission
    @GetMapping("/aliPayMgr/billDownloadUrlQuery")
    @BusinessLog(title = "支付宝支付管理_下载对账单", opType = LogAnnotionOpTypeEnum.OTHER)
    public ResponseData billDownloadUrlQuery(@Validated(AliPayParam.export.class) AliPayParam aliPayParam) {
        return new SuccessResponseData(aliPayMgrService.billDownloadUrlQuery(aliPayParam));
    }
}
