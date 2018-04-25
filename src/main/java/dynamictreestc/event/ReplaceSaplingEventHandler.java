package dynamictreestc.event;

import com.ferreusveritas.dynamictrees.ModConfigs;
import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.CompatHelper;

import dynamictreestc.DynamicTreesTC;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.blocks.BlocksTC;

@Mod.EventBusSubscriber(modid = DynamicTreesTC.MODID)
public class ReplaceSaplingEventHandler {
	
	@SubscribeEvent
	public static void onPlaceSapling(PlaceEvent event) {
		if (!ModConfigs.replaceVanillaSapling) return;
		
		IBlockState state = event.getPlacedBlock();
		
		Species species = null;
		
		if (state.getBlock() == BlocksTC.saplingGreatwood) {
			species = TreeRegistry.findSpecies(new ResourceLocation(DynamicTreesTC.MODID, "greatwood"));
		} else if (state.getBlock() == BlocksTC.saplingSilverwood) {
			species = TreeRegistry.findSpecies(new ResourceLocation(DynamicTreesTC.MODID, "silverwood"));
		}
		
		if (species != null) {
			event.getWorld().setBlockToAir(event.getPos());
			if(!species.plantSapling(event.getWorld(), event.getPos())) {
				double x = event.getPos().getX() + 0.5;
				double y = event.getPos().getY() + 0.5;
				double z = event.getPos().getZ() + 0.5;
				EntityItem itemEntity = new EntityItem(event.getWorld(), x, y, z, species.getSeedStack(1));
				CompatHelper.spawnEntity(event.getWorld(), itemEntity);
			}
		}
	}
	
}
