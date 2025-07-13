#!/bin/bash
set -euo pipefail

WIREMOCK_ADMIN_URL="http://localhost:8080/__admin"

echo "Uploading WireMock mappings..."
for mapping in __mappings/*.json; do
  echo "Uploading mapping: $mapping"
  curl -sS -X POST "${WIREMOCK_ADMIN_URL}/mappings" \
    -H "Content-Type: application/json" \
    --data-binary @"$mapping"
done

echo "Uploading WireMock files..."
for file in __files/*; do
  filename=$(basename "$file")
  echo "Uploading file: $filename"
  curl -sS -X PUT "${WIREMOCK_ADMIN_URL}/files/${filename}" \
    -H "Content-Type: application/json" \
    --data-binary @"$file"
done

echo "All mappings and files uploaded successfully."
