package dynamictreestc.trees;

import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.blocks.BlockBranchBasic;
import com.ferreusveritas.dynamictrees.blocks.BlockBranchThick;
import com.ferreusveritas.dynamictrees.blocks.BlockDynamicSapling;
import com.ferreusveritas.dynamictrees.blocks.BlockSurfaceRoot;
import com.ferreusveritas.dynamictrees.systems.GrowSignal;
import com.ferreusveritas.dynamictrees.systems.dropcreators.DropCreatorSeed;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenClearVolume;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenFlareBottom;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenMound;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenRoots;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;

import dynamictreestc.DynamicTreesTC;
import dynamictreestc.ModContent;
import dynamictreestc.featuregen.FeatureGenShimmerleaf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary.Type;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.world.biomes.BiomeHandler;

public class TreeSilverwood extends TreeFamily {
	
	public class SpeciesSilverwood extends Species {
		
		SpeciesSilverwood(TreeFamily treeFamily) {
			super(treeFamily.getName(), treeFamily, ModContent.silverwoodLeavesProperties);
			
			setBasicGrowingParameters(1.15f, 12.0f, 6, 4, 0.75f);
			
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
			
			addGenFeature(new FeatureGenClearVolume(6));//Clear a spot for the thick tree trunk
			addGenFeature(new FeatureGenFlareBottom(this));//Flare the bottom
			addGenFeature(new FeatureGenMound(this, 5));//Establish mounds
			addGenFeature(new FeatureGenShimmerleaf());
			addGenFeature(new FeatureGenRoots(this, 13).setScaler(getRootScaler()));//Finally Generate Roots
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
		
		protected BiFunction<Integer, Integer, Integer> getRootScaler() {
			return (inRadius, trunkRadius) -> {
				float scale = MathHelper.clamp(trunkRadius >= 13 ? (trunkRadius / 24f) : 0, 0, 1);
				return (int) (inRadius * scale);
			};
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
		
	}
	
	
	BlockSurfaceRoot surfaceRootBlock;
	
	public TreeSilverwood() {
		super(new ResourceLocation(DynamicTreesTC.MODID, "silverwood"));
		
		IBlockState primLog = BlocksTC.logSilverwood.getDefaultState();
		setPrimitiveLog(primLog, new ItemStack(BlocksTC.logSilverwood));
		
		ModContent.silverwoodLeavesProperties.setTree(this);
		
		surfaceRootBlock = new BlockSurfaceRoot(Material.WOOD, getName() + "root");
		
		addConnectableVanillaLeaves((state) -> state.getBlock() == BlocksTC.leafSilverwood);
	}
	
	@Override
	public void createSpecies() {
		setCommonSpecies(new SpeciesSilverwood(this));
	}
	
	@Override
	public boolean isThick() {
		return true;
	}
	
	@Override
	public BlockSurfaceRoot getSurfaceRoots() {
		return surfaceRootBlock;
	}
	
	@Override
	public List<Block> getRegisterableBlocks(List<Block> blockList) {
		blockList.add(getCommonSpecies().getDynamicSapling().getBlock());
		blockList.add(surfaceRootBlock);
		return super.getRegisterableBlocks(blockList);
	}
	
	@Override
	public BlockBranch createBranch() {
		String branchName = getName() + "branch";
		BlockBranchThick branch = new BlockBranchThick(branchName);
		branch.setLightLevel(5F / 16F);
		branch.otherBlock.setLightLevel(5F / 16F);
		return branch;
	}
	
	/*protected class BlockBranchSilverwood extends BlockBranchThick {
		
		public BlockBranchSilverwood(String name) {
			this(Material.WOOD, name);
		}
		
		public BlockBranchSilverwood(Material material, String name) {
			super(material, name, false);
			otherBlock = new BlockBranchSilverwood(material, name + "x", true);
			otherBlock.otherBlock = this;
			
			cacheBranchThickStates();
		}
		
		protected BlockBranchSilverwood(Material material, String name, boolean extended) {
			super(material, name, extended);
		}
		
		@Override
		public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
			return 5;
		}
		
	}*/
	
}
