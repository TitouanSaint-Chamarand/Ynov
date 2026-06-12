# Résultats des tests — exercice 11

Ce dossier regroupe les rapports générés après l'exécution des tests.

## Générer les rapports

Depuis la racine du projet `exercice11` :

```bash
./generer-resultats-test.sh
```

Ou manuellement :

```bash
mvn clean verify 2>&1 | tee resultat-test/maven-execution.log
```

La commande `verify` lance les tests, génère la couverture JaCoCo et copie automatiquement les rapports ici.

## Rapports par outil

| Rapport | Chemin |
|---------|--------|
| JaCoCo (couverture) | `jacoco/index.html` |
| Cucumber (BDD) | `cucumber/cucumber-report.html` |
| Surefire (JUnit) | `surefire-reports/*.txt` |
| Log Maven | `maven-execution.log` |
| Synthèse texte | `resume-tests.txt` |

## Plugins utilisés

- **Surefire** — exécute les tests JUnit 5 (service, contrôleur, intégration)
- **Cucumber** — exécute les scénarios `.feature` via JUnit Platform
- **JaCoCo** — mesure la couverture de code pendant les tests
- **Antrun** — copie les rapports de `target/` vers `resultat-test/`
