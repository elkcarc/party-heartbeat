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
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class HeartbeatOverlay extends Overlay
{

    private final PartyHeartbeatPlugin plugin;
    private final PartyHeartbeatConfig config;
    Color timerColor = Color.WHITE;
    @Inject
    private Client client;

    @Inject
    private Notifier notifier;

    public boolean hasNotified = false;
    public boolean hasJingled = false;

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

    private void renderDisconnects(final Graphics2D graphics) {
        for (Player p : client.getPlayers()) {
            if(plugin.partyMemberPulses.containsKey(p.getName()))
            {
                String lastPulse = String.valueOf(plugin.partyMemberPulses.get(p.getName()));
                if (plugin.partyMemberPulses.get(p.getName()) > config.maxTicks()) {
                    if(config.showOverlay())
                    {
                        BufferedImage icon = ImageUtil.loadImageResource(PartyHeartbeatPlugin.class, "/util/icon" + config.iconSize() + ".png");
                        renderSymbol(graphics, p, icon);
                    }
                    if(config.shouldNotify() && !hasNotified)
                    {
                        notifier.notify("Party member " + p.getName() + " has Disconnected!");
                        hasNotified = true;
                    }
                    if (config.shouldNotifySound() && !hasJingled)
                    {
                        client.playSoundEffect(3924, this.config.volume());
                        hasJingled = true;
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
            image = fadeImage(image,config.overlayOpacity() / 100.0f ,50);
            OverlayUtil.renderImageLocation(graphics, textLocation, image);
        }
    }

    public static BufferedImage fadeImage(Image img, float fade, float target) {
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.drawImage(img, 0, 0, null);

        float offset = target * (1.0f - fade);
        float[] scales = { fade, fade, fade, 1.0f };
        float[] offsets = { offset, offset, offset, 0.0f };
        RescaleOp rop = new RescaleOp(scales, offsets, null);

        g.drawImage(bi, rop, 0, 0);
        g.dispose();

        return bi;
    }
}
