package dynamictreestc.featuregen;

import java.util.List;

import com.ferreusveritas.dynamictrees.api.IPostGenFeature;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.world.biomes.BiomeHandler;

/** Supplement Thaumcraft's vishroom generation */
public class FeatureGenVishroom implements IPostGenFeature {
	
	@Override
	public boolean postGeneration(World world, BlockPos rootPos, Biome biome, int radius, List<BlockPos> endPoints, SafeChunkBounds safeBounds, IBlockState initialDirtState) {
		boolean worldGen = safeBounds != SafeChunkBounds.ANY;
		if (worldGen && biome == BiomeHandler.MAGICAL_FOREST && world.rand.nextInt(6) == 0) {
			placeVishroom(world, rootPos);
			return true;
		}
		return false;
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
