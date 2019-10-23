package com.nevercaution.customdependencyinject;

import com.nevercaution.customdependencyinject.annotation.CustomComponentScan;
import com.nevercaution.customdependencyinject.stereotype.CustomComponent;
import io.github.classgraph.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class CustomApplicationContext {

    private CustomBeanFactory beanFactory = new CustomBeanFactory();

    public void initialize(Class klass) {
        refresh(klass.getPackageName());
    }

    private void refresh(String basePackage) {
        invokeBeanFactoryPostProcessors(basePackage);

        finishBeanFactoryInitialization(beanFactory);
    }

    private List<String> parse(String basePackage) {
        try (ScanResult scanResult = new ClassGraph()
                .enableAnnotationInfo()
                .whitelistPackages(basePackage)
                .scan()) {

//            String[] strings = packageNameList.toArray(String[]::new);
//            for (String s : strings) {
//                System.out.println("s = " + s);
//            }

            return scanResult.getClassesWithAnnotation(CustomComponentScan.class.getName())
                    .stream()
                    .map(classInfo -> classInfo.getAnnotationInfo(CustomComponentScan.class.getName()))
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

    private void invokeBeanFactoryPostProcessors(String basePackage) {
        // parse @CustomComponentScan
        List<String> packageNameList = parse(basePackage);

        System.out.println("packageNameList = " + packageNameList);


        String[] all = {"a", "b", "c"};

        try(ScanResult scanResult = new ClassGraph()
                .enableAnnotationInfo()
                .whitelistPackages(packageNameList.toArray(new String[0]))
                .scan()) {

        }
    }

    private void finishBeanFactoryInitialization(CustomBeanFactory beanFactory) {

    }
}
