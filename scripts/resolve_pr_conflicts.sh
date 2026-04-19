#!/usr/bin/env bash
set -euo pipefail

# Usage:
#   ./scripts/resolve_pr_conflicts.sh ours
#   ./scripts/resolve_pr_conflicts.sh theirs
#
# Run this after merging/rebasing the target branch when Git reports conflicts.
# It resolves known conflict files in bulk and stages them.

MODE="${1:-ours}"
if [[ "$MODE" != "ours" && "$MODE" != "theirs" ]]; then
  echo "Mode must be 'ours' or 'theirs'"
  exit 1
fi

FILES=(
  app/src/main/java/com/example/soyle/data/repository/SpeechRepositoryImpl.kt
  app/src/main/java/com/example/soyle/ui/navigation/NavGraph.kt
  app/src/main/java/com/example/soyle/ui/navigation/Screen.kt
  app/src/main/java/com/example/soyle/ui/screens/exercise/ExerciseScreen.kt
  app/src/main/java/com/example/soyle/ui/screens/home/HomeScreen.kt
  app/src/main/java/com/example/soyle/ui/screens/profile/ProfileScreen.kt
  app/src/main/java/com/example/soyle/ui/theme/Colors.kt
  app/src/main/java/com/example/soyle/ui/theme/Theme.kt
)

for file in "${FILES[@]}"; do
  if [[ "$MODE" == "ours" ]]; then
    git checkout --ours -- "$file" || true
  else
    git checkout --theirs -- "$file" || true
  fi
  git add "$file" || true
done

echo "Conflict files resolved with '$MODE' strategy and staged."
echo "Review with: git diff --staged"
