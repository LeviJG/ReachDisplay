package net.blueskiez77.reach_display.config;

public class DisplayConfig {

    public static final DisplayConfig INSTANCE = new DisplayConfig();
    public enum DistanceCalculationMethod { CLOSEST_POINT, RAY_HIT_POINT }
    public enum AverageMode { LOCAL_AVERAGE, LAST_HITS }

    // -------------------- INTERNAL -------------------- //
    public double distanceXOffset = 0.5;
    public double distanceYOffset = 0.55;
    public double hitXOffset = 0.5;                     // Change these values for default
    public double hitYOffset = 0.65;
    public double averageHitXOffset = 0.5;
    public double averageHitYOffset = 0.9;

    // -------------------- GENERAL -------------------- //
    public boolean enabled = true;
    public boolean showPlayersOnly = true;
    public DistanceCalculationMethod distanceCalculationMethod = DistanceCalculationMethod.CLOSEST_POINT;

    // -------------------- DISTANCE -------------------- //
    public boolean distanceEnabled = true;
    public float distanceTextScale = 1.0f;
    public int distanceTextColor = 0xFFFFFF;
    public double distanceTextOpacity = 1.0;
    public boolean distanceTextShadow = false;
    public int distanceDecimalPlaces = 2;

    // -------------------- HIT DISTANCE -------------------- //
    public boolean hitDistanceEnabled = true;
    public float hitDistanceTextScale = 1.0f;
    public int hitDistanceTextColor = 0xFFFFFF;
    public double hitDistanceTextOpacity = 1.0;
    public boolean hitDistanceTextShadow = false;
    public int hitDistanceDecimalPlaces = 2;

    // -------------------- AVERAGE HIT DISTANCE -------------------- //
    public boolean averageHitEnabled = false;
    public AverageMode averageHitMode = AverageMode.LAST_HITS;
    public int numberOfHitsCounted = 5;
    public float averageHitTextScale = 1.0f;
    public int averageHitTextColor = 0xFFFFFF;
    public double averageHitTextOpacity = 1.0;
    public boolean averageHitTextShadow = false;
    public int averageHitDecimalPlaces = 2;
}
