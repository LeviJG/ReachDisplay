package net.wolren.reach_display.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class DisplayConfig extends MidnightConfig {
    @Entry(category = "basic", name = "Enable")
    public static boolean enabled = true;

    @Entry(category = "basic", name = "Show Players Only")
    public static boolean showPlayers = true;



    @Entry(category = "distance", name = "Enable Distance Display")
    public static boolean distanceEnable = true;

    @Comment(category = "distance") public static Comment spacer1;

    @Entry(category = "distance", name = "Distance Scale", isSlider = true, min = 0.1f, max = 5f, precision = 10)
    public static float distanceScale = 1.0f;

    @Entry(category = "distance", name = "Distance X Offset", min = Integer.MIN_VALUE, max = Integer.MAX_VALUE)
    public static int xOffset = 0;

    @Entry(category = "distance", name = "Distance Y Offset", min = Integer.MIN_VALUE, max = Integer.MAX_VALUE)
    public static int yOffset = -10;

    @Entry(category = "distance", name = "Distance Color", isColor = true)
    public static String distanceColor = "FFFFFF";

    @Entry(category = "distance", name = "Distance Text Shadow")
    public static boolean distanceShadow = false;

    @Entry(category = "distance", name = "Distance Decimal Places")
    public static int distanceDecimalPlaces = 2;



    @Entry(category = "3hitDistance", name = "Enable Hit Distance Display")
    public static boolean hitDistanceEnable = true;

    @Comment(category = "3hitDistance") public static Comment spacer2;

    @Entry(category = "3hitDistance", name = "Hit Distance Scale", isSlider = true, min = 0.1f, max = 5f, precision = 10)
    public static float hitDistanceScale = 1.0f;

    @Entry(category = "3hitDistance", name = "Hit Distance X Offset", min = Integer.MIN_VALUE, max = Integer.MAX_VALUE)
    public static int hitXOffset = 0;

    @Entry(category = "3hitDistance", name = "Hit Distance Y Offset", min = Integer.MIN_VALUE, max = Integer.MAX_VALUE)
    public static int hitYOffset = -30;

    @Entry(category = "3hitDistance", name = "Hit Distance Color", isColor = true)
    public static String hitDistanceColor = "FFFFFF";

    @Entry(category = "3hitDistance", name = "Hit Distance Text Shadow")
    public static boolean hitDistanceShadow = false;

    @Entry(category = "3hitDistance", name = "Hit Distance Decimal Places")
    public static int hitDistanceDecimalPlaces = 2;



    @Entry(category = "averageHitDistance", name = "Enable Average Hit Distance")
    public static boolean averageHitDistanceEnable = false;

    @Comment(category = "averageHitDistance") public static Comment spacer3;

    @Entry(category = "averageHitDistance", name = "Average Hit Mode")
    public static AverageModeEnum averageHitMode = AverageModeEnum.LOCAL_AVERAGE;

    public enum AverageModeEnum {
        LOCAL_AVERAGE, GLOBAL_AVERAGE, LAST_HITS
    }

    @Entry(category = "averageHitDistance", name = "Number Of Hits Counted", min = 2)
    public static int averageNumberOfHitsCounted = 3;

    @Comment(category = "averageHitDistance") public static Comment spacer4;

    @Entry(category = "averageHitDistance", name = "Average Hit Distance Scale", isSlider = true, min = 0.1f, max = 5f, precision = 10)
    public static float averageHitDistanceScale = 1.0f;

    @Entry(category = "averageHitDistance", name = "Average Hit Distance X Offset", min = Integer.MIN_VALUE, max = Integer.MAX_VALUE)
    public static int averageHitXOffset = 0;

    @Entry(category = "averageHitDistance", name = "Average Hit Distance Y Offset", min = Integer.MIN_VALUE, max = Integer.MAX_VALUE)
    public static int averageHitYOffset = -100;

    @Entry(category = "averageHitDistance", name = "Average Hit Distance Color", isColor = true)
    public static String averageHitDistanceColor = "FFFFFF";

    @Entry(category = "averageHitDistance", name = "Average Hit Distance Text Shadow")
    public static boolean averageHitDistanceShadow = false;

    @Entry(category = "averageHitDistance", name = "Average Hit Distance Decimal Places")
    public static int averageHitDistanceDecimalPlaces = 2;
}

