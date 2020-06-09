package com.cisdi.cpm.auth.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service //Register SpringKit to Spring Context
public class SpringUtils implements ApplicationContextAware {
    private static ApplicationContext applicationContext = null;

    //Inner Class implements Singleton
    private static class SingletonHolder {
        private static SpringUtils springKit = new SpringUtils();
    }

    public static SpringUtils instance() {
        return SingletonHolder.springKit;
    }

    @Override
    public void setApplicationContext(ApplicationContext context)
            throws BeansException {
        SpringUtils.applicationContext = context;
    }

    public ApplicationContext getApplicationContext() {
        return SpringUtils.applicationContext;
    }

    public Object getBeanByInstance(String id) {
        return SpringUtils.applicationContext.getBean(id);
    }

    public static Object getBean(String id) {
        return applicationContext.getBean(id);
    }

}

