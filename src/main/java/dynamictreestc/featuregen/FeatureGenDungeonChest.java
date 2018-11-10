package dynamictreestc.featuregen;

import java.util.List;

import com.ferreusveritas.dynamictrees.api.IPostGenFeature;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;

/** Create a dungeon loot chest under the base of the tree */
public class FeatureGenDungeonChest implements IPostGenFeature {

	private final int depth;
	
	public FeatureGenDungeonChest(int depth) {
		this.depth = depth;
	}
	
	@Override
	public boolean postGeneration(World world, BlockPos rootPos, Biome biome, int radius, List<BlockPos> endPoints, SafeChunkBounds safeBounds, IBlockState initialDirtState) {
		BlockPos chestPos = rootPos.down(depth);
		world.setBlockState(chestPos, Blocks.CHEST.getDefaultState());
		TileEntityChest chest = (TileEntityChest) world.getTileEntity(chestPos);
		if (chest != null) {
			chest.setLootTable(LootTableList.CHESTS_SIMPLE_DUNGEON, world.rand.nextLong());
			return true;
		}
		return false;
	}
	
}
