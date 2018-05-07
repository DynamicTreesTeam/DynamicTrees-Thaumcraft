package dynamictreestc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.ferreusveritas.dynamictrees.ModConstants;
import com.ferreusveritas.dynamictrees.ModItems;
import com.ferreusveritas.dynamictrees.ModRecipes;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.api.client.ModelHelper;
import com.ferreusveritas.dynamictrees.api.treedata.ILeavesProperties;
import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.blocks.BlockDynamicLeaves;
import com.ferreusveritas.dynamictrees.blocks.LeavesProperties;
import com.ferreusveritas.dynamictrees.items.DendroPotion.DendroPotionType;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;

import dynamictreestc.blocks.BlockDynamicLeavesSilverwood;
import dynamictreestc.trees.TreeGreatwood;
import dynamictreestc.trees.TreeSilverwood;
import dynamictreestc.trees.species.SpeciesOakMagic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import thaumcraft.api.blocks.BlocksTC;

@Mod.EventBusSubscriber(modid = DynamicTreesTC.MODID)
@ObjectHolder(DynamicTreesTC.MODID)
public class ModContent {
	
	public static BlockDynamicLeavesSilverwood silverwoodLeaves;
	
	// leaves properties
	public static ILeavesProperties greatwoodLeavesProperties, silverwoodLeavesProperties;
	
	// trees added by this mod
	public static ArrayList<TreeFamily> trees = new ArrayList<TreeFamily>();
	
	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> registry = event.getRegistry();
		
		// Register Special Leaf Blocks
		silverwoodLeaves = new BlockDynamicLeavesSilverwood();
		registry.register(silverwoodLeaves);
		
		// Initialize Leaves Properties
		greatwoodLeavesProperties = new LeavesProperties(
				BlocksTC.leafGreatwood.getDefaultState(),
				new ItemStack(BlocksTC.leafGreatwood),
				TreeRegistry.findCellKit("deciduous")) {
					@Override
					public int getSmotherLeavesMax() {
						return 7;
					}
					@Override
					public int getLightRequirement() {
						return 13; // allow leaves to grow under the branches to make the tree more rounded
					}
				};
		silverwoodLeavesProperties = new LeavesProperties(
				BlocksTC.leafSilverwood.getDefaultState(),
				new ItemStack(BlocksTC.leafSilverwood),
				TreeRegistry.findCellKit("deciduous")) {
					@Override
					public int getSmotherLeavesMax() {
						return 8;
					}
					@Override
					public int getLightRequirement() {
						return 13; // allow leaves to grow under the branches to make the tree more rounded
					}
					@Override
					public int foliageColorMultiplier(IBlockState state, IBlockAccess world, BlockPos pos) {
						return 0xffffff;
					}
				};
		
		// Generate leaves for leaves properties
		TreeHelper.getLeavesBlockForSequence(DynamicTreesTC.MODID, 0, greatwoodLeavesProperties);
		
		silverwoodLeavesProperties.setDynamicLeavesState(silverwoodLeaves.getDefaultState());
		silverwoodLeaves.setProperties(0, silverwoodLeavesProperties);
		
		// Get vanilla tree families that will have species added to them
		TreeFamily oakTree = TreeRegistry.findSpecies(new ResourceLocation(ModConstants.MODID, "oak")).getFamily();
		
		// Register new species of vanilla tree types
		Species.REGISTRY.register(new SpeciesOakMagic(oakTree));
		
		// Register new tree types
		TreeFamily greatwoodTree = new TreeGreatwood();
		TreeFamily silverwoodTree = new TreeSilverwood();
		
		Collections.addAll(trees, greatwoodTree, silverwoodTree);
		trees.forEach(tree -> tree.registerSpecies(Species.REGISTRY));
		
		ArrayList<Block> treeBlocks = new ArrayList<>();
		trees.forEach(tree -> tree.getRegisterableBlocks(treeBlocks));
		treeBlocks.addAll(TreeHelper.getLeavesMapForModId(DynamicTreesTC.MODID).values());
		registry.registerAll(treeBlocks.toArray(new Block[treeBlocks.size()]));
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		
		ArrayList<Item> treeItems = new ArrayList<>();
		trees.forEach(tree -> tree.getRegisterableItems(treeItems));
		registry.registerAll(treeItems.toArray(new Item[treeItems.size()]));
	}
	
	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		Species greatwood = TreeRegistry.findSpecies(new ResourceLocation(DynamicTreesTC.MODID, "greatwood"));
		Species silverwood = TreeRegistry.findSpecies(new ResourceLocation(DynamicTreesTC.MODID, "silverwood"));
		
		ItemStack greatwoodSeed = greatwood.getSeedStack(1);
		ItemStack silverwoodSeed = silverwood.getSeedStack(1);
		
		ItemStack greatwoodTransformationPotion = ModItems.dendroPotion.setTargetTree(new ItemStack(ModItems.dendroPotion, 1, DendroPotionType.TRANSFORM.getIndex()), greatwood.getFamily());
		ItemStack silverwoodTransformationPotion = ModItems.dendroPotion.setTargetTree(new ItemStack(ModItems.dendroPotion, 1, DendroPotionType.TRANSFORM.getIndex()), silverwood.getFamily());
		
		// Add transformation potion recipes
		BrewingRecipeRegistry.addRecipe(new ItemStack(ModItems.dendroPotion, 1, DendroPotionType.TRANSFORM.getIndex()), greatwoodSeed, greatwoodTransformationPotion);
		BrewingRecipeRegistry.addRecipe(new ItemStack(ModItems.dendroPotion, 1, DendroPotionType.TRANSFORM.getIndex()), silverwoodSeed, silverwoodTransformationPotion);
		
		// Add seed <-> sapling recipes
		ModRecipes.createDirtBucketExchangeRecipes(new ItemStack(BlocksTC.saplingGreatwood), greatwoodSeed, true);
		ModRecipes.createDirtBucketExchangeRecipes(new ItemStack(BlocksTC.saplingSilverwood), silverwoodSeed, true);
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		for (TreeFamily tree : ModContent.trees) {
			ModelHelper.regModel(tree.getDynamicBranch());
			ModelHelper.regModel(tree.getCommonSpecies().getSeed());
			ModelHelper.regModel(tree);
		}

		TreeHelper.getLeavesMapForModId(DynamicTreesTC.MODID).forEach((key, leaves) -> ModelLoader.setCustomStateMapper(leaves, new StateMap.Builder().ignore(BlockLeaves.DECAYABLE).build()));
		ModelLoader.setCustomStateMapper(silverwoodLeaves, new StateMap.Builder().ignore(BlockLeaves.DECAYABLE).build());
	}
	
}
