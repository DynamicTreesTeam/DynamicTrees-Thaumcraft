package dynamictreestc.proxy;

import java.util.Random;

import com.ferreusveritas.dynamictrees.ModConfigs;
import com.ferreusveritas.dynamictrees.ModConstants;
import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.api.WorldGenRegistry;
import com.ferreusveritas.dynamictrees.api.worldgen.IBiomeSpeciesSelector;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.google.common.base.Optional;

import dynamictreestc.DynamicTreesTC;
import dynamictreestc.dropcreators.DropCreatorFruit;
import dynamictreestc.worldgen.BiomeDensityProvider;
import dynamictreestc.worldgen.BiomeSpeciesSelector;
import dynamictreestc.worldgen.DecorateEventHandler;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.world.ThaumcraftWorldGenerator;
import thaumcraft.common.world.biomes.BiomeHandler;

public class CommonProxy {
	
	public void preInit() {
		if (WorldGenRegistry.isWorldGenEnabled()) {
			MinecraftForge.TERRAIN_GEN_BUS.register(new DecorateEventHandler());
			/* 
			 * The only way to prevent Thaumcraft's trees from generating is to prevent
			 * all of Thaumcraft's vegetation from generating, so we register a new
			 * generator to replace cinderpearl generation.
			 */
			GameRegistry.registerWorldGenerator(new IWorldGenerator() {
				@Override
				public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
					int blacklist = BiomeHandler.getDimBlacklist(world.provider.getDimension());
					
					if ((blacklist == -1) && (!world.getWorldInfo().getTerrainType().getName().startsWith("flat"))) {
						Biome bgb = world.getBiome(new BlockPos(chunkX * 16 + 8, 50, chunkZ * 16 + 8));
					    if (BiomeHandler.getBiomeBlacklist(Biome.getIdForBiome(bgb)) != -1) return;
						
					    int randPosX = chunkX * 16 + 8;
					    int randPosZ = chunkZ * 16 + 8;
					    BlockPos bp = world.getHeight(new BlockPos(randPosX, 0, randPosZ));
					    
					    if (world.getBiome(bp).topBlock.getBlock() == Blocks.SAND && world.getBiome(bp).getTemperature(bp) > 1.0F && random.nextInt(30) == 0) {
					    	ThaumcraftWorldGenerator.INSTANCE.generateFlowers(world, random, bp, BlocksTC.cinderpearl, 0);
					    }
					}
				}
			}, 0);
				
			ModConfig.CONFIG_WORLD.generateTrees = false; // Disable Thaumcraft's vegetation generation
		}
	}

	public void init() {
		Species silverwood = TreeRegistry.findSpecies(new ResourceLocation(DynamicTreesTC.MODID, "silverwood"));
		silverwood.addDropCreator(new DropCreatorFruit(new ItemStack(ItemsTC.nuggets, 1, 5)).setRarity(0.75f));
		
		if (WorldGenRegistry.isWorldGenEnabled()) {
			IBiomeSpeciesSelector biomeSpeciesSelector = new BiomeSpeciesSelector();
			WorldGenRegistry.registerBiomeTreeSelector(biomeSpeciesSelector);
			WorldGenRegistry.registerBiomeDensityProvider(new BiomeDensityProvider());
			
			biomeSpeciesSelector.init();
		}
	}
	
	public void postInit() {
		
	}
	
}
