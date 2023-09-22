package com.PartyHeartbeat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SendStatus
{
    HC("HC"),
    ON("On"),
    OFF("Off");

    private final String name;

    @Override
    public String toString()
    {
        return name;
    }
}