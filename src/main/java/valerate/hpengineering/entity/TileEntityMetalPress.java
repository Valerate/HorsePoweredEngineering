package valerate.hpengineering.entity;

import java.util.ArrayList;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.property.IExtendedBlockState;
import se.gory_moon.horsepower.recipes.HPRecipeBase;
import se.gory_moon.horsepower.tileentity.TileEntityHPHorseBase;
import se.gory_moon.horsepower.util.Utils;
import valerate.hpengineering.blocks.MetalPress;
import valerate.hpengineering.recipes.MetalPressRecipe;

public class TileEntityMetalPress extends TileEntityHPHorseBase  {

	private int currentWindup;
    private int currentItemFormTime;
    private int totalItemFormTime;
    private float visualWindup = 0;

    public ItemStack mold = ItemStack.EMPTY;
    
	public TileEntityMetalPress() {
		super(3);
	}
	
	  @Override
	    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
	        compound.setInteger("currentWindup", currentWindup);
	        compound.setInteger("formTime", currentItemFormTime);
	        compound.setInteger("totalFormTime", totalItemFormTime);
	        if (!mold.isEmpty()) compound.setString("mold", mold.getUnlocalizedName());

	        return super.writeToNBT(compound);
	    }

	    @Override
	    public void readFromNBT(NBTTagCompound compound) {
	        super.readFromNBT(compound);
	        currentWindup = compound.getInteger("currentWindup");
	        
	        if (getStackInSlot(0).getCount() > 0) {
	            currentItemFormTime = compound.getInteger("chopTime");
	            totalItemFormTime = compound.getInteger("totalChopTime");
	        } else {
	            currentItemFormTime = 0;
	            totalItemFormTime = 1;
	        }
	        
	        mold = new ItemStack(compound.getCompoundTag("mold"));
	    }

	    public IExtendedBlockState getExtendedState(IExtendedBlockState state) {
	        state = (IExtendedBlockState) state.withProperty(MetalPress.FACING, getForward());
	        state = (IExtendedBlockState) state.withProperty(MetalPress.PART, state.getValue(MetalPress.PART));

	        return state;
	    }

	    public void setTextureBlock(NBTTagCompound textureBlock) {
	        getTileData().setTag("textureBlock", textureBlock);
	    }

	    public NBTTagCompound getTextureBlock() {
	        return getTileData().getCompoundTag("textureBlock");
	    }

	    @Override
	    public boolean canBeRotated() {
	        return true;
	    }

	    @Override
	    public boolean isItemValidForSlot(int index, ItemStack stack) {
	    	if (MetalPressRecipe.molds.containsKey(stack.getUnlocalizedName())) return false;
	        return true ; /// FIX
	    }

	    @Override
	    public boolean validateArea() {
	        if (searchPos == null) {
	            searchPos = Lists.newArrayList();

	            for (int x = -3; x <= 3; x++) {
	                for (int z = -3; z <= 3; z++) {
	                    if ((x <= 1 && x >= -1) && (z <= 1 && z >= -1))
	                        continue;
	                    searchPos.add(getPos().add(x, 0, z));
	                    searchPos.add(getPos().add(x, 1, z));
	                }
	            }
	        }

	        for (BlockPos pos: searchPos) {
	            if (!getWorld().getBlockState(pos).getBlock().isReplaceable(world, pos))
	                return false;
	        }
	        return true;
	    }
	    
	    /////////////////// not visible ? \\\\\\\\\\\\\\\\\\
	    
	    private Vec3d getPathPosition(int i) { // Not Visible fix?
	        double x = pos.getX() + path[i][0] * 2;
	        double y = pos.getY() + getPositionOffset();
	        double z = pos.getZ() + path[i][1] * 2;
	        return new Vec3d(x, y, z);
	    }
	    
	    
	    private boolean findWorker() {   // Not Visible fix?
	        UUID uuid = nbtWorker.getUniqueId("UUID");
	        int x = pos.getX();
	        int y = pos.getY();
	        int z = pos.getZ();

	        if (world != null) {
	            ArrayList<Class<? extends EntityCreature>> clazzes = Utils.getCreatureClasses();
	            for (Class<? extends Entity> clazz : clazzes) {
	                for (Object entity : world.getEntitiesWithinAABB(clazz, new AxisAlignedBB((double) x - 7.0D, (double) y - 7.0D, (double) z - 7.0D, (double) x + 7.0D, (double) y + 7.0D, (double) z + 7.0D))) {
	                    if (entity instanceof EntityCreature) {
	                        EntityCreature creature = (EntityCreature) entity;
	                        if (creature.getUniqueID().equals(uuid)) {
	                            setWorker(creature);
	                            return true;
	                        }
	                    }
	                }
	            }
	        }
	        return false;
	    }

	    @Override
	    public void update() {
	    	validationTimer--;
	        if (validationTimer <= 0) {
	            valid = validateArea();
	            if (valid)
	                validationTimer = 220;
	            else
	                validationTimer = 60;
	        }
	        boolean flag = false;

	        if (!hasWorker())
	            locateHorseTimer--;
	        if (!hasWorker() && nbtWorker != null && locateHorseTimer <= 0) {
	            flag = findWorker();
	        }
	        if (locateHorseTimer <= 0)
	            locateHorseTimer = 120;

	        if (!world.isRemote && valid) {
	            if (!running && canWork()) {
	                running = true;
	            } else if (running && !canWork()){
	                running = false;
	            }

	            if (running != wasRunning) {
	                target = getClosestTarget();
	                wasRunning = running;
	            }

	            if (hasWorker()) {
	                if (running) {

	                    Vec3d pos = getPathPosition(target);
	                    double x = pos.x;
	                    double y = pos.y;
	                    double z = pos.z;

	                    if (searchAreas[target] == null)
	                        searchAreas[target] = new AxisAlignedBB(x - 0.5D, y - 0.5D, z - 0.5D, x + 0.5D, y + 0.5D, z + 0.5D);

	                    if (worker.getEntityBoundingBox().intersects(searchAreas[target])) {
	                        int next = target + 1;
	                        int previous = target -1;
	                        if (next >= path.length)
	                            next = 0;
	                        if (previous < 0)
	                            previous = path.length - 1;

	                        if (origin != target && target != previous) {
	                            origin = target;
	                            flag = targetReached();
	                        }
	                        target = next;
	                    }

	                    if (worker instanceof AbstractHorse && ((AbstractHorse)worker).isEatingHaystack()) {
	                        ((AbstractHorse)worker).setEatingHaystack(false);
	                    }

	                    if (target != -1 && worker.getNavigator().noPath()) {
	                        pos = getPathPosition(target);
	                        x = pos.x;
	                        y = pos.y;
	                        z = pos.z;

	                        worker.getNavigator().tryMoveToXYZ(x, y, z, 1D);
	                    }

	                }
	            }
	        }

	        if (flag) {
	            markDirty();
	        }

	        float windup =  8;   //Configs.general.pointsForWindup > 0 ? Configs.general.pointsForWindup: 1;
	        if ( currentWindup < 7) {
	        	visualWindup = -0.74F + (1.2F * (((float)currentWindup) / (windup - 1)));
	        }else {
	        	visualWindup = -1.2F;
	        }
	        
	        //visualWindup = - 0.74F + 
	    }

	    @Override
	    public boolean targetReached() {
	        currentWindup++;

	        if (currentWindup >= 8 ) {   //Configs.general.pointsForWindup
	            currentWindup = 0;
	            currentItemFormTime++;

	            if (currentItemFormTime >= totalItemFormTime) {
	                currentItemFormTime = 0;

	                totalItemFormTime = 8;  //RECIPES
	                formItem();
	                return true;
	            }
	        }
	        markDirty();
	        return false;
	    }

	    @Override
	    public void setInventorySlotContents(int index, ItemStack stack) {
	        ItemStack itemstack = getStackInSlot(index);
	        super.setInventorySlotContents(index, stack);

	        if (index == 1 && getStackInSlot(1).isEmpty()) {
	            markDirty();
	        }

	        boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
	        if (index == 0 && !flag) {
	            totalItemFormTime = 1;     // RECIPE
	            currentItemFormTime = 0;
	            currentWindup = 0;
	            markDirty();
	        }
	    }

	    private void formItem() {
	        if (canWork()) {
	            ItemStack input = getStackInSlot(0);
	            ItemStack result = getRecipeItemStack();
	            ItemStack output = getStackInSlot(2);

	            if (output.isEmpty()) {
	            	EntityItem item = new EntityItem(getWorld(), pos.getX(), pos.getY(), pos.getZ(),result);
	            	world.spawnEntity(item);
	                //setInventorySlotContents(2, result.copy());
	            } else if (output.getItem() == result.getItem()) {
	            	EntityItem item = new EntityItem(getWorld(), pos.getX(), pos.getY(), pos.getZ(),result);
	            	world.spawnEntity(item);
	            	//output.grow(result.getCount());
	            }

	            input.shrink(1);
	            markDirty();
	        }
	    }

	    @Override
	    public ItemStack getRecipeItemStack() {
	        return MetalPressRecipe.RECIPELIST.get(getStackInSlot(1).getUnlocalizedName()).get(getStackInSlot(0).getUnlocalizedName()).getOutput();   // RECIPE Output
	    }

	    public MetalPressRecipe getRecipeHPE() {
	        return MetalPressRecipe.RECIPELIST.get(getStackInSlot(1).getUnlocalizedName()).get(getStackInSlot(0).getUnlocalizedName());   // Is it even an recipe?
	    }

	    @Override
	    public int getPositionOffset() {
	        return 0;
	    }

	    @Override
	    public int getInventoryStackLimit() {
	        return 1;
	    }
	    
	    @Override
	    public boolean canWork() {
	        if (getStackInSlot(0).isEmpty()) {
	            return false;
	        } else {
	        	MetalPressRecipe recipeBase;
	        	try {
	        		recipeBase = getRecipeHPE();  // Fik
	        		System.out.println(recipeBase);
				} catch (NullPointerException e) {
					return false;
				}
	        	
	            if (recipeBase == null) return false;
	            
	            ItemStack input = recipeBase.getInput();
	            ItemStack mold = recipeBase.getMold();
	            ItemStack itemstack = recipeBase.getOutput();
	            
	            if (!(mold.getItem() == getStackInSlot(1).getItem()) ) {
	            	System.out.println(mold + " vs " + getStackInSlot(1));
	            	return false;	   
	            }
	            	         	
	            if (getStackInSlot(0).getCount() < input.getCount()) {
	            	System.out.println("ASS");
	            	return false;
	            }
	                
	            if (itemstack.isEmpty()) {
	            	System.out.println("Nibba");
	            	return false;
	            }
	                

	            ItemStack output = getStackInSlot(2);
	            return output.isEmpty() || output.isItemEqual(itemstack) && output.getCount() + itemstack.getCount() <= output.getMaxStackSize();
	        }
	    }
	    

	    @Override
	    public int getField(int id) {
	        switch (id) {
	            case 0:
	                return totalItemFormTime;
	            case 1:
	                return currentItemFormTime;
	            case 2:
	                return currentWindup;
	            default:
	                return 0;
	        }
	    }

	    @Override
	    public void setField(int id, int value) {
	        switch (id) {
	            case 0:
	                totalItemFormTime = value;
	                break;
	            case 1:
	                currentItemFormTime = value;
	            case 2:
	                currentWindup = value;
	        }
	    }

	    @Override
	    public int getFieldCount() {
	        return 3;
	    }

	    @Override
	    public String getName() {
	        return "container.chopper";
	    }

	    @Override
	    public int getOutputSlot() {
	        return 1;
	    }

	    public float getVisualWindup() {
	        return visualWindup;
	    }

	    @Nullable
	    @Override   ///                     ???
	    public ITextComponent getDisplayName() {
	        if (valid)
	            return super.getDisplayName();
	        else
	            return super.getDisplayName(); //new TextComponentTranslation(Localization.INFO.CHOPPING_INVALID.key()).setStyle(new Style().setColor(TextFormatting.RED));
	    }

		@Override
		public HPRecipeBase getRecipe() {
			// TODO Auto-generated method stub
			return null;
		}
	    
	
}
