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
            keyName = "shouldNotify",
            name = "Notify on Disconnect",
            description = "Sends a notification event to Runelite on lack of heartbeat from party member (requires Runelite notifications on)"
    )
    default boolean shouldNotify() {
        return true;
    }

    @ConfigItem(
            position = 3,
            keyName = "shouldNotifyCustom",
            name = "Bigger Notify on Disconnect",
            description = "Sends a custom notification event on party member DC"
    )
    default boolean shouldNotifyCustom() {
        return false;
    }

    @ConfigItem(
            position = 4,
            keyName = "maxTicks",
            name = "Maximum ticks",
            description = "Maximum ticks without pulse before overlay render"
    )
    default int maxTicks() {
        return 3;
    }

}
