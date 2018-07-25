package valerate.hpengineering.init;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import se.gory_moon.horsepower.blocks.BlockFiller;
import se.gory_moon.horsepower.items.ItemBlockDouble;
import valerate.hpengineering.HPEngineering;
import valerate.hpengineering.blocks.MetalPress;
import valerate.hpengineering.entity.TileEntityMetalPress;

public class BlockInit {


    public static final MetalPress BLOCK_CHOPPER = new MetalPress();
    public static final BlockFiller BLOCK_CHOPPER_FILLER = (BlockFiller) new BlockFiller(Material.IRON, "metalpress_", true).setHarvestLevel1("axe", 0).setHardness(5F).setResistance(5F);

    @Mod.EventBusSubscriber(modid = HPEngineering.MODID)
    public static class RegistrationHandler {
        public static final Set<ItemBlock> ITEM_BLOCKS = new HashSet<>();

        /**
         * Register this mod's {@link Block}s.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event) {
            final IForgeRegistry<Block> registry = event.getRegistry();

            final Block[] blocks = {BLOCK_CHOPPER, BLOCK_CHOPPER_FILLER};

            registry.registerAll(blocks);
        }

        /**
         * Register this mod's {@link ItemBlock}s.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
            final ItemBlock[] items = {
                new ItemBlockDouble(BLOCK_CHOPPER, BLOCK_CHOPPER_FILLER),

            };

            final IForgeRegistry<Item> registry = event.getRegistry();

            for (final ItemBlock item : items) {
                registry.register(item.setRegistryName(item.getBlock().getRegistryName()));
                ITEM_BLOCKS.add(item);
            }
        }
    }

    @SuppressWarnings("deprecation")
	public static void registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntityMetalPress.class, "hpengineering:" + TileEntityMetalPress.class.getSimpleName().replaceFirst("TileEntity", ""));
    }


}
