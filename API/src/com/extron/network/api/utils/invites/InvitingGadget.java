package com.extron.network.api.utils.invites;

import com.extron.network.api.collection.Gadget;
import com.extron.network.api.event.inventory.ItemInteractEvent;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public abstract class InvitingGadget extends InviteHelper implements Gadget {
    @Override
    public void onActivate(ExtronPlayer p) {

    }

    @Override
    public void onActivateAt(ExtronPlayer p, Entity target) {
        if (target instanceof Player) {
            Player o = (Player) target;
            ExtronPlayer other = ExtronPlayer.of(o);
            p.executeCommand(getCommandName() + " " + command.getInviteCommandName() + " " + other.getName());
        }
    }

    @Override
    public void shouldActivate(ItemInteractEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            e.setCancelled(true);
        }
    }

    @Override
    protected CommandInviteBase createCommand() {
        return new GadgetInviteCommand(this,getCommandName());
    }

    protected abstract String getCommandName();

    @Override
    protected String getCantInviteYourselfMessage() {
        return ChatColor.RED + "You can't invite yourself to a " + getDisplayName() + " match!";
    }

    @Override
    protected void onInviteSent(Invite inv) {

    }

    @Override
    protected String getAlreadyInvitedMessage() {
        return ChatColor.RED + "You already invited this player to play " + getDisplayName() + "!";
    }

    @Override
    protected String getClickSuffix() {
        return "to play with him!";
    }

    @Override
    protected ChatColor getClickLinkColor() {
        return ChatColor.AQUA;
    }

    @Override
    protected String getInvitedMessage(ExtronPlayer sender) {
        return sender.getName() + ChatColor.GREEN + " has invited you to a " + ChatColor.GOLD + "" + ChatColor.BOLD + getDisplayName() + ChatColor.GREEN + " match!";
    }

    @Override
    protected String getSenderInviteMessage(ExtronPlayer invited) {
        return ChatColor.GREEN + "You invited " + invited.getName() + " to a " + ChatColor.GOLD + "" + ChatColor.BOLD + getDisplayName() + ChatColor.GREEN + " match!";
    }

    @Override
    public Invite createInvite(ExtronPlayer sender, ExtronPlayer invited) {
        return new BasicInvite(this,sender,invited,getCooldown());
    }

    @Override
    protected String getAcceptMessage() {
        return null;
    }

    @Override
    protected String getGotAcceptedMessage() {
        return null;
    }

    @Override
    protected void onInviteExpired(Invite invite) {
        invite.getSender().cancelGadgetCooldown();
    }

    @Override
    protected String getInvitedExpiredMessage(ExtronPlayer sender) {
        return ChatColor.RED + "The " + getDisplayName() + " invite from " + sender.getName() + " had expired.";
    }

    @Override
    protected String getSenderExpiredMessage(ExtronPlayer invited) {
        return ChatColor.RED + "Your " + getDisplayName() + " invite to " + invited.getName() + " had expired.";
    }

    protected abstract class InstanceBase {

        protected ExtronPlayer player;
        protected ExtronPlayer opponent;

        public InstanceBase(ExtronPlayer player, ExtronPlayer opponent) {
            this.player = player;
            this.opponent = opponent;
        }

        public InstanceBase getOtherInstance() {
            return null;
        }
    }
}
