package com.nevercaution.demoapplication.service;

import com.nevercaution.customdependencyinject.stereotype.CustomComponent;

@CustomComponent
public class NoteService {

    public void sayHelloWorld() {
        System.out.println("[NoteService] hello world!!");
    }
}
