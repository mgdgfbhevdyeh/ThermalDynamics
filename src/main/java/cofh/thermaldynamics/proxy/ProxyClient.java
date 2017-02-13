package cofh.thermaldynamics.proxy;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.render.block.BlockRenderingRegistry;
import cofh.core.render.IconRegistry;
import cofh.thermaldynamics.ThermalDynamics;
import cofh.thermaldynamics.core.TickHandlerClient;
import cofh.thermaldynamics.debughelper.CommandServerDebug;
import cofh.thermaldynamics.duct.BlockDuct;
import cofh.thermaldynamics.duct.TDDucts;
import cofh.thermaldynamics.duct.entity.EntityTransport;
import cofh.thermaldynamics.duct.entity.RenderTransport;
import cofh.thermaldynamics.duct.entity.SoundWoosh;
import cofh.thermaldynamics.duct.fluid.TileFluidDuct;
import cofh.thermaldynamics.duct.item.TileItemDuct;
import cofh.thermaldynamics.duct.item.TileItemDuctEnder;
import cofh.thermaldynamics.render.RenderDuct;
import cofh.thermaldynamics.render.RenderDuctFluids;
import cofh.thermaldynamics.render.RenderDuctItems;
import cofh.thermaldynamics.render.RenderDuctItemsEnder;
import cofh.thermaldynamics.render.item.RenderItemCover;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.Item;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ProxyClient extends Proxy {

	public static EnumBlockRenderType renderType;

	/* INIT */
	@Override
	public void preInit(FMLPreInitializationEvent event) {

		FMLCommonHandler.instance().bus().register(TickHandlerClient.instance);

		for (BlockDuct duct : ThermalDynamics.blockDuct) {
			StateMap.Builder stateMapBuilder = new StateMap.Builder();
			stateMapBuilder.ignore(BlockDuct.META);
			ModelLoader.setCustomStateMapper(duct, stateMapBuilder.build());
			ModelRegistryHelper.registerItemRenderer(Item.getItemFromBlock(duct), RenderDuct.instance);
		}

		String[] names = { "basic", "hardened", "reinforced", "signalum", "resonant" };
		Item[] items = { ThermalDynamics.itemFilter, ThermalDynamics.itemRetriever, ThermalDynamics.itemServo };
		for (Item item : items) {
			for (int i = 0; i < names.length; i++) {
				ModelResourceLocation location = new ModelResourceLocation("thermaldynamics:attachment", "type=" + item.getRegistryName().getResourcePath() + "_" + names[i]);
				ModelLoader.setCustomModelResourceLocation(item, i, location);
			}
		}
		ModelResourceLocation location = new ModelResourceLocation("thermaldynamics:attachment", "type=relay");
		ModelLoader.setCustomModelResourceLocation(ThermalDynamics.itemRelay, 0, location);

		ModelRegistryHelper.registerItemRenderer(ThermalDynamics.itemCover, RenderItemCover.instance);

		ClientCommandHandler.instance.registerCommand(new CommandServerDebug());

		//MinecraftForgeClient.registerItemRenderer(ThermalDynamics.itemCover, RenderItemCover.instance);
		RenderingRegistry.registerEntityRenderingHandler(EntityTransport.class, new IRenderFactory<EntityTransport>() {
			@Override
			public Render<? super EntityTransport> createRenderFor(RenderManager manager) {

				return new RenderTransport(manager);
			}
		});
		GameRegistry.register(SoundWoosh.WOOSH);
	}

	@Override
	public void initialize(FMLInitializationEvent event) {

		ClientRegistry.bindTileEntitySpecialRenderer(TileItemDuctEnder.class, RenderDuctItemsEnder.instance);
		ClientRegistry.bindTileEntitySpecialRenderer(TileItemDuct.class, RenderDuctItems.instance);
		ClientRegistry.bindTileEntitySpecialRenderer(TileFluidDuct.class, RenderDuctFluids.instance);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {

		ProxyClient.renderType = BlockRenderingRegistry.createRenderType("TD");
		BlockRenderingRegistry.registerRenderer(ProxyClient.renderType, RenderDuct.instance);
	}

	@Override
	@SideOnly (Side.CLIENT)
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {

		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 2; j++) {
				IconRegistry.addIcon("ServoBase" + (i * 2 + j), "thermaldynamics:blocks/duct/attachment/servo/servo_base_" + i + "" + j, event.getMap());
				IconRegistry.addIcon("RetrieverBase" + (i * 2 + j), "thermaldynamics:blocks/duct/attachment/retriever/retriever_base_" + i + "" + j, event.getMap());
			}
		}

		IconRegistry.addIcon("Signaller", "thermaldynamics:blocks/duct/attachment/signallers/signaller", event.getMap());

		IconRegistry.addIcon("CoverBase", "thermaldynamics:blocks/duct/attachment/cover/support", event.getMap());

		for (int i = 0; i < 5; i++) {
			IconRegistry.addIcon("FilterBase" + i, "thermaldynamics:blocks/duct/attachment/filter/filter_" + i + "0", event.getMap());
		}
		IconRegistry.addIcon("SideDucts", "thermaldynamics:blocks/duct/side_ducts", event.getMap());

		for (int i = 0; i < TDDucts.ductList.size(); i++) {
			if (TDDucts.isValid(i)) {
				TDDucts.ductList.get(i).registerIcons(event.getMap());
			}
		}
		TDDucts.structureInvis.registerIcons(event.getMap());
	}

	@Override
	@SideOnly (Side.CLIENT)
	@SubscribeEvent
	public void initializeIcons(TextureStitchEvent.Post event) {

		RenderDuct.initialize();
	}

}
