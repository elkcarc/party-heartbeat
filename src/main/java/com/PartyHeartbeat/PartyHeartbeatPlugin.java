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
import net.runelite.api.events.GameStateChanged;
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
		wsClient.registerMessage(ClearPartyPulse.class);
		overlayManager.add(heartbeatOverlay);
	}

	@Override
	protected void shutDown()
	{
		soundClip.close();
		partyMemberPulses.clear();
		wsClient.unregisterMessage(Pulse.class);
		wsClient.unregisterMessage(ClearPartyPulse.class);
		overlayManager.remove(heartbeatOverlay);
	}

	@Subscribe
	protected void onGameStateChanged(GameStateChanged event)
	{
		if(event.getGameState().equals(GameState.HOPPING)) //Temporarily stop tracking player on hop
		{
			ClearPartyPulse p = new ClearPartyPulse(client.getLocalPlayer().getName());
			if(party.isInParty())
			{
				clientThread.invokeLater(() -> party.send(p));
			}
		}
	}

	//Send a config update over the party
	@Subscribe
	protected void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("PartyHeartbeat") || !Objects.equals(event.getKey(), "sendPulse"))
		{
			return;
		}
		ClearPartyPulse p = new ClearPartyPulse(client.getLocalPlayer().getName());
		if(party.isInParty())
		{
			clientThread.invokeLater(() -> party.send(p));
		}
	}

	@Subscribe
	protected void onGameTick(GameTick event)
	{
		if (!party.isInParty()) //Return if not in party
			return;

		if(config.alertNonRendered())
		{
			for (PartyMember p : party.getMembers()) //Notify for each player in party (including players not rendered in the scene)
			{
				if (p == null)
				{
					continue;
				}

				if (p.isLoggedIn()) // FIXME Still thinks the players are logged in when they aren't
				{
					notifyPlayers(p.getDisplayName());
				}
			}
		}
		else
		{
			for (Player p : client.getPlayers()) //Notify for each player rendered in the scene
			{
				notifyPlayers(p.getName());
			}
		}

		for (PartyMember p : party.getMembers()) //Add a tick to last seen pulse for each player in the party (if they have sent a pulse)
		{
			if (!p.isLoggedIn()) //Continue if player is not logged in
				continue;
			if(partyMemberPulses.containsKey(p.getDisplayName()))
			{
				partyMemberPulses.put(p.getDisplayName(), partyMemberPulses.get(p.getDisplayName()) + 1); //add the tick
			}
		}

		if(config.sendPulse()) //Send your pulse if enabled
		{
			sendPulse();
		}
	}

	private void notifyPlayers(String p)
	{
		if(partyMemberPulses.containsKey(p))
		{
			if (partyMemberPulses.get(p) > config.maxTicks() && partyMemberPulses.get(p) <= config.timeout()) //Check heartbeat
			{
				if(config.shouldNotify()) //RuneLite notification
				{
					notifier.notify("Party member " + p + " has Disconnected!");
				}
				if (config.shouldNotifySound()) //Sound notification
				{
					if (soundClip != null)
					{
						FloatControl control = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);

						if (control != null)
							control.setValue((float) (config.volume() / 2 - 45));

						soundClip.setFramePosition(0);
						soundClip.start();
					}
					else //Play using game sounds if file cannot be loaded
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

	//Receives the heartbeat pulse
	@Subscribe
	protected void onPulse(Pulse event)
	{
		clientThread.invokeLater(() ->
		{
			partyMemberPulses.put(event.getPlayer(), 0); //set last seen tick to 0
		});
	}

	//Sends the heartbeat pulse
	private void sendPulse()
	{
		if (party.isInParty())
		{
			Pulse p = new Pulse(client.getLocalPlayer().getName()); //create pulse
			if (p.getPlayer() != null)
			{
				clientThread.invokeLater(() -> party.send(p)); //send
			}
		}
	}

	//Purges the player from the list of tracked players
	@Subscribe
	protected void onClearPartyPulse(ClearPartyPulse event)
	{
		clientThread.invokeLater(() ->
		{
			partyMemberPulses.remove(event.getPlayer());
		});
	}
}