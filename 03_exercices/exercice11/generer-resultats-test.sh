#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"

RESULT_DIR="resultat-test"
mkdir -p "$RESULT_DIR"

echo "=== Exécution des tests (mvn clean verify) ==="
mvn clean verify 2>&1 | tee "$RESULT_DIR/maven-execution.log"

echo ""
echo "=== Génération de la synthèse texte ==="

{
    echo "Synthèse des tests — exercice 11"
    echo "Généré le : $(date '+%Y-%m-%d %H:%M:%S')"
    echo "========================================"
    echo ""

    if [ -d "$RESULT_DIR/surefire-reports" ]; then
        for report in "$RESULT_DIR/surefire-reports"/*.txt; do
            [ -f "$report" ] || continue
            basename="$(basename "$report" .txt)"
            echo "--- $basename ---"
            grep -E "Tests run:|BUILD SUCCESS|BUILD FAILURE|Failures:|Errors:" "$report" 2>/dev/null || cat "$report"
            echo ""
        done
    fi

    if grep -q "BUILD SUCCESS" "$RESULT_DIR/maven-execution.log" 2>/dev/null; then
        echo "Résultat global : SUCCESS"
    else
        echo "Résultat global : FAILURE"
    fi
} > "$RESULT_DIR/resume-tests.txt"

echo "Rapports disponibles dans : $RESULT_DIR/"
echo "  - jacoco/index.html"
echo "  - cucumber/cucumber-report.html"
echo "  - surefire-reports/"
echo "  - resume-tests.txt"
echo "  - maven-execution.log"
