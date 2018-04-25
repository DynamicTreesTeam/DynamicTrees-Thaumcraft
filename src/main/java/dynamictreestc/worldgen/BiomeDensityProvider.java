package dynamictreestc.worldgen;

import java.util.Random;

import com.ferreusveritas.dynamictrees.api.worldgen.IBiomeDensityProvider;
import com.ferreusveritas.dynamictrees.trees.Species;

import dynamictreestc.DynamicTreesTC;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import thaumcraft.common.world.biomes.BiomeHandler;

public class BiomeDensityProvider implements IBiomeDensityProvider {

	@Override
	public EnumChance chance(Biome biome, Species species, int radius, Random rand) {
		if (biome == BiomeHandler.MAGICAL_FOREST) {
			if (radius >= 3) { // Start dropping tree spawn opportunities when the radius gets bigger than 3
				float chance = 2.0f / (radius);
				return rand.nextFloat() < ((Math.sqrt(chance) * 1.125f) + 0.25f) ? EnumChance.OK : EnumChance.CANCEL;
			}
			return EnumChance.CANCEL;
		}
		
		if ((species.getRegistryName().getResourcePath().equals("greatwood") || species.getRegistryName().getResourcePath().equals("silverwood")) && radius < 3) {
			return EnumChance.CANCEL;
		}
		
		return EnumChance.UNHANDLED;
	}

	@Override
	public double getDensity(Biome biome, double noiseDensity, Random rand) {
		//double naturalDensity = MathHelper.clamp((CompatHelper.getBiomeTreesPerChunk(biome)) / 10.0f, 0.0f, 1.0f);
		//double base = naturalDensity * noiseDensity;
		
		if (biome == BiomeHandler.MAGICAL_FOREST) return ((noiseDensity * 0.25) + 0.75) * 0.5; // would be 0.2, but my trees are skinnier
		
		return -1;
	}

	@Override
	public ResourceLocation getName() {
		return new ResourceLocation(DynamicTreesTC.MODID, "default");
	}

	@Override
	public int getPriority() {
		return 5;
	}
	
}
