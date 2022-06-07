package cn.stylefeng.guns.modular.policyFiles.aspects;

import cn.stylefeng.guns.core.util.PageUtil;
import cn.stylefeng.guns.modular.policyFiles.entity.PolicyDataConfig;
import cn.stylefeng.guns.modular.policyFiles.entity.PolicyServiceStatistics;
import cn.stylefeng.guns.modular.policyFiles.service.PolicyServiceStatisticsService;
import cn.stylefeng.guns.modular.policyFiles.util.PubUtil;
import com.alibaba.fastjson.JSON;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

@Component
@Aspect
@Transactional(rollbackFor = Exception.class)
public class PolicyServiceStatisticsAspects {
    @Autowired
    private PolicyServiceStatisticsService policyServiceStatisticsService;
    @Autowired
    private PubUtil pubUtil;
    @Autowired
    private PolicyDataConfig policyDataConfig;
    //公共的切入点表达式
    @Pointcut("execution(public * cn.stylefeng.guns.modular.policyFiles.controller.PolicyServiceStatisticsAspectsController..*(..))")
    public void pointCuts(){
    }
    //切面前置操作
    @Before("pointCuts()")
    public  void logStart(JoinPoint joinPoint)
    {
        //方法执行前获取参数
        Object[] args = joinPoint.getArgs();

        //获取切入点方法的名字
        String methodName = joinPoint.getSignature().getName();
    }
    //切面后置操作
    @After("pointCuts()")
    public  void  logEnd(JoinPoint joinPoint)
    {
        //获取切入点方法的名字
        String methodName = joinPoint.getSignature().getName();
        //获取参数名
        Object[] args=joinPoint.getArgs();
    }
    @AfterReturning(value = "execution(public * cn.stylefeng.guns.modular.policyFiles.controller.PolicyServiceStatisticsAspectsController..*(..))" , returning = "returning")
    public void doReturn(JoinPoint joinPoint,Object returning){
        String methodName = joinPoint.getSignature().getName();
        String result=String.valueOf(returning);
        PolicyServiceStatistics policyServiceStatistics=new PolicyServiceStatistics();
        //获取参数名
        Object[] args=joinPoint.getArgs();
        String orgName=String.valueOf(args[0]);
        String orgIdentification=String.valueOf(args[1]);
        String url=JSON.parseObject(JSON.toJSONString(returning)).getString("addressUrl");
        String creator=JSON.parseObject(JSON.toJSONString(returning)).getString("creator");
        policyServiceStatistics.setCreator(creator);
        policyServiceStatistics.setOrgName(orgName);
        policyServiceStatistics.setOrgIdentification(orgIdentification);
        policyServiceStatistics.setUrl(url);
        String now=pubUtil.dateTimeTurnToStr(new Date());
        policyServiceStatistics.setDockingTime(now);
        policyServiceStatistics.setCreateTime(now);
        HashMap<String,String> orgNameMap=new HashMap<String,String>();
        String localOrdepart="";
        if(orgName!=null||!"".equals(orgName)||!"null".equals(orgName)){
            orgNameMap=pubUtil.orgMap(orgName);
            boolean flag=orgNameMap.isEmpty();
            if(flag==false){
                localOrdepart=orgNameMap.get("localOrdepart");
                policyServiceStatistics.setGovOrDepart(localOrdepart);
            }else{
                policyServiceStatistics.setGovOrDepart("");
            }
        }
        policyServiceStatistics.setUserServer("数据服务");
        policyServiceStatistics.setStatus("正常");
        long counts=policyServiceStatisticsService.doesItExist(policyServiceStatistics);
        if(counts>0){
            policyServiceStatisticsService.updateOrgListByOrgNameAndOrgIdentification(policyServiceStatistics);
        }else{
            policyServiceStatisticsService.djServiceStatistics(policyServiceStatistics);
        }

    }
    @AfterThrowing(value = "execution(public * cn.stylefeng.guns.modular.policyFiles.controller.PolicyServiceStatisticsAspectsController..*(..))",throwing = "ex")
    public void doThrow(JoinPoint joinPoint,Exception ex){
        String methodName = joinPoint.getSignature().getName();
    }
}
