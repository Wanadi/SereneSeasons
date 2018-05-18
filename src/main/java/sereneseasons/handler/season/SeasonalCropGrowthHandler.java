package sereneseasons.handler.season;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.IGrowable;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import sereneseasons.api.SSBlocks;
import sereneseasons.config.FertilityConfig;
import sereneseasons.init.ModFertility;

public class SeasonalCropGrowthHandler
{

	@SubscribeEvent
	public void onItemTooltipAdded(ItemTooltipEvent event)
	{
		ModFertility.setupTooltips(event);
	}

	@SubscribeEvent
	public void onCropGrowth(BlockEvent.CropGrowEvent event)
	{
		Block plant = event.getState().getBlock();

		if (plant instanceof BlockCrops)
		{
			boolean isFertile = ModFertility.isCropFertile(plant.getRegistryName().toString(), event.getWorld());
			
			if (isFertilityApplicable(plant) && !isFertile && !isGreenhouseGlassAboveBlock(event.getWorld(), event.getPos()))
			{
				if (FertilityConfig.general_category.crops_break)
				{
					event.getWorld().destroyBlock(event.getPos(), true);
				}
				else
				{
					event.setResult(Event.Result.DENY);
				}
			}
		}
	}

	@SubscribeEvent
	public void onBonemeal(BonemealEvent event)
	{
		Block plant = event.getBlock().getBlock();
		boolean isFertile = ModFertility.isCropFertile(plant.getRegistryName().toString(), event.getWorld());
		
		if (isFertilityApplicable(plant) && !isFertile && !isGreenhouseGlassAboveBlock(event.getWorld(), event.getPos()))
		{
			if (FertilityConfig.general_category.crops_break)
			{
				event.getWorld().destroyBlock(event.getPos(), true);
			}
			
			event.setCanceled(true);
		}
	}

	private boolean isFertilityApplicable(Block block)
	{
		if (!(block instanceof IGrowable))
		{
			return false;
		}
		if ((block instanceof BlockSapling && FertilityConfig.general_category.ignore_saplings))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	private boolean isGreenhouseGlassAboveBlock(World world, BlockPos cropPos)
	{
		for (int i = 0; i < FertilityConfig.general_category.greenhouse_glass_max_height; i++)
		{
			if (world.getBlockState(cropPos.add(0, i + 1, 0)).getBlock().equals(SSBlocks.greenhouse_glass))
			{
				return true;
			}
		}
		
		return false;
	}
}
