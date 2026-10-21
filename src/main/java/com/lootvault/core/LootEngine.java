package com.lootvault.core;

import com.lootvault.config.ModConfig;
import com.lootvault.util.LanguageManager;
import com.lootvault.util.SmartFilterUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LootEngine {
    private static int lastProcessedSyncId = -1;
    private static long lastLootTime = 0;
    private static int fullInventoryStallCount = 0;

    public static void checkAndLoot() {
        ModConfig config = ModConfig.get();
        if (!config.enabled) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        ClientPlayerInteractionManager interactionManager = client.interactionManager;

        if (player == null || interactionManager == null) {
            return;
        }

        ScreenHandler handler = player.currentScreenHandler;
        if (handler == null) {
            lastProcessedSyncId = -1;
            fullInventoryStallCount = 0;
            return;
        }

        int containerSlotCount = getContainerSlotCount(handler, config);
        if (containerSlotCount <= 0) {
            return;
        }

        List<Integer> slotsWithItems = new ArrayList<>();
        int availableSlots = Math.min(containerSlotCount, handler.slots.size());
        for (int i = 0; i < availableSlots; i++) {
            ItemStack stack = handler.getSlot(i).getStack();
            if (stack != null && !stack.isEmpty()) {
                slotsWithItems.add(i);
            }
        }

        if (slotsWithItems.isEmpty()) {
            if (lastProcessedSyncId == handler.syncId) {
                if (config.autoClose) {
                    player.closeHandledScreen();
                    lastProcessedSyncId = -1;
                }
            }
            return;
        }

        long now = System.currentTimeMillis();
        if (config.delayMs > 0 && (now - lastLootTime) < config.delayMs) {
            return;
        }

        List<Integer> eligibleSlots;
        if (config.smartFilter) {
            eligibleSlots = applySmartFilter(handler, slotsWithItems, containerSlotCount);

            if (config.lootMode.equals("smart_only") && eligibleSlots.isEmpty()) {
                if (config.autoClose) {
                    player.closeHandledScreen();
                    lastProcessedSyncId = -1;
                }
                return;
            }

            if (config.lootMode.equals("all")) {
                List<Integer> remaining = new ArrayList<>(slotsWithItems);
                remaining.removeAll(eligibleSlots);
                if (config.randomOrder) {
                    Collections.shuffle(eligibleSlots);
                    Collections.shuffle(remaining);
                }
                List<Integer> ordered = new ArrayList<>(eligibleSlots);
                ordered.addAll(remaining);
                eligibleSlots = ordered;
            } else if (config.randomOrder) {
                Collections.shuffle(eligibleSlots);
            }
        } else {
            eligibleSlots = new ArrayList<>(slotsWithItems);
            if (config.randomOrder) {
                Collections.shuffle(eligibleSlots);
            }
        }

        if (eligibleSlots.isEmpty()) {
            return;
        }

        boolean hasPlayerSpace = false;
        int totalSlots = handler.slots.size();
        for (int pSlot = containerSlotCount; pSlot < totalSlots; pSlot++) {
            ItemStack pStack = handler.getSlot(pSlot).getStack();
            if (pStack == null || pStack.isEmpty()) {
                hasPlayerSpace = true;
                break;
            }
        }

        if (!hasPlayerSpace) {
            boolean canMerge = false;
            for (int s : eligibleSlots) {
                ItemStack cStack = handler.getSlot(s).getStack();
                if (cStack != null && !cStack.isEmpty() && cStack.isStackable()) {
                    for (int pSlot = containerSlotCount; pSlot < totalSlots; pSlot++) {
                        ItemStack pStack = handler.getSlot(pSlot).getStack();
                        if (pStack != null && ItemStack.areItemsAndComponentsEqual(cStack, pStack)
                                && pStack.getCount() < pStack.getMaxCount()) {
                            canMerge = true;
                            break;
                        }
                    }
                }
                if (canMerge) break;
            }

            if (!canMerge) {
                fullInventoryStallCount++;
                if (fullInventoryStallCount > 3) {
                    if (config.chatNotifications && fullInventoryStallCount == 4) {
                        player.sendMessage(LanguageManager.get("message.lootvault.prefix")
                                .copy().append(LanguageManager.get("message.lootvault.inv_full")), true);
                    }
                    return;
                }
            }
        } else {
            fullInventoryStallCount = 0;
        }

        lastLootTime = now;
        lastProcessedSyncId = handler.syncId;

        if (config.delayMs <= 0) {
            int count = eligibleSlots.size();
            for (int slotId : eligibleSlots) {
                interactionManager.clickSlot(handler.syncId, slotId, 0, SlotActionType.QUICK_MOVE, player);
            }

            if (config.chatNotifications && count > 0) {
                player.sendMessage(LanguageManager.get("message.lootvault.prefix")
                        .copy().append(LanguageManager.get("message.lootvault.looted", count)), true);
            }

            if (config.autoClose) {
                player.closeHandledScreen();
                lastProcessedSyncId = -1;
            }
        } else {
            int targetSlot = config.randomOrder
                    ? eligibleSlots.get(ThreadLocalRandom.current().nextInt(eligibleSlots.size()))
                    : eligibleSlots.get(0);
            interactionManager.clickSlot(handler.syncId, targetSlot, 0, SlotActionType.QUICK_MOVE, player);
        }
    }

    private static int getContainerSlotCount(ScreenHandler handler, ModConfig config) {
        if (handler instanceof ShulkerBoxScreenHandler) {
            if (!config.lootShulkers) return 0;
            return 27;
        }

        if (handler instanceof GenericContainerScreenHandler genericHandler) {
            int rows = genericHandler.getRows();

            if (rows == 3) {
                if (!config.lootChests && !config.lootBarrels) return 0;
            } else if (rows == 6) {
                if (!config.lootChests) return 0;
            } else {
                if (!config.lootChests) return 0;
            }

            return rows * 9;
        }

        return 0;
    }

    private static List<Integer> applySmartFilter(ScreenHandler handler,
                                                   List<Integer> slotsWithItems,
                                                   int containerSlotCount) {
        List<ItemStack> playerStacks = new ArrayList<>();
        int totalSlots = handler.slots.size();
        for (int pSlot = containerSlotCount; pSlot < totalSlots; pSlot++) {
            ItemStack pStack = handler.getSlot(pSlot).getStack();
            if (pStack != null && !pStack.isEmpty()) {
                playerStacks.add(pStack);
            }
        }

        List<Integer> eligible = new ArrayList<>();
        for (int slotId : slotsWithItems) {
            ItemStack containerStack = handler.getSlot(slotId).getStack();
            if (SmartFilterUtil.shouldPull(containerStack, playerStacks)) {
                eligible.add(slotId);
            }
        }

        return eligible;
    }
}
