package me.earth.earthhack.impl.modules.player.xcarry;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.util.client.SimpleData;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

public class XCarry extends Module
{
    public XCarry()
    {
        super("XCarry", Category.Player);
        this.listeners.add(new ListenerCloseWindow(this));
        this.setData(new SimpleData(this, "Allows you to store items in " +
                "your crafting inventory and drag slot."));
    }

    @Override
    protected void onDisable()
    {
        if (mc.player != null)
        {
            mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(
                    mc.player.playerScreenHandler.syncId));
        }
    }

}
