package com.extron.network.api.utils.invites;

import com.extron.network.api.Main;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.TextUtils;
import org.bukkit.ChatColor;

import java.util.*;

public abstract class InviteHelper {

    public List<Invite> invites;
    protected CommandInviteBase command;

    public InviteHelper() {
        this.invites = new ArrayList<>();
        this.command = createCommand();
        Main.registerCommand(command);
    }

    protected abstract CommandInviteBase createCommand();

    public void invite(ExtronPlayer sender, ExtronPlayer invited) {
        if (sender.equals(invited)) {
            sender.sendMessage(getCantInviteYourselfMessage());
            return;
        }
        Invite inv = createInvite(sender,invited);
        if (getInvitesOfPlayer(invited).contains(inv)) {
            sender.sendMessage(getAlreadyInvitedMessage());
            return;
        }
        this.invites.add(inv);
        sender.sendMessage(getSenderInviteMessage(invited));
        invited.sendMessage(getInvitedMessage(sender));
        TextUtils.sendClickableMessage(invited,getClickLinkColor() + "Click here",command.getName() + " " + command.getAcceptCommandName() + " " + sender.getName()," " + getClickSuffix());
        inv.startTimer();
        onInviteSent(inv);
    }

    protected abstract String getCantInviteYourselfMessage();

    protected abstract void onInviteSent(Invite inv);

    protected abstract String getAlreadyInvitedMessage();

    protected abstract String getClickSuffix();

    protected abstract ChatColor getClickLinkColor();

    protected abstract String getInvitedMessage(ExtronPlayer sender);

    protected abstract String getSenderInviteMessage(ExtronPlayer invited);

    public abstract Invite createInvite(ExtronPlayer sender, ExtronPlayer invited);

    public Set<Invite> getInvitesOfPlayer(ExtronPlayer player) {
        Set<Invite> set = new HashSet<>();
        for (Invite i : invites) {
            if (i.getInvited().equals(player)) {
                set.add(i);
            }
        }
        return set;
    }

    public void accept(ExtronPlayer accepted, ExtronPlayer sender) {
        for (Invite i : getInvitesOfPlayer(accepted)) {
            if (i.getSender().equals(sender)) {
                invites.remove(i);
                i.cancel();
            }
        }
        sender.sendMessage(getGotAcceptedMessage());
        accepted.sendMessage(getAcceptMessage());
        onInviteAccepted(sender,accepted);
    }

    protected abstract void onInviteAccepted(ExtronPlayer sender, ExtronPlayer accepted);

    protected abstract String getAcceptMessage();

    protected abstract String getGotAcceptedMessage();

    public void expiredInvite(Invite invite) {
        invites.remove(invite);
        invite.getSender().sendMessage(getSenderExpiredMessage(invite.getInvited()));
        invite.getInvited().sendMessage(getInvitedExpiredMessage(invite.getSender()));
        onInviteExpired(invite);
    }

    protected abstract void onInviteExpired(Invite invite);

    protected abstract String getInvitedExpiredMessage(ExtronPlayer sender);

    protected abstract String getSenderExpiredMessage(ExtronPlayer invited);

}
