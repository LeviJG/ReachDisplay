package net.wolren.reach_display.data;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.wolren.reach_display.config.DisplayConfig;

import java.io.*;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Queue;

public class SharedData {
    private static SharedData instance;
    private double localAverageDistance = 0;
    private int localAverageHitCount = 0;
    private Queue<Double> lastHitsDistance = new LinkedList<>();
    private double averageDistance = 0;
    private static final String GLOBAL_AVERAGE_FILE_NAME = "global_average_hits.txt";
    private double distance;
    private Entity entity;
    private PrintWriter writer;

    private SharedData() {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        File globalAverageFile = configDir.resolve(GLOBAL_AVERAGE_FILE_NAME).toFile();
        try {
            writer = new PrintWriter(new FileWriter(globalAverageFile, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SharedData getInstance() {
        if (instance == null) {
            instance = new SharedData();
        }
        return instance;
    }

    public void setDistanceAndTarget(double distance, Entity entity) {
        this.distance = distance;
        this.entity = entity;
    }

    public void addDistanceToAverage(double distance) {
        switch (DisplayConfig.averageHitMode) {
            case LOCAL_AVERAGE -> {
                localAverageDistance += distance;
                localAverageHitCount++;
                averageDistance = localAverageDistance / localAverageHitCount;
            }
            case GLOBAL_AVERAGE -> {
                writer.append(Double.toString(distance)).append(", ");
                writer.flush();

                Path configDir = FabricLoader.getInstance().getConfigDir();
                File globalAverageFile = configDir.resolve(GLOBAL_AVERAGE_FILE_NAME).toFile();

                try (BufferedReader reader = new BufferedReader(new FileReader(globalAverageFile))) {
                    String line;
                    double sum = 0;
                    int count = 0;
                    while ((line = reader.readLine()) != null) {
                        String[] distances = line.split(", ");
                        for (String distanceStr : distances) {
                            if (!distanceStr.trim().isEmpty()) {
                                sum += Double.parseDouble(distanceStr);
                                count++;
                            }
                        }
                    }
                    averageDistance = count > 0 ? sum / count : 0;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            case LAST_HITS -> {
                this.lastHitsDistance.add(distance);
                if (this.lastHitsDistance.size() > DisplayConfig.averageNumberOfHitsCounted) {
                    this.lastHitsDistance.poll();
                }
                averageDistance = calculateAverageLastHitsDistance();
            }
        }
    }

    private double calculateAverageLastHitsDistance() {
        double sum = 0;
        for (double distance : lastHitsDistance) {
            sum += distance;
        }
        return sum / lastHitsDistance.size();
    }

    public double getAverageDistance() {
        return averageDistance;
    }

    public double getDistance() {
        return distance;
    }

    public Entity getEntity() {
        return entity;
    }
}
