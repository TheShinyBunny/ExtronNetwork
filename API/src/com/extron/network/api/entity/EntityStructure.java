package com.extron.network.api.entity;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.utils.building.Structure;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftArmorStand;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityStructure extends BasicEntity {

    protected List<EntityBlock> pieces;

    public EntityStructure(ExtronWorld world, Structure s) {
        super(world);
        this.pieces = new ArrayList<>();
        this.spawnLocation = s.getOrigin();
        for (Structure.Piece p : s.getPieces()) {
            pieces.add(EntityBlock.fromStructurePiece(p));
            p.getLocation().getBlock().setType(Material.AIR);
        }
    }

    @Override
    public void spawn() {
        super.spawn();
        this.getEntity().setSmall(true);
        this.getEntity().setGravity(false);
        this.getEntity().setVisible(false);
        for (EntityBlock b : pieces) {
            b.spawn();
        }
    }

    @Override
    public void kill() {
        super.kill();
        for (EntityBlock b : pieces) {
            b.kill();
        }
    }

    @Override
    public CraftArmorStand getEntity() {
        return (CraftArmorStand) entity;
    }

    @Override
    public EntityType getType() {
        return EntityType.ARMOR_STAND;
    }
}
