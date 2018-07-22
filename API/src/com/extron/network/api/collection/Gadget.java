package com.extron.network.api.collection;

import com.extron.network.api.event.inventory.ItemInteractEvent;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;


public interface Gadget extends LobbyCollectible {

    /**
     * Called after the player right clicks while holding this gadget in the hotbar.
     * Used to activate the gadget logic
     * @param p
     */
    void onActivate(ExtronPlayer p);

    /**
     * Called when the player right clicks another entity while holding this gadget.
     * @param p
     * @param target
     */
    void onActivateAt(ExtronPlayer p, Entity target);

    /**
     * The time in seconds it takes to the gadget to be usable again.
     * @return
     */
    int getCooldown();

    /**
     * Called right before {@link #onActivate(ExtronPlayer)} and {@link #onActivateAt(ExtronPlayer, Entity)} are called.
     * Cancel the interact event to cancel the activation and starting of the cooldown in case of invalid operation, not enough space for the gadget, etc.
     * @param e
     */
    void shouldActivate(ItemInteractEvent e);

    @Override
    default LobbyCollectibleType getType() {
        return LobbyCollectibleType.GADGET;
    }

    default void onClick(ItemInteractEvent e) {
        ExtronPlayer p = e.getPlayer();
        if (p.getGadgetCooldown() > 0) {
            System.out.println("sending cooldown message");
            p.sendMessage(ChatColor.RED + "You can use the " + this.getDisplayName() + " in " + ((p.getGadgetCooldown() / 20) + 1) + " " + TextUtils.addNeededS(p.getGadgetCooldown() / 20, "second") + "!");
        } else {
            e.setCancelled(false);
            this.shouldActivate(e);
            if (!e.isCancelled()) {
                this.onActivate(p);
                if (e.getEntity() != null) {
                    this.onActivateAt(p, e.getEntity());
                }
                p.startGadgetCooldown(this);
                e.setCancelled(true);
            }
        }
    }


}
