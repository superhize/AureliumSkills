package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Source;
import com.archyx.aureliumskills.skills.abilities.Ability;
import com.archyx.aureliumskills.skills.abilities.MiningAbilities;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;


public class MiningLeveler extends SkillLeveler implements Listener {

	public MiningLeveler(AureliumSkills plugin) {
		super(plugin, Ability.MINER);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	@SuppressWarnings("deprecation")
	public void onBlockBreak(BlockBreakEvent event) {
		if (OptionL.isEnabled(Skill.MINING)) {
			//Check cancelled
			if (OptionL.getBoolean(Option.MINING_CHECK_CANCELLED)) {
				if (event.isCancelled()) {
					return;
				}
			}
			//Checks if in blocked world
			if (AureliumSkills.worldManager.isInBlockedWorld(event.getBlock().getLocation())) {
				return;
			}
			//Checks if in blocked region
			if (AureliumSkills.worldGuardEnabled) {
				if (AureliumSkills.worldGuardSupport.isInBlockedRegion(event.getBlock().getLocation())) {
					return;
				}
			}
			//Check block replace
			if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE)) {
				if (event.getBlock().hasMetadata("skillsPlaced")) {
					return;
				}
			}
			Player p = event.getPlayer();
			Block b = event.getBlock();
			Skill s = Skill.MINING;
			Material mat = event.getBlock().getType();
			//Check for permission
			if (!p.hasPermission("aureliumskills.mining")) {
				return;
			}
			//Check creative mode disable
			if (OptionL.getBoolean(Option.DISABLE_IN_CREATIVE_MODE)) {
				if (p.getGameMode().equals(GameMode.CREATIVE)) {
					return;
				}
			}
			if (mat.equals(Material.STONE)) {
				if (XMaterial.isNewVersion()) {
					Leveler.addXp(p, s, getXp(p, Source.STONE));
				}
				else {
					switch (b.getData()) {
						case 0:
							Leveler.addXp(p, s, getXp(p, Source.STONE));
							break;
						case 1:
							Leveler.addXp(p, s, getXp(p, Source.GRANITE));
							break;
						case 3:
							Leveler.addXp(p, s, getXp(p, Source.DIORITE));
							break;
						case 5:
							Leveler.addXp(p, s, getXp(p, Source.ANDESITE));
							break;
					}
				}
			}
			else if (mat.equals(XMaterial.GRANITE.parseMaterial())) {
				Leveler.addXp(p, s, getXp(p, Source.GRANITE));
			}
			else if (mat.equals(XMaterial.DIORITE.parseMaterial())) {
				Leveler.addXp(p, s, getXp(p, Source.DIORITE));
			}
			else if (mat.equals(XMaterial.ANDESITE.parseMaterial())) {
				Leveler.addXp(p, s, getXp(p, Source.ANDESITE));
			}
			else if (mat.equals(Material.COBBLESTONE)) {
				Leveler.addXp(p, s, getXp(p, Source.COBBLESTONE));
			}
			else if (mat.equals(Material.COAL_ORE)) {
				Leveler.addXp(p, s, getXp(p, Source.COAL_ORE));
				applyAbilities(p, b);
			}
			else if (mat.equals(XMaterial.NETHER_QUARTZ_ORE.parseMaterial())) {
				Leveler.addXp(p, s, getXp(p, Source.QUARTZ_ORE));
				applyAbilities(p, b);
			}
			else if (mat.equals(Material.IRON_ORE)) {
				Leveler.addXp(p, s, getXp(p, Source.IRON_ORE));
				applyAbilities(p, b);
			}
			else if (mat.equals(XMaterial.REDSTONE_ORE.parseMaterial()) || mat.name().equals("GLOWING_REDSTONE_ORE")) {
				Leveler.addXp(p, s, getXp(p, Source.REDSTONE_ORE));
				applyAbilities(p, b);
			}
			else if (mat.equals(Material.LAPIS_ORE)) {
				Leveler.addXp(p, s, getXp(p, Source.LAPIS_ORE));
				applyAbilities(p, b);
			}
			else if (mat.equals(Material.GOLD_ORE)) {
				Leveler.addXp(p, s, getXp(p, Source.GOLD_ORE));
				applyAbilities(p, b);
			}
			else if (mat.equals(Material.DIAMOND_ORE)) {
				Leveler.addXp(p, s, getXp(p, Source.DIAMOND_ORE));
				applyAbilities(p, b);
			}
			else if (mat.equals(Material.EMERALD_ORE)) {
				Leveler.addXp(p, s, getXp(p, Source.EMERALD_ORE));
				applyAbilities(p, b);
			}
			else if (mat.equals(Material.NETHERRACK)) {
				Leveler.addXp(p, s, getXp(p, Source.NETHERRACK));
				applyAbilities(p, b);
			}
			else if (mat.equals(XMaterial.BLACKSTONE.parseMaterial())) {
				Leveler.addXp(p, s, getXp(p, Source.BLACKSTONE));
				applyAbilities(p, b);
			}
			else if (mat.equals(XMaterial.BASALT.parseMaterial())) {
				Leveler.addXp(p, s, getXp(p, Source.BASALT));
			}
			else if (mat.equals(XMaterial.NETHER_GOLD_ORE.parseMaterial())) {
				Leveler.addXp(p, s, getXp(p, Source.NETHER_GOLD_ORE));
				applyAbilities(p, b);
			}
			else if (mat.equals(XMaterial.ANCIENT_DEBRIS.parseMaterial())) {
				Leveler.addXp(p, s, getXp(p, Source.ANCIENT_DEBRIS));
				applyAbilities(p, b);
			}
			else if (mat.equals(XMaterial.END_STONE.parseMaterial())) {
				Leveler.addXp(p, s, getXp(p, Source.END_STONE));
			}
			else if (mat.equals(XMaterial.OBSIDIAN.parseMaterial())) {
				Leveler.addXp(p, s, getXp(p, Source.OBSIDIAN));
			}
			else if (mat.equals(XMaterial.MAGMA_BLOCK.parseMaterial())) {
				Leveler.addXp(p, s, getXp(p, Source.MAGMA_BLOCK));
			}
			else if (XMaterial.isNewVersion()) {
				if (mat.equals(XMaterial.TERRACOTTA.parseMaterial())) {
					Leveler.addXp(p, s, getXp(p, Source.TERRACOTTA));
				}
				else if (mat.equals(XMaterial.RED_TERRACOTTA.parseMaterial())) {
					Leveler.addXp(p, s, getXp(p, Source.RED_TERRACOTTA));
				}
				else if (mat.equals(XMaterial.ORANGE_TERRACOTTA.parseMaterial())) {
					Leveler.addXp(p, s, getXp(p, Source.ORANGE_TERRACOTTA));
				}
				else if (mat.equals(XMaterial.YELLOW_TERRACOTTA.parseMaterial())) {
					Leveler.addXp(p, s, getXp(p, Source.YELLOW_TERRACOTTA));
				}
				else if (mat.equals(XMaterial.WHITE_TERRACOTTA.parseMaterial())) {
					Leveler.addXp(p, s, getXp(p, Source.WHITE_TERRACOTTA));
				}
				else if (mat.equals(XMaterial.LIGHT_GRAY_TERRACOTTA.parseMaterial())) {
					Leveler.addXp(p, s, getXp(p, Source.LIGHT_GRAY_TERRACOTTA));
				}
				else if (mat.equals(XMaterial.BROWN_TERRACOTTA.parseMaterial())) {
					Leveler.addXp(p, s, getXp(p, Source.BROWN_TERRACOTTA));
				}
			}
			else {
				if (mat.equals(XMaterial.TERRACOTTA.parseMaterial())) {
					Leveler.addXp(p, s, getXp(p, Source.TERRACOTTA));
				}
				else if (mat.equals(XMaterial.WHITE_TERRACOTTA.parseMaterial())) {
					switch (b.getData()) {
						case 0:
							Leveler.addXp(p, s, getXp(p, Source.WHITE_TERRACOTTA));
							break;
						case 1:
							Leveler.addXp(p, s, getXp(p, Source.ORANGE_TERRACOTTA));
							break;
						case 4:
							Leveler.addXp(p, s, getXp(p, Source.YELLOW_TERRACOTTA));
							break;
						case 8:
							Leveler.addXp(p, s, getXp(p, Source.LIGHT_GRAY_TERRACOTTA));
							break;
						case 12:
							Leveler.addXp(p, s, getXp(p, Source.BROWN_TERRACOTTA));
							break;
						case 14:
							Leveler.addXp(p, s, getXp(p, Source.RED_TERRACOTTA));
							break;
					}
				}
			}
			// Check custom blocks
			checkCustomBlocks(p, b, s);
		}
	}
	
	private void applyAbilities(Player p, Block b) {
		MiningAbilities.luckyMiner(p, b);
	}
}
