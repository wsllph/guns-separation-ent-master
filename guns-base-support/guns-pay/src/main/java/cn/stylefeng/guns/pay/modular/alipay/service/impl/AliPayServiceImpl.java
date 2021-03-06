package cn.stylefeng.guns.pay.modular.alipay.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.stylefeng.guns.core.context.constant.ConstantContextHolder;
import cn.stylefeng.guns.core.exception.ServiceException;
import cn.stylefeng.guns.pay.core.consts.AliPayConstant;
import cn.stylefeng.guns.pay.core.pojo.AliPayBean;
import cn.stylefeng.guns.pay.core.utils.AliPayHandleErrorUtil;
import cn.stylefeng.guns.pay.core.utils.AliPayModelGenUtil;
import cn.stylefeng.guns.pay.modular.alipay.entity.AliPayTradeHistory;
import cn.stylefeng.guns.pay.modular.alipay.enums.AliPayExceptionEnum;
import cn.stylefeng.guns.pay.modular.alipay.enums.AliPayTradeHistoryStatusEnum;
import cn.stylefeng.guns.pay.modular.alipay.factory.AliPayTradeHistoryFactory;
import cn.stylefeng.guns.pay.modular.alipay.param.AliPayParam;
import cn.stylefeng.guns.pay.modular.alipay.service.AliPayService;
import cn.stylefeng.guns.pay.modular.alipay.service.AliPayTradeHistoryService;
import com.alipay.api.AlipayConstants;
import com.alipay.api.domain.AlipayTradeCreateModel;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.ijpay.alipay.AliPayApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付宝支付service接口实现类
 *
 * @author xuyuxiang
 * @date 2020/8/11 15:57
 **/
@Service
public class AliPayServiceImpl implements AliPayService {

    private static final Log log = Log.get();

    @Resource
    private AliPayBean aliPayBean;

