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
import net.runelite.api.Player;
import net.runelite.api.events.GameTick;
import net.runelite.client.Notifier;
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
	private Notifier notifier;

	@Inject
	Hashtable<String, Integer> partyMemberPulses = new Hashtable<String, Integer>();

	@Provides
	PartyHeartbeatConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PartyHeartbeatConfig.class);
	}

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
		UpdatePartyPulse p = new UpdatePartyPulse(client.getLocalPlayer().getName());
		if(party.isInParty())
		{
			clientThread.invokeLater(() -> party.send(p));
		}
	}

	@Subscribe
	protected void onGameTick(GameTick event)
	{
		if (!party.isInParty()) //return if not in party
			return;

		for (PartyMember p : party.getMembers()) //notify for each player in party
		{
			notifyPlayers(p);
		}

		for (PartyMember p : party.getMembers()) //add a tick to last seen pulse for each player in the party (if they have sent a pulse)
		{
			if (!p.isLoggedIn()) //continue if player is not logged in
				continue;
			if(partyMemberPulses.containsKey(p.getDisplayName()))
			{
				partyMemberPulses.put(p.getDisplayName(), partyMemberPulses.get(p.getDisplayName()) + 1); //add the tick
			}
		}

		if(config.sendPulse()) //send your pulse if enabled
		{
			sendPulse();
		}
	}

	private void notifyPlayers(PartyMember p)
	{
		if(partyMemberPulses.containsKey(p.getDisplayName()))
		{
			if (partyMemberPulses.get(p.getDisplayName()) > config.maxTicks())
			{
				if(config.shouldNotify()) //runelite notification
				{
					notifier.notify("Party member " + p.getDisplayName() + " has Disconnected!");
				}
				if (config.shouldNotifySound()) //sound notification
				{

					if (soundClip != null)
					{
						FloatControl control = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);

						if (control != null)
							control.setValue((float) (config.volume() / 2 - 45));

						soundClip.setFramePosition(0);
						soundClip.start();
					}
					else //play using game sounds if file cannot be loaded
						client.playSoundEffect(3926);
				}
			}
		}
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

	//receives the heartbeat pulse
	@Subscribe
	protected void onPulse(Pulse event)
	{
		clientThread.invokeLater(() ->
		{
			partyMemberPulses.put(event.getPlayer(), 0); //set last seen tick to 0
		});
	}

	//sends the heartbeat pulse
	private void sendPulse()
	{
		if (party.isInParty()) //is in party
		{
			Pulse p = new Pulse(client.getLocalPlayer().getName()); //create pulse
			if (p.getPlayer() != null)
			{
				clientThread.invokeLater(() -> party.send(p)); //send
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
}