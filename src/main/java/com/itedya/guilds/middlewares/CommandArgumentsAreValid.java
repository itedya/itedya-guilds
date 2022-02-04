package com.itedya.guilds.middlewares;

import com.itedya.guilds.dtos.Dto;

public class CommandArgumentsAreValid extends AbstractHandler {
    private final Dto dto;

    public CommandArgumentsAreValid(Dto dto) {
        this.dto = dto;
    }

    @Override
    public String handle() {
        var res = dto.validate();
        if (res != null) return res;

        return super.handle();
    }
}
