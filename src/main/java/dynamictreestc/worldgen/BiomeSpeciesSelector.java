package dynamictreestc.worldgen;

import java.util.HashMap;
import java.util.Random;

import com.ferreusveritas.dynamictrees.DynamicTrees;
import com.ferreusveritas.dynamictrees.ModConstants;
import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.api.worldgen.IBiomeSpeciesSelector;
import com.ferreusveritas.dynamictrees.trees.Species;

import dynamictreestc.DynamicTreesTC;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import thaumcraft.common.world.biomes.BiomeHandler;

public class BiomeSpeciesSelector implements IBiomeSpeciesSelector {

	Species greatwood, silverwood, oak;
	
	HashMap<Integer, DecisionProvider> fastTreeLookup = new HashMap<Integer, DecisionProvider>();
	
	@Override
	public ResourceLocation getName() {
		return new ResourceLocation(DynamicTreesTC.MODID, "default");
	}

	@Override
	public int getPriority() {
		return 5;
	}

	@Override
	public Decision getSpecies(World world, Biome biome, BlockPos pos, IBlockState state, Random rand) {
		if (biome == null) return new Decision();
		
		int biomeId = Biome.getIdForBiome(biome);
		DecisionProvider select;
				
		if (fastTreeLookup.containsKey(biomeId)) {
			select = fastTreeLookup.get(biomeId); // Speedily look up the selector for the biome id
		} else if (biome == BiomeHandler.MAGICAL_FOREST) {
			select = new RandomDecision(rand).addSpecies(greatwood, 60).addSpecies(silverwood, 1).addSpecies(oak, 9); // TODO: replace oak with large oak
			
			fastTreeLookup.put(biomeId, select); // Cache decision for future use
		} else {
			select = new RandomDecision(rand).addUnhandled(360);
			boolean flag = false;
			
			int bi = Biome.getIdForBiome(biome);
			
			float greatwoodChance = BiomeHandler.getBiomeSupportsGreatwood(bi);
			
			if (greatwoodChance > 0) {
				((RandomDecision) select).addSpecies(greatwood, 3);
				flag = true;
			}
			
			if (greatwoodChance > 0 || biomeId == 18 || biomeId == 28) {
				((RandomDecision) select).addSpecies(silverwood, 1);
				flag = true;
			}
			
			if (!flag) select = new StaticDecision(new Decision());
			
			fastTreeLookup.put(biomeId, select); // Cache decision for future use
		}
		
		return select.getDecision();
	}

	@Override
	public void init() {
		greatwood = TreeRegistry.findSpecies(new ResourceLocation(DynamicTreesTC.MODID, "greatwood"));
		silverwood = TreeRegistry.findSpecies(new ResourceLocation(DynamicTreesTC.MODID, "silverwood"));
		oak = TreeRegistry.findSpecies(new ResourceLocation(ModConstants.MODID, "oak"));
	}
	
}

