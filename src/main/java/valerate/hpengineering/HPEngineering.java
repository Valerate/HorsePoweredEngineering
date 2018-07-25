package valerate.hpengineering;

import java.io.File;
import java.util.HashMap;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import valerate.hpengineering.init.BlockInit;
import valerate.hpengineering.recipes.MetalPressRecipe;

@Mod(modid = HPEngineering.MODID, name = HPEngineering.NAME, version = HPEngineering.VERSION, dependencies = "required-after:horsepower")
public class HPEngineering {
	
	static {OBJLoader.INSTANCE.addDomain(HPEngineering.MODID);
    }

	public static final String MODID = "hpengineering";
	public static final String NAME = "Horse Powered Engineering";
	public static final String VERSION = "1.0";
	public static final String CLIENT = "valerate.hpengineering.proxy.ClientProxy";
	public static final String COMMON = "valerate.hpengineering.proxy.CommonProxy";
	
	
	public static File config;
	
	@Instance
	public static HPEngineering instance;
	
	@SidedProxy(clientSide = CLIENT, serverSide = COMMON)
	public static valerate.hpengineering.proxy.CommonProxy proxy; 

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit();
        //PacketHandler.init();

        //FMLInterModComms.sendMessage("waila", "register", Reference.WAILA_PROVIDER);

        BlockInit.registerTileEntities();

        //if (Loader.isModLoaded("crafttweaker"))
        //    tweakerPlugin = new TweakerPluginImpl();

        //tweakerPlugin.register();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
        //ModItems.registerRecipes();
    }

    @EventHandler
    public void loadComplete(FMLPostInitializationEvent event) {
        //tweakerPlugin.getRemove().forEach(IHPAction::run);
        //tweakerPlugin.getAdd().forEach(IHPAction::run);

        //HPEventHandler.reloadConfig();
        proxy.loadComplete();
        new MetalPressRecipe(new ItemStack(Blocks.IRON_BLOCK), new ItemStack(Items.IRON_INGOT,9) , ItemStack.EMPTY, 5);
        new MetalPressRecipe(new ItemStack(Blocks.GLOWSTONE), new ItemStack(Items.GLOWSTONE_DUST,4),  ItemStack.EMPTY, 5);
        MetalPressRecipe.molds.put(Blocks.AIR.getUnlocalizedName(),new ItemStack(Blocks.AIR));
        MetalPressRecipe.molds.put(Items.STICK.getUnlocalizedName(), new ItemStack(Items.STICK));
        new MetalPressRecipe(new ItemStack(Blocks.IRON_BLOCK), new ItemStack(Items.IRON_DOOR,2) ,new ItemStack(Items.STICK), 5);
        
        
        
        for (HashMap<String, MetalPressRecipe> recipeList: MetalPressRecipe.RECIPELIST.values()) {
        	System.out.println(recipeList);
        	for (MetalPressRecipe recipe: recipeList.values()) {
        		System.out.println(recipe.toString());
        	}
        	
        }
    }

    @EventHandler
    public void serverLoad(FMLServerAboutToStartEvent event) {
        //HPRecipes.instance().reloadRecipes();
        //Utils.sendSavedErrors();
    }

    @EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent event) {

    }
}
