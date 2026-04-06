package com.fhsh.daitda.user.application.command;

public record LoginCommand(
        String email,
        String password
) {
}
