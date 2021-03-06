package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Source;
import com.archyx.aureliumskills.skills.abilities.Ability;
import com.google.common.collect.Sets;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;
import java.util.UUID;

public class AgilityLeveler extends SkillLeveler implements Listener {
	
	private final Set<UUID> prevPlayersOnGround = Sets.newHashSet();
	
	public AgilityLeveler(AureliumSkills plugin) {
		super(plugin, Ability.JUMPER);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	@SuppressWarnings("deprecation")
	public void onFall(EntityDamageEvent event) {
		if (OptionL.isEnabled(Skill.AGILITY)) {
			//Check cancelled
			if (OptionL.getBoolean(Option.AGILITY_CHECK_CANCELLED)) {
				if (event.isCancelled()) {
					return;
				}
			}
			if (event.getCause().equals(DamageCause.FALL)) {
				if (event.getEntity() instanceof Player) {
					Player player = (Player) event.getEntity();
					//Checks if in blocked world
					if (AureliumSkills.worldManager.isInBlockedWorld(player.getLocation())) {
						return;
					}
					//Checks if in blocked region
					if (AureliumSkills.worldGuardEnabled) {
						if (AureliumSkills.worldGuardSupport.isInBlockedRegion(player.getLocation())) {
							return;
						}
					}
					//Check for permission
					if (!player.hasPermission("aureliumskills.agility")) {
						return;
					}
					//Check creative mode disable
					if (OptionL.getBoolean(Option.DISABLE_IN_CREATIVE_MODE)) {
						if (player.getGameMode().equals(GameMode.CREATIVE)) {
							return;
						}
					}
					if (event.getFinalDamage() < player.getHealth()) {
						Leveler.addXp(player, Skill.AGILITY, getXp(player, event.getOriginalDamage(EntityDamageEvent.DamageModifier.BASE) * getXp(Source.FALL_DAMAGE)));
					}
				}
			}
		}
	}
	
    @EventHandler
	@SuppressWarnings("deprecation")
    public void onMove(PlayerMoveEvent e) {
    	if (OptionL.isEnabled(Skill.AGILITY)) {
    		//Check cancelled
    		if (OptionL.getBoolean(Option.AGILITY_CHECK_CANCELLED)) {
    			if (e.isCancelled()) {
    				return;
				}
			}
			Player player = e.getPlayer();
			//Checks if in blocked world
			if (AureliumSkills.worldManager.isInBlockedWorld(player.getLocation())) {
				return;
			}
			//Checks if in blocked region
			if (AureliumSkills.worldGuardEnabled) {
				if (AureliumSkills.worldGuardSupport.isInBlockedRegion(player.getLocation())) {
					return;
				}
			}
			//Check for permission
			if (!player.hasPermission("aureliumskills.agility")) {
				return;
			}
			//Check creative mode disable
			if (OptionL.getBoolean(Option.DISABLE_IN_CREATIVE_MODE)) {
				if (player.getGameMode().equals(GameMode.CREATIVE)) {
					return;
				}
			}
	        if (player.getVelocity().getY() > 0) {
	            double jumpVelocity = 0.42F;
	            if (player.hasPotionEffect(PotionEffectType.JUMP)) {
					PotionEffect effect = player.getPotionEffect(PotionEffectType.JUMP);
					if (effect != null) {
						jumpVelocity += ((float) (effect.getAmplifier() + 1) * 0.1F);
					}
	            }
	            if (e.getPlayer().getLocation().getBlock().getType() != Material.LADDER && prevPlayersOnGround.contains(player.getUniqueId())) {
	                if (!player.isOnGround() && Double.compare(player.getVelocity().getY(), jumpVelocity) == 0) {
	                	if (player.hasMetadata("skillsJumps")) {
	                		player.setMetadata("skillsJumps", new FixedMetadataValue(plugin, player.getMetadata("skillsJumps").get(0).asInt() + 1));
	                		if (player.getMetadata("skillsJumps").get(0).asInt() >= 100) {
	                			Leveler.addXp(player, Skill.AGILITY, getXp(player, Source.JUMP_PER_100));
	                			player.removeMetadata("skillsJumps", plugin);
	                		}
	                	}
	                	else {
	                		player.setMetadata("skillsJumps", new FixedMetadataValue(plugin, 1));
	                	}
	                }
	            }
	        }
	        if (player.isOnGround()) {
	            prevPlayersOnGround.add(player.getUniqueId());
	        } else {
	            prevPlayersOnGround.remove(player.getUniqueId());
	        }
    	}
    }
	
}
