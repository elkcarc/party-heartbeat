package com.PartyHeartbeat;

import java.io.BufferedInputStream;
import java.util.Hashtable;
import java.util.Objects;
import javax.inject.Inject;
import javax.sound.sampled.*;

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
	protected Clip soundClip;


	@Inject
	Hashtable<String, Integer> partyMemberPulses = new Hashtable<String, Integer>();

	@Override
	protected void startUp()
	{
		soundClip = loadSoundClip(config.volume());
		wsClient.registerMessage(Pulse.class);
		wsClient.registerMessage(UpdatePartyPulse.class);
		overlayManager.add(heartbeatOverlay);
	}

	@Override
	protected void shutDown()
	{
		soundClip.close();
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
	protected void onGameState(GameState event)
	{
		if(event.equals(GameState.LOGGED_IN))
		{
			partyMemberPulses.put(client.getLocalPlayer().getName(), 0);
		}
	}

	//send a config update over the party
	@Subscribe
	protected void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("PartyHeartbeat") || !Objects.equals(event.getKey(), "sendPulse"))
		{
			return;
		}
		if (Objects.equals(event.getKey(), "shouldNotifySound"))
		{
			if (soundClip == null)
				soundClip = loadSoundClip(config.volume());
			else
				soundClip.close();

		}
		UpdatePartyPulse p = new UpdatePartyPulse(client.getLocalPlayer().getName());
		if(party.isInParty())
		{
			clientThread.invokeLater(() -> party.send(p));
		}
	}

	//add a tick to last seen pulse
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

	//receives the heartbeat pulse (set last seen pulse to 0
	@Subscribe
	protected void onPulse(Pulse event)
	{
		clientThread.invokeLater(() ->
		{
			partyMemberPulses.put(event.getPlayer(), 0);
		});
	}

	//sends the heartbeat pulse
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

	//clears the tracked users table if a config update is received.
	@Subscribe
	protected void onUpdatePartyPulse(UpdatePartyPulse event)
	{
		clientThread.invokeLater(() ->
		{
			partyMemberPulses.clear();
		});
	}

	protected Clip loadSoundClip(int volume)
	{
		try
		{
			AudioInputStream stream = AudioSystem.getAudioInputStream(new
					BufferedInputStream(PartyHeartbeatPlugin.class.getResourceAsStream("/util/offerdeclined.wav")));
			AudioFormat format = stream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			Clip soundClip = (Clip) AudioSystem.getLine(info);
			soundClip.open(stream);
			FloatControl control = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);

			if (control != null)
					control.setValue((float) (volume / 2 - 45));

			return soundClip;
		}
		catch (Exception exception)
		{
			return null;
		}
	}
}