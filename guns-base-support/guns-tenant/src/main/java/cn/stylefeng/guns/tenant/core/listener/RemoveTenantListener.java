package cn.stylefeng.guns.tenant.core.listener;

import cn.stylefeng.guns.core.tenant.context.TenantCodeHolder;
import cn.stylefeng.guns.core.tenant.context.TenantDbNameHolder;
import org.springframework.context.ApplicationListener;
import org.springframework.web.context.support.ServletRequestHandledEvent;

/**
 * 临时清空租户的ThreadLocal缓存
 *
 * @author fengshuonan
 * @date 2020/9/3 21:18
 */
public class RemoveTenantListener implements ApplicationListener<ServletRequestHandledEvent> {

    @Override
    public void onApplicationEvent(ServletRequestHandledEvent event) {
        TenantCodeHolder.remove();
        TenantDbNameHolder.remove();
    }

}