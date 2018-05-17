package dynamictreestc.trees;

import java.util.List;
import java.util.Random;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.blocks.BlockDynamicSapling;
import com.ferreusveritas.dynamictrees.systems.GrowSignal;
import com.ferreusveritas.dynamictrees.systems.dropcreators.DropCreatorSeed;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;

import dynamictreestc.DynamicTreesTC;
import dynamictreestc.ModContent;
import dynamictreestc.featuregen.FeatureGenWeb;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.BiomeDictionary.Type;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.world.biomes.BiomeHandler;

public class TreeGreatwood extends TreeFamily {
	
	public class SpeciesGreatwood extends Species {
		
		FeatureGenWeb webGen;
		
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
					int chance = 132;
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
			
			webGen = new FeatureGenWeb(this).setQuantity(12);
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
		public boolean rot(World world, BlockPos pos, int neighborCount, int radius, Random random) {
			if (super.rot(world, pos, neighborCount, radius, random)) {
				if (radius > 4 && TreeHelper.isRooty(world.getBlockState(pos.down())) && world.getLightFor(EnumSkyBlock.SKY, pos) < 4) {
					world.setBlockState(pos, BlocksTC.vishroom.getDefaultState()); // Change branch to a mushroom
				}
				return true;
			}
			return false;
		}
		
		@Override
		public void postGeneration(World world, BlockPos rootPos, Biome biome, int radius, List<BlockPos> endPoints, SafeChunkBounds safeBounds) {
			super.postGeneration(world, rootPos, biome, radius, endPoints, safeBounds);
			
			boolean worldGen = safeBounds != SafeChunkBounds.ANY;
			
			// Add spiders to some greatwoods
			int spiderChance = biome == BiomeHandler.MAGICAL_FOREST ? 21 : 8; // Lower chance in Magical Forests due to higher tree density
			if (worldGen && world.rand.nextInt(spiderChance) == 0) {
				addSpiders(world, rootPos, endPoints, safeBounds);
			}
			
			// Supplement Thaumcraft's vishroom generation
			if (worldGen && biome == BiomeHandler.MAGICAL_FOREST && world.rand.nextInt(6) == 0) {
				placeVishroom(world, rootPos);
			}
		}
		
		public void addSpiders(World world, BlockPos rootPos, List<BlockPos> endPoints, SafeChunkBounds safeBounds) {
			int webQuantity = (int) (endPoints.size() * ((world.rand.nextFloat() * 0.5f) + 0.75f));
			webGen.setQuantity(webQuantity).gen(world, rootPos, endPoints, safeBounds);
			
			BlockPos spawnerPos = rootPos.down();
			world.setBlockState(spawnerPos, Blocks.MOB_SPAWNER.getDefaultState());
			TileEntityMobSpawner spawner = (TileEntityMobSpawner) world.getTileEntity(spawnerPos);
			if (spawner != null) {
				spawner.getSpawnerBaseLogic().setEntityId(EntityList.getKey(EntityCaveSpider.class));
			}
			
			BlockPos chestPos = rootPos.down(2);
			world.setBlockState(chestPos, Blocks.CHEST.getDefaultState());
			TileEntityChest chest = (TileEntityChest) world.getTileEntity(chestPos);
			if (chest != null) {
				chest.setLootTable(LootTableList.CHESTS_SIMPLE_DUNGEON, world.rand.nextLong());
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
