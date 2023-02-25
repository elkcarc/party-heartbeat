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
            keyName = "iconSize",
            name = "Select the icon size",
            description = "Select the size you want your disconnected icon to be"
    )
    default IconSize iconSize()
    {
        return IconSize.TWENTY;
    }


    @ConfigItem(
            position = 1,
            keyName = "shouldNotify",
            name = "Runelite notification",
            description = "Sends a notification event (flash/sound/chatbox message) to Runelite on lack of heartbeat from party member (requires Runelite notifications on)"
    )
    default boolean shouldNotify() {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "shouldNotifySound",
            name = "Notification Sound on disconnect",
            description = "Sends a notification sound on party member DC"
    )
    default boolean shouldNotifySound() {
        return true;
    }

    @ConfigItem(
            position = 3,
            keyName = "volume",
            name = "Sound volume",
            description = "Sets the notifcation volume"
    )
    default int volume()
    {
        return 50;
    }

    @ConfigItem(
            position = 4,
            keyName = "maxTicks",
            name = "Ticks before Notification",
            description = "Maximum ticks without notification pulse before overlay render"
    )
    default int maxTicks() {
        return 3;
    }
}
