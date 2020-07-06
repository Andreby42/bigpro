package com.thc.platform.modules.ocr.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SpringUtil implements ApplicationContextAware {
    private static ApplicationContext context;

    public SpringUtil() {
    }

    public void setApplicationContext(ApplicationContext arg0) throws BeansException {
        log.info("application context set: " + arg0);
        context = arg0;
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    public static <T> T getBean(Class<T> clazz) {
        if (clazz == null) {
            log.warn("bean clazz is null");
            throw new RuntimeException("类名为空");
        } else if (context == null) {
            log.warn("spring context is null");
            throw new RuntimeException();
        } else {
            return context.getBean(clazz);
        }
    }
}
