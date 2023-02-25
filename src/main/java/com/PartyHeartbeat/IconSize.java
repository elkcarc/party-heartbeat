package com.PartyHeartbeat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IconSize
{
    TEN("10"),
    TWENTY("20"),
    THIRTY("30"),
    FORTY("40"),
    FIFTY("50");

    private final String size;

    @Override
    public String toString()
    {
        return size;
    }
}
