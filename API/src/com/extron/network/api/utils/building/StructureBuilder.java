package com.extron.network.api.utils.building;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

public class StructureBuilder {

    private final int xSize;
    private final int ySize;
    private final int zSize;

    private char[][][] structure;
    private Map<Character,Material> materialMap;
    private Map<Character,Integer> dataMap;

    private int currentLayer; //y
    private int currentStrip; // x

    public StructureBuilder(int xSize, int ySize, int zSize) {
        this.structure = new char[xSize][ySize][zSize];
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        this.materialMap = new HashMap<>();
        this.dataMap = new HashMap<>();
        this.currentLayer = ySize-1;
    }

    public StructureBuilder addLayer(String... layer) {
        if (currentLayer < 0) return this;
        for (String s : layer) {
            this.addStrip(s);
        }
        currentLayer--;
        currentStrip = 0;
        return this;
    }

    public StructureBuilder addStrip(String strip) {
        if (currentStrip >= xSize) {
            return this;
        }
        int i = 0;
        for (char c : strip.toCharArray()) {
            structure[currentStrip][currentLayer][i] = c;
            System.out.println("set character to " + currentStrip + " layer " + currentLayer + " index " + i + " = " + c);
            i++;
            if (i >= zSize) break;
        }
        currentStrip++;
        return this;
    }

    public StructureBuilder addMaterial(char c, Material m) {
        return addMaterial(c,m,0);
    }

    public StructureBuilder addMaterial(char c, Material m, int data) {
        materialMap.put(c,m);
        if (data != 0) {
            dataMap.put(c,data);
        }
        return this;
    }

    public Structure build(Location negativeCorner) {
        Structure s = new Structure(negativeCorner);
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                for (int z = 0; z < zSize; z++) {
                    char c = structure[x][y][z];
                    if (c != '\u0000' && !Character.isSpaceChar(c)) {
                        Material m = materialMap.get(c);
                        if (m != null) {
                            System.out.println("placing block of type " + m.name());
                            Integer d = dataMap.get(c);
                            byte data;
                            if (d == null) {
                                data = 0;
                            } else {
                                data = d.byteValue();
                            }
                            System.out.println("data " + data);
                            Block b = negativeCorner.clone().add(x,y,z).getBlock();
                            b.setType(m);
                            b.setData(data);
                            s.append(m,data,x,y,z);
                        }
                    }
                }
            }
        }
        return s;
    }
}
