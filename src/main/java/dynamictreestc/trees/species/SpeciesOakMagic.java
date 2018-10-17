package dynamictreestc.trees.species;

import java.util.List;
import java.util.Random;

import com.ferreusveritas.dynamictrees.ModBlocks;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.items.Seed;
import com.ferreusveritas.dynamictrees.trees.SpeciesRare;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;

import dynamictreestc.DynamicTreesTC;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary.Type;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.world.biomes.BiomeHandler;

public class SpeciesOakMagic extends SpeciesRare {
	
	public SpeciesOakMagic(TreeFamily treeFamily) {
		super(new ResourceLocation(DynamicTreesTC.MODID, treeFamily.getName().getResourcePath() + "magic"), treeFamily, ModBlocks.oakLeavesProperties);
		
		setBasicGrowingParameters(0.1f, 14.0f, 4, 4, 1.25f);
		
		envFactor(Type.COLD, 0.75f);
		envFactor(Type.HOT, 0.50f);
		envFactor(Type.DRY, 0.50f);
		envFactor(Type.FOREST, 1.05f);
		
		setupStandardSeedDropping();
	}
	
	@Override
	public boolean isBiomePerfect(Biome biome) {
		return isOneOfBiomes(biome, BiomeHandler.MAGICAL_FOREST);
	}
	
	@Override
	public boolean isAcceptableSoil(World world, BlockPos pos, IBlockState soilBlockState) {
		return super.isAcceptableSoil(world, pos, soilBlockState) || soilBlockState.getBlock() instanceof BlockDirt || soilBlockState.getBlock() instanceof BlockGrass;
	}
	
	@Override
	public boolean rot(World world, BlockPos pos, int neighborCount, int radius, Random random, boolean rapid) {
		if (super.rot(world, pos, neighborCount, radius, random, rapid)) {
			if (radius > 4 && TreeHelper.isRooty(world.getBlockState(pos.down())) && world.getLightFor(EnumSkyBlock.SKY, pos) < 4) {
				world.setBlockState(pos, BlocksTC.vishroom.getDefaultState()); // Change branch to a mushroom
			}
			return true;
		}
		return false;
	}
	
	@Override
	public ItemStack getSeedStack(int qty) {
		return getFamily().getCommonSpecies().getSeedStack(qty);
	}
	
	@Override
	public Seed getSeed() {
		return getFamily().getCommonSpecies().getSeed();
	}
	
	@Override
	public void postGeneration(World world, BlockPos rootPos, Biome biome, int radius, List<BlockPos> endPoints, SafeChunkBounds safeBounds, IBlockState initialDirtState) {
		super.postGeneration(world, rootPos, biome, radius, endPoints, safeBounds, initialDirtState);
		
		// Supplement Thaumcraft's vishroom generation
		if (safeBounds != SafeChunkBounds.ANY && biome == BiomeHandler.MAGICAL_FOREST && world.rand.nextInt(6) == 0) {
			placeVishroom(world, rootPos);
		}
	}
	
	public void placeVishroom(World world, BlockPos rootPos) {
		EnumFacing dir = EnumFacing.HORIZONTALS[world.rand.nextInt(4)];
		BlockPos pos = rootPos.offset(dir);
		EnumFacing dir2 = EnumFacing.HORIZONTALS[world.rand.nextInt(4)];
		if (dir2 != dir && dir2 != dir.getOpposite()) pos = pos.offset(dir2);
		
		for (int i = 0; i < 3; i++) {
			if (BlocksTC.vishroom.canPlaceBlockAt(world, pos)) {
				world.setBlockState(pos, BlocksTC.vishroom.getDefaultState());
				break;
			}
			pos = pos.up();
		}
	}
	
}
