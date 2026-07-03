package net.wolren.reach_display.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class DisplayConfig extends MidnightConfig {
    @Entry(category = "basic", name = "Enable")
    public static boolean enabled = true;

    @Comment(category = "basic")
    public static Comment spacerBasic;

    @Entry(category = "basic", name = "Enable Entity Filter")
    public static boolean entityFilterEnable = true;

    @Entry(category = "basic", name = "Entity Filter Mode")
    public static EntityFilterMode entityFilterMode = EntityFilterMode.WHITELIST;

    @Entry(category = "basic", name = "Filter Players")
    public static boolean entityFilterPlayers = true;

    @Entry(category = "basic", name = "Filter Hostile Mobs")
    public static boolean entityFilterHostile = false;

    @Entry(category = "basic", name = "Filter Passive Mobs")
    public static boolean entityFilterPassive = false;

    @Entry(category = "basic", name = "Filter Boss Mobs")
    public static boolean entityFilterBoss = false;

    @Entry(category = "basic", name = "Filter Other Mobs")
    public static boolean entityFilterOther = false;

    @Entry(category = "basic", name = "Filter Custom Entity IDs")
    public static String entityFilterCustomIDs = "";

    @Entry(category = "distance", name = "Enable Distance Display")
    public static boolean distanceEnable = true;

    @Comment(category = "distance")
    public static Comment spacer1;

    @Entry(category = "distance", name = "Distance Scale", isSlider = true, min = 0.1f, max = 5f, precision = 10)
    public static float distanceScale = 1.0f;

    @Entry(category = "distance", name = "Distance X Offset", min = Integer.MIN_VALUE, max = Integer.MAX_VALUE)
    public static int xOffset = 0;

    @Entry(category = "distance", name = "Distance Y Offset", min = Integer.MIN_VALUE, max = Integer.MAX_VALUE)
    public static int yOffset = -10;

    @Entry(category = "distance", name = "Distance Color", isColor = true)
    public static String distanceColor = "#FFFFFF";

    @Entry(category = "distance", name = "Distance Opacity", isSlider = true, min = 0.0f, max = 1.0f)
    public static float distanceOpacity = 1.0f;

    @Entry(category = "distance", name = "Distance Text Shadow")
    public static boolean distanceShadow = true;

    @Entry(category = "distance", name = "Distance Decimal Places")
    public static int distanceDecimalPlaces = 2;

    @Entry(category = "distance", name = "Distance Bold")
    public static boolean distanceBold = false;

    @Entry(category = "distance", name = "Distance Italic")
    public static boolean distanceItalic = false;

    @Entry(category = "distance", name = "Distance Underline")
    public static boolean distanceUnderline = false;

    @Entry(category = "distance", name = "Distance Smooth Interpolation")
    public static boolean distanceSmoothInterpolation = true;

    @Entry(category = "distance", name = "Distance Update Rate (ms)", min = 0, max = 1000)
    public static int distanceUpdateRate = 0;

    @Entry(category = "distance", name = "Distance Display Mode")
    public static DisplayMode distanceDisplayMode = DisplayMode.NUMBER_ONLY;
    @Entry(category = "distance", name = "Distance Calculation Method")
    public static DistanceCalculationMethod distanceCalculationMethod = DistanceCalculationMethod.RAY_HIT_POINT;
    @Entry(category = "distance", name = "Distance Color Bands")
    public static boolean distanceColorBandsEnabled = false;
    @Entry(category = "distance", name = "Distance Band 1 Threshold", isSlider = true, min = 0.0f, max = 10.0f, precision = 10)
    public static float distanceBand1Threshold = 1.0f;
    @Entry(category = "distance", name = "Distance Band 1 Color", isColor = true)
    public static String distanceBand1Color = "#00FF00";
    @Entry(category = "distance", name = "Distance Band 2 Threshold", isSlider = true, min = 0.0f, max = 10.0f, precision = 10)
    public static float distanceBand2Threshold = 2.5f;
    @Entry(category = "distance", name = "Distance Band 2 Color", isColor = true)
    public static String distanceBand2Color = "#FFFF00";
    @Entry(category = "distance", name = "Distance Band 3 Color", isColor = true)
    public static String distanceBand3Color = "#FF0000";
    @Entry(category = "distance", name = "Distance Gradient")
    public static boolean distanceGradientEnabled = false;
    @Entry(category = "distance", name = "Distance Gradient Start Color", isColor = true)
    public static String distanceGradientStartColor = "#00FF00";
    @Entry(category = "distance", name = "Distance Gradient End Color", isColor = true)
    public static String distanceGradientEndColor = "#FF0000";
    @Entry(category = "distance", name = "Distance Background")
    public static boolean distanceBackground = false;
    @Entry(category = "distance", name = "Distance Background Color", isColor = true)
    public static String distanceBackgroundColor = "#000000";
    @Entry(category = "distance", name = "Distance Background Opacity", isSlider = true, min = 0.0f, max = 1.0f)
    public static float distanceBackgroundOpacity = 0.5f;
    @Entry(category = "distance", name = "Distance Shadow Color", isColor = true)
    public static String distanceShadowColor = "#000000";
    @Entry(category = "3hitDistance", name = "Enable Hit Distance Display")
    public static boolean hitDistanceEnable = true;
    @Comment(category = "3hitDistance")
    public static Comment spacer2;
    @Entry(category = "3hitDistance", name = "Hit Distance Scale", isSlider = true, min = 0.1f, max = 5f, precision = 10)
    public static float hitDistanceScale = 1.0f;
    @Entry(category = "3hitDistance", name = "Hit Distance X Offset", min = Integer.MIN_VALUE, max = Integer.MAX_VALUE)
    public static int hitXOffset = 4;
    @Entry(category = "3hitDistance", name = "Hit Distance Y Offset", min = Integer.MIN_VALUE, max = Integer.MAX_VALUE)
    public static int hitYOffset = 4;
    @Entry(category = "3hitDistance", name = "Hit Distance Color", isColor = true)
    public static String hitDistanceColor = "#FFAA00";
    @Entry(category = "3hitDistance", name = "Hit Distance Opacity", isSlider = true, min = 0.0f, max = 1.0f)
    public static float hitDistanceOpacity = 1.0f;
    @Entry(category = "3hitDistance", name = "Hit Distance Text Shadow")
    public static boolean hitDistanceShadow = true;
    @Entry(category = "3hitDistance", name = "Hit Distance Decimal Places")
    public static int hitDistanceDecimalPlaces = 2;
    @Entry(category = "3hitDistance", name = "Hit Bold")
    public static boolean hitDistanceBold = false;
    @Entry(category = "3hitDistance", name = "Hit Italic")
    public static boolean hitDistanceItalic = false;
    @Entry(category = "3hitDistance", name = "Hit Underline")
    public static boolean hitDistanceUnderline = false;
    @Entry(category = "3hitDistance", name = "Hit Display Mode")
    public static DisplayMode hitDistanceDisplayMode = DisplayMode.NUMBER_ONLY;
    @Entry(category = "3hitDistance", name = "Hit Keep Last Distance")
    public static boolean hitDistanceKeep = true;
    @Entry(category = "3hitDistance", name = "Hit Reset Seconds", min = 0, max = 600)
    public static int hitDistanceResetSeconds = 15;
    @Entry(category = "3hitDistance", name = "Hit Distance Calculation Method")
    public static DistanceCalculationMethod hitDistanceCalculationMethod = DistanceCalculationMethod.RAY_HIT_POINT;
    @Entry(category = "3hitDistance", name = "Hit Color Bands")
    public static boolean hitDistanceColorBandsEnabled = true;
    @Entry(category = "3hitDistance", name = "Hit Band 1 Threshold", isSlider = true, min = 0.0f, max = 10.0f, precision = 10)
    public static float hitDistanceBand1Threshold = 1.0f;
    @Entry(category = "3hitDistance", name = "Hit Band 1 Color", isColor = true)
    public static String hitDistanceBand1Color = "#00FF00";
    @Entry(category = "3hitDistance", name = "Hit Band 2 Threshold", isSlider = true, min = 0.0f, max = 10.0f, precision = 10)
    public static float hitDistanceBand2Threshold = 2.5f;
    @Entry(category = "3hitDistance", name = "Hit Band 2 Color", isColor = true)
    public static String hitDistanceBand2Color = "#FFFF00";
    @Entry(category = "3hitDistance", name = "Hit Band 3 Color", isColor = true)
    public static String hitDistanceBand3Color = "#FF0000";
    @Entry(category = "3hitDistance", name = "Hit Gradient")
    public static boolean hitDistanceGradientEnabled = false;
    @Entry(category = "3hitDistance", name = "Hit Gradient Start Color", isColor = true)
    public static String hitDistanceGradientStartColor = "#00FF00";
    @Entry(category = "3hitDistance", name = "Hit Gradient End Color", isColor = true)
    public static String hitDistanceGradientEndColor = "#FF0000";
    @Entry(category = "3hitDistance", name = "Hit Background")
    public static boolean hitDistanceBackground = false;
    @Entry(category = "3hitDistance", name = "Hit Background Color", isColor = true)
    public static String hitDistanceBackgroundColor = "#000000";
    @Entry(category = "3hitDistance", name = "Hit Background Opacity", isSlider = true, min = 0.0f, max = 1.0f)
    public static float hitDistanceBackgroundOpacity = 0.5f;
    @Entry(category = "3hitDistance", name = "Hit Shadow Color", isColor = true)
    public static String hitDistanceShadowColor = "#000000";
    @Entry(category = "averageHitDistance", name = "Enable Average Hit Distance")
    public static boolean averageHitDistanceEnable = false;
    @Comment(category = "averageHitDistance")
    public static Comment spacer3;
    @Entry(category = "averageHitDistance", name = "Average Hit Mode")
    public static AverageModeEnum averageHitMode = AverageModeEnum.LAST_HITS;
    @Entry(category = "averageHitDistance", name = "Number Of Hits Counted", min = 2, max = 32767)
    public static int averageNumberOfHitsCounted = 3;
    @Comment(category = "averageHitDistance")
    public static Comment spacer4;
    @Entry(category = "averageHitDistance", name = "Average Hit Distance Scale", isSlider = true, min = 0.1f, max = 5f, precision = 10)
    public static float averageHitDistanceScale = 1.0f;
    @Entry(category = "averageHitDistance", name = "Average Hit Distance X Offset", min = Integer.MIN_VALUE, max = Integer.MAX_VALUE)
    public static int averageHitXOffset = 4;
    @Entry(category = "averageHitDistance", name = "Average Hit Distance Y Offset", min = Integer.MIN_VALUE, max = Integer.MAX_VALUE)
    public static int averageHitYOffset = 20;
    @Entry(category = "averageHitDistance", name = "Average Hit Distance Color", isColor = true)
    public static String averageHitDistanceColor = "#00FFFF";
    @Entry(category = "averageHitDistance", name = "Average Hit Distance Opacity", isSlider = true, min = 0.0f, max = 1.0f)
    public static float averageHitDistanceOpacity = 1.0f;
    @Entry(category = "averageHitDistance", name = "Average Hit Distance Text Shadow")
    public static boolean averageHitDistanceShadow = true;
    @Entry(category = "averageHitDistance", name = "Average Hit Distance Decimal Places")
    public static int averageHitDistanceDecimalPlaces = 2;
    @Entry(category = "averageHitDistance", name = "Average Hit Bold")
    public static boolean averageHitDistanceBold = false;
    @Entry(category = "averageHitDistance", name = "Average Hit Italic")
    public static boolean averageHitDistanceItalic = false;
    @Entry(category = "averageHitDistance", name = "Average Hit Underline")
    public static boolean averageHitDistanceUnderline = false;
    @Entry(category = "averageHitDistance", name = "Average Hit Display Mode")
    public static DisplayMode averageHitDistanceDisplayMode = DisplayMode.NUMBER_ONLY;
    @Entry(category = "averageHitDistance", name = "Average Hit Color Bands")
    public static boolean averageHitDistanceColorBandsEnabled = false;
    @Entry(category = "averageHitDistance", name = "Average Hit Band 1 Threshold", isSlider = true, min = 0.0f, max = 10.0f, precision = 10)
    public static float averageHitDistanceBand1Threshold = 1.0f;
    @Entry(category = "averageHitDistance", name = "Average Hit Band 1 Color", isColor = true)
    public static String averageHitDistanceBand1Color = "#00FF00";
    @Entry(category = "averageHitDistance", name = "Average Hit Band 2 Threshold", isSlider = true, min = 0.0f, max = 10.0f, precision = 10)
    public static float averageHitDistanceBand2Threshold = 2.5f;
    @Entry(category = "averageHitDistance", name = "Average Hit Band 2 Color", isColor = true)
    public static String averageHitDistanceBand2Color = "#FFFF00";
    @Entry(category = "averageHitDistance", name = "Average Hit Band 3 Color", isColor = true)
    public static String averageHitDistanceBand3Color = "#FF0000";
    @Entry(category = "averageHitDistance", name = "Average Hit Gradient")
    public static boolean averageHitDistanceGradientEnabled = false;
    @Entry(category = "averageHitDistance", name = "Average Hit Gradient Start Color", isColor = true)
    public static String averageHitDistanceGradientStartColor = "#00FF00";
    @Entry(category = "averageHitDistance", name = "Average Hit Gradient End Color", isColor = true)
    public static String averageHitDistanceGradientEndColor = "#FF0000";
    @Entry(category = "averageHitDistance", name = "Average Hit Background")
    public static boolean averageHitDistanceBackground = false;
    @Entry(category = "averageHitDistance", name = "Average Hit Background Color", isColor = true)
    public static String averageHitDistanceBackgroundColor = "#000000";
    @Entry(category = "averageHitDistance", name = "Average Hit Background Opacity", isSlider = true, min = 0.0f, max = 1.0f)
    public static float averageHitDistanceBackgroundOpacity = 0.5f;
    @Entry(category = "averageHitDistance", name = "Average Hit Shadow Color", isColor = true)
    public static String averageHitDistanceShadowColor = "#000000";

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
