package com.PartyHeartbeat;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("PartyHeartbeat")
public interface PartyHeartbeatConfig extends Config
{
    @ConfigItem(
            position = 0,
            keyName = "maxTicks",
            name = "Ticks Before Alert",
            description = "Maximum ticks without notification pulse before overlay render"
    )
    default int maxTicks()
    {
        return 4;
    }

    @ConfigItem(
            position = 1,
            keyName = "sendPulse",
            name = "Send Status",
            description = "Decides whether or not to send connection status (disable if you don't care about disconnecting)"
    )
    default boolean sendPulse()
    {
        return true;
    }

    @ConfigItem(
            position = 2,
            keyName = "showOverlay",
            name = "Show Overlay",
            description = "Show the disconnected icon over the player"
    )
    default boolean showOverlay()
    {
        return true;
    }

    @ConfigItem(
            position = 3,
            keyName = "alertNonRendered",
            name = "Alert for Non-Rendered Players",
            description = "Alert for players not visible on the game screen (in a different world/instance/too far to see). Will cause alerts on World Hops"
    )
    default boolean alertNonRendered()
    {
        return false;
    }

    @ConfigItem(
            position = 4,
            keyName = "shouldNotify",
            name = "Runelite Notification",
            description = "Sends a notification event (flash/sound/chatbox message) to Runelite on lack of heartbeat from party member (requires Runelite notifications on)"
    )
    default boolean shouldNotify()
    {
        return false;
    }

    @ConfigItem(
            position = 5,
            keyName = "shouldNotifySound",
            name = "Notification Sound on Disconnect",
            description = "Sends a notification sound on party member DC"
    )
    default boolean shouldNotifySound()
    {
        return true;
    }

    @ConfigItem(
            position = 6,
            keyName = "volume",
            name = "Sound Volume",
            description = "Sets the notification volume (requires client restart)"
    )
    default int volume()
    {
        return 50;
    }

    @ConfigItem(
            position = 7,
            keyName = "iconSize",
            name = "Select Icon Size",
            description = "Select the size you want your disconnected icon to be"
    )
    default IconSize iconSize()
    {
        return IconSize.TWENTY;
    }
}
