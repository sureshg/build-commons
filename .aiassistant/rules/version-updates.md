---
apply: always
---

# Update Version Catalog

## Task
Run `caupain --no-cache` to update the version catalog.

## Steps
1. Execute `caupain --no-cache` in the project root directory
2. Verify the command completes successfully
3. Update `gradle/libs.versions.toml` with the reported library versions
4. No other changes to the codebase

## Rules
1. Only update version values in the `[versions]` section
2. Preserve exact formatting (quotes, spacing, alignment, line order)
3. Never modify `.gradle.kts` or `.gradle` build files

## Example
```toml
# BEFORE
kotlin = "2.3.20"

# AFTER
kotlin = "2.3.21"
```
