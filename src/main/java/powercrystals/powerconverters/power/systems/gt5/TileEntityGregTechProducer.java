package powercrystals.powerconverters.power.systems.gt5;

import gregtech.api.metatileentity.BaseTileEntity;
import gregtech.api.interfaces.tileentity.IEnergyConnected;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import powercrystals.powerconverters.position.BlockPosition;
import powercrystals.powerconverters.power.PowerSystemManager;
import powercrystals.powerconverters.power.systems.PowerGregTech5;

import java.util.Map.Entry;

/**
 ** Note:
 ** In order to Support more than 1 Amp
 ** Some things should be changed:
 **  - You must consider datatype's max value 
 **    as some calculations may overflow in UV etc etc 
 **  - The Bridge GUI should be changed to show 'Amps' and Voltage
 **  - The GUI should allow to adjunst the output instead of adding tons of 
 **    producers/consumers, so the User could choose between 1 - maxAmp
 **   
 **/
 
public class TileEntityGregTechProducer extends BaseGTProducerTileEntity<IEnergyConnected> implements IEnergyConnected {
	private static final long[] typeVoltageIndex = new long[]{8, 32, 128, 512, 2048, 8192, 32768, 131072, 524288};
	private byte voltageIndex;
    private long voltage, maxAmperage;
    private double lastSentEnergy;
    private byte gtTileColor;
    private boolean needsBlockUpdate = true;
    
    @SuppressWarnings("UnusedDeclaration")
    public TileEntityGregTechProducer() {
        this(0);
    }

    public TileEntityGregTechProducer(int voltageIndex) {
        super(PowerSystemManager.getInstance().getPowerSystemByName(PowerGregTech5.id), voltageIndex, IEnergyConnected.class);
        
        setVoltageByIndex(voltageIndex);
        setMaxAmperage(1);
        setColorization((byte)-1);
    }

    private void setVoltageByIndex(int index) {
		if(index > 8)
			index = 0;

		voltage = typeVoltageIndex[index];
		voltageIndex = (byte)index;			
    }

    private void setMaxAmperage(long amp) {
    	maxAmperage = amp;
    }   
    
    /** 
     * Incrases the maxAmperage by one Step overflow will be catched (>16 == 1)
     * 
     * @return long the new maxAmperage
     */
    public long incMaxAmperage(){
		long tmp = (maxAmperage + 1);
		
		if(tmp > 16)
			tmp = 1;
		
		maxAmperage = tmp;
		
		return maxAmperage;				    	
    }


    @Override
    public void updateEntity() {
        super.updateEntity();
        
        if(!worldObj.isRemote){
           	if(needsBlockUpdate) {
				
           		// GT's TE caches which surrounding TE's are present
				for (ForgeDirection d : ForgeDirection.VALID_DIRECTIONS) {

					TileEntity te = BlockPosition.getAdjacentTileEntity(this, d);
					if(te instanceof BaseTileEntity){
						((BaseTileEntity)te).onAdjacentBlockChange(xCoord, yCoord, zCoord);
					}
	        	}
				
				// Well, we've also to invalidate/refresh our neighbour cache again	        	
				onNeighboorChanged();
					                     	
				needsBlockUpdate = false;
			}
		}			                            
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        setVoltageByIndex( tag.getByte("voltageIndex") );
        setMaxAmperage( tag.getLong("maxAmperage") );
        setColorization( tag.getByte("gtTileColor") );
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        
        tag.setByte("voltageIndex", voltageIndex);
        tag.setLong("maxAmperage", maxAmperage);
        tag.setByte("gtTileColor", gtTileColor);
	}
	
	
    @Override
    public void validate() {
        super.validate();
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

    @Override
    public double produceEnergy(double energy) {
        boolean powered = getWorldObj().getStrongestIndirectPower(xCoord, yCoord, zCoord) > 0;
    	double EU = energy / getPowerSystem().getInternalEnergyPerOutput(0);
		long lEU = (long)EU;
		long usedEU = 0;
				
		if(!powered && lEU >= voltage) { // enough energy avail. to output at least 1A
			long ampsLeft = lEU / voltage; 
			
			for (Entry<ForgeDirection, IEnergyConnected> it : this.getTiles().entrySet()) {
			
				if(ampsLeft < maxAmperage)
					break;
			
				IEnergyConnected t = it.getValue();
				
				if(getColorization() >= 0) {
					byte tColor = t.getColorization();
					if(tColor >= 0 && tColor != getColorization())
						continue;
				}
				
				long ampsUsed = t.injectEnergyUnits( (byte)it.getKey().getOpposite().ordinal(), voltage, maxAmperage );
				ampsLeft -= ampsUsed;
				
				usedEU += (ampsUsed * voltage);
			}
			
		}    	   	

        return energy - ( usedEU * getPowerSystem().getInternalEnergyPerOutput(0) );
    }

    /** GregTech API Part **/
	@Override
	public long injectEnergyUnits(byte aSide, long aVoltage, long aAmperage) {
		return 0;
	}
	    
    @Override
    public boolean inputEnergyFrom(byte aSide) {
    	return false;
    }
    
    @Override
    public boolean outputsEnergyTo(byte aSide) {
    	return true;
    }

	// @TODO: Implement Colorization Support
    @Override
    public byte getColorization() {
    	return -1;
    }
    
    @Override
    public byte setColorization(byte aColor) {
    	return -1;
    }

}
