package valerate.hpengineering.blocks.models;

import net.minecraft.util.IStringSerializable;

public enum MetalPressModels implements IStringSerializable {
    BASE,
    BLADE;

    @Override
    public String getName() {
        return name().toLowerCase();
    }
    
    
}