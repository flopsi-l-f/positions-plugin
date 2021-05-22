package ch.frauenfelderflorian.positionsplugin.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Positions {
    public HashMap<String, Location> usablePositions;
    private HashMap<String, ArrayList<String>> savablePositions;
    public String fileName;
    private final Yaml yaml;

    public Positions(String filename) {
        this.usablePositions = new HashMap<>();
        this.savablePositions = new HashMap<>();
        this.fileName = filename;
        this.yaml = new Yaml();
    }

    private ArrayList<String> locationGetArrayList(Location loc) {
        ArrayList<String> list = new ArrayList<>();
        list.add(0, Objects.requireNonNull(loc.getWorld()).getName());
        list.add(1, Double.toString(loc.getX()));
        list.add(2, Double.toString(loc.getY()));
        list.add(3, Double.toString(loc.getZ()));
        list.add(4, Double.toString(loc.getYaw()));
        list.add(5, Double.toString(loc.getPitch()));
        return list;
    }

    private Location arrayListGetLocation(ArrayList<String> list) {
        return new Location(
                Bukkit.getWorld(list.get(0)),
                Double.parseDouble(list.get(1)),
                Double.parseDouble(list.get(2)),
                Double.parseDouble(list.get(3)),
                Float.parseFloat(list.get(4)),
                Float.parseFloat(list.get(5))
        );
    }

    public void add(String name, Location location) {
        this.usablePositions.put(name, location);
        save();
    }

    public String positionToString(String name) {
        return name + ": " + this.usablePositions.get(name).getBlockX() + "  "
                + this.usablePositions.get(name).getBlockY() + "  "
                + this.usablePositions.get(name).getBlockZ();
    }

    public void save() {
        File f = new File(this.fileName);
        this.savablePositions.clear();
        try {
            boolean fileCreated = f.createNewFile();
            if (fileCreated) {
                Logger.getLogger(Positions.class.getName()).log(Level.INFO, "new file created");
                Bukkit.broadcastMessage("new positions file created");
            }
        } catch (IOException e) {
            Logger.getLogger(Positions.class.getName()).log(Level.SEVERE, "filesystem error", e);
        }
        try {
            FileWriter writer = new FileWriter(this.fileName);
            for (Map.Entry<String, Location> entry : this.usablePositions.entrySet()) {
                ArrayList<String> value = locationGetArrayList(entry.getValue());
                this.savablePositions.put(entry.getKey(), value);
            }
            yaml.dump(this.savablePositions, writer);
            Bukkit.broadcastMessage("saved positions to positions file");
        } catch (IOException e) {
            Logger.getLogger(Positions.class.getName()).log(Level.SEVERE, "file error", e);
            Bukkit.broadcastMessage("positions file cannot be accessed");
        }
    }

    public void load() {
        try {
            FileReader input = new FileReader(this.fileName);
            this.savablePositions = yaml.load(input);
        } catch (FileNotFoundException e) {
            Logger.getLogger(Positions.class.getName()).log(Level.SEVERE, "file error", e);
            Bukkit.broadcastMessage("file not found");
        }
        for (Map.Entry<String, ArrayList<String>> entry : this.savablePositions.entrySet()) {
            ArrayList<String> value = new ArrayList<>(entry.getValue());
            Location loc = arrayListGetLocation(value);
            this.usablePositions.put(entry.getKey(), loc);
        }
        Bukkit.broadcastMessage("positions loaded from file");
    }
}