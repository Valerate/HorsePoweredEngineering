package valerate.hpengineering.blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemLead;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;
import se.gory_moon.horsepower.advancements.Manager;
import se.gory_moon.horsepower.blocks.BlockFiller;
import se.gory_moon.horsepower.blocks.BlockHPBase;
import se.gory_moon.horsepower.blocks.property.PropertyUnlistedString;
import se.gory_moon.horsepower.tileentity.TileEntityHPBase;
import se.gory_moon.horsepower.tileentity.TileEntityHPHorseBase;
import se.gory_moon.horsepower.util.RenderUtils;
import se.gory_moon.horsepower.util.Utils;
import valerate.hpengineering.blocks.models.MetalPressModels;
import valerate.hpengineering.entity.TileEntityMetalPress;
import valerate.hpengineering.init.BlockInit;
import valerate.hpengineering.recipes.MetalPressRecipe;

public class MetalPress extends BlockHPBase  {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", Arrays.asList(EnumFacing.HORIZONTALS));
    public static final PropertyEnum<MetalPressModels> PART = PropertyEnum.create("part", MetalPressModels.class);
    
    public static final PropertyUnlistedString SIDE_TEXTURE = new PropertyUnlistedString("side_texture");
    public static final PropertyUnlistedString TOP_TEXTURE = new PropertyUnlistedString("top_texture");


