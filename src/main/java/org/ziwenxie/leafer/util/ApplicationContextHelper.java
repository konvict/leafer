package org.ziwenxie.leafer.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class ApplicationContextHelper implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 实现ApplicationContextAware接口的context注入函数, 将其存入静态变量
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        ApplicationContextHelper.applicationContext = applicationContext;
    }

    /**
     * 取得存储在静态变量中的ApplicationContext
     */
    public static ApplicationContext getContext() {
        return applicationContext;
    }

    /**
     * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型
     */
    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }

    /**
     * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型
     */
    public static <T> T getBean(Class<T> clazz) {
        System.out.println(clazz);
        return (T) applicationContext.getBeansOfType(clazz);
    }

    /**
     * 清除applicationContext静态变量
     */
    public static void cleanApplicationContext() {
        applicationContext = null;
    }

    /**
     * 得到applicationContext静态变量
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

}
