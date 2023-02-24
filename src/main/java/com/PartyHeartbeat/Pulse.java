package com.PartyHeartbeat;

import lombok.EqualsAndHashCode;
import lombok.Value;
import net.runelite.api.Player;
import net.runelite.client.party.messages.PartyMemberMessage;

@Value
@EqualsAndHashCode(callSuper = true)
public class Pulse extends PartyMemberMessage
{
	Player player;
}
