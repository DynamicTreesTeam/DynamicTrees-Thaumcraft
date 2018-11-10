package dynamictreestc.featuregen;

import java.util.List;

import com.ferreusveritas.dynamictrees.api.IPostGenFeature;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

/** Adds a mob spawner under the tree */
public class FeatureGenMobSpawner implements IPostGenFeature {
	
	private final int depth;
	private final Class entityClass;
	
	public FeatureGenMobSpawner(Class entityClass, int depth) {
		this.entityClass = entityClass;
		this.depth = depth;
	}
	
	@Override
	public boolean postGeneration(World world, BlockPos rootPos, Biome biome, int radius, List<BlockPos> endPoints, SafeChunkBounds safeBounds, IBlockState initialDirtState) {
		BlockPos spawnerPos = rootPos.down(depth);
		world.setBlockState(spawnerPos, Blocks.MOB_SPAWNER.getDefaultState());
		TileEntityMobSpawner spawner = (TileEntityMobSpawner) world.getTileEntity(spawnerPos);
		if (spawner != null) {
			spawner.getSpawnerBaseLogic().setEntityId(EntityList.getKey(entityClass));
			return true;
		}
		return false;
	}
	
}
