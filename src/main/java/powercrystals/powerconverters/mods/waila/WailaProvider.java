package powercrystals.powerconverters.mods.waila;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import powercrystals.powerconverters.common.BridgeSideData;
import powercrystals.powerconverters.common.TileEntityEnergyBridge;
import powercrystals.powerconverters.power.base.BlockPowerConverter;
import powercrystals.powerconverters.power.base.TileEntityBridgeComponent;
import powercrystals.powerconverters.power.base.TileEntityEnergyConsumer;
import powercrystals.powerconverters.power.base.TileEntityEnergyProducer;

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
        TileEntity tileEntity = accessor.getTileEntity();
        if(tileEntity instanceof TileEntityEnergyBridge) {
            TileEntityEnergyBridge bridge = (TileEntityEnergyBridge) tileEntity;
            int percent = (int) ((double)bridge.getEnergyStored() / (double) bridge.getEnergyStoredMax() * 100);
            x.add(String.format("%s%% Charged", percent));
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                BridgeSideData data = bridge.getDataForSide(dir);
                if (data != null && data.powerSystem != null) {
                    String unit = data.powerSystem.getUnit(data.subtype);
                    int stored = (int) (bridge.getEnergyStored() / data.powerSystem.getInternalEnergyPerOutput(data.subtype));
                    String toAdd = String.format("%s %s", stored, unit);
                    if (!x.contains(toAdd))
                        x.add(toAdd);
                }
            }

        }
        else if (tileEntity instanceof TileEntityEnergyConsumer) {
            TileEntityEnergyConsumer component = (TileEntityEnergyConsumer) accessor.getTileEntity();
            TileEntityEnergyBridge bridge = component.getFirstBridge();
            MovingObjectPosition position = accessor.getPosition();
            boolean powered = accessor.getWorld().getBlockPowerInput(position.blockX, position.blockY, position.blockZ) > 0;
            x.add(powered ? "Disabled" : "Enabled");
            // TODO: Make not reset energy
            x.add(String.format("%d %s/t", (int) component.getInputRate(), component.getPowerSystem().getUnit()));
        }
        else if (tileEntity instanceof TileEntityEnergyProducer) {
            TileEntityEnergyProducer component = (TileEntityEnergyProducer) accessor.getTileEntity();
            TileEntityEnergyBridge bridge = component.getFirstBridge();
            MovingObjectPosition position = accessor.getPosition();
            boolean powered = accessor.getWorld().getBlockPowerInput(position.blockX, position.blockY, position.blockZ) > 0;
            x.add(powered ? "Disabled" : "Enabled");
            //x.add(String.format("%d %s/t", (int) component, component.getPowerSystem().getUnit()));
        }
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
