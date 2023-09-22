package com.PartyHeartbeat;

import lombok.Getter;

@Getter
public enum SendStatus
{
    HCIM("Hardcore", 0),
    ON("On", 1),
    OFF("Off", 2);

    private final String name;
    private final int id;

    SendStatus(String name, int id)
    {
        this.name = name;
        this.id = id;
    }
}