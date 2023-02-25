package com.PartyHeartbeat;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.client.Notifier;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;

public class HeartbeatOverlay extends Overlay
{

    private final PartyHeartbeatPlugin plugin;
    private final PartyHeartbeatConfig config;
    @Inject
    private Client client;


    @Inject
    HeartbeatOverlay(PartyHeartbeatPlugin plugin, PartyHeartbeatConfig config)
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.plugin = plugin;
        this.config = config;
    }


    @Override
    public Dimension render(Graphics2D graphics)
    {
        renderDisconnects(graphics);
        return null;
    }

    //render the overlay and notify if a player is over the threshold
    private void renderDisconnects(final Graphics2D graphics)
    {
        for (Player p : plugin.disconnectedMembers)
        {
            BufferedImage icon = ImageUtil.loadImageResource(PartyHeartbeatPlugin.class, "/util/icon" + config.iconSize() + ".png");
            renderSymbol(graphics, p, icon);
        }
    }

    //image util to render the overlay
    private void renderSymbol(Graphics2D graphics, Player player, BufferedImage image)
    {
        Point textLocation = player.getCanvasImageLocation(image, player.getLogicalHeight() / 2);
        if (textLocation != null)
        {
            OverlayUtil.renderImageLocation(graphics, textLocation, image);
        }
    }
}
