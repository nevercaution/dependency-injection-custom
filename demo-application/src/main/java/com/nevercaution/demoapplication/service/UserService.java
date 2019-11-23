package com.nevercaution.demoapplication.service;

import com.nevercaution.customdependencyinject.annotation.CustomAutoWired;
import com.nevercaution.customdependencyinject.stereotype.CustomComponent;

@CustomComponent
public class UserService {

    @CustomAutoWired
    private NoteService noteService;

    public String pullOut() {

        noteService.sayHelloWorld();

        return "[UserService] rabbit!";
    }
}
