package com.PartyHeartbeat;

import java.util.Hashtable;
import javax.inject.Inject;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.PartyChanged;
import net.runelite.client.party.PartyMember;
import net.runelite.client.party.PartyService;
import net.runelite.client.party.WSClient;
import net.runelite.client.party.events.UserJoin;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

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
	private OverlayManager overlayManager;

	@Inject
	private HeartbeatOverlay heartbeatOverlay;

	@Provides
	PartyHeartbeatConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PartyHeartbeatConfig.class);
	}

	private Player player;

	@Inject
	Hashtable<String, Integer> partyMemberPulses = new Hashtable<String, Integer>();

	@Override
	protected void startUp()
	{
		wsClient.registerMessage(Pulse.class);
		overlayManager.add(heartbeatOverlay);
	}

	@Override
	protected void shutDown()
	{
		wsClient.unregisterMessage(Pulse.class);
		overlayManager.remove(heartbeatOverlay);
	}

	@Subscribe
	protected void onGameState(GameState event)
	{
		if(event.equals(GameState.LOGGED_IN))
		{
			player = client.getLocalPlayer();
		}
	}

	@Subscribe
	protected void onPartyChanged(PartyChanged event)
	{
		partyMemberPulses.clear();
		partyMemberPulses = new Hashtable<String, Integer>();
		for (PartyMember partyMember : party.getMembers())
		{
			partyMemberPulses.put(partyMember.getDisplayName(), 0);
		}
	}

	@Subscribe
	protected void onUserJoin(UserJoin event)
	{
		partyMemberPulses.clear();
		partyMemberPulses = new Hashtable<String, Integer>();
		for (PartyMember partyMember : party.getMembers())
		{
			partyMemberPulses.put(partyMember.getDisplayName(), 0);
		}
	}

	@Subscribe
	protected void onGameTick(GameTick event)
	{
		for (PartyMember p : party.getMembers())
		{
			if(partyMemberPulses.containsKey(p.getDisplayName()))
			{
				partyMemberPulses.put(p.getDisplayName(), partyMemberPulses.get(p.getDisplayName() + 1));
			}
		}
		sendPulse();
	}


	@Subscribe
	protected void onPulse(Pulse event)
	{
		clientThread.invokeLater(() ->
		{
			Player p = event.getPlayer();
			if (partyMemberPulses.containsKey(p.getName()))
			{
				partyMemberPulses.put(p.getName(), 0);
			}
		});
	}


	private void sendPulse()
	{
		if (party.isInParty())
		{
			Pulse p = new Pulse(player);
			clientThread.invokeLater(() -> party.send(p));
		}
	}
}