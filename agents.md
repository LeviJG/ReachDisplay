# Agent Knowledge

## Project Structure

Multi-module Gradle/Fabric project with 4 subprojects:

| Subproject        | MC Version | Loom Version   | MidnightLib          | ModMenu |
|-------------------|------------|----------------|----------------------|---------|
| version-1_17_1_18 | 1.18.2     | fabric 0.77.0  | 0.4.4                | 3.2.5   |
| version-1_19      | 1.19.4     | fabric 0.86.0  | 1.3.0-fabric         | 6.3.1   |
| version-1_20_1    | 1.20.1     | fabric 0.90.0  | 1.9.3+1.20.1-fabric  | 7.2.2   |
| version-1_21_11   | 1.21.11    | fabric 0.140.2 | 1.9.3+1.21.11-fabric | 17.0.0  |

version-26_1_2 exists on disk but is COMMENTED OUT from settings.gradle — Fabric doesn't have 26.x mappings yet.

All builds require `--no-daemon` flag. The daemon never terminates.

## Fresh Testing After Config Changes

**Run directories are INSIDE each subproject**, not at root:

- `version-1_17_1_18/run/`
- `version-1_19/run/`
- `version-1_20_1/run/`
- `version-1_21_11/run/`

Deleting `run/` at the project root does nothing. Delete the subproject-specific `run/` directory for the version you're
testing.

After deleting run/ + changing config defaults, always do a clean build:

```
.\gradlew --no-daemon clean :version-X:build
```

## MidnightLib Color Pitfall

`Color.decode()` in MidnightLib interprets a leading `0` in hex strings (e.g., `"00FF00"`) as **octal**, then `F` is not
a valid octal digit → crash when opening the config screen.

**Fix:** Always prefix color defaults with `#` (e.g., `"#00FF000"`). The `#` forces `Color.decode()` to treat the string
as hex.

In parsers (`parseColorWithDefault()` in InGameHudMixin, and ReachDisplayClient for circle color), strip `#` before
passing to `Long.parseLong()`.

## Subproject API Differences

| Feature                | 1.18.2                               | 1.19.4                               | 1.20.1               | 1.21.11                       |
|------------------------|--------------------------------------|--------------------------------------|----------------------|-------------------------------|
| MidnightLib isSlider   | No                                   | Yes                                  | Yes                  | Yes                           |
| MidnightLib categories | No                                   | No                                   | Yes                  | Yes                           |
| Shadow toggle          | No (always on)                       | No (always on)                       | Yes                  | Yes                           |
| Render API             | MatrixStack                          | MatrixStack                          | DrawContext          | DrawContext+RenderTickCounter |
| Scale method           | scale(x,y,z) 3-param                 | scale(x,y,z) 3-param                 | scale(x,y,z) 3-param | scale(x,y) 2-param only       |
| Text background fill   | DrawableHelper.fill(MatrixStack,...) | DrawableHelper.fill(MatrixStack,...) | context.fill(...)    | context.fill(...)             |
| Custom shadow color    | No (uses drawWithShadow black)       | No (uses drawWithShadow black)       | Yes (manual render)  | Yes (manual render)           |

## Entity Filter System

Implemented across all 4 subprojects. Combined into "basic" category (1.20.1/1.21.11) or end-of-config (1.18.2/1.19).

**Config entries:**

- `entityFilterEnable` (true), `entityFilterMode` (WHITELIST/BLACKLIST)
- Category toggles: `entityFilterPlayers=true`, `entityFilterHostile=false`, `entityFilterPassive=false`,
  `entityFilterBoss=false`, `entityFilterOther=false`
- `entityFilterCustomIDs` ("") — comma-separated entity registry IDs

**EntityFilterHelper** (`net.wolren.reach_display.filter`):

- `categorize(Entity)` → Category (PLAYER/HOSTILE/PASSIVE/BOSS/OTHER)
- Class-based: PlayerEntity, Monster, AnimalEntity/PassiveEntity, name-based boss detection
- `shouldTrack(Entity)` — checks enable + category matches + custom IDs + filter mode
- WHITELIST: track if category enabled OR entity ID in custom list
- BLACKLIST: block if category disabled AND entity NOT in custom list

**Integration:**

- All 3 display sections in InGameHudMixin filter with: `!entityFilterEnable || shouldTrack(entity)`
- When entity filter is ON, controls tracking via category toggles + custom IDs
- When entity filter is OFF, shows all entities (no filtering)
- Uses `EntityType.getId(entity.getType()).toString()` for version-compatible entity ID lookup

13-14 source files per subproject (7 Java, 6 resources). Java files:

```
net.wolren.reach_display/
  ReachDisplay.java          — main ModInitializer (empty or trivial)
  ReachDisplayClient.java    — ClientModInitializer (rendering setup)
  config/DisplayConfig.java  — MidnightLib config annotations
  data/SharedData.java       — hit data persistence
  filter/EntityFilterHelper.java — entity categorization + filtering
  mixin/InGameHudMixin.java  — crosshair HUD rendering
  mixin/PlayerAttackMixin.java — attack distance capture
```
