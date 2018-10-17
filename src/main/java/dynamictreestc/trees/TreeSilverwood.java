package dynamictreestc.trees;

import java.util.List;
import java.util.Random;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.blocks.BlockBranchBasic;
import com.ferreusveritas.dynamictrees.blocks.BlockDynamicSapling;
import com.ferreusveritas.dynamictrees.systems.GrowSignal;
import com.ferreusveritas.dynamictrees.systems.dropcreators.DropCreatorSeed;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;

import dynamictreestc.DynamicTreesTC;
import dynamictreestc.ModContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary.Type;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.world.biomes.BiomeHandler;
import thaumcraft.common.world.objects.WorldGenCustomFlowers;

public class TreeSilverwood extends TreeFamily {
	
	public class SpeciesSilverwood extends Species {
		
		SpeciesSilverwood(TreeFamily treeFamily) {
			super(treeFamily.getName(), treeFamily, ModContent.silverwoodLeavesProperties);
			
			setBasicGrowingParameters(0.4f, 12.0f, 6, 4, 0.75f);
			
			setDynamicSapling(new BlockDynamicSapling("silverwoodsapling").getDefaultState());
			
			envFactor(Type.COLD, 0.75f);
			envFactor(Type.HOT, 0.75f);
			envFactor(Type.DRY, 0.5f);
			envFactor(Type.FOREST, 1.05f);
			envFactor(Type.MAGICAL, 1.1f);
			
			generateSeed();
			
			addDropCreator(new DropCreatorSeed(0.25f) {
				@Override
				public List<ItemStack> getHarvestDrop(World world, Species species, BlockPos leafPos, Random random, List<ItemStack> dropList, int soilLife, int fortune) {
					int chance = 176;
					if (fortune > 0) {
						chance -= 2 << fortune;
						if (chance < 10) { 
							chance = 10;
						}
					}
					if (random.nextInt(chance) == 0) {
						dropList.add(species.getSeedStack(1));
					}
					return dropList;
				}
				
				@Override
				public List<ItemStack> getLeavesDrop(IBlockAccess access, Species species, BlockPos breakPos, Random random, List<ItemStack> dropList, int fortune) {
					int chance = 88;
					if (fortune > 0) {
						chance -= 2 << fortune;
						if (chance < 10) { 
							chance = 10;
						}
					}
					if (random.nextInt(chance) == 0) {
						dropList.add(species.getSeedStack(1));
					}
					return dropList;
				}
			});
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
		protected EnumFacing newDirectionSelected(EnumFacing newDir, GrowSignal signal) {
			if (signal.isInTrunk() && newDir != EnumFacing.UP) { // Turned out of trunk
				signal.energy *= 0.3f;
				if (signal.energy > 3) signal.energy = 3;
			}
			return newDir;
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
		public void postGeneration(World world, BlockPos rootPos, Biome biome, int radius, List<BlockPos> endPoints, SafeChunkBounds safeBounds, IBlockState initialDirtState) {
			super.postGeneration(world, rootPos, biome, radius, endPoints, safeBounds, initialDirtState);
			
			if (safeBounds != SafeChunkBounds.ANY) {
				WorldGenerator flowers = new WorldGenCustomFlowers(BlocksTC.shimmerleaf, 0);
				flowers.generate(world, world.rand, rootPos.up());
			}
		}
		
	}
	
	public TreeSilverwood() {
		super(new ResourceLocation(DynamicTreesTC.MODID, "silverwood"));
		
		IBlockState primLog = BlocksTC.logSilverwood.getDefaultState();
		setPrimitiveLog(primLog, new ItemStack(BlocksTC.logSilverwood));
		
		ModContent.silverwoodLeavesProperties.setTree(this);
		
		addConnectableVanillaLeaves((state) -> state.getBlock() == BlocksTC.leafSilverwood);
	}
	
	@Override
	public void createSpecies() {
		setCommonSpecies(new SpeciesSilverwood(this));
	}
	
	@Override
	public List<Block> getRegisterableBlocks(List<Block> blockList) {
		blockList.add(getCommonSpecies().getDynamicSapling().getBlock());
		return super.getRegisterableBlocks(blockList);
	}
	
	@Override
	public BlockBranch createBranch() {
		return new BlockBranchBasic(getName() + "branch") {
			@Override
			public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
				return 5;
			}
		};
	}
	
}
