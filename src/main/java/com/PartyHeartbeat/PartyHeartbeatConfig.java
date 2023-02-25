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
            description = "Show the disconnected icon over the player"
    )
    default boolean showOverlay() {
        return true;
    }


    @ConfigItem(
            position = 1,
            keyName = "shouldNotify",
            name = "Runelite notification on disconnect",
            description = "Sends a notification event (flash/sound/chatbox message) to Runelite on lack of heartbeat from party member (requires Runelite notifications on)"
    )
    default boolean shouldNotify() {
        return true;
    }

    @ConfigItem(
            position = 2,
            keyName = "shouldNotifySound",
            name = "Notification Sound on disconnect",
            description = "Sends a notification sound on party member DC"
    )
    default boolean shouldNotifySound() {
        return false;
    }

    @ConfigItem(
            position = 3,
            keyName = "volume",
            name = "Notification sound volume",
            description = "Sets the notifcation volume"
    )
    default int volume()
    {
        return 50;
    }

    @ConfigItem(
            position = 4,
            keyName = "maxTicks",
            name = "Maximum ticks without connection",
            description = "Maximum ticks without notification pulse before overlay render"
    )
    default int maxTicks() {
        return 3;
    }
}
