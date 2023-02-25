package com.PartyHeartbeat;

import lombok.EqualsAndHashCode;
import lombok.Value;
import net.runelite.client.party.messages.PartyMemberMessage;

//connection status update
@Value
@EqualsAndHashCode(callSuper = true)
public class Pulse extends PartyMemberMessage
{
	String player;
}
