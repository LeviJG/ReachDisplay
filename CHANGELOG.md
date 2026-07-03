# Changelog

## 3.0.0 — Multi-Version Restructure + Entity Filter

### Major Changes

- **Multi-project restructure** — Split into 4 version-specific subprojects:
  - `version-1_17_1_18` (MC 1.18.2)
  - `version-1_19` (MC 1.19.4)
  - `version-1_20_1` (MC 1.20.1)
  - `version-1_21_11` (MC 1.21.11)
- **Entity filter system** — New filter/EntityFilterHelper with whitelist/blacklist mode, category toggles (Players, Hostile, Passive, Boss, Other), and custom entity ID matching
- **Unified build config** — Shared Gradle configuration via root `build.gradle` (processResources, sourcesJar, publishing)

### Entity Filter

- Config entries: `entityFilterEnable`, `entityFilterMode` (WHITELIST/BLACKLIST), per-category toggles, `entityFilterCustomIDs`
- All 3 HUD display sections filtered with `!enable || shouldTrack(entity)`
- Works across all 4 supported versions

### Build System

- Root `build.gradle` with `subprojects` block for common config
- Added Fabric maven repository to all subprojects
- `org.gradle.parallel=true` for faster builds
- `--no-daemon` required (known daemon termination issue)

### Infrastructure

- `agents.md` — Project knowledge reference for AI-assisted development
- `.gitignore` — Added `*.ipr`, `*.iws`, `out/`, `*.log`, `Thumbs.db`, `.DS_Store`; fixed `/run/` → `run/` for subproject run dirs
- Community standards: CODE_OF_CONDUCT, CONTRIBUTING, SECURITY, issue & PR templates
- CI/CD: GitHub Actions (build, CodeQL, Scorecards, Dependabot auto-merge, stale issue management)
- Gradle updated to 9.5.0
- Java target updated to 21

### Fixes

- `run/` directories now properly excluded at subproject level
- IntelliJ workspace/project files (`.iws`, `.ipr`) excluded from version control

### Available Jars

| Version | File |
|---------|------|
| 1.18.2  | `reach_display-3.0.0-1.18.2.jar` |
| 1.19.4  | `reach_display-3.0.0-1.19.4.jar` |
| 1.20.1  | `reach_display-3.0.0-1.20.1.jar` |
| 1.21.11 | `reach_display-3.0.0-1.21.11.jar` |
