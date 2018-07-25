package valerate.hpengineering.recipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import net.minecraft.item.ItemStack;
import se.gory_moon.horsepower.util.Utils;

public class MetalPressRecipe {

	public static HashMap<String, HashMap<String, MetalPressRecipe>> RECIPELIST = new HashMap<String , HashMap<String, MetalPressRecipe>>();
	public static HashMap<String, ItemStack> molds = new HashMap<String, ItemStack>();
	
	//mold {Input,recipe}
	
	private ItemStack input;
    private ItemStack output;
    private ItemStack mold;
    private int time;
	
	public MetalPressRecipe(ItemStack input, ItemStack output, ItemStack mold, int time) {
		
        this.input = input;
        this.output = output;
        this.time = time;
        this.mold = mold;
        HashMap<String, MetalPressRecipe> check = RECIPELIST.get(mold.getUnlocalizedName());
        if ((check == null)) {
        	RECIPELIST.put(mold.getUnlocalizedName(), new HashMap<String,MetalPressRecipe>());
        	RECIPELIST.get(mold.getUnlocalizedName()).put(input.getUnlocalizedName(), this);
        }else if (check.get(input.getUnlocalizedName()) == null) {
        	check.put(input.getUnlocalizedName(), this);
        }
        
	}
	
	public ItemStack getMetalPressResult(ItemStack input, ItemStack mold) {
		
		if (RECIPELIST.get(mold.getUnlocalizedName()) != null) {
        	if (compareItemStacks( RECIPELIST.get(mold.getUnlocalizedName()).get(input.getUnlocalizedName()).input, input)) {
        		return RECIPELIST.get(mold.getUnlocalizedName()).get(input.getUnlocalizedName()).getOutput();
        	}
        }
		return ItemStack.EMPTY;
	}
	
	private boolean compareItemStacks(ItemStack stack1, ItemStack stack2) {
		
		return stack2.getItem() == stack1.getItem() && (stack2.getMetadata() == 32767 || stack2.getMetadata() == stack1.getMetadata());
	}
	
	public ItemStack getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }


    public ItemStack getMold() {
        return mold;
    }

    public int getTime() {
        return time;
    }

    public static ItemStack getWithSize(ItemStack stack, int size) {
        stack.setCount(size);
        return stack;
    }


    @Override
    public int hashCode() {
        int result = Utils.getItemStackHashCode(input);
        result = 31 * result + Utils.getItemStackHashCode(output);
        result = 31 * result + Utils.getItemStackHashCode(mold);
        result = 31 * result + time;
        return result;
    }

    @Override
    public String toString() {
        return "  "  + input + " with mold " + mold  + " creates " + output + " and takes " + time;
    }
	
}
