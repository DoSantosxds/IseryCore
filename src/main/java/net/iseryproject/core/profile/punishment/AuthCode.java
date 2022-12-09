package net.iseryproject.core.profile.punishment;

import lombok.Getter;

public class AuthCode {

    @Getter
    private String authKey;
    @Getter
    private String discordID;
    @Getter
    private long authCreatedAt;

    public AuthCode(String authKey, String discordID, long authCreatedAt) {
        this.authKey = authKey;
        this.discordID = discordID;
        this.authCreatedAt = authCreatedAt;
    }

}
