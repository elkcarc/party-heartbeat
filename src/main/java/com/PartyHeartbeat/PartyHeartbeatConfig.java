package com.PartyHeartbeat;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("PartyHeartbeat")
public interface PartyHeartbeatConfig extends Config {
    @ConfigItem(
            position = 0,
            keyName = "showOverlay",
            name = "Show Overlay",
            description = "Show the Disconnected icon over the player"
    )
    default boolean showOverlay() {
        return true;
    }

    @ConfigItem(
            position = 1,
            keyName = "ShowTicks",
            name = "Show timer in ticks",
            description = "Shows timer in ticks"
    )
    default boolean showTicks() {
        return true;
    }

    @ConfigItem(
            position = 2,
            keyName = "maxTicks",
            name = "Maximum ticks",
            description = "Maximum ticks without pulse before overlay render"
    )
    default int maxTicks() {
        return 2;
    }
}
