package net.wolren.reach_display.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class DisplayConfig extends MidnightConfig {
    @Entry(name = "Enable")
    public static boolean enabled = true;

    @Comment()
    public static Comment spacer0;

    @Entry(name = "Enable Distance Display")
    public static boolean distanceEnable = true;

    @Entry(name = "Distance Scale", min = 0.1f, max = 5f)
    public static float distanceScale = 1.0f;

    @Entry(name = "Distance X Offset", min = Integer.MIN_VALUE, max = Integer.MAX_VALUE)
    public static int xOffset = 0;

    @Entry(name = "Distance Y Offset", min = Integer.MIN_VALUE, max = Integer.MAX_VALUE)
    public static int yOffset = -10;

    @Entry(name = "Distance Color", isColor = true)
    public static String distanceColor = "#FFFFFF";

    @Entry(name = "Distance Opacity", min = 0.0f, max = 1.0f)
    public static float distanceOpacity = 1.0f;

    @Entry(name = "Distance Decimal Places")
    public static int distanceDecimalPlaces = 2;

    @Entry(name = "Distance Bold")
    public static boolean distanceBold = false;

    @Entry(name = "Distance Italic")
    public static boolean distanceItalic = false;

    @Entry(name = "Distance Underline")
    public static boolean distanceUnderline = false;

    @Entry(name = "Distance Update Rate (ms)", min = 0, max = 1000)
    public static int distanceUpdateRate = 0;

    @Entry(name = "Distance Smooth Interpolation")
    public static boolean distanceSmoothInterpolation = true;

    @Entry(name = "Distance Display Mode")
    public static DisplayMode distanceDisplayMode = DisplayMode.NUMBER_ONLY;
    @Entry(name = "Distance Calculation Method")
    public static DistanceCalculationMethod distanceCalculationMethod = DistanceCalculationMethod.RAY_HIT_POINT;
    @Entry(name = "Distance Color Bands")
    public static boolean distanceColorBandsEnabled = false;
    @Entry(name = "Distance Band 1 Threshold")
    public static float distanceBand1Threshold = 1.0f;
    @Entry(name = "Distance Band 1 Color", isColor = true)
    public static String distanceBand1Color = "#00FF00";
    @Entry(name = "Distance Band 2 Threshold")
    public static float distanceBand2Threshold = 2.5f;
    @Entry(name = "Distance Band 2 Color", isColor = true)
    public static String distanceBand2Color = "#FFFF00";
    @Entry(name = "Distance Band 3 Color", isColor = true)
    public static String distanceBand3Color = "#FF0000";
    @Entry(name = "Distance Gradient")
    public static boolean distanceGradientEnabled = false;
    @Entry(name = "Distance Gradient Start Color", isColor = true)
    public static String distanceGradientStartColor = "#00FF00";
    @Entry(name = "Distance Gradient End Color", isColor = true)
    public static String distanceGradientEndColor = "#FF0000";
    @Entry(name = "Distance Background")
    public static boolean distanceBackground = false;
    @Entry(name = "Distance Background Color", isColor = true)
    public static String distanceBackgroundColor = "#000000";
    @Entry(name = "Distance Background Opacity", min = 0.0f, max = 1.0f)
    public static float distanceBackgroundOpacity = 0.5f;
    @Comment()
    public static Comment spacer1;
    @Entry(name = "Enable Hit Distance Display")
    public static boolean hitDistanceEnable = true;
    @Entry(name = "Hit Distance Scale", min = 0.1f, max = 5f)
    public static float hitDistanceScale = 1.0f;
    @Entry(name = "Hit Distance X Offset", min = Integer.MIN_VALUE, max = Integer.MAX_VALUE)
    public static int hitXOffset = 4;
    @Entry(name = "Hit Distance Y Offset", min = Integer.MIN_VALUE, max = Integer.MAX_VALUE)
    public static int hitYOffset = 4;
    @Entry(name = "Hit Distance Color", isColor = true)
    public static String hitDistanceColor = "#FFAA00";
    @Entry(name = "Hit Distance Opacity", min = 0.0f, max = 1.0f)
    public static float hitDistanceOpacity = 1.0f;
    @Entry(name = "Hit Distance Decimal Places")
    public static int hitDistanceDecimalPlaces = 2;
    @Entry(name = "Hit Bold")
    public static boolean hitDistanceBold = false;
    @Entry(name = "Hit Italic")
    public static boolean hitDistanceItalic = false;
    @Entry(name = "Hit Underline")
    public static boolean hitDistanceUnderline = false;
    @Entry(name = "Hit Display Mode")
    public static DisplayMode hitDistanceDisplayMode = DisplayMode.NUMBER_ONLY;
    @Entry(name = "Hit Keep Last Distance")
    public static boolean hitDistanceKeep = true;
    @Entry(name = "Hit Reset Seconds", min = 0, max = 600)
    public static int hitDistanceResetSeconds = 15;
    @Entry(name = "Hit Distance Calculation Method")
    public static DistanceCalculationMethod hitDistanceCalculationMethod = DistanceCalculationMethod.RAY_HIT_POINT;
    @Entry(name = "Hit Color Bands")
    public static boolean hitDistanceColorBandsEnabled = true;
    @Entry(name = "Hit Band 1 Threshold")
    public static float hitDistanceBand1Threshold = 1.0f;
    @Entry(name = "Hit Band 1 Color", isColor = true)
    public static String hitDistanceBand1Color = "#00FF00";
    @Entry(name = "Hit Band 2 Threshold")
    public static float hitDistanceBand2Threshold = 2.5f;
    @Entry(name = "Hit Band 2 Color", isColor = true)
    public static String hitDistanceBand2Color = "#FFFF00";
    @Entry(name = "Hit Band 3 Color", isColor = true)
    public static String hitDistanceBand3Color = "#FF0000";
    @Entry(name = "Hit Gradient")
    public static boolean hitDistanceGradientEnabled = false;
    @Entry(name = "Hit Gradient Start Color", isColor = true)
    public static String hitDistanceGradientStartColor = "#00FF00";
    @Entry(name = "Hit Gradient End Color", isColor = true)
    public static String hitDistanceGradientEndColor = "#FF0000";
    @Entry(name = "Hit Background")
    public static boolean hitDistanceBackground = false;
    @Entry(name = "Hit Background Color", isColor = true)
    public static String hitDistanceBackgroundColor = "#000000";
    @Entry(name = "Hit Background Opacity", min = 0.0f, max = 1.0f)
    public static float hitDistanceBackgroundOpacity = 0.5f;
    @Comment()
    public static Comment spacer2;
    @Entry(name = "Enable Average Hit Distance")
    public static boolean averageHitDistanceEnable = false;
    @Entry(name = "Average Hit Mode")
    public static AverageModeEnum averageHitMode = AverageModeEnum.LAST_HITS;
    @Entry(name = "Number Of Hits Counted", min = 2, max = 32767)
    public static int averageNumberOfHitsCounted = 3;
    @Comment()
    public static Comment spacer3;
    @Entry(name = "Average Hit Distance Scale", min = 0.1f, max = 5f)
    public static float averageHitDistanceScale = 1.0f;
    @Entry(name = "Average Hit Distance X Offset", min = Integer.MIN_VALUE, max = Integer.MAX_VALUE)
    public static int averageHitXOffset = 4;
    @Entry(name = "Average Hit Distance Y Offset", min = Integer.MIN_VALUE, max = Integer.MAX_VALUE)
    public static int averageHitYOffset = 20;
    @Entry(name = "Average Hit Distance Color", isColor = true)
    public static String averageHitDistanceColor = "#00FFFF";
    @Entry(name = "Average Hit Distance Opacity", min = 0.0f, max = 1.0f)
    public static float averageHitDistanceOpacity = 1.0f;
    @Entry(name = "Average Hit Distance Decimal Places")
    public static int averageHitDistanceDecimalPlaces = 2;
    @Entry(name = "Average Hit Bold")
    public static boolean averageHitDistanceBold = false;
    @Entry(name = "Average Hit Italic")
    public static boolean averageHitDistanceItalic = false;
    @Entry(name = "Average Hit Underline")
    public static boolean averageHitDistanceUnderline = false;
    @Entry(name = "Average Hit Display Mode")
    public static DisplayMode averageHitDistanceDisplayMode = DisplayMode.NUMBER_ONLY;
    @Entry(name = "Average Hit Color Bands")
    public static boolean averageHitDistanceColorBandsEnabled = false;
    @Entry(name = "Average Hit Band 1 Threshold")
    public static float averageHitDistanceBand1Threshold = 1.0f;
    @Entry(name = "Average Hit Band 1 Color", isColor = true)
    public static String averageHitDistanceBand1Color = "#00FF00";
    @Entry(name = "Average Hit Band 2 Threshold")
    public static float averageHitDistanceBand2Threshold = 2.5f;
    @Entry(name = "Average Hit Band 2 Color", isColor = true)
    public static String averageHitDistanceBand2Color = "#FFFF00";
    @Entry(name = "Average Hit Band 3 Color", isColor = true)
    public static String averageHitDistanceBand3Color = "#FF0000";
    @Entry(name = "Average Hit Gradient")
    public static boolean averageHitDistanceGradientEnabled = false;
    @Entry(name = "Average Hit Gradient Start Color", isColor = true)
    public static String averageHitDistanceGradientStartColor = "#00FF00";
    @Entry(name = "Average Hit Gradient End Color", isColor = true)
    public static String averageHitDistanceGradientEndColor = "#FF0000";
    @Entry(name = "Average Hit Background")
    public static boolean averageHitDistanceBackground = false;
    @Entry(name = "Average Hit Background Color", isColor = true)
    public static String averageHitDistanceBackgroundColor = "#000000";
    @Entry(name = "Average Hit Background Opacity", min = 0.0f, max = 1.0f)
    public static float averageHitDistanceBackgroundOpacity = 0.5f;
    @Entry(name = "Enable Entity Filter")
    public static boolean entityFilterEnable = true;
    @Entry(name = "Entity Filter Mode")
    public static EntityFilterMode entityFilterMode = EntityFilterMode.WHITELIST;
    @Entry(name = "Filter Players")
    public static boolean entityFilterPlayers = true;
    @Entry(name = "Filter Hostile Mobs")
    public static boolean entityFilterHostile = false;
    @Entry(name = "Filter Passive Mobs")
    public static boolean entityFilterPassive = false;
    @Entry(name = "Filter Boss Mobs")
    public static boolean entityFilterBoss = false;
    @Entry(name = "Filter Other Mobs")
    public static boolean entityFilterOther = false;
    @Entry(name = "Filter Custom Entity IDs")
    public static String entityFilterCustomIDs = "";

    public enum DistanceCalculationMethod {
        RAY_HIT_POINT,
        CLOSEST_POINT,
    }

    public enum DisplayMode {
        NUMBER_ONLY,
        WITH_BLOCKS,
        WITH_M
    }

    public enum EntityFilterMode {
        WHITELIST,
        BLACKLIST
    }

    public enum AverageModeEnum {
        LOCAL_AVERAGE, GLOBAL_AVERAGE, LAST_HITS
    }

}
