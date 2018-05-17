package dynamictreestc.featuregen;

import java.util.List;

import com.ferreusveritas.dynamictrees.api.IGenFeature;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.CoordUtils;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class FeatureGenWeb implements IGenFeature {
	
	protected int qty = 16;
	protected float verSpread = 60;
	protected float rayDistance = 4;
	protected Species species;
	protected Block webBlock = Blocks.WEB;
	
	public FeatureGenWeb(Species species) {
		this.species = species;
	}
	
	public FeatureGenWeb setQuantity(int qty) {
		this.qty = qty;
		return this;
	}
	
	public FeatureGenWeb setVerSpread(float verSpread) {
		this.verSpread = verSpread;
		return this;
	}
	
	public FeatureGenWeb setRayDistance(float rayDistance) {
		this.rayDistance = rayDistance;
		return this;
	}
	
	@Override
	public void gen(World world, BlockPos treePos, List<BlockPos> endPoints, SafeChunkBounds safeBounds) {
		if (!endPoints.isEmpty()) {
			for (int i = 0; i < qty; i++) {
				BlockPos endPoint = endPoints.get(world.rand.nextInt(endPoints.size()));
				addWeb(world, species, treePos, endPoint, safeBounds);
			}
		}
	}
	
	protected void addWeb(World world, Species species, BlockPos treePos, BlockPos branchPos, SafeChunkBounds safeBounds) {
		RayTraceResult result = CoordUtils.branchRayTrace(world, species, treePos, branchPos, 90, verSpread, rayDistance, safeBounds);
		
		if (result != null) {
			BlockPos webPos = result.getBlockPos().offset(result.sideHit);
			if (webPos != BlockPos.ORIGIN && world.isAirBlock(webPos)) {
				world.setBlockState(webPos, webBlock.getDefaultState());
			}
		}
	}
	
	public static int coordHashCode(BlockPos pos) {
		int hash = (pos.getX() * 4111 ^ pos.getY() * 271 ^ pos.getZ() * 3067) >> 1;
		return hash & 0xFFFF;
	}
	
}
