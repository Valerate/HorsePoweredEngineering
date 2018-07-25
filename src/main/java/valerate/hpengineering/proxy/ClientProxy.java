package valerate.hpengineering.proxy;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import valerate.hpengineering.entity.TileEntityMetalPress;
import valerate.hpengineering.proxy.client.TileEntityMetalPressRender;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

	@Override
    public void preInit() {
        super.preInit();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMetalPress.class, new TileEntityMetalPressRender());

    }

    @Override
    public void init() {
    }

    @Override
    public void loadComplete() {

    }
}
