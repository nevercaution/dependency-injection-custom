package com.nevercaution.customdependencyinject;

import com.nevercaution.customdependencyinject.annotation.CustomComponentScan;
import com.nevercaution.customdependencyinject.stereotype.CustomComponent;
import com.nevercaution.customdependencyinject.support.CustomBeanDefinition;
import com.nevercaution.customdependencyinject.support.CustomBeanFactory;
import io.github.classgraph.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


public class CustomApplicationContext {

    private CustomBeanFactory beanFactory = new CustomBeanFactory();

    public void initialize(Class klass) {
        refresh(klass.getPackageName());
    }

    private void refresh(String basePackage) {

        System.out.println("CustomApplicationContext.refresh >> basePackage : " + basePackage);
        // Invoke factory processors registered as beans in the context.
        invokeBeanFactoryPostProcessors(basePackage);

        // Instantiate all remaining (non-lazy-init) singletons.
        finishBeanFactoryInitialization(beanFactory);
    }

    private void invokeBeanFactoryPostProcessors(String basePackage) {
        // parse @CustomComponentScan
        List<String> packageNameList = parse(basePackage);

        System.out.println("packageNameList = " + packageNameList);

        // parse @CustomComponent
        List<String> resourceList = findResource(packageNameList);

        beanFactory.setResources(resourceList);
    }

    private List<String> parse(String basePackage) {
        try (ScanResult scanResult = new ClassGraph()
                .enableAnnotationInfo()
                .whitelistPackages(basePackage)
                .scan()) {

            final String annotationName = CustomComponentScan.class.getName();
            return scanResult.getClassesWithAnnotation(annotationName)
                    .stream()
                    .map(classInfo -> classInfo.getAnnotationInfo(annotationName))
                    .map(AnnotationInfo::getParameterValues)
                    .map(valueList -> valueList.stream()
                            .filter(item -> "basePackage".equals(item.getName()))
                            .map(AnnotationParameterValue::getValue)
                            .map(item -> (String[]) item)
                            .map(values -> String.join("", values))
                            .collect(Collectors.toList()))
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        }
    }

    private List<String> findResource(List<String> packageList) {
        if (packageList.isEmpty()) {
            throw new IllegalAccessError("packageList is empty!");
        }

        try(ScanResult scanResult = new ClassGraph()
                .enableAnnotationInfo()
                .enableClassInfo()
                .whitelistPackages(packageList.toArray(String[]::new))
                .scan()) {

            List<String> resourceList = scanResult.getClassesWithAnnotation(CustomComponent.class.getName())
                    .stream()
                    .map(ClassInfo::getName)
                    .collect(Collectors.toList());

            System.out.println("resourceList = " + resourceList);
            return resourceList;
        }
    }

    private void finishBeanFactoryInitialization(CustomBeanFactory beanFactory) {
        Map<String, CustomBeanDefinition> beanDefinitions = beanFactory.getBeanDefinitions();

        beanDefinitions.keySet().forEach(key -> {
            CustomBeanDefinition beanDefinition = beanDefinitions.get(key);
            beanFactory.initialBean(beanDefinition);
        });

        autowiredBeans(beanFactory);
    }

    private void autowiredBeans(CustomBeanFactory beanFactory) {
        Map<String, CustomBeanDefinition> beanDefinitions = beanFactory.getBeanDefinitions();

        List<Object> objects = new ArrayList<>();
        beanDefinitions.keySet().forEach(key -> {
            CustomBeanDefinition beanDefinition = beanDefinitions.get(key);
            beanDefinition.getAutowiredFields().forEach(field -> {
                Class<?> type = field.getType();
                Object obj = beanFactory.getMergedBeanDefinitions().get(type);

                field.setAccessible(true);
                try {
                    field.set(beanDefinition.getBean(), obj);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });

            Object o = beanFactory.getMergedBeanDefinitions().get(beanDefinition.getKlass());
            objects.add(o);
        });

        beanFactory.setSingletonObjects(objects);
    }

    public Optional<Object> getBean(String beanName) {
        return beanFactory.getBean(beanName);
    }
}