    private static final AxisAlignedBB BOUND_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1D, 1.0D + 12D/16D, 1D);
    private static final AxisAlignedBB COLLISION_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1D, 1.0D + 3D/16D, 1D);
	
    public MetalPress() {
    	super(Material.IRON);
        setHardness(5.0F);
        setResistance(5.0F);
        setHarvestLevel("pickaxe", 1);
        setSoundType(SoundType.METAL);
        setRegistryName("metalpress");
        setUnlocalizedName("metalpress");
	}
    
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return COLLISION_AABB;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return COLLISION_AABB;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean hasCustomBreakingProgress(IBlockState state) {
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[] {PART, FACING}, new IUnlistedProperty[]{SIDE_TEXTURE, TOP_TEXTURE});
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        if (!((World) world).isRemote && pos.up().equals(neighbor) && !(world.getBlockState(neighbor).getBlock() instanceof BlockFiller)) {
            ((World) world).setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            EnumFacing enumfacing = state.getValue(FACING);
            worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing).withProperty(PART, MetalPressModels.BASE), 2);
        }
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(PART, MetalPressModels.BASE);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }

        return getDefaultState().withProperty(FACING, enumfacing).withProperty(PART, MetalPressModels.BASE);
    }

    @Override
    public void emptiedOutput(World world, BlockPos pos) {}

    @Override
    public void onWorkerAttached(EntityPlayer playerIn, EntityCreature creature) {
        if (playerIn instanceof EntityPlayerMP) {
           //Manager.USE_METALFORMER.trigger((EntityPlayerMP) playerIn);  ///////////////// <--- Fix
        }
    }

    @Nonnull
    @Override
    public Class<?> getTileClass() {
        return TileEntityMetalPress.class;
    }
    
    
    
    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState extendedState = (IExtendedBlockState) state;

        TileEntityHPBase tile = getTileEntity(world, pos);
        if (tile != null) {
            return getExtendedState(tile, tile.getExtendedState(extendedState));
        }

        return super.getExtendedState(state, world, pos);
    }

    private void writeDataOntoItemstack(@Nonnull ItemStack item, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, boolean inventorySave) {
        // get block data from the block
        TileEntity te = world.getTileEntity(pos);
        if(te != null && (te instanceof TileEntityMetalPress || te instanceof TileEntityMetalPress)) {
            NBTTagCompound tag = item.hasTagCompound() ? item.getTagCompound(): new NBTTagCompound();

            // texture
            NBTTagCompound data = te.getTileData().getCompoundTag("textureBlock");

            if (!data.hasNoTags()) {
                tag.setTag("textureBlock", data);
            }

            if (!tag.hasNoTags()) {
                item.setTagCompound(tag);
            }
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        List<ItemStack> drops = new ArrayList<>();
        Item item = this.getItemDropped(state, world.rand, 0);
        if (item != Items.AIR) {
            drops.add(new ItemStack(item, 1, this.damageDropped(state)));
        }

        if(drops.size() > 0) {
            ItemStack stack = drops.get(0);
            writeDataOntoItemstack(stack, world, pos, state, false);
            return stack;
        }

        return super.getPickBlock(state, target, world, pos, player);
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
        if(!worldIn.isRemote && !worldIn.restoringBlockSnapshots) {

            List<ItemStack> items = this.getDrops(worldIn, pos, state, fortune);
            chance = net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, fortune, chance, false, harvesters.get());

            for(ItemStack item : items) {
                // save the data from the block onto the item
                if(item.getItem() == Item.getItemFromBlock(this)) {
                    writeDataOntoItemstack(item, worldIn, pos, state, chance >= 1f);
                }
            }

            for(ItemStack item : items) {
                if(worldIn.rand.nextFloat() <= chance) {
                    spawnAsEntity(worldIn, pos, item);
                }
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound(): new NBTTagCompound();
        TileEntityHPBase tile = getTileEntity(worldIn, pos);
        if (tile == null)
            return;
        NBTTagCompound baseTag = tag != null ? tag.getCompoundTag("textureBlock"): new NBTTagCompound();
        tile.getTileData().setTag("textureBlock", baseTag);
    }

    public static IExtendedBlockState getExtendedState(TileEntityHPBase te, IExtendedBlockState state) {
        String side_texture = te.getTileData().getString("side_texture");
        String top_texture = te.getTileData().getString("top_texture");

        if (side_texture.isEmpty() || top_texture.isEmpty()) {
            ItemStack stack = new ItemStack(te.getTileData().getCompoundTag("textureBlock"));
            if (!stack.isEmpty() && te.getWorld().isRemote) {
                Block block = Block.getBlockFromItem(stack.getItem());
                IBlockState state1 = block.getStateFromMeta(stack.getMetadata());
                side_texture = RenderUtils.getTextureFromBlockstate(state1).getIconName();
                top_texture = RenderUtils.getTopTextureFromBlockstate(state1).getIconName();
                te.getTileData().setString("side_texture", side_texture);
                te.getTileData().setString("top_texture", top_texture);
            }
        }

        if (!side_texture.isEmpty())
            state = state.withProperty(SIDE_TEXTURE, side_texture);
        if (!top_texture.isEmpty())
            state = state.withProperty(TOP_TEXTURE, top_texture);

        return state;
    }
    
    @Override
    public int getSlot(IBlockState state, float hitX, float hitY, float hitZ) {
		if (hitY >= 0.65) return 1;
        return 0;
    }
    
     
    
    /// OVVERIDE ?
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        TileEntityHPBase te = (TileEntityHPBase) worldIn.getTileEntity(pos);
        TileEntityHPHorseBase teH = null;
        if (te == null) return false;
        if (te instanceof TileEntityHPHorseBase)
            teH = (TileEntityHPHorseBase) te;
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        EntityCreature creature = null;
        if (teH != null) {
            ArrayList<Class<? extends EntityCreature>> clazzes = Utils.getCreatureClasses();
            search:
            for (Class<? extends Entity> clazz : clazzes) {
                for (Object entity : worldIn.getEntitiesWithinAABB(clazz, new AxisAlignedBB((double) x - 7.0D, (double) y - 7.0D, (double) z - 7.0D, (double) x + 7.0D, (double) y + 7.0D, (double) z + 7.0D))) {
                    if (entity instanceof EntityCreature) {
                        EntityCreature tmp = (EntityCreature) entity;
                        if ((tmp.getLeashed() && tmp.getLeashHolder() == playerIn)) {
                            creature = tmp;
                            break search;
                        }
                    }
                }
            }
        }
        if (teH != null && ((stack.getItem() instanceof ItemLead && creature != null) || creature != null)) {
            if (!teH.hasWorker()) {
                creature.clearLeashed(true, false);
                teH.setWorker(creature);
                onWorkerAttached(playerIn, creature);
                return true;
            } else {
                return false;
            }
        } else if (!stack.isEmpty() && te.isItemValidForSlot(0, stack)) {
            ItemStack itemStack = te.getStackInSlot(0);
            boolean flag = false;

            if (itemStack.isEmpty()) {
                te.setInventorySlotContents(0, stack.copy());
                stack.setCount(stack.getCount() - te.getInventoryStackLimit(stack));
                flag = true;
            } else if (TileEntityHPBase.canCombine(itemStack, stack)) {
                int i = Math.min(te.getInventoryStackLimit(stack), stack.getMaxStackSize()) - itemStack.getCount();
                int j = Math.min(stack.getCount(), i);
                stack.shrink(j);
                itemStack.grow(j);
                flag = j > 0;
            }

            if (flag)
                return true;
        }
        
        
        
        int slot = getSlot(state.getBlock().getExtendedState(state, worldIn, pos), hitX, hitY, hitZ);
        ItemStack result = ItemStack.EMPTY;
        if (hand != EnumHand.OFF_HAND) {
        	if (stack.isEmpty() || stack == te.getStackInSlot(0) ){
        		result = te.removeStackFromSlot(slot);
        	}else if (slot == 1 && te.getStackInSlot(1).isEmpty() && MetalPressRecipe.molds.get(stack.getUnlocalizedName()) != null ){
        		te.setInventorySlotContents(1, new ItemStack(stack.getItem()));
        		stack.setCount(-1);
        		if (stack.getCount() < 1) {
        			stack = new ItemStack(Blocks.AIR);
        		}
        	}
        }

        if (result.isEmpty()) {
            if (!stack.isEmpty())
                return false;
            if (teH != null && playerIn.isSneaking())
                teH.setWorkerToPlayer(playerIn);
        }

        if (!result.isEmpty()) {
            ItemHandlerHelper.giveItemToPlayer(playerIn, result, EntityEquipmentSlot.MAINHAND.getSlotIndex());
        }
        	

        te.markDirty();
        return true;
    }
    

   /* @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(Localization.ITEM.HORSE_CHOPPING.SIZE.translate(Colors.WHITE.toString(), Colors.LIGHTGRAY.toString()));
        tooltip.add(Localization.ITEM.HORSE_CHOPPING.LOCATION.translate());
        tooltip.add(Localization.ITEM.HORSE_CHOPPING.USE.translate());*/
}