    @Resource
    private AliPayTradeHistoryService aliPayTradeHistoryService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void pcPay(AliPayParam aliPayParam, HttpServletResponse response) {
        try {
            //生成支付宝pc支付的model
            AlipayTradePagePayModel aliPayTradePagePayModel = AliPayModelGenUtil.getAliPayTradePagePayModel(aliPayParam);

            //将支付宝model转为交易记录model
            AliPayTradeHistory aliPayTradeHistory = AliPayTradeHistoryFactory.genAliPayTradeHistoryModelByPcPay(aliPayTradePagePayModel);

            //保存交易记录
            aliPayTradeHistoryService.add(aliPayTradeHistory);

            AliPayApi.tradePage(response, aliPayTradePagePayModel,
                    AliPayModelGenUtil.getNotifyUrl(aliPayBean),
                    AliPayModelGenUtil.getReturnUrl(aliPayBean));
        } catch (Exception e) {
            log.error(">>> 支付宝支付异常：{}", e.getMessage());
            throw new ServiceException(AliPayExceptionEnum.PAY_ERROR.getCode(), e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void tradePreCreatePay(AliPayParam aliPayParam, HttpServletResponse response) {
        try {
            //生成支付宝扫码支付的model
            AlipayTradePrecreateModel aliPayTradePreCreateModel = AliPayModelGenUtil.getAliPayTradePreCreateModel(aliPayParam);

            //将支付宝model转为交易记录model
            AliPayTradeHistory aliPayTradeHistory = AliPayTradeHistoryFactory.genAliPayTradeHistoryModelByTradePreCreatePay(aliPayTradePreCreateModel);

            //保存交易记录
            aliPayTradeHistoryService.add(aliPayTradeHistory);

            String resultStr = AliPayApi.tradePrecreatePayToResponse(aliPayTradePreCreateModel,
                    AliPayModelGenUtil.getNotifyUrl(aliPayBean)).getBody();
            JSONObject responseObj = this.doHandleWithJsonObject(resultStr, AliPayConstant.PRECREATE_PAY_RESPONSE);
            //处理错误结果
            AliPayHandleErrorUtil.handelErrorResponse(responseObj);
            String qrCodeUrl = responseObj.getStr(AliPayConstant.PRECREATE_PAY_QR_CODE);
            byte[] bytes = QrCodeUtil.generatePng(qrCodeUrl, QrConfig.create());
            IoUtil.write(response.getOutputStream(), true, bytes);
        } catch (Exception e) {
            log.error(">>> 支付宝支付异常：{}", e.getMessage());
            throw new ServiceException(AliPayExceptionEnum.PAY_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public void wapPay(AliPayParam aliPayParam, HttpServletResponse response) {
        try {
            //生成支付宝wap支付的model
            AlipayTradeWapPayModel aliPayTradeWapPayModel = AliPayModelGenUtil.getAliPayTradeWapPayModel(aliPayParam);

            //将支付宝model转为交易记录model
            AliPayTradeHistory aliPayTradeHistory = AliPayTradeHistoryFactory.genAliPayTradeHistoryModelByWapPay(aliPayTradeWapPayModel);

            //保存交易记录
            aliPayTradeHistoryService.add(aliPayTradeHistory);

            AliPayApi.wapPay(response, aliPayTradeWapPayModel,
                    AliPayModelGenUtil.getNotifyUrl(aliPayBean),
                    AliPayModelGenUtil.getReturnUrl(aliPayBean));
        } catch (Exception e) {
            log.error(">>> 支付宝支付异常：{}", e.getMessage());
            throw new ServiceException(AliPayExceptionEnum.PAY_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public Dict transfer(AliPayParam aliPayParam) {
        try {
            String resultStr = AliPayApi.transferToResponse(AliPayModelGenUtil.getAliPayFundTransToAccountTransferModel(aliPayParam)).getBody();
            return this.doHandleWithDict(resultStr, AliPayConstant.TRANSFER_RESPONSE);
        } catch (Exception e) {
            log.error(">>> 支付宝支付异常：{}", e.getMessage());
            throw new ServiceException(AliPayExceptionEnum.PAY_ERROR.getCode(), e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Dict tradeRefund(AliPayParam aliPayParam) {
        try {
            //将商户对应的订单状态改为已关闭有退款
            aliPayTradeHistoryService.changeStatus(aliPayParam.getOutTradeNo(), null, AliPayTradeHistoryStatusEnum.CLOSED_WITH_REFUND.getCode());
            String resultStr = AliPayApi.tradeRefundToResponse(AliPayModelGenUtil.getAliPayTradeRefundModel(aliPayParam)).getBody();
            return this.doHandleWithDict(resultStr, AliPayConstant.TRADE_REFUND_RESPONSE);
        } catch (Exception e) {
            log.error(">>> 支付宝支付异常：{}", e.getMessage());
            throw new ServiceException(AliPayExceptionEnum.PAY_ERROR.getCode(), e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Dict tradeCreate(AliPayParam aliPayParam) {
        try {
            AlipayTradeCreateModel aliPayTradeCreateModel = AliPayModelGenUtil.getAliPayTradeCreateModel(aliPayParam);

            //生成支付宝扫码支付的model
            AlipayTradePrecreateModel aliPayTradePreCreateModel = AliPayModelGenUtil.getAliPayTradePreCreateModel(aliPayParam);

            //将支付宝model转为交易记录model
            AliPayTradeHistory aliPayTradeHistory = AliPayTradeHistoryFactory.genAliPayTradeHistoryModelByTradePreCreatePay(aliPayTradePreCreateModel);

            //保存交易记录
            aliPayTradeHistoryService.add(aliPayTradeHistory);

            String resultStr = AliPayApi.tradeCreateToResponse(aliPayTradeCreateModel,
                    AliPayModelGenUtil.getNotifyUrl(aliPayBean)).getBody();
            return this.doHandleWithDict(resultStr, AliPayConstant.TRADE_CREATED_RESPONSE);
        } catch (Exception e) {
            log.error(">>> 支付宝支付异常：{}", e.getMessage());
            throw new ServiceException(AliPayExceptionEnum.PAY_ERROR.getCode(), e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Dict tradeCancel(AliPayParam aliPayParam) {
        try {
            //将商户对应的订单状态改为已取消
            aliPayTradeHistoryService.changeStatus(aliPayParam.getOutTradeNo(), null, AliPayTradeHistoryStatusEnum.CANCELED.getCode());

            String resultStr = AliPayApi.tradeCancelToResponse(AliPayModelGenUtil.getAliPayTradeCancelModel(aliPayParam)).getBody();
            return this.doHandleWithDict(resultStr, AliPayConstant.TRADE_CANCEL_RESPONSE);
        } catch (Exception e) {
            log.error(">>> 支付宝支付异常：{}", e.getMessage());
            throw new ServiceException(AliPayExceptionEnum.PAY_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public Dict tradeClose(AliPayParam aliPayParam) {
        try {

            //将商户对应的订单状态改为已关闭
            aliPayTradeHistoryService.changeStatus(aliPayParam.getOutTradeNo(), null, AliPayTradeHistoryStatusEnum.CLOSED.getCode());

            String resultStr = AliPayApi.tradeCloseToResponse(AliPayModelGenUtil.getAliPayTradeCloseModel(aliPayParam)).getBody();
            return this.doHandleWithDict(resultStr, AliPayConstant.TRADE_CLOSE_RESPONSE);
        } catch (Exception e) {
            log.error(">>> 支付宝支付异常：{}", e.getMessage());
            throw new ServiceException(AliPayExceptionEnum.PAY_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public Dict tradeOrderSettle(AliPayParam aliPayParam) {
        try {
            String resultStr = AliPayApi.tradeOrderSettleToResponse(AliPayModelGenUtil.getAliPayTradeOrderSettleModel(aliPayParam)).getBody();
            return this.doHandleWithDict(resultStr, AliPayConstant.TRADE_ORDER_SETTLE_RESPONSE);
        } catch (Exception e) {
            log.error(">>> 支付宝支付异常：{}", e.getMessage());
            throw new ServiceException(AliPayExceptionEnum.PAY_ERROR.getCode(), e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String certNotify(HttpServletRequest request) {
        try {
            // 获取支付宝POST过来的反馈信息
            Map<String, String> result = AliPayApi.toMap(request);

            //验证签名
            boolean verifyResult = AlipaySignature.rsaCertCheckV1(result, aliPayBean.getAliPayCertPath(), CharsetUtil.UTF_8, AlipayConstants.SIGN_TYPE_RSA2);
            if (verifyResult) {

                // 按照支付结果异步通知中的描述，对支付结果中的业务内容进行1\2\3二次校验，校验成功后在response中返回success，校验失败返回failure
                this.checkResult(result);

                // 另起线程处理业务
                ThreadUtil.execute(() -> {
                    // 处理支付成功逻辑
                    try {
                        //获取商家订单号
                        String outTradeNo = result.get(AliPayConstant.OUT_TRADE_NO);
                        //获取支付宝订单号
                        String tradeNo = result.get(AliPayConstant.TRADE_NO);
                        //修改交易状态
                        aliPayTradeHistoryService.changeStatus(outTradeNo, tradeNo, AliPayTradeHistoryStatusEnum.PAYED.getCode());
                    } catch (Exception e) {
                        // 业务处理失败，可查看日志进行补偿，跟支付宝已经没多大关系
                        log.error(">>> 支付宝回调业务处理报错，回调信息：{}", JSONUtil.toJsonStr(result));
                    }
                });
                // 如果签名验证正确，立即返回success，后续业务另起线程单独处理
                return "success";
            } else {
                log.error(">>> 支付宝回调签名认证失败：{}");
                return "failure";
            }
        } catch (Exception e) {
            log.error(">>> 支付宝支付异常：{}", e.getMessage());
            throw new ServiceException(AliPayExceptionEnum.PAY_ERROR.getCode(), e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void certReturn(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 获取支付宝POST过来的反馈信息
            Map<String, String> result = AliPayApi.toMap(request);

            //验证签名
            boolean verifyResult = AlipaySignature.rsaCertCheckV1(result, aliPayBean.getAliPayCertPath(), CharsetUtil.UTF_8, AlipayConstants.SIGN_TYPE_RSA2);
            if (verifyResult) {

                // 按照支付结果异步通知中的描述，对支付结果中的业务内容进行1\2\3二次校验
                this.checkResult(result);

                // 另起线程处理业务
                ThreadUtil.execute(() -> {
                    // 处理支付成功逻辑
                    try {
                        //获取商家订单号
                        String outTradeNo = result.get(AliPayConstant.OUT_TRADE_NO);
                        //获取支付宝订单号
                        String tradeNo = result.get(AliPayConstant.TRADE_NO);
                        //修改交易状态
                        aliPayTradeHistoryService.changeStatus(outTradeNo, tradeNo, AliPayTradeHistoryStatusEnum.PAYED.getCode());
                    } catch (Exception e) {
                        // 业务处理失败，可查看日志进行补偿，跟支付宝已经没多大关系
                        log.error(">>> 支付宝回调业务处理报错，回调信息：{}", JSONUtil.toJsonStr(result));
                    }
                });
                //获取要转发的地址
                String returnUrl = ConstantContextHolder.getAlipayReturnUrl();
                //转发
                response.sendRedirect(returnUrl);
            } else {
                log.error(">>> 支付宝回调签名认证失败：{}");
            }
        } catch (Exception e) {
            log.error(">>> 支付宝支付异常：{}", e.getMessage());
            throw new ServiceException(AliPayExceptionEnum.PAY_ERROR.getCode(), e.getMessage());
        }
    }

    /**
     * 通用方法返回Dict
     *
     * @author xuyuxiang
     * @date 2020/8/20 10:37
     **/
    @SuppressWarnings("ALL")
    @Override
    public Dict doHandleWithDict(String resultStr, String jsonObjectKey) {
        Dict dict;
        try {
            JSONObject jsonObject = JSONUtil.parseObj(resultStr);
            JSONObject responseObj = jsonObject.getJSONObject(jsonObjectKey);
            //处理错误结果
            AliPayHandleErrorUtil.handelErrorResponse(responseObj);
            dict = new Dict(JSONUtil.toBean(responseObj, Map.class));
        } catch (Exception e) {
            log.error(">>> 支付宝支付异常：{}", e.getMessage());
            throw new ServiceException(AliPayExceptionEnum.PAY_ERROR.getCode(), e.getMessage());
        }
        return dict;
    }

    /**
     * 通用方法返回JSONObject
     *
     * @author xuyuxiang
     * @date 2020/8/20 10:37
     **/
    @Override
    public JSONObject doHandleWithJsonObject(String resultStr, String jsonObjectKey) {
        try {
            JSONObject jsonObject = JSONUtil.parseObj(resultStr);
            JSONObject responseObj = jsonObject.getJSONObject(jsonObjectKey);
            //处理错误结果
            AliPayHandleErrorUtil.handelErrorResponse(responseObj);
            return responseObj;
        } catch (Exception e) {
            log.error(">>> 支付宝支付异常：{}", e.getMessage());
            throw new ServiceException(AliPayExceptionEnum.PAY_ERROR.getCode(), e.getMessage());
        }
    }


    /**
     * <pre>
     * 第一步:验证签名,签名通过后进行第二步
     * 第二步:按以下步骤进行验证
     * 1、商户需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
     * 2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
     * 4、验证app_id是否为该商户本身。上述1、2、3有任何一个验证不通过，则表明本次通知是异常通知，务必忽略。
     * 在上述验证通过后商户必须根据支付宝不同类型的业务通知，正确的进行不同的业务处理，并且过滤重复的通知结果数据。
     * 在支付宝的业务通知中，只有交易通知状态为TRADE_SUCCESS或TRADE_FINISHED时，支付宝才会认定为买家付款成功。
     * </pre>
     *
     * @author xuyuxiang
     * @date 2020/8/20 10:37
     */
    private void checkResult(Map<String, String> result) {

        //获取商家订单号
        String outTradeNo = result.get(AliPayConstant.OUT_TRADE_NO);

        // 1、商户需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号
        aliPayTradeHistoryService.validateOutTradeNo(outTradeNo);

        //获取订单金额
        BigDecimal amount = Convert.toBigDecimal(result.get(AliPayConstant.TOTAL_AMOUNT));

        // 2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
        aliPayTradeHistoryService.validateAmount(outTradeNo, amount);

        //获取app_id
        String appId = result.get(AliPayConstant.APP_ID);

        // 3、验证app_id是否为该商户本身
        if (!appId.equals(aliPayBean.getAppId())) {
            throw new ServiceException(AliPayExceptionEnum.APP_ID_ERROR);
        }
    }
}
