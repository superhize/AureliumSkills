package com.archyx.aureliumskills.util;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.PlayerSkillInstance;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.stats.Stat;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class PlaceholderSupport extends PlaceholderExpansion {

    private final Plugin plugin;
    private final NumberFormat format1;
    private final NumberFormat format2;

    public PlaceholderSupport(Plugin plugin) {
        this.plugin = plugin;
        format1 = new DecimalFormat("#,###.#");
        format2 = new DecimalFormat("#,###.##");
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String getIdentifier() {
        return "aureliumskills";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        //Gets total combined skill level
        if (identifier.equals("power")) {
            if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
                return String.valueOf(SkillLoader.playerSkills.get(player.getUniqueId()).getPowerLevel());
            }
        }

        //Gets HP with scaling as an integer
        if (identifier.equals("hp")) {
            return String.valueOf((int) (player.getHealth() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)));
        }

        //Gets HP with scaling with 1 decimal
        if (identifier.equals("hp_1")) {
            return String.valueOf(format1.format(player.getHealth() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)));
        }

        //Gets max hp
        if (identifier.equals("hp_max")) {
            AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (attribute != null) {
                return String.valueOf((int) (attribute.getValue() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)));
            }
            else {
                return "";
            }
        }

        //Gets HP with scaling with 2 decimal
        if (identifier.equals("hp_2")) {
            return String.valueOf(format2.format(player.getHealth() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)));
        }

        //Gets HP Percent as an integer
        if (identifier.equals("hp_percent")) {
            AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (attribute != null) {
                return String.valueOf((int) (player.getHealth() / attribute.getValue()));
            }
            else {
                return "";
            }
        }

        //Gets mana
        if (identifier.equals("mana")) {
            return String.valueOf(AureliumSkills.manaManager.getMana(player.getUniqueId()));
        }

        //Gets max mana
        if (identifier.equals("mana_max")) {
            return String.valueOf(AureliumSkills.manaManager.getMaxMana(player.getUniqueId()));
        }

        //Gets stat values
        for (Stat stat : Stat.values()) {
            if (identifier.equals(stat.name().toLowerCase())) {
                if (SkillLoader.playerStats.containsKey(player.getUniqueId())) {
                    return String.valueOf(SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(stat));
                }
            }
        }

        //Gets skill levels
        for (Skill skill : Skill.values()) {
            if (identifier.equals(skill.name().toLowerCase())) {
                if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
                    return String.valueOf(SkillLoader.playerSkills.get(player.getUniqueId()).getSkillLevel(skill));
                }
            }
            else if (identifier.equals(skill.name().toLowerCase() + "_roman")) {
                if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
                    return RomanNumber.toRoman(SkillLoader.playerSkills.get(player.getUniqueId()).getSkillLevel(skill));
                }
            }
        }

        if (identifier.startsWith("lb_")) {
            String leaderboardType = LoreUtil.replace(identifier, "lb_", "");
            if (leaderboardType.startsWith("power_")) {
                int place = NumberUtils.toInt(LoreUtil.replace(leaderboardType, "power_", ""));
                if (place > 0) {
                    List<PlayerSkillInstance> list = AureliumSkills.leaderboard.readPowerLeaderboard(place, 1);
                    if (list.size() > 0) {
                        PlayerSkillInstance playerSkill = list.get(0);
                        return Bukkit.getOfflinePlayer(playerSkill.getPlayerId()).getName() + " - " + playerSkill.getPowerLevel();
                    }
                    else return "";
                }
                else {
                    if (identifier.endsWith("name")) {
                        int namePlace = NumberUtils.toInt(LoreUtil.replace(leaderboardType, "power_", "", "_name", ""));
                        if (namePlace > 0) {
                            List<PlayerSkillInstance> list = AureliumSkills.leaderboard.readPowerLeaderboard(namePlace, 1);
                            if (list.size() > 0) {
                                PlayerSkillInstance playerSkill = list.get(0);
                                return Bukkit.getOfflinePlayer(playerSkill.getPlayerId()).getName();
                            }
                            else return "";
                        }
                    }
                    else if (identifier.endsWith("value")) {
                        int valuePlace = NumberUtils.toInt(LoreUtil.replace(leaderboardType, "power_", "", "_value", ""));
                        if (valuePlace > 0) {
                            List<PlayerSkillInstance> list = AureliumSkills.leaderboard.readPowerLeaderboard(valuePlace, 1);
                            if (list.size() > 0) {
                                PlayerSkillInstance playerSkill = list.get(0);
                                return String.valueOf(playerSkill.getPowerLevel());
                            }
                            else return "";
                        }
                    }
                }
            }
            else {
                for (Skill skill : Skill.values()) {
                    if (leaderboardType.startsWith(skill.name().toLowerCase() + "_")) {
                        int place = NumberUtils.toInt(LoreUtil.replace(leaderboardType, skill.name().toLowerCase() + "_", ""));
                        if (place > 0) {
                            List<PlayerSkillInstance> list = AureliumSkills.leaderboard.readSkillLeaderboard(skill, 1, 1);
                            if (list.size() > 0) {
                                PlayerSkillInstance playerSkill = list.get(0);
                                return Bukkit.getOfflinePlayer(playerSkill.getPlayerId()).getName() + " - " + playerSkill.getSkillLevel(skill);
                            }
                            else return "";
                        }
                        else {
                            if (identifier.endsWith("name")) {
                                int namePlace = NumberUtils.toInt(LoreUtil.replace(leaderboardType, skill.name().toLowerCase() + "_", "", "_name", ""));
                                if (namePlace > 0) {
                                    List<PlayerSkillInstance> list = AureliumSkills.leaderboard.readSkillLeaderboard(skill, namePlace, 1);
                                    if (list.size() > 0) {
                                        PlayerSkillInstance playerSkill = list.get(0);
                                        return Bukkit.getOfflinePlayer(playerSkill.getPlayerId()).getName();
                                    }
                                    else return "";
                                }
                            }
                            else if (identifier.endsWith("value")) {
                                int valuePlace = NumberUtils.toInt(LoreUtil.replace(leaderboardType, skill.name().toLowerCase() + "_", "", "_value", ""));
                                if (valuePlace > 0) {
                                    List<PlayerSkillInstance> list = AureliumSkills.leaderboard.readSkillLeaderboard(skill, valuePlace, 1);
                                    if (list.size() > 0) {
                                        PlayerSkillInstance playerSkill = list.get(0);
                                        return String.valueOf(playerSkill.getSkillLevel(skill));
                                    }
                                    else return "";
                                }
                            }
                        }
                    }
                }
            }
        }

        if (identifier.equals("rank")) {
            return String.valueOf(AureliumSkills.leaderboard.getPowerRank(player.getUniqueId()));
        }

        if (identifier.startsWith("rank_")) {
            String skillName = LoreUtil.replace(identifier, "rank_", "");
            try {
                Skill skill = Skill.valueOf(skillName.toUpperCase());
                return String.valueOf(AureliumSkills.leaderboard.getSkillRank(skill, player.getUniqueId()));
            }
            catch (Exception e) {
                return "";
            }
        }

        return null;
    }

}
