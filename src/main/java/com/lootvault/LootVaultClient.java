package com.lootvault;

import com.lootvault.config.ModConfig;
import com.lootvault.core.LootEngine;
import com.lootvault.util.LanguageManager;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LootVaultClient implements ClientModInitializer {
    public static final String MOD_ID = "lootvault";
    public static final Logger LOGGER = LoggerFactory.getLogger("LootVault");
    public static final KeyBinding.Category LOOTVAULT_CATEGORY = KeyBinding.Category.create(Identifier.of("lootvault", "category"));

    private static KeyBinding toggleKeyBinding;
    public static volatile boolean isListeningForKeybind = false;

    @Override
    public void onInitializeClient() {
        LOGGER.info("[LootVault] Initializing v2.0.0 for Minecraft 1.21.11...");

        ModConfig config = ModConfig.load();

        int initialKey = config.keyCode != 0 ? config.keyCode : GLFW.GLFW_KEY_J;
        toggleKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.lootvault.toggle",
                InputUtil.Type.KEYSYM,
                initialKey,
                LOOTVAULT_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!isListeningForKeybind) {
                while (toggleKeyBinding.wasPressed()) {
                    ModConfig cfg = ModConfig.get();
                    cfg.enabled = !cfg.enabled;
                    cfg.save();

                    if (client.player != null) {
                        Text stateMsg = cfg.enabled
                                ? LanguageManager.get("message.lootvault.enabled")
                                : LanguageManager.get("message.lootvault.disabled");

                        client.player.sendMessage(
                                LanguageManager.get("message.lootvault.prefix").copy().append(stateMsg), true);
                    }
                }
            }

            LootEngine.checkAndLoot();
        });

        registerCommands();

        LOGGER.info("[LootVault] Successfully initialized!");
    }

    public static boolean handleKeyInput(int key, int scancode) {
        if (!isListeningForKeybind) {
            return false;
        }

        MinecraftClient client = MinecraftClient.getInstance();

        if (key == GLFW.GLFW_KEY_ESCAPE) {
            isListeningForKeybind = false;
            if (client.player != null) {
                client.player.sendMessage(LanguageManager.get("message.lootvault.prefix")
                        .copy().append(LanguageManager.get("message.lootvault.keybind_cancelled")), false);
            }
            return true;
        }

        isListeningForKeybind = false;
        InputUtil.Key inputKey = InputUtil.fromKeyCode(new KeyInput(key, scancode, 0));
        String name = inputKey.getLocalizedText().getString();

        ModConfig cfg = ModConfig.get();
        cfg.keyCode = key;
        cfg.keyName = name;
        cfg.save();

        if (toggleKeyBinding != null) {
            toggleKeyBinding.setBoundKey(inputKey);
            KeyBinding.updateKeysByCode();
        }

        if (client.player != null) {
            client.player.sendMessage(LanguageManager.get("message.lootvault.prefix")
                    .copy().append(LanguageManager.get("message.lootvault.keybind_set", name)), false);
        }

        return true;
    }

    private void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("vault")

                    .then(ClientCommandManager.literal("toggle")
                            .executes(context -> {
                                ModConfig cfg = ModConfig.get();
                                cfg.enabled = !cfg.enabled;
                                cfg.save();

                                Text stateMsg = cfg.enabled
                                        ? LanguageManager.get("message.lootvault.enabled")
                                        : LanguageManager.get("message.lootvault.disabled");

                                context.getSource().sendFeedback(
                                        LanguageManager.get("message.lootvault.prefix").copy().append(stateMsg));
                                return 1;
                            }))

                    .then(ClientCommandManager.literal("bind")
                            .executes(context -> {
                                isListeningForKeybind = true;
                                context.getSource().sendFeedback(LanguageManager.get("message.lootvault.prefix")
                                        .copy().append(LanguageManager.get("message.lootvault.keybind_listening")));
                                return 1;
                            }))

                    .then(ClientCommandManager.literal("delay")
                            .then(ClientCommandManager.argument("ms", IntegerArgumentType.integer(0, 5000))
                                    .executes(context -> {
                                        int ms = IntegerArgumentType.getInteger(context, "ms");
                                        ModConfig cfg = ModConfig.get();
                                        cfg.delayMs = ms;
                                        cfg.save();

                                        context.getSource().sendFeedback(LanguageManager.get("message.lootvault.prefix")
                                                .copy().append(LanguageManager.get("message.lootvault.delay_set", ms)));
                                        return 1;
                                    })))

                    .then(ClientCommandManager.literal("autoclose")
                            .executes(context -> {
                                ModConfig cfg = ModConfig.get();
                                cfg.autoClose = !cfg.autoClose;
                                cfg.save();

                                Text msg = cfg.autoClose
                                        ? LanguageManager.get("message.lootvault.autoclose_on")
                                        : LanguageManager.get("message.lootvault.autoclose_off");

                                context.getSource().sendFeedback(
                                        LanguageManager.get("message.lootvault.prefix").copy().append(msg));
                                return 1;
                            }))

                    .then(ClientCommandManager.literal("chests")
                            .executes(context -> {
                                ModConfig cfg = ModConfig.get();
                                cfg.lootChests = !cfg.lootChests;
                                cfg.save();

                                Text msg = cfg.lootChests
                                        ? LanguageManager.get("message.lootvault.chests_on")
                                        : LanguageManager.get("message.lootvault.chests_off");

                                context.getSource().sendFeedback(
                                        LanguageManager.get("message.lootvault.prefix").copy().append(msg));
                                return 1;
                            }))

                    .then(ClientCommandManager.literal("shulkers")
                            .executes(context -> {
                                ModConfig cfg = ModConfig.get();
                                cfg.lootShulkers = !cfg.lootShulkers;
                                cfg.save();

                                Text msg = cfg.lootShulkers
                                        ? LanguageManager.get("message.lootvault.shulkers_on")
                                        : LanguageManager.get("message.lootvault.shulkers_off");

                                context.getSource().sendFeedback(
                                        LanguageManager.get("message.lootvault.prefix").copy().append(msg));
                                return 1;
                            }))

                    .then(ClientCommandManager.literal("barrels")
                            .executes(context -> {
                                ModConfig cfg = ModConfig.get();
                                cfg.lootBarrels = !cfg.lootBarrels;
                                cfg.save();

                                Text msg = cfg.lootBarrels
                                        ? LanguageManager.get("message.lootvault.barrels_on")
                                        : LanguageManager.get("message.lootvault.barrels_off");

                                context.getSource().sendFeedback(
                                        LanguageManager.get("message.lootvault.prefix").copy().append(msg));
                                return 1;
                            }))

                    .then(ClientCommandManager.literal("smart")
                            .executes(context -> {
                                ModConfig cfg = ModConfig.get();
                                cfg.smartFilter = !cfg.smartFilter;
                                cfg.save();

                                Text msg = cfg.smartFilter
                                        ? LanguageManager.get("message.lootvault.smart_on")
                                        : LanguageManager.get("message.lootvault.smart_off");

                                context.getSource().sendFeedback(
                                        LanguageManager.get("message.lootvault.prefix").copy().append(msg));
                                return 1;
                            })
                            .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(context -> {
                                        boolean val = BoolArgumentType.getBool(context, "enabled");
                                        ModConfig cfg = ModConfig.get();
                                        cfg.smartFilter = val;
                                        cfg.save();

                                        Text msg = cfg.smartFilter
                                                ? LanguageManager.get("message.lootvault.smart_on")
                                                : LanguageManager.get("message.lootvault.smart_off");

                                        context.getSource().sendFeedback(
                                                LanguageManager.get("message.lootvault.prefix").copy().append(msg));
                                        return 1;
                                    })))

                    .then(ClientCommandManager.literal("mode")
                            .then(ClientCommandManager.argument("mode", StringArgumentType.word())
                                    .executes(context -> {
                                        String modeStr = StringArgumentType.getString(context, "mode").toLowerCase();
                                        ModConfig cfg = ModConfig.get();

                                        if (modeStr.equals("all")) {
                                            cfg.lootMode = "all";
                                        } else if (modeStr.equals("smart_only") || modeStr.equals("smart")) {
                                            cfg.lootMode = "smart_only";
                                        } else {
                                            context.getSource().sendFeedback(
                                                    LanguageManager.get("message.lootvault.prefix").copy()
                                                            .append(LanguageManager.get("message.lootvault.mode_invalid")));
                                            return 0;
                                        }

                                        cfg.save();
                                        context.getSource().sendFeedback(
                                                LanguageManager.get("message.lootvault.prefix").copy()
                                                        .append(LanguageManager.get("message.lootvault.mode_set", cfg.lootMode)));
                                        return 1;
                                    })))

                    .then(ClientCommandManager.literal("lang")
                            .then(ClientCommandManager.argument("language", StringArgumentType.word())
                                    .executes(context -> {
                                        String rawLang = StringArgumentType.getString(context, "language").toLowerCase();
                                        String selected = "en";
                                        if (rawLang.startsWith("tr")) {
                                            selected = "tr";
                                        } else if (rawLang.startsWith("es")) {
                                            selected = "es";
                                        } else if (rawLang.startsWith("en")) {
                                            selected = "en";
                                        }

                                        ModConfig cfg = ModConfig.get();
                                        cfg.language = selected;
                                        cfg.save();

                                        context.getSource().sendFeedback(LanguageManager.get("message.lootvault.prefix")
                                                .copy().append(LanguageManager.get("message.lootvault.lang_set")));
                                        return 1;
                                    })))

                    .then(ClientCommandManager.literal("random")
                            .executes(context -> {
                                ModConfig cfg = ModConfig.get();
                                cfg.randomOrder = !cfg.randomOrder;
                                cfg.save();

                                Text msg = cfg.randomOrder
                                        ? LanguageManager.get("message.lootvault.random_on")
                                        : LanguageManager.get("message.lootvault.random_off");

                                context.getSource().sendFeedback(
                                        LanguageManager.get("message.lootvault.prefix").copy().append(msg));
                                return 1;
                            })
                            .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                                    .executes(context -> {
                                        boolean val = BoolArgumentType.getBool(context, "enabled");
                                        ModConfig cfg = ModConfig.get();
                                        cfg.randomOrder = val;
                                        cfg.save();

                                        Text msg = cfg.randomOrder
                                                ? LanguageManager.get("message.lootvault.random_on")
                                                : LanguageManager.get("message.lootvault.random_off");

                                        context.getSource().sendFeedback(
                                                LanguageManager.get("message.lootvault.prefix").copy().append(msg));
                                        return 1;
                                    })))

                    .then(ClientCommandManager.literal("status")
                            .executes(context -> {
                                ModConfig cfg = ModConfig.get();
                                String on = LanguageManager.getRaw("message.lootvault.state_on");
                                String off = LanguageManager.getRaw("message.lootvault.state_off");

                                StringBuilder sb = new StringBuilder();
                                sb.append(LanguageManager.getRaw("message.lootvault.status_header")).append("\n");
                                sb.append(LanguageManager.getRaw("message.lootvault.status_overall", cfg.enabled ? on : off)).append("\n");
                                sb.append(LanguageManager.getRaw("message.lootvault.status_keybind", cfg.keyName)).append("\n");
                                sb.append(LanguageManager.getRaw("message.lootvault.status_delay", cfg.delayMs)).append("\n");
                                sb.append(LanguageManager.getRaw("message.lootvault.status_autoclose", cfg.autoClose ? on : off)).append("\n");
                                sb.append(LanguageManager.getRaw("message.lootvault.status_shulkers", cfg.lootShulkers ? on : off)).append("\n");
                                sb.append(LanguageManager.getRaw("message.lootvault.status_chests", cfg.lootChests ? on : off)).append("\n");
                                sb.append(LanguageManager.getRaw("message.lootvault.status_barrels", cfg.lootBarrels ? on : off)).append("\n");
                                sb.append(LanguageManager.getRaw("message.lootvault.status_smart", cfg.smartFilter ? on : off)).append("\n");
                                sb.append(LanguageManager.getRaw("message.lootvault.status_random", cfg.randomOrder ? on : off)).append("\n");
                                sb.append(LanguageManager.getRaw("message.lootvault.status_mode", cfg.lootMode)).append("\n");
                                sb.append(LanguageManager.getRaw("message.lootvault.status_lang", cfg.language.toUpperCase()));

                                context.getSource().sendFeedback(Text.literal(sb.toString()));
                                return 1;
                            }))
            );
        });
    }
}
