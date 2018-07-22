package com.extron.network.api.entity;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.Main;
import com.extron.network.api.utils.building.Structure;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

public class EntityBlock extends BasicEntity {

    private final Material type;
    private final byte data;
    private ExtronFallingBlock fallingBlock;

    public EntityBlock(ExtronWorld world, Location loc, Material type, byte data) {
        super(world);
        this.spawnLocation = loc;
        this.type = type;
        this.data = data;
    }

    @Override
    public void spawn() {
        super.spawn();
        if (entity != null) {
            fallingBlock = new ExtronFallingBlock(world,spawnLocation,type,data,false,new Vector(0,0,0));
            fallingBlock.spawn();
            fallingBlock.setNeverDespawn();
            getEntity().setPassenger(fallingBlock.entity);
            getEntity().setVisible(false);
            getEntity().setGravity(false);
            getEntity().setSmall(true);
        }
    }

    public static EntityBlock fromStructurePiece(Structure.Piece p) {
        return new EntityBlock(Main.getWorld(p.getLocation().getWorld()),p.getLocation().getBlock().getLocation().add(0.5,0,0.5),p.getType(),p.getData());
    }

    public ExtronFallingBlock getFallingBlock() {
        return fallingBlock;
    }

    @Override
    public CraftArmorStand getEntity() {
        return (CraftArmorStand)entity;
    }

    @Override
    public void kill() {
        super.kill();
        if (fallingBlock != null) {
            fallingBlock.kill();
        }
    }

    @Override
    public EntityType getType() {
        return EntityType.ARMOR_STAND;
    }
}
