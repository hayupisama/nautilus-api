#!/usr/bin/env bash
# Met a jour la copie du contrat OpenAPI depuis un tag du depot nautilus-contrat-api.
# Usage : scripts/sync-contract.sh <tag>   (ex. v0.2.0). Necessite `gh` authentifie localement.
set -euo pipefail

if [ $# -ne 1 ]; then
  echo "usage: $0 <tag>" >&2
  exit 2
fi
tag="$1"
root="$(cd "$(dirname "$0")/.." && pwd)"
target="$root/src/main/openapi/openapi.yaml"
tmp="$(mktemp)"
trap 'rm -f "$tmp"' EXIT

gh api "repos/hayupisama/nautilus-contrat-api/contents/openapi.yaml?ref=${tag}" \
  -H "Accept: application/vnd.github.raw" > "$tmp"
tr -d '\r' < "$tmp" > "$target"
sha="$(sha256sum "$target" | cut -d' ' -f1)"
printf 'tag=%s\nsha256=%s\n' "$tag" "$sha" > "$root/src/main/openapi/CONTRACT_VERSION"
echo "Contrat ${tag} synchronise (sha256=${sha})."
