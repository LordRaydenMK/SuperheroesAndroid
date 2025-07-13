#!/bin/bash
set -euo pipefail

WIREMOCK_URL="http://localhost:8080/__admin"

# Get the script's directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MAPPINGS_DIR="$SCRIPT_DIR/__mappings"
FILES_DIR="$SCRIPT_DIR/__files"

echo "📤 Uploading mappings from $MAPPINGS_DIR..."
if compgen -G "$MAPPINGS_DIR/*.json" > /dev/null; then
  for mapping in "$MAPPINGS_DIR"/*.json; do
    echo "→ $mapping"
    curl -sS -X POST "$WIREMOCK_URL/mappings" \
      -H "Content-Type: application/json" \
      --data-binary @"$mapping"
  done
else
  echo "⚠️  No mapping files found in $MAPPINGS_DIR"
fi

echo "📤 Uploading files from $FILES_DIR..."
if compgen -G "$FILES_DIR/*" > /dev/null; then
  for filename in "$FILES_DIR"/*; do
    echo "→ $filename"
    curl -sS -X PUT "$WIREMOCK_URL/files/$filename" \
      -H "Content-Type: application/json" \
      --data-binary @"$filename"
  done
else
  echo "⚠️  No files found in $FILES_DIR"
fi

echo "✅ All mappings and files uploaded!"
