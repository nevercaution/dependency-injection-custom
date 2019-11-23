package com.nevercaution.customdependencyinject.support;

import com.nevercaution.customdependencyinject.annotation.CustomAutoWired;
import com.nevercaution.customdependencyinject.stereotype.CustomComponent;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CustomBeanFactory {

    private List<String> beanNames = new ArrayList<>();
    private Map<String, CustomBeanDefinition> beanDefinitions = new ConcurrentHashMap<>(64);
    private Map<Type, Object> mergedBeanDefinitions = new ConcurrentHashMap<>(64);
    private List<Object> singletonObjects = new ArrayList<>();

    public void setResources(List<String> resourceList) {
        resourceList.forEach(this::setResources);
    }

    private void setResources(String resource) {
        CustomBeanDefinition beanDefinition = new CustomBeanDefinition();

        Class klass = null;
        try {
            klass = Class.forName(resource);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (isNotCandidate(klass)) {
            return;
        }

        String beanName = lowerFirstCase(klass.getSimpleName());

        List<Field> autowiredFields = new ArrayList<>();
        for (Field field : klass.getDeclaredFields()) {
            if (field.isAnnotationPresent(CustomAutoWired.class)) {
                autowiredFields.add(field);
            }
        }

        beanNames.add(beanName);
        beanDefinition.setBeanName(beanName);
        beanDefinition.setKlass(klass);
        beanDefinition.setAutowiredFields(autowiredFields);
        beanDefinitions.put(beanName, beanDefinition);
    }

    /**
     * Capitalize the first letter of the class name to lowercase
     * @param simpleName
     * @return
     */
    private String lowerFirstCase(String simpleName) {
        // TODO Auto-generated method stub
        char [] chars = simpleName.toCharArray();
        chars[0]+=32;
        return String.valueOf(chars);
    }

    private boolean isNotCandidate(Class klass) {
        if (klass.isInterface()) {
            return true;
        }

        if (!klass.isAnnotationPresent(CustomComponent.class)) {
            return true;
        }
        return false;
    }

    public void initialBean(CustomBeanDefinition beanDefinition) {
        Object instance = null;
        try {
            instance = beanDefinition.getKlass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        mergedBeanDefinitions.put(beanDefinition.getKlass(), instance);
        beanDefinition.setBean(instance);
    }

    public Optional<Object> getBean(String beanName) {
        return singletonObjects.stream()
                .filter(item -> lowerFirstCase(item.getClass().getSimpleName()).equals(beanName))
                .findFirst();
    }

    public List<String> getBeanNames() {
        return beanNames;
    }

    public void setBeanNames(List<String> beanNames) {
        this.beanNames = beanNames;
    }

    public Map<String, CustomBeanDefinition> getBeanDefinitions() {
        return beanDefinitions;
    }

    public void setBeanDefinitions(Map<String, CustomBeanDefinition> beanDefinitions) {
        this.beanDefinitions = beanDefinitions;
    }

    public Map<Type, Object> getMergedBeanDefinitions() {
        return mergedBeanDefinitions;
    }

    public void setMergedBeanDefinitions(Map<Type, Object> mergedBeanDefinitions) {
        this.mergedBeanDefinitions = mergedBeanDefinitions;
    }

    public List<Object> getSingletonObjects() {
        return singletonObjects;
    }

    public void setSingletonObjects(List<Object> singletonObjects) {
        this.singletonObjects = singletonObjects;
    }
}
