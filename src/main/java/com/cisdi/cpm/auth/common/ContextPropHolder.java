package com.cisdi.cpm.auth.common;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ContextPropHolder extends PropertyPlaceholderConfigurer {
    private static Map<String, Object> propsMap = new HashMap<String, Object>();

    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) {
        super.processProperties(beanFactoryToProcess, props);
        //put all properties into static Map
        for (Object obj : props.keySet()) {
            propsMap.put(String.valueOf(obj), props.getProperty(String.valueOf(obj)));
        }
    }

    public static String getContextProperty(String propertyName) {
        return (String) propsMap.get(propertyName);
    }
}
