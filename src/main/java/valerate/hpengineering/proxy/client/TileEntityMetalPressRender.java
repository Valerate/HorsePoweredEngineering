package valerate.hpengineering.proxy.client;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import se.gory_moon.horsepower.blocks.BlockHPBase;
import se.gory_moon.horsepower.tileentity.TileEntityHPBase;
import valerate.hpengineering.blocks.MetalPress;
import valerate.hpengineering.blocks.models.MetalPressModels;
import valerate.hpengineering.entity.TileEntityMetalPress;
import se.gory_moon.horsepower.util.RenderUtils;

public class TileEntityMetalPressRender extends TileEntityBaseRender<TileEntityMetalPress> {
	
	
	 @Override
	   public void render(TileEntityMetalPress te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

	        Tessellator tessellator = Tessellator.getInstance();
	        BufferBuilder buffer = tessellator.getBuffer();
	        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
	        IBlockState blockState = te.getWorld().getBlockState( te.getPos() );
	        System.out.println( te.getPos() );
	        if (!(blockState.getBlock() instanceof BlockHPBase)) return;
	        
	        
	        
	       

	        IBlockState bladeState = blockState.withProperty(MetalPress.PART, MetalPressModels.BLADE);
	        if (!(bladeState.getBlock() instanceof BlockHPBase)) return;
	        IBakedModel bladeModel = dispatcher.getBlockModelShapes().getModelForState(bladeState);
	        setRenderSettings();
	        preDestroyRender(destroyStage);
	        

	        buffer.begin( GL11.GL_QUADS, DefaultVertexFormats.BLOCK );
	        // The translation ensures the vertex buffer positions are relative to 0,0,0 instead of the block pos
	        // This makes the translations that follow much easier
	        buffer.setTranslation( -te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ() );

	        if (destroyStage >= 0) {
	            buffer.noColor();
	            renderBlockDamage(bladeState, te.getPos(), getDestroyBlockIcon(destroyStage), te.getWorld());
	        } else
	            dispatcher.getBlockModelRenderer().renderModel( te.getWorld(), bladeModel, blockState, te.getPos(), buffer, false );

	        buffer.setTranslation( 0, 0, 0 );

	        GlStateManager.pushMatrix();
	        GlStateManager.translate( x, y, z );

	        // Apply GL transformations relative to the center of the block: 1) TE rotation and 2) crank rotation
	        //GlStateManager.translate( 0.5, 0.5, 0.5 );
	        GlStateManager.translate( 0, 1+ te.getVisualWindup(), 0 );
	        //GlStateManager.translate( -0.5, -0.5, -0.5 );

	        tessellator.draw();
	        GlStateManager.popMatrix();
	        buffer.setTranslation(0.0D, 0.0D, 0.0D);
	        postDestroyRender(destroyStage);
	        RenderHelper.enableStandardItemLighting();
	        
	        
	        
	        
	        //renderLeach(x + 0.5, y + 4, z + 0.5, x + 0.5, y + 0.2, z + 0.5, x + 0.5, y + 1.7, z + 0.5);

	        if (te.hasWorker())
	            renderLeash(te.getWorker(), x, y, z, 0D, 1.1D, 0D, partialTicks, te.getPos());

	        GlStateManager.pushMatrix();
	        GlStateManager.translate(x, y, z);
	        if (!te.getStackInSlot(0).isEmpty())
	            renderStillItem(te, te.getStackInSlot(0), 0.5F, 0.30F, 0.5F, 1.3F);
	        GlStateManager.popMatrix();

	        GlStateManager.pushMatrix();
	        GlStateManager.translate(x, y, z);
	        if (!te.getStackInSlot(1).isEmpty())
	            renderItemMold(te, te.getStackInSlot(1), 0.5F, te.getVisualWindup()+1.35F, 0.5F, 1.3F);
	        GlStateManager.popMatrix();

	        GlStateManager.pushMatrix();
	        GlStateManager.translate(x, y, z);
	        if (!te.getStackInSlot(2).isEmpty())
	            renderStillItem(te, te.getStackInSlot(2), 0.5F, 0.30F, 0.5F, 1.3F);
	        GlStateManager.popMatrix();

	        GlStateManager.pushMatrix();
	        GlStateManager.translate(x, y, z);
	        drawDisplayText(te, x, y + 1, z);

	        if (!te.isValid())
	            RenderUtils.renderInvalidArea(te.getWorld(), te.getPos(), 0);
	        GlStateManager.popMatrix();
	    }
	 
	 private void renderItemMold(TileEntityHPBase te, ItemStack stack, float x, float y, float z, float scale) {
	        RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
	        if (stack != null) {
	            GlStateManager.translate(x, y, z);
	            EntityItem entityitem = new EntityItem(te.getWorld(), 0.0D, 0.0D, 0.0D, stack.copy());
	            entityitem.hoverStart = 0.0F;
	            GlStateManager.pushMatrix();
	            GlStateManager.disableLighting();

	            GlStateManager.rotate(-90, 1, 1.0F, 0);
	            GlStateManager.scale(0.5F * scale, 0.5F * scale, 0.5F * scale);
	            GlStateManager.pushAttrib();
	            RenderHelper.enableStandardItemLighting();
	            itemRenderer.renderItem(entityitem.getItem(), ItemCameraTransforms.TransformType.FIXED);
	            RenderHelper.disableStandardItemLighting();
	            GlStateManager.popAttrib();

	            GlStateManager.enableLighting();
	            GlStateManager.popMatrix();
	        }
	 }
}
