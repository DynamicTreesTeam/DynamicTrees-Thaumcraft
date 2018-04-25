package dynamictreestc.blocks;

import java.util.ArrayList;
import java.util.Random;

import com.ferreusveritas.dynamictrees.api.treedata.ILeavesProperties;
import com.ferreusveritas.dynamictrees.blocks.BlockDynamicLeaves;
import com.ferreusveritas.dynamictrees.blocks.LeavesProperties;
import com.ferreusveritas.dynamictrees.util.MathHelper;

import dynamictreestc.DynamicTreesTC;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.world.aura.AuraHandler;

public class BlockDynamicLeavesSilverwood extends BlockDynamicLeaves {
	
	public BlockDynamicLeavesSilverwood() {
		super();
		setRegistryName(DynamicTreesTC.MODID, "leaves_silverwood");
		setUnlocalizedName("leaves_silverwood");
	}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (!worldIn.isRemote) {
	      if (AuraHandler.getVis(worldIn, pos) < AuraHandler.getAuraBase(worldIn, pos)) {
	        AuraHandler.addVis(worldIn, pos, 0.01F);
	      }
	    }
	    super.updateTick(worldIn, pos, state, rand);
	}
	
}
