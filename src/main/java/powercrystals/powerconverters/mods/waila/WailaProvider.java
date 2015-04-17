package powercrystals.powerconverters.mods.waila;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import powercrystals.powerconverters.common.TileEntityEnergyBridge;
import powercrystals.powerconverters.power.base.BlockPowerConverter;
import powercrystals.powerconverters.power.base.TileEntityBridgeComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * Adds text to Waila, when it is installed
 */
public class WailaProvider implements IWailaDataProvider {

    public static void register(IWailaRegistrar registrar) {
        WailaProvider provider = new WailaProvider();

        registrar.registerStackProvider(provider, BlockPowerConverter.class);
        registrar.registerBodyProvider(provider, TileEntityBridgeComponent.class);
        registrar.registerBodyProvider(provider, TileEntityEnergyBridge.class);
    }

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        List<String> x = new ArrayList<String>();
        x.add("Test_Body");
        return x;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
        return null;
    }
}
