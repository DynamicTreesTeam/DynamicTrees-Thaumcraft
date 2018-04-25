package dynamictreestc.worldgen;

import com.ferreusveritas.dynamictrees.ModConfigs;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.common.world.biomes.BiomeHandler;

public class DecorateEventHandler {

	@SubscribeEvent(priority=EventPriority.HIGH, receiveCanceled=true)
	public void onEvent(DecorateBiomeEvent.Decorate event) {
		if (event.getType() == EventType.TREE) {
			Biome biome = event.getWorld().getBiome(event.getPos());
			ResourceLocation resloc = biome.getRegistryName();
			if (resloc.getResourceDomain().equals("thaumcraft")) {
				event.setResult(Result.DENY);
			}
		}
	}
	
}
