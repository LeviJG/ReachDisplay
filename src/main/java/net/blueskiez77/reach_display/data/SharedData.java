package net.blueskiez77.reach_display.data;

import net.minecraft.world.entity.Entity;
import net.blueskiez77.reach_display.config.DisplayConfig;
import java.util.LinkedList;
import java.util.Queue;

public class SharedData {
    private static SharedData instance;
    private double localAverageDistance = 0;
    private int localAverageHitCount = 0;
    private final Queue<Double> lastHitsDistance = new LinkedList<>();
    private double averageDistance = 0;
    private double distance;
    private Entity entity;

    private SharedData() {}

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
            case LAST_HITS -> {
                this.lastHitsDistance.add(distance);
                while (this.lastHitsDistance.size() > DisplayConfig.averageNumberOfHitsCounted) {
                    this.lastHitsDistance.poll();
                }
                averageDistance = calculateAverageLastHitsDistance();
            }
        }
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

    private double calculateAverageLastHitsDistance() {
        double sum = 0;
        for (double distance : lastHitsDistance) {
            sum += distance;
        }
        return sum / lastHitsDistance.size();
    }
}