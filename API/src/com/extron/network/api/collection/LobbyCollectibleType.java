package com.extron.network.api.collection;

import com.extron.network.api.Main;
import com.extron.network.api.collection.pet.Pet;
import com.extron.network.api.event.inventory.ButtonClickEvent;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.ListUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LobbyCollectibleType extends CollectibleTypeBase {
    public static final LobbyCollectibleType PET = (LobbyCollectibleType) new LobbyCollectibleType("pet")
            .setIcon(Material.ROTTEN_FLESH)
            .setDescription("Spawn and adopt some minecraft pets, and walk with them in the lobby!")
            .setOnSelect((p, s) -> {
                p.pet = p.getCollection().getPetInstance((Pet) s);
                if (p.isOnline()) {
                    p.pet.spawn();
                    p.sendMessage(ChatColor.GREEN + "Spawned your " + p.pet.getName() + " pet!");
                }
            })
            .setOnDeselect(player -> {if (player.pet != null) {player.pet.kill(); player.sendMessage(ChatColor.GREEN + "Despawned your " + player.pet.getName() + " pet.");}});
    public static final LobbyCollectibleType GADGET = (LobbyCollectibleType) new LobbyCollectibleType("gadget")
            .setIcon(Material.PISTON_BASE)
            .setDescription("Play along with other players in the lobby or by yourself with some little gadgets!")
            .setOnDeselect(ExtronPlayer::reloadInventory)
            .setOnSelect((player, selectable) -> player.reloadInventory());


    public LobbyCollectibleType(String id) {
        super(id);
    }

    public static List<LobbyCollectibleType> getAllTypes() {
        List<LobbyCollectibleType> list = new ArrayList<>();
        for (CollectibleType t : Main.getCollectibleTypes()) {
            if (t instanceof LobbyCollectibleType) {
                list.add((LobbyCollectibleType) t);
            }
        }
        return list;
    }

    @Override
    public void select(ExtronPlayer player, Selectable c) {
        Selectable prev = player.getSelectedCollectible(this);
        if (prev != null) {
            this.deselect(player);
        }
        player.getCollection().select(this,c);
        if (onSelect != null) {
            onSelect.accept(player,c);
        }
        player.sendMessage(ChatColor.GREEN + "Selected " + name + " " + ChatColor.YELLOW + c.getDisplayName() + "!");
    }

    @Override
    public void deselect(ExtronPlayer player) {
        player.getCollection().deselect(this);
        if (onDeselect != null) {
            onDeselect.accept(player);
        }
    }

    @Override
    public List<? extends CategoryItem> getAll() {
        return ListUtils.castAll(Main.getCollectiblesOfType(this),LobbyCollectible.class);
    }

    @Override
    public String getParentName() {
        return "Collectibles";
    }

    @Override
    public boolean isMainCategory() {
        return true;
    }


    @Override
    public void menuClick(Collectible c, ExtronPlayer p, ButtonClickEvent click) {
        if (menuClick != null) {
            boolean b = menuClick.test(c,p,click);
            if (!b) return;
        }
        if (p.foundCollectible(c)) {
            if (c instanceof Selectable) {
                if (p.getSelectedCollectible(this) == c) {
                    System.out.println("deselecting");
                    this.deselect(p);
                } else {
                    System.out.println("selecting");
                    this.select(p, (Selectable) c);
                }
            }
        } else {
            p.sendMessage(ChatColor.RED + "You haven't found this " + name + " yet!");
        }
    }

    @Override
    public String getDisplayName() {
        return "Collectibles";
    }

    public List<Collectible> getFound(ExtronPlayer player) {
        return player.getCollection().getAll(this);
    }

}
