package com.nevercaution.customdependencyinject.support;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CustomBeanDefinition {
    private Class klass;
    private Object bean;
    private String beanName;
    private List<Field> autowiredFields = new ArrayList<>();

    public Class getKlass() {
        return klass;
    }

    public void setKlass(Class klass) {
        this.klass = klass;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public List<Field> getAutowiredFields() {
        return autowiredFields;
    }

    public void setAutowiredFields(List<Field> autowiredFields) {
        this.autowiredFields = autowiredFields;
    }
}
