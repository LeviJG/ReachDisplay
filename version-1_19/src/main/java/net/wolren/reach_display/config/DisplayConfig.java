package net.wolren.reach_display.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class DisplayConfig extends MidnightConfig {
    @Entry(name = "Enable")
    public static boolean enabled = true;

    @Entry(name = "Show Players Only")
    public static boolean showPlayers = true;

    @Comment() public static Comment spacer0;

    @Entry(name = "Enable Distance Display")
    public static boolean distanceEnable = true;

    @Entry(name = "Distance Scale", isSlider = true, min = 0.1f, max = 5f, precision = 10)
    public static float distanceScale = 1.0f;

    @Entry(name = "Distance X Offset", min = Integer.MIN_VALUE, max = Integer.MAX_VALUE)
    public static int xOffset = 0;

    @Entry(name = "Distance Y Offset", min = Integer.MIN_VALUE, max = Integer.MAX_VALUE)
    public static int yOffset = -10;

    @Entry(name = "Distance Color", isColor = true)
    public static String distanceColor = "FFFFFF";

    @Entry(name = "Distance Decimal Places")
    public static int distanceDecimalPlaces = 2;

    @Comment() public static Comment spacer1;

    @Entry(name = "Enable Hit Distance Display")
    public static boolean hitDistanceEnable = true;

    @Entry(name = "Hit Distance Scale", isSlider = true, min = 0.1f, max = 5f, precision = 10)
    public static float hitDistanceScale = 1.0f;

    @Entry(name = "Hit Distance X Offset", min = Integer.MIN_VALUE, max = Integer.MAX_VALUE)
    public static int hitXOffset = 0;

    @Entry(name = "Hit Distance Y Offset", min = Integer.MIN_VALUE, max = Integer.MAX_VALUE)
    public static int hitYOffset = -30;

    @Entry(name = "Hit Distance Color", isColor = true)
    public static String hitDistanceColor = "FFFFFF";

    @Entry(name = "Hit Distance Decimal Places")
    public static int hitDistanceDecimalPlaces = 2;

    @Comment() public static Comment spacer2;

    @Entry(name = "Enable Average Hit Distance")
    public static boolean averageHitDistanceEnable = false;

    @Entry(name = "Average Hit Mode")
    public static AverageModeEnum averageHitMode = AverageModeEnum.LOCAL_AVERAGE;

    public enum AverageModeEnum {
        LOCAL_AVERAGE, GLOBAL_AVERAGE, LAST_HITS
    }

    @Entry(name = "Number Of Hits Counted", min = 2)
    public static int averageNumberOfHitsCounted = 3;

    @Comment() public static Comment spacer3;

    @Entry(name = "Average Hit Distance Scale", isSlider = true, min = 0.1f, max = 5f, precision = 10)
    public static float averageHitDistanceScale = 1.0f;

    @Entry(name = "Average Hit Distance X Offset", min = Integer.MIN_VALUE, max = Integer.MAX_VALUE)
    public static int averageHitXOffset = 0;

    @Entry(name = "Average Hit Distance Y Offset", min = Integer.MIN_VALUE, max = Integer.MAX_VALUE)
    public static int averageHitYOffset = -100;

    @Entry(name = "Average Hit Distance Color", isColor = true)
    public static String averageHitDistanceColor = "FFFFFF";

    @Entry(name = "Average Hit Distance Decimal Places")
    public static int averageHitDistanceDecimalPlaces = 2;
}

