package com.PartyHeartbeat;

import lombok.EqualsAndHashCode;
import lombok.Value;
import net.runelite.client.party.messages.PartyMemberMessage;

//a message type that triggers a purge of the player off the list of players being tracked
@Value
@EqualsAndHashCode(callSuper = true)
public class ClearPartyPulse extends PartyMemberMessage
{
    String player;
}
