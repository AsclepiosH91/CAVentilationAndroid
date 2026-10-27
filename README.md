# CA Ventilator

Application Android moderne permettant de **ventiler automatiquement un chiffre d'affaires (CA) départemental sur un catalogue de produits**. Développée en Kotlin avec Jetpack Compose et une architecture MVVM stricte.

---

## Sommaire

- [Aperçu fonctionnel](#aperçu-fonctionnel)
- [Architecture](#architecture)
- [Stack technique](#stack-technique)
- [Support multilingue](#support-multilingue)
- [Structure du projet](#structure-du-projet)
- [Algorithme de ventilation](#algorithme-de-ventilation)
- [Persistance des données](#persistance-des-données)
- [Export Excel](#export-excel)
- [Installation et build](#installation-et-build)
- [Navigation adaptative](#navigation-adaptative)
- [Thème et accessibilité](#thème-et-accessibilité)

---

## Aperçu fonctionnel

CA Ventilator résout un problème d'optimisation budgétaire courant dans les équipes commerciales : **répartir un CA cible semestriel sur des produits à prix fixe** de façon à maximiser l'absorption du budget sans le dépasser.

L'application couvre trois domaines :

| Écran | Rôle |
|---|---|
| **Produits** | Créer, modifier, supprimer des produits du catalogue (nom + prix unitaire) |
| **Départements** | Définir des départements avec un CA cible par semestre (S1 / S2) |
| **Ventilation** | Sélectionner un département et un semestre, lancer le calcul, visualiser et exporter les résultats |

L'application intègre également un **support multilingue (Français / Anglais)** avec un bouton de changement de langue dynamique dans la barre supérieure.

---

## Architecture

Le projet suit l'architecture **MVVM** recommandée par Google, avec une séparation en couches stricte inspirée de la Clean Architecture.

```
┌──────────────────────────────────────────────────────┐
│  Presentation layer  (Compose + ViewModel)           │
│  MainActivity · ProductScreen · DepartmentScreen     │
│  VentilationScreen · *ViewModel (StateFlow)          │
├──────────────────────────────────────────────────────┤
│  Domain layer  (Kotlin pur, sans dépendance Android) │
│  Models · VentilateCAUseCase · ExportExcelUseCase    │
│  Repository interfaces                               │
├──────────────────────────────────────────────────────┤
│  Data layer  (Room + Repository implementations)     │
│  AppDatabase · DAOs · Entities · Mappers             │
├──────────────────────────────────────────────────────┤
│  DI / Infrastructure  (Hilt)                         │
│  DatabaseModule · RepositoryModule                   │
└──────────────────────────────────────────────────────┘
```

Le flux de données est **unidirectionnel** :

- Les `ViewModel` exposent un `StateFlow<UiState>` immuable.
- Les composables Compose collectent l'état via `collectAsStateWithLifecycle()`.
- Les actions utilisateur remontent via des lambdas ou des appels directs au ViewModel.
- Les Use Cases du Domain orchestrent la logique métier indépendamment de l'UI.

---

## Stack technique

| Catégorie | Bibliothèque / outil | Version |
|---|---|---|
| Langage | Kotlin | 2.3.0 |
| UI | Jetpack Compose BOM | 2026.05.01 |
| Design system | Material Design 3 | — |
| Navigation | Navigation Compose | 2.9.8 |
| Navigation adaptative | `material3-adaptive-navigation-suite` | 1.4.0 |
| Multilingue | AppCompat (AppLocales / LocaleManager) | 1.7.0 |
| Architecture | ViewModel + StateFlow | Lifecycle 2.10.0 |
| Injection de dépendances | Hilt | 2.59.2 |
| Base de données locale | Room | 2.8.4 |
| Traitement asynchrone | Kotlin Coroutines | 1.11.0 |
| Export Excel | Apache POI OOXML | 5.5.1 |
| Partage de fichiers | Storage Access Framework (SAF) | — |
| Build | Gradle (KTS) + Version Catalog | AGP 9.4.1 |

---

## Support multilingue

L'application prend en charge le **français** et l'**anglais** de manière native. Toutes les chaînes de caractères de l'interface, des messages de validation et des fichiers Excel générés sont externalisées et traduites dans des fichiers de ressources dédiés.

### Fonctionnalités multilingues

- **Sélecteur de langue instantané (`LanguageSwitchIconButton`)** : Présent dans la barre supérieure (`TopAppBar`) de chaque écran, un composant dédié permet de basculer la langue de l'application à tout moment entre le français (`FR`) et l'anglais (`EN`).
- **Persistance automatique (`AppCompatDelegate`)** : La langue sélectionnée est conservée au redémarrage de l'application grâce au mécanisme officiel Per-App Language Preferences d'Android (API 33+ et compatibilité via `AppCompatDelegate.setApplicationLocales`).
- **Gestion propre des messages dans les ViewModels (`UiText`)** : Les messages d'erreur et de validation émis par les ViewModels utilisent une classe scellée `UiText` qui résout les ressources au moment de l'affichage Compose selon la locale active.
- **Exportation Excel traduite** : Les titres, en-têtes et libellés du fichier `.xlsx` généré par `ExportExcelUseCase` s'adaptent dynamiquement à la langue active de l'utilisateur.

### Fichiers de ressources

```
app/src/main/res/
├─ values/strings.xml        # Ressources de secours (français par défaut)
├─ values-fr/strings.xml     # Traductions françaises
├─ values-en/strings.xml     # Traductions anglaises
└─ xml/locales_config.xml    # Configuration des locales autorisées (fr, en)
```

---

## Structure du projet

```
├─ .gradle\
│  ├─ 9.3.0\
│  │  ├─ checksums\
│  │  │  └─ checksums.lock
│  │  ├─ expanded\
│  │  ├─ fileChanges\
│  │  │  └─ last-build.bin
│  │  ├─ fileHashes\
│  │  │  └─ fileHashes.lock
│  │  ├─ vcsMetadata\
│  │  └─ gc.properties
│  ├─ 9.5.0\
│  │  ├─ checksums\
│  │  │  ├─ checksums.lock
│  │  │  ├─ md5-checksums.bin
│  │  │  └─ sha1-checksums.bin
│  │  ├─ executionHistory\
│  │  │  ├─ executionHistory.bin
│  │  │  └─ executionHistory.lock
│  │  ├─ expanded\
│  │  ├─ fileChanges\
│  │  │  └─ last-build.bin
│  │  ├─ fileHashes\
│  │  │  ├─ fileHashes.bin
│  │  │  ├─ fileHashes.lock
│  │  │  └─ resourceHashesCache.bin
│  │  ├─ kotlin-dsl-plugin-entries\
│  │  │  ├─ jar-entries.bin
│  │  │  └─ kotlin-dsl-plugin-entries.lock
│  │  ├─ vcsMetadata\
│  │  └─ gc.properties
│  ├─ buildOutputCleanup\
│  │  ├─ buildOutputCleanup.lock
│  │  ├─ cache.properties
│  │  └─ outputFiles.bin
│  ├─ configuration-cache\
│  │  ├─ 05ef118b-6735-4edf-b12d-72356a77c657\
│  │  ├─ 113ce125-a937-4cd5-90ac-1217f0ef669b\
│  │  ├─ 1q2c8jzj3mpgvugebyswh1583\
│  │  │  └─ candidates.bin
│  │  ├─ 2aaf7563-9d36-466b-88fb-80fc21d15aca\
│  │  │  └─ classloaderscopes13465642519430081389.tmp
│  │  ├─ 5873b22b-e3b0-48b3-a503-5acb90f5a06a\
│  │  ├─ 5b33dda5-d82d-48aa-83e5-001cec1f21d3\
│  │  │  ├─ _app.work.bin
│  │  │  ├─ .globals.work.bin
│  │  │  ├─ .strings.work.bin
│  │  │  ├─ buildfingerprint.bin
│  │  │  ├─ classloaderscopes.bin
│  │  │  ├─ entry.bin
│  │  │  ├─ projectfingerprint.bin
│  │  │  └─ work.bin
│  │  ├─ 7320a5n024x54tjy9hkv16isw\
│  │  │  └─ candidates.bin
│  │  ├─ 8913af6b-1a8a-40c8-aa8d-c80aaa4332be\
│  │  ├─ 89cd89a0-4db6-4419-bd65-e8190980a861\
│  │  ├─ 901bce99-82b9-4e6e-b5db-491911aa6528\
│  │  ├─ 9179b0a4-2019-4ea8-8329-8a8e7b41cac8\
│  │  ├─ 97074e51-2d1b-4f5d-8328-6887868ef597\
│  │  ├─ a912d762-1f36-41df-9c84-e9566dbb6bf0\
│  │  │  ├─ _app.work.bin
│  │  │  ├─ .globals.work.bin
│  │  │  ├─ .strings.work.bin
│  │  │  ├─ buildfingerprint.bin
│  │  │  ├─ classloaderscopes.bin
│  │  │  ├─ entry.bin
│  │  │  ├─ projectfingerprint.bin
│  │  │  └─ work.bin
│  │  ├─ a9b2433b-a8c5-4703-bddd-2f916fcaa91c\
│  │  ├─ abc6b1c0-c67d-4edf-b749-a83bd737b1f6\
│  │  ├─ abe2bd68-db42-4472-9507-422c4df01d2e\
│  │  ├─ dns6fypzdnlbcdsvlzsavwycq\
│  │  │  └─ candidates.bin
│  │  ├─ e2ec0dbe-e6cc-4d48-b0c7-31ed9bb19d17\
│  │  │  ├─ _app.work.bin
│  │  │  ├─ .globals.work.bin
│  │  │  ├─ .strings.work.bin
│  │  │  ├─ buildfingerprint.bin
│  │  │  ├─ classloaderscopes.bin
│  │  │  ├─ entry.bin
│  │  │  ├─ projectfingerprint.bin
│  │  │  └─ work.bin
│  │  ├─ configuration-cache.lock
│  │  └─ gc.properties
│  ├─ vcs-1\
│  │  └─ gc.properties
│  ├─ config.properties
│  └─ file-system.probe
├─ .idea\
│  ├─ caches\
│  │  └─ deviceStreaming.xml
│  ├─ inspectionProfiles\
│  │  └─ Project_Default.xml
│  ├─ .gitignore
│  ├─ .name
│  ├─ AndroidProjectSystem.xml
│  ├─ appInsightsSettings.xml
│  ├─ compiler.xml
│  ├─ deploymentTargetSelector.xml
│  ├─ deviceManager.xml
│  ├─ gradle.xml
│  ├─ markdown.xml
│  ├─ migrations.xml
│  ├─ misc.xml
│  ├─ planningMode.xml
│  ├─ runConfigurations.xml
│  ├─ vcs.xml
│  └─ workspace.xml
├─ .kotlin\
│  └─ sessions\
├─ app\
│  ├─ build\
│  │  ├─ generated\
│  │  │  ├─ ap_generated_sources\
│  │  │  │  └─ debug\
│  │  │  │     └─ out\
│  │  │  ├─ hilt\
│  │  │  │  ├─ component_sources\
│  │  │  │  │  └─ debug\
│  │  │  │  │     └─ com\
│  │  │  │  └─ component_trees\
│  │  │  │     └─ debug\
│  │  │  │        ├─ com\
│  │  │  │        └─ dagger\
│  │  │  ├─ ksp\
│  │  │  │  └─ debug\
│  │  │  │     ├─ java\
│  │  │  │     │  ├─ com\
│  │  │  │     │  ├─ dagger\
│  │  │  │     │  └─ hilt_aggregated_deps\
│  │  │  │     ├─ kotlin\
│  │  │  │     │  └─ com\
│  │  │  │     └─ resources\
│  │  │  │        └─ META-INF\
│  │  │  ├─ res\
│  │  │  │  ├─ pngs\
│  │  │  │  │  └─ debug\
│  │  │  │  └─ resValues\
│  │  │  │     └─ debug\
│  │  │  └─ updated_navigation_xml\
│  │  │     └─ debug\
│  │  ├─ intermediates\
│  │  │  ├─ aar_metadata_check\
│  │  │  │  └─ debug\
│  │  │  │     └─ checkDebugAarMetadata\
│  │  │  ├─ android_res_source_set_path_map\
│  │  │  │  └─ debug\
│  │  │  │     └─ mapDebugSourceSetPaths\
│  │  │  │        └─ file-map.txt
│  │  │  ├─ annotation_processor_list\
│  │  │  │  └─ debug\
│  │  │  │     └─ javaPreCompileDebug\
│  │  │  │        └─ annotationProcessors.json
│  │  │  ├─ apk\
│  │  │  │  └─ debug\
│  │  │  │     ├─ app-debug.apk
│  │  │  │     └─ output-metadata.json
│  │  │  ├─ apk_ide_redirect_file\
│  │  │  │  └─ debug\
│  │  │  │     └─ createDebugApkListingFileRedirect\
│  │  │  │        └─ redirect.txt
│  │  │  ├─ app_metadata\
│  │  │  │  └─ debug\
│  │  │  │     └─ writeDebugAppMetadata\
│  │  │  │        └─ app-metadata.properties
│  │  │  ├─ assets\
│  │  │  │  └─ debug\
│  │  │  │     └─ mergeDebugAssets\
│  │  │  ├─ built_in_kotlinc\
│  │  │  │  └─ debug\
│  │  │  │     └─ compileDebugKotlin\
│  │  │  │        └─ classes\
│  │  │  ├─ classes\
│  │  │  │  └─ debug\
│  │  │  │     ├─ hiltJavaCompileDebug\
│  │  │  │     │  ├─ com\
│  │  │  │     │  └─ dagger\
│  │  │  │     └─ transformDebugClassesWithAsm\
│  │  │  │        ├─ dirs\
│  │  │  │        └─ jars\
│  │  │  ├─ compatible_screen_manifest\
│  │  │  │  └─ debug\
│  │  │  │     └─ createDebugCompatibleScreenManifests\
│  │  │  │        └─ output-metadata.json
│  │  │  ├─ compile_and_runtime_not_namespaced_r_class_jar\
│  │  │  │  └─ debug\
│  │  │  │     └─ processDebugResources\
│  │  │  │        └─ R.jar
│  │  │  ├─ compile_and_runtime_r_class_jar\
│  │  │  │  └─ debug\
│  │  │  │     └─ processDebugResources\
│  │  │  │        └─ R.jar
│  │  │  ├─ compile_r_class_jar\
│  │  │  │  └─ debug\
│  │  │  │     └─ generateDebugRFile\
│  │  │  │        └─ R.jar
│  │  │  ├─ compile_symbol_list\
│  │  │  │  └─ debug\
│  │  │  │     └─ generateDebugRFile\
│  │  │  │        └─ R.txt
│  │  │  ├─ compiled_navigation_res\
│  │  │  │  └─ debug\
│  │  │  │     └─ compileDebugNavigationResources\
│  │  │  ├─ compressed_assets\
│  │  │  │  └─ debug\
│  │  │  │     └─ compressDebugAssets\
│  │  │  │        └─ out\
│  │  │  ├─ data_binding_layout_info_type_merge\
│  │  │  │  └─ debug\
│  │  │  │     └─ mergeDebugResources\
│  │  │  │        └─ out\
│  │  │  ├─ data_binding_layout_info_type_package\
│  │  │  │  └─ debug\
│  │  │  │     └─ packageDebugResources\
│  │  │  │        └─ out\
│  │  │  ├─ desugar_graph\
│  │  │  │  └─ debug\
│  │  │  │     └─ dexBuilderDebug\
│  │  │  │        └─ out\
│  │  │  ├─ dex\
│  │  │  │  └─ debug\
│  │  │  │     ├─ mergeExtDexDebug\
│  │  │  │     │  ├─ classes.dex
│  │  │  │     │  ├─ classes2.dex
│  │  │  │     │  ├─ classes3.dex
│  │  │  │     │  ├─ classes4.dex
│  │  │  │     │  ├─ classes5.dex
│  │  │  │     │  └─ classes6.dex
│  │  │  │     ├─ mergeLibDexDebug\
│  │  │  │     │  ├─ 0\
│  │  │  │     │  ├─ 1\
│  │  │  │     │  ├─ 10\
│  │  │  │     │  ├─ 11\
│  │  │  │     │  ├─ 12\
│  │  │  │     │  ├─ 13\
│  │  │  │     │  ├─ 14\
│  │  │  │     │  ├─ 15\
│  │  │  │     │  ├─ 2\
│  │  │  │     │  ├─ 3\
│  │  │  │     │  ├─ 4\
│  │  │  │     │  ├─ 5\
│  │  │  │     │  ├─ 6\
│  │  │  │     │  ├─ 7\
│  │  │  │     │  ├─ 8\
│  │  │  │     │  └─ 9\
│  │  │  │     └─ mergeProjectDexDebug\
│  │  │  │        ├─ 0\
│  │  │  │        ├─ 1\
│  │  │  │        ├─ 10\
│  │  │  │        ├─ 11\
│  │  │  │        ├─ 12\
│  │  │  │        ├─ 13\
│  │  │  │        ├─ 14\
│  │  │  │        ├─ 15\
│  │  │  │        ├─ 2\
│  │  │  │        ├─ 3\
│  │  │  │        ├─ 4\
│  │  │  │        ├─ 5\
│  │  │  │        ├─ 6\
│  │  │  │        ├─ 7\
│  │  │  │        ├─ 8\
│  │  │  │        └─ 9\
│  │  │  ├─ dex_archive_input_jar_hashes\
│  │  │  │  └─ debug\
│  │  │  │     └─ dexBuilderDebug\
│  │  │  │        └─ out
│  │  │  ├─ dex_number_of_buckets_file\
│  │  │  │  └─ debug\
│  │  │  │     └─ dexBuilderDebug\
│  │  │  │        └─ out
│  │  │  ├─ duplicate_classes_check\
│  │  │  │  └─ debug\
│  │  │  │     └─ checkDebugDuplicateClasses\
│  │  │  ├─ external_file_lib_dex_archives\
│  │  │  │  └─ debug\
│  │  │  │     └─ desugarDebugFileDependencies\
│  │  │  ├─ external_libs_dex_archive\
│  │  │  │  └─ debug\
│  │  │  │     └─ dexBuilderDebug\
│  │  │  │        └─ out\
│  │  │  ├─ external_libs_dex_archive_with_artifact_transforms\
│  │  │  │  └─ debug\
│  │  │  │     └─ dexBuilderDebug\
│  │  │  │        └─ out\
│  │  │  ├─ global_synthetics_dex\
│  │  │  │  └─ debug\
│  │  │  │     ├─ generateDebugGlobalSynthetics\
│  │  │  │     │  └─ classes.dex
│  │  │  │     └─ mergeDebugGlobalSynthetics\
│  │  │  ├─ global_synthetics_external_lib\
│  │  │  │  └─ debug\
│  │  │  │     └─ dexBuilderDebug\
│  │  │  │        └─ out\
│  │  │  ├─ global_synthetics_external_libs_artifact_transform\
│  │  │  │  └─ debug\
│  │  │  │     └─ dexBuilderDebug\
│  │  │  │        └─ out\
│  │  │  ├─ global_synthetics_file_lib\
│  │  │  │  └─ debug\
│  │  │  │     └─ desugarDebugFileDependencies\
│  │  │  ├─ global_synthetics_mixed_scope\
│  │  │  │  └─ debug\
│  │  │  │     └─ dexBuilderDebug\
│  │  │  │        └─ out\
│  │  │  ├─ global_synthetics_project\
│  │  │  │  └─ debug\
│  │  │  │     └─ dexBuilderDebug\
│  │  │  │        └─ out\
│  │  │  ├─ global_synthetics_subproject\
│  │  │  │  └─ debug\
│  │  │  │     └─ dexBuilderDebug\
│  │  │  │        └─ out\
│  │  │  ├─ hilt\
│  │  │  │  └─ component_classes\
│  │  │  │     └─ debug\
│  │  │  │        ├─ com\
│  │  │  │        └─ dagger\
│  │  │  ├─ incremental\
│  │  │  │  ├─ debug\
│  │  │  │  │  ├─ mergeDebugResources\
│  │  │  │  │  │  ├─ merged.dir\
│  │  │  │  │  │  ├─ stripped.dir\
│  │  │  │  │  │  ├─ compile-file-map.properties
│  │  │  │  │  │  └─ merger.xml
│  │  │  │  │  └─ packageDebugResources\
│  │  │  │  │     ├─ merged.dir\
│  │  │  │  │     ├─ stripped.dir\
│  │  │  │  │     ├─ compile-file-map.properties
│  │  │  │  │     └─ merger.xml
│  │  │  │  ├─ debug-mergeJavaRes\
│  │  │  │  │  ├─ zip-cache\
│  │  │  │  │  │  ├─ _Dys8wlV1a7ffJQWfGLSyw==
│  │  │  │  │  │  ├─ _Uf0VyYI6_vEADNZcWVmSg==
│  │  │  │  │  │  ├─ +I5KbXwn0bMXHqKashky1A==
│  │  │  │  │  │  ├─ +scjmBYRtGYQPBl5qRUDVg==
│  │  │  │  │  │  ├─ 063yIeaX+Svtupcok6KG6w==
│  │  │  │  │  │  ├─ 0pcZeLrVFRyazD7033kQgA==
│  │  │  │  │  │  ├─ 0UEREnztv4fbLXzkytxrag==
│  │  │  │  │  │  ├─ 14R1zj+yHjrRuRB5xYFnJw==
│  │  │  │  │  │  ├─ 1m2lzEpOt4BmYvqi_yZNPA==
│  │  │  │  │  │  ├─ 2eGE04Cq0Ovgn3yeZRgDuw==
│  │  │  │  │  │  ├─ 2o2tAJ0eKmR6J+HXU_AmUA==
│  │  │  │  │  │  ├─ 3ePRhyjF_IkZprqenOVTnw==
│  │  │  │  │  │  ├─ 3udWurEeOussR7FB4pbxIw==
│  │  │  │  │  │  ├─ 3xQieVIySOnT5mNoXnTQLw==
│  │  │  │  │  │  ├─ 4dOS1m0hPhJZ+jU2OI6Aiw==
│  │  │  │  │  │  ├─ 4NT2IYkOfPZLd9CGbE0zNQ==
│  │  │  │  │  │  ├─ 580xafrGUTcmvU5nd7qegA==
│  │  │  │  │  │  ├─ 5dBcsVRARYup3324fkUzjA==
│  │  │  │  │  │  ├─ 5X66z25wNChrHGB9Y_1P1A==
│  │  │  │  │  │  ├─ 5XKvcpzOJ3aTCjHJgjVSkg==
│  │  │  │  │  │  ├─ 6UNof9VuSK+1Hbwdw9MAmQ==
│  │  │  │  │  │  ├─ 6WLij4DreIPLdXqSCnpq1Q==
│  │  │  │  │  │  ├─ 7BoJM4yAh_COi3i6creRkQ==
│  │  │  │  │  │  ├─ 7fTQVgn7uZIv7ivzUzFdbw==
│  │  │  │  │  │  ├─ 80J3Bvpnweu4vup7P7C_yA==
│  │  │  │  │  │  ├─ 83TCjeRKQ9z3yMOjLvHC8g==
│  │  │  │  │  │  ├─ 9aOBmjTxAia7yQjAnAyy3Q==
│  │  │  │  │  │  ├─ 9b9Eil34Myb3d+S4hbeWDA==
│  │  │  │  │  │  ├─ 9Vorb1yeDgczUYXB8sDqbg==
│  │  │  │  │  │  ├─ aeDovq4AVHhatDWWlpcRLg==
│  │  │  │  │  │  ├─ AN+O0ErrPc059XQdj3TWYQ==
│  │  │  │  │  │  ├─ axzZX3LMyZ+6dmKSYuDWYg==
│  │  │  │  │  │  ├─ b3zX0Do4p5HMfKpj3ba0QQ==
│  │  │  │  │  │  ├─ BeIOsZpLvEC_F8Gf8SXz3w==
│  │  │  │  │  │  ├─ BPIgLcW_8Ik7zEOfMI5iLg==
│  │  │  │  │  │  ├─ BpnEF8lWSDkA8A3W+EZovQ==
│  │  │  │  │  │  ├─ BVKFFiLrGjrrHemwBKGKFw==
│  │  │  │  │  │  ├─ c+VSu5tSSpj8MZM8_CkW3w==
│  │  │  │  │  │  ├─ c1NSjbnESjeWNHPZtMpWvA==
│  │  │  │  │  │  ├─ CckGBjyAUKdJCjqtbLfTFg==
│  │  │  │  │  │  ├─ CyzAh4l5aS+L3KgtczRc7Q==
│  │  │  │  │  │  ├─ d3R0znFLuY4xMCcExE2KAQ==
│  │  │  │  │  │  ├─ d80XXKCrBqGH2JrgADUbeg==
│  │  │  │  │  │  ├─ DEy__YHuiGfnGEddQ2VWiA==
│  │  │  │  │  │  ├─ dKDMAw4DUwKnsLTuz8AUuw==
│  │  │  │  │  │  ├─ DxvUU__O8OokVAU+ko9CHA==
│  │  │  │  │  │  ├─ emQTAg2EfNT7c471WIQ9_w==
│  │  │  │  │  │  ├─ EPiYKvH8_9oK_oGAl6mBTg==
│  │  │  │  │  │  ├─ erZN5kLC4icA9ghOSPPw8g==
│  │  │  │  │  │  ├─ ezIZqus9+XsMuEpupGP0wg==
│  │  │  │  │  │  ├─ FjPdJLYT_8chQ4QTRYDFIA==
│  │  │  │  │  │  ├─ ftxAKLJmKWZFWx191IsKOw==
│  │  │  │  │  │  ├─ G2yYCZzfMF7umBPwVeTGnA==
│  │  │  │  │  │  ├─ gFR1GT7DNAVaJC5yctsyDQ==
│  │  │  │  │  │  ├─ GoR7dhgQUfyLk4MrrA3KKw==
│  │  │  │  │  │  ├─ GP4ZDuF+MO1_nFNKoN56uQ==
│  │  │  │  │  │  ├─ GrVEvPbPiQspmE0X6yk5bg==
│  │  │  │  │  │  ├─ HDQw8gfoqOGL0EgNFOojzg==
│  │  │  │  │  │  ├─ hFfB98mkLZHBms6sH3l0KA==
│  │  │  │  │  │  ├─ HNURdqJAT3kq1jSKr9BC8A==
│  │  │  │  │  │  ├─ HpqnQtK_Bo42jeF0v3N6Hg==
│  │  │  │  │  │  ├─ HsXHawCxsdhCXym9PPJ3Zw==
│  │  │  │  │  │  ├─ IBTc8SbX7z9VBb3jIYcdng==
│  │  │  │  │  │  ├─ id3+C5XMWOUwwgW6gfNYBA==
│  │  │  │  │  │  ├─ ILt8dWDDxKBpHCEFvntbWg==
│  │  │  │  │  │  ├─ j8JE2GQCKG3y_bXSOs_JPA==
│  │  │  │  │  │  ├─ jgoQ2Jn8TgsAil8faMvyQw==
│  │  │  │  │  │  ├─ jJzfDcjtm05g0tByuGA_5w==
│  │  │  │  │  │  ├─ JKJhPuRf7akDlBK+62YJrw==
│  │  │  │  │  │  ├─ JQyx92faXsJNipUIE418rA==
│  │  │  │  │  │  ├─ Jy+9EI+JFvIvSvFw9wqgWg==
│  │  │  │  │  │  ├─ kfnAJUNP4s2tR02VHyAVcA==
│  │  │  │  │  │  ├─ KgQVMGZ1QtpsRUoWDHasGA==
│  │  │  │  │  │  ├─ KhL8dkRpw+5WmEvGp7Utew==
│  │  │  │  │  │  ├─ lAZaqB9RPNnVmVRwI3yV9Q==
│  │  │  │  │  │  ├─ LtVoascuWIbF6EpURywJ6A==
│  │  │  │  │  │  ├─ lYGX_kIO0DR_+2cCU32zPQ==
│  │  │  │  │  │  ├─ Lz1+XzqrrWtpg+td8HFLUw==
│  │  │  │  │  │  ├─ lz2OK+XFZg+Bitg7TwJu1A==
│  │  │  │  │  │  ├─ MGFQ9mxFjt36i4g1UCxi3A==
│  │  │  │  │  │  ├─ mM0ZTC89wr7Gk1x9r6nD5g==
│  │  │  │  │  │  ├─ NH5GcRaTYqPfXktfiinf+Q==
│  │  │  │  │  │  ├─ nHw49gc3CzkKAb0Cco42Ew==
│  │  │  │  │  │  ├─ NnTOPeF3+Xnt9mafx1EITA==
│  │  │  │  │  │  ├─ OWUrzDlk5Ipe5QZFaJRcJw==
│  │  │  │  │  │  ├─ p7BJSW8JcVKo1IZPM8Q_fw==
│  │  │  │  │  │  ├─ peQTooFAp1tHelrzP_uEpA==
│  │  │  │  │  │  ├─ phrLjaKA+2iUVWW5d2t2_A==
│  │  │  │  │  │  ├─ pN+NaS4CRV98+qOhN27OYg==
│  │  │  │  │  │  ├─ pq2x1f0BywBi1l5AA3Rx1w==
│  │  │  │  │  │  ├─ QAzCt7Nv35ABQwMnxjINzQ==
│  │  │  │  │  │  ├─ QCdhCsVoN0_COcFJjIJz2A==
│  │  │  │  │  │  ├─ QF0uEpTaCrLK+x9SNKSEgw==
│  │  │  │  │  │  ├─ QmeKcQ0QpxPSVoP1EadtIw==
│  │  │  │  │  │  ├─ QPg2w74DWW771JyuMz6yjg==
│  │  │  │  │  │  ├─ QsBJTylDXiZmkoHj6SnHiQ==
│  │  │  │  │  │  ├─ qVqkufZyIHHBxFnjOt8uPw==
│  │  │  │  │  │  ├─ rDVKmX+NokMsQ_dqG27jtQ==
│  │  │  │  │  │  ├─ rf0bn3n7Svm11aXSUZSx5A==
│  │  │  │  │  │  ├─ Rg1x4OQAUMzuVYSXHifjkQ==
│  │  │  │  │  │  ├─ rVhDWYneJnRbeWCkS0GFGg==
│  │  │  │  │  │  ├─ rznfqI1Sqewbxpi9si9i8A==
│  │  │  │  │  │  ├─ SdHrpSgdtvzApRjFIMe8NA==
│  │  │  │  │  │  ├─ SE7a66QkdvuzBjvKp+MY6Q==
│  │  │  │  │  │  ├─ sm48EGaEDcJVHv9oSotKkA==
│  │  │  │  │  │  ├─ UdiuyYjYCpEt2CvqswSJmA==
│  │  │  │  │  │  ├─ UI2RLiSzy6mpeCszbJenQA==
│  │  │  │  │  │  ├─ UmGJcoS0fsprKEEp2zXecA==
│  │  │  │  │  │  ├─ UurBDfg55uv8FjFrOL3A9A==
│  │  │  │  │  │  ├─ uzfkp9zV67xeWoNxi4NX9g==
│  │  │  │  │  │  ├─ vKDp1PVdLR7hn8JDRD54Hw==
│  │  │  │  │  │  ├─ VUSAGjY28hMo_lh45ePJwg==
│  │  │  │  │  │  ├─ Vywfrbb80e7E1ulymlMJUw==
│  │  │  │  │  │  ├─ WhFNMtXbLfkf+JNWLP9LMQ==
│  │  │  │  │  │  ├─ WZj_5ekyPfQUp0EwwjaoAg==
│  │  │  │  │  │  ├─ xART8tnL4rlI5tjXnSy1TA==
│  │  │  │  │  │  ├─ XfHF6z5VxUQWKfD1IcvIhg==
│  │  │  │  │  │  ├─ xSukiomb5AqJ+HsNauj54A==
│  │  │  │  │  │  ├─ Y9pixlEKNSZzrs9TmyedVQ==
│  │  │  │  │  │  ├─ yvTm2q7im5XiIhcNkTGZ4g==
│  │  │  │  │  │  ├─ YZ8zmGT6SSQsB7KYk4ueiA==
│  │  │  │  │  │  ├─ YztXDcQ9oahVGnGp5yAr9w==
│  │  │  │  │  │  ├─ ZeexazFdbtET980yb5wbyg==
│  │  │  │  │  │  ├─ ZLGSa+TwtPfJNpfVvNRhQg==
│  │  │  │  │  │  ├─ zqYksKDHq5qmJXctne043A==
│  │  │  │  │  │  ├─ zuzwQnPDjla3VmXlXhjpzQ==
│  │  │  │  │  │  ├─ ZY8SyLyQC3kKr+oWEng38A==
│  │  │  │  │  │  └─ zzdesVpFnbMz3XaNxbumZQ==
│  │  │  │  │  └─ merge-state
│  │  │  │  ├─ mergeDebugAssets\
│  │  │  │  │  └─ merger.xml
│  │  │  │  ├─ mergeDebugJniLibFolders\
│  │  │  │  │  └─ merger.xml
│  │  │  │  ├─ mergeDebugShaders\
│  │  │  │  │  └─ merger.xml
│  │  │  │  ├─ packageDebug\
│  │  │  │  │  └─ tmp\
│  │  │  │  │     └─ debug\
│  │  │  │  ├─ processDebugResources\
│  │  │  │  │  └─ resources-list-for-linked-resources-binary-format-debug.ap_.txt
│  │  │  │  └─ transformDebugClassesWithAsm\
│  │  │  ├─ java_res\
│  │  │  │  └─ debug\
│  │  │  │     └─ processDebugJavaRes\
│  │  │  │        └─ out\
│  │  │  ├─ javac\
│  │  │  │  └─ debug\
│  │  │  │     └─ compileDebugJavaWithJavac\
│  │  │  │        └─ classes\
│  │  │  ├─ linked_resources_binary_format\
│  │  │  │  └─ debug\
│  │  │  │     └─ processDebugResources\
│  │  │  │        ├─ linked-resources-binary-format-debug.ap_
│  │  │  │        └─ output-metadata.json
│  │  │  ├─ local_only_symbol_list\
│  │  │  │  └─ debug\
│  │  │  │     └─ parseDebugLocalResources\
│  │  │  │        └─ R-def.txt
│  │  │  ├─ manifest_merge_blame_file\
│  │  │  │  └─ debug\
│  │  │  │     └─ processDebugMainManifest\
│  │  │  │        └─ manifest-merger-blame-debug-report.txt
│  │  │  ├─ merged_java_res\
│  │  │  │  └─ debug\
│  │  │  │     └─ mergeDebugJavaResource\
│  │  │  │        └─ base.jar
│  │  │  ├─ merged_jni_libs\
│  │  │  │  └─ debug\
│  │  │  │     └─ mergeDebugJniLibFolders\
│  │  │  │        └─ out\
│  │  │  ├─ merged_manifest\
│  │  │  │  └─ debug\
│  │  │  │     └─ processDebugMainManifest\
│  │  │  │        └─ AndroidManifest.xml
│  │  │  ├─ merged_manifests\
│  │  │  │  └─ debug\
│  │  │  │     └─ processDebugManifest\
│  │  │  │        ├─ AndroidManifest.xml
│  │  │  │        └─ output-metadata.json
│  │  │  ├─ merged_native_libs\
│  │  │  │  └─ debug\
│  │  │  │     └─ mergeDebugNativeLibs\
│  │  │  │        └─ out\
│  │  │  ├─ merged_res\
│  │  │  │  └─ debug\
│  │  │  │     └─ mergeDebugResources\
│  │  │  │        ├─ drawable_ic_launcher_foreground.xml.flat
│  │  │  │        ├─ mipmap_ic_launcher_round.png.flat
│  │  │  │        ├─ mipmap_ic_launcher.png.flat
│  │  │  │        ├─ values_values.arsc.flat
│  │  │  │        ├─ values-af_values-af.arsc.flat
│  │  │  │        ├─ values-am_values-am.arsc.flat
│  │  │  │        ├─ values-ar_values-ar.arsc.flat
│  │  │  │        ├─ values-as_values-as.arsc.flat
│  │  │  │        ├─ values-az_values-az.arsc.flat
│  │  │  │        ├─ values-b+sr+Latn_values-b+sr+Latn.arsc.flat
│  │  │  │        ├─ values-be_values-be.arsc.flat
│  │  │  │        ├─ values-bg_values-bg.arsc.flat
│  │  │  │        ├─ values-bn_values-bn.arsc.flat
│  │  │  │        ├─ values-bs_values-bs.arsc.flat
│  │  │  │        ├─ values-ca_values-ca.arsc.flat
│  │  │  │        ├─ values-cs_values-cs.arsc.flat
│  │  │  │        ├─ values-da_values-da.arsc.flat
│  │  │  │        ├─ values-de_values-de.arsc.flat
│  │  │  │        ├─ values-el_values-el.arsc.flat
│  │  │  │        ├─ values-en_values-en.arsc.flat
│  │  │  │        ├─ values-en-rAU_values-en-rAU.arsc.flat
│  │  │  │        ├─ values-en-rCA_values-en-rCA.arsc.flat
│  │  │  │        ├─ values-en-rGB_values-en-rGB.arsc.flat
│  │  │  │        ├─ values-en-rIN_values-en-rIN.arsc.flat
│  │  │  │        ├─ values-en-rXC_values-en-rXC.arsc.flat
│  │  │  │        ├─ values-es_values-es.arsc.flat
│  │  │  │        ├─ values-es-rUS_values-es-rUS.arsc.flat
│  │  │  │        ├─ values-et_values-et.arsc.flat
│  │  │  │        ├─ values-eu_values-eu.arsc.flat
│  │  │  │        ├─ values-fa_values-fa.arsc.flat
│  │  │  │        ├─ values-fi_values-fi.arsc.flat
│  │  │  │        ├─ values-fr_values-fr.arsc.flat
│  │  │  │        ├─ values-fr-rCA_values-fr-rCA.arsc.flat
│  │  │  │        ├─ values-fr-rFR_values-fr-rFR.arsc.flat
│  │  │  │        ├─ values-gl_values-gl.arsc.flat
│  │  │  │        ├─ values-gu_values-gu.arsc.flat
│  │  │  │        ├─ values-hi_values-hi.arsc.flat
│  │  │  │        ├─ values-hr_values-hr.arsc.flat
│  │  │  │        ├─ values-hu_values-hu.arsc.flat
│  │  │  │        ├─ values-hy_values-hy.arsc.flat
│  │  │  │        ├─ values-in_values-in.arsc.flat
│  │  │  │        ├─ values-is_values-is.arsc.flat
│  │  │  │        ├─ values-it_values-it.arsc.flat
│  │  │  │        ├─ values-iw_values-iw.arsc.flat
│  │  │  │        ├─ values-ja_values-ja.arsc.flat
│  │  │  │        ├─ values-ka_values-ka.arsc.flat
│  │  │  │        ├─ values-kk_values-kk.arsc.flat
│  │  │  │        ├─ values-km_values-km.arsc.flat
│  │  │  │        ├─ values-kn_values-kn.arsc.flat
│  │  │  │        ├─ values-ko_values-ko.arsc.flat
│  │  │  │        ├─ values-ky_values-ky.arsc.flat
│  │  │  │        ├─ values-lo_values-lo.arsc.flat
│  │  │  │        ├─ values-lt_values-lt.arsc.flat
│  │  │  │        ├─ values-lv_values-lv.arsc.flat
│  │  │  │        ├─ values-mk_values-mk.arsc.flat
│  │  │  │        ├─ values-ml_values-ml.arsc.flat
│  │  │  │        ├─ values-mn_values-mn.arsc.flat
│  │  │  │        ├─ values-mr_values-mr.arsc.flat
│  │  │  │        ├─ values-ms_values-ms.arsc.flat
│  │  │  │        ├─ values-my_values-my.arsc.flat
│  │  │  │        ├─ values-nb_values-nb.arsc.flat
│  │  │  │        ├─ values-ne_values-ne.arsc.flat
│  │  │  │        ├─ values-nl_values-nl.arsc.flat
│  │  │  │        ├─ values-or_values-or.arsc.flat
│  │  │  │        ├─ values-pa_values-pa.arsc.flat
│  │  │  │        ├─ values-pl_values-pl.arsc.flat
│  │  │  │        ├─ values-pt_values-pt.arsc.flat
│  │  │  │        ├─ values-pt-rBR_values-pt-rBR.arsc.flat
│  │  │  │        ├─ values-pt-rPT_values-pt-rPT.arsc.flat
│  │  │  │        ├─ values-ro_values-ro.arsc.flat
│  │  │  │        ├─ values-ru_values-ru.arsc.flat
│  │  │  │        ├─ values-si_values-si.arsc.flat
│  │  │  │        ├─ values-sk_values-sk.arsc.flat
│  │  │  │        ├─ values-sl_values-sl.arsc.flat
│  │  │  │        ├─ values-sq_values-sq.arsc.flat
│  │  │  │        ├─ values-sr_values-sr.arsc.flat
│  │  │  │        ├─ values-sv_values-sv.arsc.flat
│  │  │  │        ├─ values-sw_values-sw.arsc.flat
│  │  │  │        ├─ values-ta_values-ta.arsc.flat
│  │  │  │        ├─ values-te_values-te.arsc.flat
│  │  │  │        ├─ values-th_values-th.arsc.flat
│  │  │  │        ├─ values-tl_values-tl.arsc.flat
│  │  │  │        ├─ values-tr_values-tr.arsc.flat
│  │  │  │        ├─ values-uk_values-uk.arsc.flat
│  │  │  │        ├─ values-ur_values-ur.arsc.flat
│  │  │  │        ├─ values-uz_values-uz.arsc.flat
│  │  │  │        ├─ values-v30_values-v30.arsc.flat
│  │  │  │        ├─ values-vi_values-vi.arsc.flat
│  │  │  │        ├─ values-zh-rCN_values-zh-rCN.arsc.flat
│  │  │  │        ├─ values-zh-rHK_values-zh-rHK.arsc.flat
│  │  │  │        ├─ values-zh-rTW_values-zh-rTW.arsc.flat
│  │  │  │        ├─ values-zu_values-zu.arsc.flat
│  │  │  │        └─ xml_file_paths.xml.flat
│  │  │  ├─ merged_res_blame_folder\
│  │  │  │  └─ debug\
│  │  │  │     └─ mergeDebugResources\
│  │  │  │        └─ out\
│  │  │  ├─ merged_shaders\
│  │  │  │  └─ debug\
│  │  │  │     └─ mergeDebugShaders\
│  │  │  │        └─ out\
│  │  │  ├─ merged_startup_profile\
│  │  │  │  └─ debug\
│  │  │  │     └─ mergeDebugStartupProfile\
│  │  │  ├─ merged_test_only_native_libs\
│  │  │  │  └─ debug\
│  │  │  │     └─ mergeDebugNativeLibs\
│  │  │  │        └─ out\
│  │  │  ├─ mixed_scope_dex_archive\
│  │  │  │  └─ debug\
│  │  │  │     └─ dexBuilderDebug\
│  │  │  │        └─ out\
│  │  │  ├─ navigation_json\
│  │  │  │  └─ debug\
│  │  │  │     └─ extractDeepLinksDebug\
│  │  │  │        └─ navigation.json
│  │  │  ├─ nested_resources_validation_report\
│  │  │  │  └─ debug\
│  │  │  │     └─ generateDebugResources\
│  │  │  │        └─ nestedResourcesValidationReport.txt
│  │  │  ├─ packaged_manifests\
│  │  │  │  └─ debug\
│  │  │  │     └─ processDebugManifestForPackage\
│  │  │  │        ├─ AndroidManifest.xml
│  │  │  │        └─ output-metadata.json
│  │  │  ├─ packaged_res\
│  │  │  │  └─ debug\
│  │  │  │     └─ packageDebugResources\
│  │  │  │        ├─ drawable\
│  │  │  │        ├─ mipmap\
│  │  │  │        ├─ mipmap-anydpi-v26\
│  │  │  │        ├─ values\
│  │  │  │        ├─ values-en\
│  │  │  │        ├─ values-fr-rFR\
│  │  │  │        └─ xml\
│  │  │  ├─ project_dex_archive\
│  │  │  │  └─ debug\
│  │  │  │     └─ dexBuilderDebug\
│  │  │  │        └─ out\
│  │  │  ├─ runtime_symbol_list\
│  │  │  │  └─ debug\
│  │  │  │     └─ processDebugResources\
│  │  │  │        └─ R.txt
│  │  │  ├─ signing_config_versions\
│  │  │  │  └─ debug\
│  │  │  │     └─ writeDebugSigningConfigVersions\
│  │  │  │        └─ signing-config-versions.json
│  │  │  ├─ source_set_path_map\
│  │  │  │  └─ debug\
│  │  │  │     └─ mapDebugSourceSetPaths\
│  │  │  │        └─ file-map.txt
│  │  │  ├─ stable_resource_ids_file\
│  │  │  │  └─ debug\
│  │  │  │     └─ processDebugResources\
│  │  │  │        └─ stableIds.txt
│  │  │  ├─ stripped_native_libs\
│  │  │  │  └─ debug\
│  │  │  │     └─ stripDebugDebugSymbols\
│  │  │  │        └─ out\
│  │  │  ├─ sub_project_dex_archive\
│  │  │  │  └─ debug\
│  │  │  │     └─ dexBuilderDebug\
│  │  │  │        └─ out\
│  │  │  ├─ symbol_list_with_package_name\
│  │  │  │  └─ debug\
│  │  │  │     ├─ generateDebugRFile\
│  │  │  │     │  └─ package-aware-r.txt
│  │  │  │     └─ processDebugResources\
│  │  │  │        └─ package-aware-r.txt
│  │  │  └─ validate_signing_config\
│  │  │     └─ debug\
│  │  │        └─ validateSigningDebug\
│  │  ├─ kotlin\
│  │  │  ├─ compileDebugKotlin\
│  │  │  │  ├─ cacheable\
│  │  │  │  │  ├─ caches-jvm\
│  │  │  │  │  │  ├─ compilerPluginFiles\
│  │  │  │  │  │  ├─ inputs\
│  │  │  │  │  │  ├─ jvm\
│  │  │  │  │  │  └─ lookups\
│  │  │  │  │  └─ last-build.bin
│  │  │  │  ├─ classpath-snapshot\
│  │  │  │  │  └─ shrunk-classpath-snapshot.bin
│  │  │  │  └─ local-state\
│  │  │  └─ kspDebugKotlin\
│  │  │     └─ local-state\
│  │  ├─ kspCaches\
│  │  │  └─ debug\
│  │  │     ├─ backups\
│  │  │     │  ├─ java\
│  │  │     │  │  ├─ com\
│  │  │     │  │  ├─ dagger\
│  │  │     │  │  └─ hilt_aggregated_deps\
│  │  │     │  ├─ kotlin\
│  │  │     │  │  └─ com\
│  │  │     │  └─ resources\
│  │  │     │     └─ META-INF\
│  │  │     ├─ logs\
│  │  │     ├─ symbolLookups\
│  │  │     │  ├─ counters.tab
│  │  │     │  ├─ file-to-id.tab
│  │  │     │  ├─ file-to-id.tab_i
│  │  │     │  ├─ file-to-id.tab_i.len
│  │  │     │  ├─ file-to-id.tab.keystream
│  │  │     │  ├─ file-to-id.tab.keystream.len
│  │  │     │  ├─ file-to-id.tab.len
│  │  │     │  ├─ file-to-id.tab.values.at
│  │  │     │  ├─ id-to-file.tab
│  │  │     │  ├─ id-to-file.tab_i
│  │  │     │  ├─ id-to-file.tab_i.len
│  │  │     │  ├─ id-to-file.tab.keystream
│  │  │     │  ├─ id-to-file.tab.keystream.len
│  │  │     │  ├─ id-to-file.tab.len
│  │  │     │  ├─ id-to-file.tab.values.at
│  │  │     │  ├─ lookups.tab
│  │  │     │  ├─ lookups.tab_i
│  │  │     │  ├─ lookups.tab_i.len
│  │  │     │  ├─ lookups.tab.keystream
│  │  │     │  ├─ lookups.tab.keystream.len
│  │  │     │  ├─ lookups.tab.len
│  │  │     │  └─ lookups.tab.values.at
│  │  │     ├─ ap-classpath-entries.bin
│  │  │     ├─ caches.uptodate
│  │  │     ├─ classpath-entries.bin
│  │  │     ├─ classpath-structure.bin
│  │  │     ├─ sealed
│  │  │     ├─ sourceToOutputs
│  │  │     └─ symbols
│  │  ├─ outputs\
│  │  │  ├─ apk\
│  │  │  │  └─ debug\
│  │  │  │     ├─ app-debug.apk
│  │  │  │     └─ output-metadata.json
│  │  │  └─ logs\
│  │  │     └─ manifest-merger-debug-report.txt
│  │  └─ tmp\
│  │     ├─ compileDebugJavaWithJavac\
│  │     │  ├─ compileTransaction\
│  │     │  │  ├─ backup-dir\
│  │     │  │  └─ stash-dir\
│  │     │  └─ previous-compilation-data.bin
│  │     ├─ hiltJavaCompileDebug\
│  │     │  ├─ compileTransaction\
│  │     │  │  ├─ backup-dir\
│  │     │  │  └─ stash-dir\
│  │     │  └─ previous-compilation-data.bin
│  │     └─ kotlin-classes\
│  │        └─ debug\
│  │           ├─ com\
│  │           │  └─ caventilator\
│  │           └─ META-INF\
│  │              └─ app_debug.kotlin_module
│  ├─ src\
│  │  └─ main\
│  │     ├─ java\
│  │     │  └─ com\
│  │     │     └─ caventilator\
│  │     │        ├─ data\
│  │     │        ├─ di\
│  │     │        ├─ domain\
│  │     │        ├─ presentation\
│  │     │        └─ CAVentilatorApp.kt
│  │     ├─ res\
│  │     │  ├─ drawable\
│  │     │  │  └─ ic_launcher_foreground.xml
│  │     │  ├─ mipmap\
│  │     │  │  ├─ ic_launcher_round.png
│  │     │  │  └─ ic_launcher.png
│  │     │  ├─ values\
│  │     │  │  ├─ colors.xml
│  │     │  │  ├─ strings.xml
│  │     │  │  └─ themes.xml
│  │     │  ├─ values-en\
│  │     │  │  └─ strings.xml
│  │     │  ├─ values-fr-rFR\
│  │     │  │  └─ strings.xml
│  │     │  └─ xml\
│  │     │     └─ file_paths.xml
│  │     └─ AndroidManifest.xml
│  ├─ build.gradle.kts
│  └─ proguard-rules.pro
├─ build\
│  └─ reports\
│     ├─ configuration-cache\
│     │  ├─ 1q2c8jzj3mpgvugebyswh1583\
│     │  │  └─ dakijrklbbb8crrlwcmo5j189\
│     │  │     └─ configuration-cache-report.html
│     │  ├─ 7320a5n024x54tjy9hkv16isw\
│     │  │  ├─ 3po4aeegoxlfq22ts5h5iupue\
│     │  │  │  └─ configuration-cache-report.html
│     │  │  ├─ 6d44kw050f08qm1t2o9wb1f1i\
│     │  │  │  └─ configuration-cache-report.html
│     │  │  ├─ 95e9tcxvjsamf2hfn4p55bc5p\
│     │  │  │  └─ configuration-cache-report.html
│     │  │  ├─ 9rbel7wsr7but5r2gt4mxe81a\
│     │  │  │  └─ configuration-cache-report.html
│     │  │  ├─ ady9g4x18qn9ge8b3klsbevg1\
│     │  │  │  └─ configuration-cache-report.html
│     │  │  ├─ b908zj9aryybzu18n3jdgbfvy\
│     │  │  │  └─ configuration-cache-report.html
│     │  │  ├─ bk7wt3njadqobs7pt1aeom6ua\
│     │  │  │  └─ configuration-cache-report.html
│     │  │  ├─ cwo1whfl7mylf26uyp0i6pmqw\
│     │  │  │  └─ configuration-cache-report.html
│     │  │  └─ u7l6i7bz0ajkpk8kvgi0g08d\
│     │  │     └─ configuration-cache-report.html
│     │  └─ dns6fypzdnlbcdsvlzsavwycq\
│     │     └─ db964oe53azh5vej8xnv6lc0u\
│     │        └─ configuration-cache-report.html
│     └─ problems\
│        └─ problems-report.html
├─ gradle\
│  ├─ wrapper\
│  │  └─ gradle-wrapper.properties
│  ├─ gradle-daemon-jvm.properties
│  └─ libs.versions.toml
├─ .gitattributes
├─ build.gradle.kts
├─ export-tree.ps1
├─ gradle.properties
├─ local.properties
├─ README.md
└─ settings.gradle.kts

```

---

## Algorithme de ventilation

Le cœur de l'application est dans `VentilateCAUseCase`. Il s'agit d'une variante du **problème du sac-à-dos non-borné** (*Unbounded Knapsack*) résolue par une **approche gloutonne améliorée**.

### Principe

L'objectif est de trouver les quantités entières `q₁, q₂, …, qₙ` telles que :

```
maximiser   Σ (qᵢ × pᵢ)
sous        Σ (qᵢ × pᵢ) ≤ CA_cible
avec        qᵢ ∈ ℕ
```

### Déroulement de l'algorithme

1. **Tri** des produits par prix unitaire décroissant.
2. **Itération** sur toutes les quantités possibles `q₀` pour le produit le plus cher (de `floor(CA/p₀)` jusqu'à `0`).
3. Pour chaque valeur de `q₀`, **remplissage glouton** des produits suivants avec le budget restant.
4. **Conservation** de l'allocation dont la somme ventilée est maximale sans dépasser le CA cible.
5. **Early-exit** dès que le résidu courant est inférieur au prix minimum des produits restants — aucun affinement possible.

### Complexité

`O(CA / prix_max × n)` — très performant en pratique pour des prix supérieurs à 1 €. Le calcul est délégué sur `Dispatchers.Default` pour ne pas bloquer le thread UI.

### Résultat retourné

```kotlin
data class VentilationResult(
    val department: Department,
    val semester: Semester,
    val targetCA: Double,
    val items: List<VentilationItem>,   // produits alloués avec quantité et montant
    val totalVentilated: Double,        // CA effectivement ventilé
    val residual: Double                // écart non absorbé
)
```

---

## Persistance des données

La persistance repose sur **Room Database** (`ca_ventilator.db`).

### Entités

| Table | Colonnes |
|---|---|
| `products` | `id` (PK auto), `name`, `unit_price` |
| `departments` | `id` (PK auto), `name`, `target_s1`, `target_s2` |

### DAOs

- `ProductDao` : `observeAll()` (Flow), `getAll()`, `upsert()`, `deleteById()`
- `DepartmentDao` : `observeAll()` (Flow), `getById()`, `upsert()`, `deleteById()`

Les `Flow` Room sont collectés dans les ViewModels via `viewModelScope`, garantissant la **recharge automatique** à chaque modification.

### Séparation des couches

Les entités Room (`*Entity`) sont isolées dans la couche Data. Des fonctions d'extension `toDomain()` / `toEntity()` dans `Mappers.kt` assurent la conversion sans que le Domain ne connaisse Room.

---

## Export Excel

L'export est géré par `ExportExcelUseCase` qui utilise **Apache POI OOXML** pour produire un fichier `.xlsx` structuré.

### Déclenchement (Storage Access Framework)

Le bouton d'export dans `VentilationScreen` lance `ActivityResultContracts.CreateDocument` avec le MIME type `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`. L'utilisateur choisit librement l'emplacement et le nom du fichier. L'URI obtenu est transmis à `ExportExcelUseCase.exportToUri(uri, result)`.

### Structure du fichier généré

| Ligne | Contenu |
|---|---|
| Titre | Nom du département et semestre (fond bleu foncé, texte blanc) |
| Informations | Département, semestre, CA cible |
| En-têtes | Produit · Prix unitaire · Quantité · Montant ventilé |
| Lignes produits | Une ligne par produit alloué, format monnaie `#,##0.00 €` |
| Total | CA total ventilé (fond turquoise, gras) |
| Résidu | Écart non absorbé (fond rouge) |

L'écriture sur le flux de l'URI se fait sur `Dispatchers.IO`.

---

## Installation et build

### Prérequis

- Android Studio Hedgehog (2023.1.1) ou supérieur
- JDK 17
- SDK Android 26+ (cible : 35)
- Connexion Internet (premier build, téléchargement des dépendances Gradle)

### Étapes

```bash
# 1. Cloner ou décompresser le projet
unzip CAVentilator.zip
cd CAVentilator

# 2. Ouvrir dans Android Studio
# File → Open → sélectionner le dossier CAVentilator/

# 3. Laisser Gradle synchroniser les dépendances
# (Android Studio propose la synchronisation automatiquement)

# 4. Build de debug
./gradlew assembleDebug

# 5. Installer sur un émulateur ou appareil connecté
./gradlew installDebug
```

### Note sur Apache POI

POI est une bibliothèque Java standard non prévue pour Android. Les règles `proguard-rules.pro` et les exclusions de packaging dans `build.gradle.kts` sont nécessaires pour éviter les conflits de fichiers META-INF et assurer le bon fonctionnement en mode release :

```kotlin
// app/build.gradle.kts
packaging {
    resources {
        excludes += "META-INF/DEPENDENCIES"
        pickFirsts += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
    }
}
```

---

## Navigation adaptative

`MainActivity` utilise `NavigationSuiteScaffold` de `material3-adaptive-navigation-suite`, qui **adapte automatiquement** le composant de navigation au facteur de forme de l'appareil :

| Appareil | Composant affiché |
|---|---|
| Téléphone portrait | `NavigationBar` (barre du bas) |
| Téléphone paysage / tablette 7" | `NavigationRail` (rail latéral) |
| Tablette 10"+ | `NavigationDrawer` (tiroir permanent) |

Les trois destinations (`Produits`, `Départements`, `Ventilation`) sont déclarées une seule fois dans `AppDestination.all`.

---

## Thème et accessibilité

### Dynamic Color (Material You)

Sur Android 12+ (API 31), l'application utilise `dynamicLightColorScheme()` / `dynamicDarkColorScheme()` pour adapter automatiquement sa palette aux couleurs du fond d'écran de l'utilisateur. Sur les versions antérieures, une palette Material 3 statique bleu/vert/violet est utilisée en fallback.

### Mode sombre

Le thème réagit nativement à `isSystemInDarkTheme()`. Aucune configuration manuelle n'est requise.

### Edge-to-edge

`enableEdgeToEdge()` est appelé dans `MainActivity.onCreate()`. Les `Scaffold` Compose gèrent les `WindowInsets` pour éviter que le contenu ne soit masqué par les barres système.

---

## Licence

Projet généré à des fins de démonstration architecturale. Libre d'utilisation et de modification.
