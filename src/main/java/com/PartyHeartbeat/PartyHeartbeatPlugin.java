package com.PartyHeartbeat;

import java.util.Hashtable;
import javax.inject.Inject;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.party.PartyMember;
import net.runelite.client.party.PartyService;
import net.runelite.client.party.WSClient;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
		name = "Party Heartbeat",
		description = "Show Party Disconnections"
)
public class PartyHeartbeatPlugin extends Plugin
{

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private WSClient wsClient;

	@Inject
	private PartyService party;

	@Inject
	private Hooks hooks;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private HeartbeatOverlay heartbeatOverlay;

	@Inject
	private PartyHeartbeatConfig config;


	@Inject
	Hashtable<String, Integer> partyMemberPulses = new Hashtable<String, Integer>();

	@Override
	protected void startUp()
	{
		wsClient.registerMessage(Pulse.class);
		wsClient.registerMessage(UpdatePartyPulse.class);
		overlayManager.add(heartbeatOverlay);
	}

	@Override
	protected void shutDown()
	{
		partyMemberPulses.clear();
		wsClient.unregisterMessage(Pulse.class);
		wsClient.unregisterMessage(UpdatePartyPulse.class);
		overlayManager.remove(heartbeatOverlay);
	}


	@Provides
	PartyHeartbeatConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PartyHeartbeatConfig.class);
	}

	@Subscribe
	protected void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("PartyHeartbeat"))
		{
			return;
		}
		partyMemberPulses.clear();
		//party.send();
	}

	@Subscribe
	protected void onGameState(GameState event)
	{
		if(event.equals(GameState.LOGGED_IN))
		{
			partyMemberPulses.put(client.getLocalPlayer().getName(), 0);
		}
	}

	@Subscribe
	protected void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("PartyHeartbeat"))
		{
			return;
		}
		UpdatePartyPulse p = new UpdatePartyPulse(client.getLocalPlayer().getName());
		clientThread.invokeLater(() -> party.send(p));
	}

	@Subscribe
	protected void onGameTick(GameTick event)
	{
		if (!party.isInParty())
			return;

		heartbeatOverlay.hasJingled = false;
		heartbeatOverlay.hasNotified = false;
		for (PartyMember p : party.getMembers())
		{
			if (!p.isLoggedIn())
				continue;
			if(partyMemberPulses.containsKey(p.getDisplayName()))
			{
				partyMemberPulses.put(p.getDisplayName(), partyMemberPulses.get(p.getDisplayName()) + 1);
			}
		}
		sendPulse();
	}


	@Subscribe
	protected void onPulse(Pulse event)
	{
		clientThread.invokeLater(() ->
		{
			partyMemberPulses.put(event.getPlayer(), 0);
		});
	}


	private void sendPulse()
	{
		if (party.isInParty())
		{
			if(config.sendPulse())
			{
				Pulse p = new Pulse(client.getLocalPlayer().getName());
				if (p.getPlayer() != null)
				{
					clientThread.invokeLater(() -> party.send(p));
				}
			}
		}
	}

	@Subscribe
	protected void onUpdatePartyPulse(UpdatePartyPulse event)
	{
		clientThread.invokeLater(() ->
		{
			partyMemberPulses.clear();
		});
	}
}