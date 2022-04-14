package org.titanic.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author Hanno Skowronek, Chung Ho Chiu
 */
@Component
public class SpringContextUtils implements ApplicationContextAware {
    private static ApplicationContext context;


    @Override
    public void setApplicationContext(ApplicationContext appContext)
            throws BeansException {
        SpringContextUtils.context = appContext;

    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    public static <T> T getBean(Class<T> beanClass){
        return context.getBean(beanClass);
    }

}