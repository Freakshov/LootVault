package com.lootvault.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.ItemStack;

public final class SmartFilterUtil {

    private SmartFilterUtil() {}

    public static boolean shouldPull(ItemStack containerStack, Iterable<ItemStack> playerStacks) {
        if (containerStack == null || containerStack.isEmpty()) {
            return false;
        }

        if (containerStack.isStackable()) {
            return true;
        }

        boolean hasEnchantments = hasAnyEnchantment(containerStack);

        boolean playerHasSameBase = false;
        boolean playerHasSameOrBetter = false;

        for (ItemStack playerStack : playerStacks) {
            if (playerStack == null || playerStack.isEmpty()) {
                continue;
            }

            if (!ItemStack.areItemsEqual(containerStack, playerStack)) {
                continue;
            }

            playerHasSameBase = true;

            if (hasEnchantments) {
                if (isEnchantmentSubsetOrEqual(containerStack, playerStack)) {
                    playerHasSameOrBetter = true;
                }
            } else {
                playerHasSameOrBetter = true;
            }
        }

        if (!playerHasSameBase) {
            return true;
        }

        if (hasEnchantments && !playerHasSameOrBetter) {
            return true;
        }

        return false;
    }

    public static boolean hasAnyEnchantment(ItemStack stack) {
        ItemEnchantmentsComponent enchants = stack.get(DataComponentTypes.ENCHANTMENTS);
        if (enchants != null && !enchants.isEmpty()) {
            return true;
        }

        ItemEnchantmentsComponent storedEnchants = stack.get(DataComponentTypes.STORED_ENCHANTMENTS);
        return storedEnchants != null && !storedEnchants.isEmpty();
    }

    public static int getEnchantmentPower(ItemStack stack) {
        int power = 0;

        ItemEnchantmentsComponent enchants = stack.get(DataComponentTypes.ENCHANTMENTS);
        if (enchants != null) {
            power += sumEnchantmentLevels(enchants);
        }

        ItemEnchantmentsComponent storedEnchants = stack.get(DataComponentTypes.STORED_ENCHANTMENTS);
        if (storedEnchants != null) {
            power += sumEnchantmentLevels(storedEnchants);
        }

        return power;
    }

    private static boolean isEnchantmentSubsetOrEqual(ItemStack containerStack, ItemStack playerStack) {
        int containerPower = getEnchantmentPower(containerStack);
        int playerPower = getEnchantmentPower(playerStack);
        return playerPower >= containerPower;
    }

    private static int sumEnchantmentLevels(ItemEnchantmentsComponent component) {
        if (component == null || component.isEmpty()) {
            return 0;
        }
        final int[] total = {0};
        component.getEnchantmentEntries().forEach(entry -> {
            total[0] += entry.getIntValue();
        });
        return total[0];
    }
}
