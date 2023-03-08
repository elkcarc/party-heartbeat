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
            description = "Maximum ticks without notification pulse before alert"
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
            description = "Show the disconnection icon over the player"
    )
    default boolean showOverlay()
    {
        return true;
    }

    @ConfigItem(
            position = 3,
            keyName = "alertNonRendered",
            name = "Alert for Non-Rendered Players",
            description = "Alert for players not visible on the game screen (logged out/in a different world/instance/too far to see)"
    )
    default boolean alertNonRendered()
    {
        return false;
    }

    @ConfigItem(
            position = 4,
            keyName = "shouldNotify",
            name = "RuneLite Notification",
            description = "Sends a notification event (flash/sound/chatbox message) on lack of heartbeat from party member (requires RuneLite notifications on)"
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
            keyName = "timeout",
            name = "Notification Timeout",
            description = "Sets the notification timeout after a disconnect is connected"
    )
    default int timeout() { return 20; }

    @ConfigItem(
            position = 8,
            keyName = "iconSize",
            name = "Select Icon Size",
            description = "Select the size of the disconnection icon"
    )
    default IconSize iconSize()
    {
        return IconSize.TWENTY;
    }
}
