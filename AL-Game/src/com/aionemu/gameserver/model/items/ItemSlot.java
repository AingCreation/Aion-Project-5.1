package com.aionemu.gameserver.model.items;

import java.util.ArrayList;
import java.util.List;
public enum ItemSlot {
    MAIN_HAND(1L),
    SUB_HAND(1L << 1),
    HELMET(1L << 2),
    TORSO(1L << 3),
    GLOVES(1L << 4),
    BOOTS(1L << 5),
    EARRINGS_LEFT(1L << 6),
    EARRINGS_RIGHT(1L << 7),
    RING_LEFT(1L << 8),
    RING_RIGHT(1L << 9),
    NECKLACE(1L << 10),
    SHOULDER(1L << 11),
    PANTS(1L << 12),
    POWER_SHARD_RIGHT(1L << 13),
    POWER_SHARD_LEFT(1L << 14),
    WINGS(1L << 15),
    WAIST(1L << 16),
    MAIN_OFF_HAND(1L << 17),
    SUB_OFF_HAND(1L << 18),
    PLUME(1L << 19),
    BRACELET(1L << 20),
    MAIN_OR_SUB(MAIN_HAND.slotIdMask | SUB_HAND.slotIdMask, true), // 3
    MAIN_OFF_OR_SUB_OFF(MAIN_OFF_HAND.slotIdMask | SUB_OFF_HAND.slotIdMask, true),
    EARRING_RIGHT_OR_LEFT(EARRINGS_LEFT.slotIdMask | EARRINGS_RIGHT.slotIdMask, true), // 192
    RING_RIGHT_OR_LEFT(RING_LEFT.slotIdMask | RING_RIGHT.slotIdMask, true), // 768
    SHARD_RIGHT_OR_LEFT(POWER_SHARD_LEFT.slotIdMask | POWER_SHARD_RIGHT.slotIdMask, true), // 24576
    RIGHT_HAND(MAIN_HAND.slotIdMask | MAIN_OFF_HAND.slotIdMask, true),
    LEFT_HAND(SUB_HAND.slotIdMask | SUB_OFF_HAND.slotIdMask, true),
    //TORSO_GLOVE_FOOT_SHOULDER_LEG(0, true), // TODO
    // STIGMA slots
    STIGMA1(1L << 30),//4.8 checked 1073741824
    STIGMA2(1L << 31),//4.8 checked 2147483648
    STIGMA3(1L << 32),//4.8 checked 4294967296
    REGULAR_STIGMAS(STIGMA1.slotIdMask | STIGMA2.slotIdMask | STIGMA3.slotIdMask, true),
    ADV_STIGMA1(1L << 33),//4.8 checked 8589934592
    ADV_STIGMA2(1L << 34),//4.8 checked 17179869184
    ADVANCED_STIGMAS(ADV_STIGMA1.slotIdMask | ADV_STIGMA2.slotIdMask, true),
    MAJ_STIGMA(1L << 35),//4.8 checked 34359738368
    SPECIAL_STIGMA(1L << 36),//5.x checked 68719476736
    MAJOR_STIGMAS(MAJ_STIGMA.slotIdMask, true),
    ALL_STIGMA(REGULAR_STIGMAS.slotIdMask | ADVANCED_STIGMAS.slotIdMask | MAJOR_STIGMAS.slotIdMask, true);
    private long slotIdMask;
    private boolean combo;
    private ItemSlot(long mask) {
        this(mask, false);
    }
    private ItemSlot(long mask, boolean combo) {
        this.slotIdMask = mask;
        this.combo = combo;
    }
    public long getSlotIdMask() {
        return slotIdMask;
    }
    /**
     * @return the combo
     */
    public boolean isCombo() {
        return combo;
    }
    public static boolean isRegularStigma(long slot) {
        return (REGULAR_STIGMAS.slotIdMask & slot) == slot;
    }
    public static boolean isAdvancedStigma(long slot) {
        return (ADVANCED_STIGMAS.slotIdMask & slot) == slot;
    }
    public static boolean isMajorStigma (long slot) {
        return (MAJOR_STIGMAS.slotIdMask & slot) == slot;
    }
    public static boolean isSpecialStigma (long slot) {
        return (SPECIAL_STIGMA.slotIdMask & slot) == slot;
    }
    public static boolean isStigma(long slot) {
        return (ALL_STIGMA.slotIdMask & slot) == slot;
    }
    public static ItemSlot[] getSlotsFor(long slot) {
        List<ItemSlot> slots = new ArrayList<ItemSlot>();
        for (ItemSlot itemSlot : values()) {
            if (slot != 0 && !itemSlot.isCombo() && (slot & itemSlot.slotIdMask) == itemSlot.slotIdMask) {
                slots.add(itemSlot);
            }
        }
        return slots.toArray(new ItemSlot[slots.size()]);
    }
    public static ItemSlot getSlotFor(long slot) {
        ItemSlot[] slots = getSlotsFor(slot);
        if (slots != null && slots.length > 0) {
            return slots[0];
        }
        throw new IllegalArgumentException("Invalid provided slotIdMask " + slot);
    }
}