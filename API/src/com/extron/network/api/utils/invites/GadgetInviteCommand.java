package com.extron.network.api.utils.invites;

import com.extron.network.api.collection.Gadget;
import com.extron.network.api.permission.Permission;
import com.extron.network.api.permission.PermissionBasic;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.ChatColor;

public class GadgetInviteCommand extends CommandInviteBase {

    private final String name;
    private Gadget gadget;

    public GadgetInviteCommand(InvitingGadget gadget, String name) {
        super(gadget);
        this.gadget = gadget;
        this.name = name;
    }

    @Override
    protected Permission getInvitePermission() {
        return new PermissionBasic("You haven't found the " + gadget.getDisplayName() + " gadget yet!");
    }

    @Override
    protected String getInvalidAcceptMessage() {
        return ChatColor.RED + "This player didn't invited you to a " + gadget.getDisplayName() + " match!";
    }

    @Override
    public void onInvitePlayer(ExtronPlayer sender, ExtronPlayer invited) throws Exception {
        if (!invited.isOnline()) {
            error("This player is not online!");
        }
    }

    @Override
    public void onInviteAccepted(ExtronPlayer accepted, ExtronPlayer sender) throws Exception {
        if (!sender.isOnline()) {
            error("This player is not online!");
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean addHelpSubCommand() {
        return false;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
