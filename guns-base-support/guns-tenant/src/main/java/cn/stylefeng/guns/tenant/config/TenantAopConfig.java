package cn.stylefeng.guns.tenant.config;

import cn.stylefeng.guns.tenant.core.aop.TenantSourceExAop;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 多租户的配置
 *
 * @author fengshuonan
 * @date 2020/9/3 22:02
 */
@Configuration
public class TenantAopConfig {

    /**
     * 多租户切换aop的配置
     *
     * @author fengshuonan
     * @date 2020/9/3 22:03
     */
    @Bean
    public TenantSourceExAop tenantSourceExAop() {
        return new TenantSourceExAop();
    }

}
