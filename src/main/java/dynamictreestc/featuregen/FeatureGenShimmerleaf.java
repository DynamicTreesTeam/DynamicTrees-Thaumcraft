package dynamictreestc.featuregen;

import java.util.List;

import com.ferreusveritas.dynamictrees.api.IPostGenFeature;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.world.objects.WorldGenCustomFlowers;

public class FeatureGenShimmerleaf implements IPostGenFeature {

	@Override
	public boolean postGeneration(World world, BlockPos rootPos, Biome biome, int radius, List<BlockPos> endPoints, SafeChunkBounds safeBounds, IBlockState initialDirtState) {
		
		if (safeBounds != SafeChunkBounds.ANY) {
			WorldGenerator flowers = new WorldGenCustomFlowers(BlocksTC.shimmerleaf, 0);
			flowers.generate(world, world.rand, rootPos.up());
			return true;
		}
		
		return false;
	}
	
}
