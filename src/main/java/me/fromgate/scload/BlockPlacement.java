/**
 * 
 * This file exists because WorldEdit moved the equivalent public code into private access ._.
 * 
 */
package me.fromgate.scload;

import java.util.HashMap;
import java.util.Map;

import com.sk89q.worldedit.extent.reorder.MultiStageReorder.PlacementPriority;
import com.sk89q.worldedit.world.block.BlockCategories;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;

public class BlockPlacement {
	
	private static Map<BlockType, PlacementPriority> priorityMap = new HashMap<>();
	
	// copy pasted lol
	static {
		BlockCategories.SAPLINGS.getAll().forEach(type -> priorityMap.put(type, PlacementPriority.LAST));
        BlockCategories.FLOWER_POTS.getAll().forEach(type -> priorityMap.put(type, PlacementPriority.LAST));
        BlockCategories.BUTTONS.getAll().forEach(type -> priorityMap.put(type, PlacementPriority.LAST));
        BlockCategories.ANVIL.getAll().forEach(type -> priorityMap.put(type, PlacementPriority.LAST));
        BlockCategories.WOODEN_PRESSURE_PLATES.getAll().forEach(type -> priorityMap.put(type, PlacementPriority.LAST));
        BlockCategories.CARPETS.getAll().forEach(type -> priorityMap.put(type, PlacementPriority.LAST));
        BlockCategories.RAILS.getAll().forEach(type -> priorityMap.put(type, PlacementPriority.LAST));
        priorityMap.put(BlockTypes.BLACK_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.BLUE_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.BROWN_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.CYAN_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.GRAY_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.GREEN_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.LIGHT_BLUE_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.LIGHT_GRAY_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.LIME_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.MAGENTA_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.ORANGE_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.PINK_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.PURPLE_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.RED_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.WHITE_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.YELLOW_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.GRASS, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.TALL_GRASS, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.ROSE_BUSH, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.DANDELION, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.BROWN_MUSHROOM, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.RED_MUSHROOM, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.FERN, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.LARGE_FERN, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.OXEYE_DAISY, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.AZURE_BLUET, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.TORCH, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.WALL_TORCH, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.FIRE, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.REDSTONE_WIRE, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.CARROTS, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.POTATOES, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.WHEAT, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.BEETROOTS, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.COCOA, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.LADDER, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.LEVER, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.REDSTONE_TORCH, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.REDSTONE_WALL_TORCH, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.SNOW, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.NETHER_PORTAL, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.END_PORTAL, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.REPEATER, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.VINE, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.LILY_PAD, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.NETHER_WART, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.PISTON, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.STICKY_PISTON, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.TRIPWIRE_HOOK, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.TRIPWIRE, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.STONE_PRESSURE_PLATE, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.HEAVY_WEIGHTED_PRESSURE_PLATE, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.LIGHT_WEIGHTED_PRESSURE_PLATE, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.COMPARATOR, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.IRON_TRAPDOOR, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.ACACIA_TRAPDOOR, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.BIRCH_TRAPDOOR, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.DARK_OAK_TRAPDOOR, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.JUNGLE_TRAPDOOR, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.OAK_TRAPDOOR, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.SPRUCE_TRAPDOOR, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.DAYLIGHT_DETECTOR, PlacementPriority.LAST);
        
        // Final
        BlockCategories.DOORS.getAll().forEach(type -> priorityMap.put(type, PlacementPriority.FINAL));
        BlockCategories.BANNERS.getAll().forEach(type -> priorityMap.put(type, PlacementPriority.FINAL));
        priorityMap.put(BlockTypes.SIGN, PlacementPriority.FINAL);
        priorityMap.put(BlockTypes.WALL_SIGN, PlacementPriority.FINAL);
        priorityMap.put(BlockTypes.CACTUS, PlacementPriority.FINAL);
        priorityMap.put(BlockTypes.SUGAR_CANE, PlacementPriority.FINAL);
        priorityMap.put(BlockTypes.CAKE, PlacementPriority.FINAL);
        priorityMap.put(BlockTypes.PISTON_HEAD, PlacementPriority.FINAL);
        priorityMap.put(BlockTypes.MOVING_PISTON, PlacementPriority.FINAL);
	}

	public static boolean shouldPlaceFinal (BlockType type) {
		return priorityMap.containsKey(type) && priorityMap.get(type) == PlacementPriority.FINAL;
	}
	
	public static boolean shouldPlaceLast (BlockType type) {
		return priorityMap.containsKey(type) && priorityMap.get(type) == PlacementPriority.LAST;
	}

}
