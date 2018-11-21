package dynamictreestc.featuregen;

import java.util.List;

import com.ferreusveritas.dynamictrees.api.IPostGenFeature;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.world.biomes.BiomeHandler;

/** Supplement Thaumcraft's vishroom generation */
public class FeatureGenVishroom implements IPostGenFeature {
	
	protected int maxAttempts = 1;
	protected int chance = 6;
	
	public FeatureGenVishroom setMaxAttempts(int max) {
		this.maxAttempts = max;
		return this;
	}
	
	public FeatureGenVishroom setChance(int chance) {
		this.chance = chance;
		return this;
	}
	
	@Override
	public boolean postGeneration(World world, BlockPos rootPos, Biome biome, int radius, List<BlockPos> endPoints, SafeChunkBounds safeBounds, IBlockState initialDirtState) {
		boolean worldGen = safeBounds != SafeChunkBounds.ANY;
		if (worldGen && biome == BiomeHandler.MAGICAL_FOREST && world.rand.nextInt(chance) == 0) {
			for (int i = 0; i < this.maxAttempts; i++) {
				if (placeVishroom(world, rootPos)) return true;
			}
		}
		return false;
	}
	
	public boolean placeVishroom(World world, BlockPos rootPos) {
		BlockPos treePos = rootPos.up();
		int trunkRadius = TreeHelper.getRadius(world, treePos);
		
		EnumFacing dir = EnumFacing.HORIZONTALS[world.rand.nextInt(4)];
		BlockPos pos = rootPos.offset(dir, trunkRadius > 8 ? 2 : 1);
		if (world.rand.nextInt(2) == 0) {
			pos = pos.offset(world.rand.nextBoolean() ? dir.rotateY() : dir.rotateYCCW());
		}
		
		for (int i = 0; i < 3; i++) {
			if (BlocksTC.vishroom.canPlaceBlockAt(world, pos)) {
				world.setBlockState(pos, BlocksTC.vishroom.getDefaultState());
				return true;
			}
			pos = pos.up();
		}
		
		return false;
	}
	
}
