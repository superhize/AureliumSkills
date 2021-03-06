package com.archyx.aureliumskills.lang;

import co.aikar.commands.MessageKeys;
import co.aikar.commands.MinecraftMessageKeys;
import co.aikar.commands.PaperCommandManager;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.util.LoreUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Lang implements Listener {

	private static final Map<Locale, Map<MessageKey, String>> messages = new HashMap<>();
	private static final Map<UUID, Locale> playerLanguages = new HashMap<>();
	private static Map<Locale, String> definedLanguages;
	public static Locale defaultLanguage;
	private final Plugin plugin;

	public Lang(Plugin plugin) {
		this.plugin = plugin;
		File file = new File(plugin.getDataFolder(), "messages_en.yml");
		if (!file.exists()) {
			plugin.saveResource("messages_en.yml", false);
		}
		loadLanguageFiles();
	}

	private void loadLanguageFiles() {
		if (!new File(plugin.getDataFolder(), "messages_id.yml").exists()) {
			plugin.saveResource("messages_id.yml", false);
		}
		if (!new File(plugin.getDataFolder(), "messages_es.yml").exists()) {
			plugin.saveResource("messages_es.yml", false);
		}
		if (!new File(plugin.getDataFolder(), "messages_fr.yml").exists()) {
			plugin.saveResource("messages_fr.yml", false);
		}
		if (!new File(plugin.getDataFolder(), "messages_zh-TW.yml").exists()) {
			plugin.saveResource("messages_zh-TW.yml", false);
		}
		if (!new File(plugin.getDataFolder(), "messages_tr.yml").exists()) {
			plugin.saveResource("messages_tr.yml", false);
		}
		if (!new File(plugin.getDataFolder(), "messages_pl.yml").exists()) {
			plugin.saveResource("messages_pl.yml", false);
		}
		if (!new File(plugin.getDataFolder(), "messages_pt-BR.yml").exists()) {
			plugin.saveResource("messages_pt-BR.yml", false);
		}
	}

	public void loadEmbeddedMessages(PaperCommandManager commandManager) {
		// Loads default file from embedded resource
		InputStream inputStream = plugin.getResource("messages_en.yml");
		if (inputStream != null) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
			Locale locale = new Locale("en");
			// Load messages
			loadMessages(config, locale, commandManager);
		}
	}

	public void loadLanguages(PaperCommandManager commandManager) {
		Bukkit.getLogger().info("[AureliumSkills] Loading languages...");
		long startTime = System.currentTimeMillis();
		FileConfiguration pluginConfig = plugin.getConfig();
		// Load languages list and default, add to default if not present
		List<String> languages = new ArrayList<>(pluginConfig.getStringList("languages"));
		String defaultLanguageString = pluginConfig.getString("default-language");
		if (defaultLanguageString == null) {
			defaultLanguageString = "en";
		}
		else {
			defaultLanguageString = defaultLanguageString.toLowerCase();
		}
		if (!languages.contains(defaultLanguageString)) {
			languages.add(defaultLanguageString);
		}
		// Sets default language
		defaultLanguage = new Locale(defaultLanguageString);
		definedLanguages = new HashMap<>();
		// Load languages
		int languagesLoaded = 0;
		for (String language : languages) {
			// Load file
			try {
				Locale locale = new Locale(language);
				File file = new File(plugin.getDataFolder(), "messages_" + language + ".yml");
				// Load and update file
				FileConfiguration config = updateFile(file, YamlConfiguration.loadConfiguration(file), language);
				if (config.contains("file_version")) {
					// Load messages
					loadMessages(config, locale, commandManager);
					languagesLoaded++;
					definedLanguages.put(locale, language);
				}
				else {
					Bukkit.getLogger().warning("[AureliumSkills] Could not load file messages_" + language + ".yml! Does this file exist and does it contain a file_version?");
					if (language.equals(defaultLanguageString)) {
						Bukkit.getLogger().warning("[AureliumSkills] The default-language could not be loaded, setting the default language to en!");
						defaultLanguage = Locale.ENGLISH;
					}
				}
			} catch (Exception e) {
				Bukkit.getLogger().warning("[AureliumSkills] Error loading messages file messages_" + language + ".yml");
				e.printStackTrace();
			}
		}
		long endTime = System.currentTimeMillis();
		Bukkit.getLogger().info("[AureliumSkills] Loaded " + languagesLoaded + " languages in " + (endTime - startTime) + "ms");
	}

	private void loadMessages(FileConfiguration config, Locale locale, PaperCommandManager commandManager) {
		// Load units
		Map<UnitMessage, String> units = new HashMap<>();
		for (UnitMessage key : UnitMessage.values()) {
			String message = config.getString(key.getPath());
			if (message != null) {
				units.put(key, message.replace('&', '§'));
			}
		}
		// Load message keys
		Map<MessageKey, String> messages = new HashMap<>();
		for (MessageKey key : MessageKey.values()) {
			String message = config.getString(key.getPath());
			if (message != null) {
				messages.put(key, LoreUtil.replace(message
						,"&", "§"
						,"{mana_unit}", units.get(UnitMessage.MANA)
						,"{hp_unit}", units.get(UnitMessage.HP)
						,"{xp_unit}", units.get(UnitMessage.XP)));
			}
			else {
				if (locale.equals(Locale.ENGLISH)) {
					Bukkit.getLogger().warning("[AureliumSkills] [" + locale.toLanguageTag() + "] Message with path " + key.getPath() + " not found!");
				}
			}
		}
		for (ACFCoreMessage message : ACFCoreMessage.values()) {
			String path = message.getPath();
			commandManager.getLocales().addMessage(locale, MessageKeys.valueOf(message.name()), LoreUtil.replace(config.getString(path), "&", "§"));
		}
		for (ACFMinecraftMessage message : ACFMinecraftMessage.values()) {
			String path = message.getPath();
			commandManager.getLocales().addMessage(locale, MinecraftMessageKeys.valueOf(message.name()), LoreUtil.replace(config.getString(path), "&", "§"));
		}
		Lang.messages.put(locale, messages);
	}

	private FileConfiguration updateFile(File file, FileConfiguration config, String language) {
		if (config.contains("file_version")) {
			InputStream stream = plugin.getResource("messages_" + language + ".yml");
			if (stream != null) {
				int currentVersion = config.getInt("file_version");
				FileConfiguration imbConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
				int imbVersion = imbConfig.getInt("file_version");
				//If versions do not match
				if (currentVersion != imbVersion) {
					try {
						ConfigurationSection configSection = imbConfig.getConfigurationSection("");
						int keysAdded = 0;
						if (configSection != null) {
							for (String key : configSection.getKeys(true)) {
								if (!config.contains(key)) {
									config.set(key, imbConfig.get(key));
									keysAdded++;
								}
							}
						}
						config.set("file_version", imbVersion);
						config.save(file);
						Bukkit.getLogger().info("[AureliumSkills] messages_" + language + ".yml was updated to a new file version, " + keysAdded + " new keys were added.");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return YamlConfiguration.loadConfiguration(file);
	}

	public static String getMessage(MessageKey key, Locale locale) {
		// Set default locale if locale not present
		if (!messages.containsKey(locale)) {
			locale = defaultLanguage;
		}
		String message = messages.get(locale).get(key);
		if (message != null) {
			return message;
		} else {
			return Lang.messages.get(defaultLanguage).get(key);
		}
	}

	public static Locale getLanguage(Player player) {
		Locale locale = playerLanguages.get(player.getUniqueId());
		return locale != null ? locale : defaultLanguage;
	}

	public static Locale getLanguage(CommandSender sender) {
		if (sender instanceof Player) {
			Locale locale = playerLanguages.get(((Player) sender).getUniqueId());
			return locale != null ? locale : defaultLanguage;
		}
		else {
			return defaultLanguage;
		}
	}

	public static void setLanguage(Player player, Locale locale) {
		Lang.playerLanguages.put(player.getUniqueId(), locale);
	}

	public static boolean hasLocale(Locale locale) {
		return messages.containsKey(locale);
	}

	public static Map<Locale, String> getDefinedLanguages() {
		return definedLanguages;
	}

	public static Set<String> getDefinedLanguagesSet() {
		Set<String> languages = new HashSet<>();
		for (Map.Entry<Locale, String> entry : definedLanguages.entrySet()) {
			languages.add(entry.getValue());
		}
		return languages;
	}

	public static Locale getDefaultLanguage() {
		return defaultLanguage;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (!playerLanguages.containsKey(player.getUniqueId())) {
			// Try to detect client locale if option is enabled
			if (OptionL.getBoolean(Option.TRY_DETECT_CLIENT_LANGUAGE)) {
				try {
					Locale locale = new Locale(player.getLocale().split("_")[0].toLowerCase());
					if (messages.containsKey(locale)) {
						playerLanguages.put(player.getUniqueId(), locale);
					} else {
						playerLanguages.put(player.getUniqueId(), defaultLanguage);
					}
				} catch (Exception e) {
					playerLanguages.put(player.getUniqueId(), defaultLanguage);
				}
			}
			// Otherwise set to default
			else {
				playerLanguages.put(player.getUniqueId(), defaultLanguage);
			}
		}
	}

}
