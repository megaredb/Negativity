package com.elikill58.negativity.common;

import java.util.ArrayList;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.block.BlockBreakEvent;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInFlying;
import com.elikill58.negativity.universal.Version;

public class PacketListener implements Listeners {

	@EventListener
	public void onPacketReceive(PacketReceiveEvent e) {
		if(!e.hasPlayer() || e.getPacket().getPacketType() == null)
			return;
		Player p = e.getPlayer();
		AbstractPacket packet = e.getPacket();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		np.ALL_PACKETS++;
		PacketType type = packet.getPacketType();
		if(type.isFlyingPacket()) {
			NPacketPlayInFlying flying = (NPacketPlayInFlying) packet.getPacket();
			if(flying.hasLook || flying.hasLook) // if it's real flying
				np.PACKETS.put(type, np.PACKETS.getOrDefault(type, 0) + 1);
		}
		if(type == PacketType.Client.BLOCK_DIG && !Version.getVersion().equals(Version.V1_7) && packet.getPacket() instanceof NPacketPlayInBlockDig) {
			NPacketPlayInBlockDig blockDig = (NPacketPlayInBlockDig) packet.getPacket();
			if(blockDig.action != DigAction.FINISHED_DIGGING)
				return;
			
			Block b = blockDig.getBlock(p.getWorld());
			BlockBreakEvent event = new BlockBreakEvent(p, b);
			EventManager.callEvent(event);
			if(event.isCancelled())
				packet.setCancelled(event.isCancelled());
		}
		new ArrayList<>(np.getCheckProcessors()).forEach((cp) -> cp.handlePacketReceived(e));
	}
}
