package com.PartyHeartbeat;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class HeartbeatOverlay extends Overlay
{

    private final PartyHeartbeatPlugin plugin;
    private final PartyHeartbeatConfig config;
    Color timerColor = Color.WHITE;

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
        renderProjectiles(graphics);
        return null;
    }

    private void renderProjectiles(final Graphics2D graphics) {
        for (Player p : client.getPlayers()) {
            if(plugin.partyMemberPulses.containsKey(p.getName()))
            {
                String lastPulse = String.valueOf(plugin.partyMemberPulses.get(p.getName()));
                if (plugin.partyMemberPulses.get(p.getName()) > config.maxTicks()) {
                    if(config.showTicks())
                    {
                        renderPlayerOverlay(graphics, p, lastPulse, timerColor);
                    }
                    if(config.showOverlay())
                    {
                        BufferedImage icon = ImageUtil.loadImageResource(PartyHeartbeatPlugin.class, "/util/icon.png");
                        renderSymbol(graphics, p, icon);
                    }
                }
            }
        }
    }

    private void renderPlayerOverlay(Graphics2D graphics, Player player, String text, Color color)
    {
        Point point = Perspective.localToCanvas(client, player.getLocalLocation(), client.getPlane(), player.getLogicalHeight());

        if (point != null)
        {
            FontMetrics fm = graphics.getFontMetrics();
            int size = fm.getHeight();

            OverlayUtil.renderTextLocation(
                    graphics,
                    new Point(point.getX() + size + 5, point.getY()),
                    text,
                    color
            );
        }
    }

    private void renderSymbol(Graphics2D graphics, Player player, BufferedImage image)
    {
        Point textLocation = player.getCanvasImageLocation(image, player.getLogicalHeight() / 2);
        if (textLocation != null)
        {
            OverlayUtil.renderImageLocation(graphics, textLocation, image);
        }
    }
}
