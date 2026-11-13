package com.fudgeu.playlist.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item.Properties;

public class PersonalJukebox extends Item {
    public PersonalJukebox(Properties settings) {
        super(settings);
    }

    public InteractionResultHolder<ItemStack> use(Level world, Player playerEntity, InteractionHand hand) {
        playerEntity.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.0F, 1.0F);
        return InteractionResultHolder.success(playerEntity.getItemInHand(hand));
    }
}
