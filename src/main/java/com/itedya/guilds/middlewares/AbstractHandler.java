package com.itedya.guilds.middlewares;

public abstract class AbstractHandler {
    protected AbstractHandler nextHandler;

    public void setNext(AbstractHandler handler) {
        this.nextHandler = handler;
    }

    public String handle() {
        if (nextHandler != null) {
            return nextHandler.handle();
        }

        return null;
    }
}
