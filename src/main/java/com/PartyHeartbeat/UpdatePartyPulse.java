package com.PartyHeartbeat;

import lombok.EqualsAndHashCode;
import lombok.Value;
import net.runelite.client.party.messages.PartyMemberMessage;

//a message type that triggers a reset of the user's being tracked
@Value
@EqualsAndHashCode(callSuper = true)
public class UpdatePartyPulse extends PartyMemberMessage
{
    String player;
}
