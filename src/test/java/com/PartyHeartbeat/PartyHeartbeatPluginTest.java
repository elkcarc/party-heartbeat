package com.PartyHeartbeat;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class PartyHeartbeatPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(PartyHeartbeatPlugin.class);
		RuneLite.main(args);
	}
}