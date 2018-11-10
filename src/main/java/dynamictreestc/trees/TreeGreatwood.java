package dynamictreestc.trees;

import java.util.List;
import java.util.Random;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.blocks.BlockDynamicSapling;
import com.ferreusveritas.dynamictrees.systems.GrowSignal;
import com.ferreusveritas.dynamictrees.systems.dropcreators.DropCreatorSeed;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenGroup;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenPredicate;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;

import dynamictreestc.DynamicTreesTC;
import dynamictreestc.ModContent;
import dynamictreestc.featuregen.FeatureGenDungeonChest;
import dynamictreestc.featuregen.FeatureGenMobSpawner;
import dynamictreestc.featuregen.FeatureGenVishroom;
import dynamictreestc.featuregen.FeatureGenWeb;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary.Type;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.world.biomes.BiomeHandler;

public class TreeGreatwood extends TreeFamily {
	
	public class SpeciesGreatwood extends Species {
				
		SpeciesGreatwood(TreeFamily treeFamily) {
			super(treeFamily.getName(), treeFamily, ModContent.greatwoodLeavesProperties);
			
			setBasicGrowingParameters(0.25f, 22.0f, 8, 7, 1.25f);
			setSoilLongevity(14); // Grows for a long long time
			
			setDynamicSapling(new BlockDynamicSapling("greatwoodsapling").getDefaultState());
			
			envFactor(Type.COLD, 0.75f);
			envFactor(Type.HOT, 0.75f);
			envFactor(Type.DRY, 0.5f);
			envFactor(Type.FOREST, 1.05f);
			envFactor(Type.MAGICAL, 1.1f);
			
			generateSeed();
			
			setupStandardSeedDropping();
			addDropCreator(new DropCreatorSeed(0.25f) {
				@Override
				public List<ItemStack> getHarvestDrop(World world, Species species, BlockPos leafPos, Random random, List<ItemStack> dropList, int soilLife, int fortune) {
					int chance = 150;
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
			});

			final Random rand = new Random();
			
			addGenFeature(new FeatureGenWeb(this));//Cobweb generator
			
			addGenFeature(// Add spiders to some greatwoods
				new FeatureGenPredicate(
					new FeatureGenGroup()//Add two features to the predicate.  If true they both run
						.add(new FeatureGenMobSpawner(EntityCaveSpider.class, 1)) //Adds a cave spider spawner under the tree
						.add(new FeatureGenDungeonChest(2)))//Create a dungeon loot chest under the base of the tree
				.onlyWorldGen(true) //Only allow generation in world gen
				.setBiomePredicate(biome -> rand.nextInt(biome == BiomeHandler.MAGICAL_FOREST ? 28 : 8) == 0 )// Lower chance in Magical Forests due to higher tree density
			);
			addGenFeature(new FeatureGenVishroom());//Supplement Thaumcraft's vishroom generation
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
		public int getReinfTravel() {
			return 3;
		}
		
		@Override
		protected EnumFacing newDirectionSelected(EnumFacing newDir, GrowSignal signal) {
			if (signal.isInTrunk() && newDir != EnumFacing.UP) { // Turned out of trunk
				signal.energy *= 1.25f;
				if (signal.energy > 7) signal.energy = 7;
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
		
	}
	
	public TreeGreatwood() {
		super(new ResourceLocation(DynamicTreesTC.MODID, "greatwood"));
		
		IBlockState primLog = BlocksTC.logGreatwood.getDefaultState();
		setPrimitiveLog(primLog, new ItemStack(BlocksTC.logGreatwood));
		
		ModContent.greatwoodLeavesProperties.setTree(this);
		
		addConnectableVanillaLeaves((state) -> state.getBlock() == BlocksTC.leafGreatwood);
	}
	
	@Override
	public void createSpecies() {
		setCommonSpecies(new SpeciesGreatwood(this));
	}
	
	@Override
	public List<Block> getRegisterableBlocks(List<Block> blockList) {
		blockList.add(getCommonSpecies().getDynamicSapling().getBlock());
		return super.getRegisterableBlocks(blockList);
	}
	
}
