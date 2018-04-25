package dynamictreestc;

import com.ferreusveritas.dynamictrees.api.WorldGenRegistry;
import com.ferreusveritas.dynamictrees.api.worldgen.IBiomeSpeciesSelector;

import dynamictreestc.proxy.CommonProxy;
import dynamictreestc.worldgen.BiomeDensityProvider;
import dynamictreestc.worldgen.BiomeSpeciesSelector;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid=DynamicTreesTC.MODID, name = DynamicTreesTC.NAME, version = DynamicTreesTC.VERSION, dependencies = DynamicTreesTC.DEPENDENCIES)
public class DynamicTreesTC {
	
	public static final String MODID = "dynamictreestc";
	public static final String NAME = "Dynamic Trees TC";
	public static final String VERSION = "alpha 1";
	public static final String DEPENDENCIES = "required-after:dynamictrees@[1.12.2-0.7.6,);required-after:thaumcraft";
	
	@Mod.Instance
	public static DynamicTreesTC instance;
	
	@SidedProxy(clientSide = "dynamictreestc.proxy.ClientProxy", serverSide = "dynamictreestc.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		registerBiomeHandlers();
		
		proxy.init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		proxy.postInit();
	}
	
	public void registerBiomeHandlers() {
		if (WorldGenRegistry.isWorldGenEnabled()) {
			IBiomeSpeciesSelector biomeSpeciesSelector = new BiomeSpeciesSelector();
			WorldGenRegistry.registerBiomeTreeSelector(biomeSpeciesSelector);
			WorldGenRegistry.registerBiomeDensityProvider(new BiomeDensityProvider());
			
			biomeSpeciesSelector.init();
		}
	}
	
}
