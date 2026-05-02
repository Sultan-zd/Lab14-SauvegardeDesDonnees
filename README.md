# Lab 14 - Sauvegarde et Sécurisation des Données

Ce projet est un laboratoire pratique sur les différentes méthodes de stockage de données sous Android, mettant l'accent sur les **bonnes pratiques de sécurité** et la protection des informations sensibles.

**Dépôt GitHub :** [https://github.com/Sultan-zd/Lab14-SauvegardeDesDonn-es.git](https://github.com/Sultan-zd/Lab14-SauvegardeDesDonnees.git)

## Objectifs du Lab
- Maîtriser les différents types de stockage (SharedPreferences, Fichiers, JSON, Cache).
- Implémenter le chiffrement des données sensibles.
- Appliquer les principes de sécurité recommandés par Google (Jetpack Security).
- Gérer le cycle de vie des secrets (expiration et rotation).

## Fonctionnalités de Sécurité (Checklist)
L'application respecte les critères de sécurité suivants :
- [x] **Chiffrement AES-256** : Utilisation de `EncryptedSharedPreferences` pour les tokens et secrets.
- [x] **Zéro Fuite Logcat** : Aucun token ou mot de passe n'apparaît dans les logs. Seuls les statuts ou longueurs sont affichés.
- [x] **MODE_PRIVATE** : Tous les fichiers internes et préférences sont créés avec un accès restreint à l'application uniquement.
- [x] **Masquage UI** : Les champs sensibles utilisent `inputType="textPassword"`.
- [x] **Nettoyage Complet** : Fonctionnalité de purge atomique (Prefs + Secrets + Fichiers + Cache).
- [x] **Gestion de l'Expiration** : Concept de rotation avec invalidation automatique du token après 24 heures.
- [x] **UTF-8 Imposé** : Encodage standardisé pour garantir l'intégrité des fichiers texte.
- [x] **Isolation du Cache** : Utilisation du cache uniquement pour des données temporaires et régénérables.
- [x] **Stockage Externe Sécurisé** : Export limité aux répertoires spécifiques à l'application (`getExternalFilesDir`).

## Structure du Projet
- **`prefs/`** : Gestion des préférences claires (`AppPrefs`) et chiffrées (`SecurePrefs`).
- **`files/`** : Stockage interne de texte brut et d'objets structurés en JSON.
- **`cache/`** : Gestion des fichiers volatils.
- **`external/`** : Interaction avec le stockage externe spécifique à l'app.
- **`model/`** : Modèles de données (ex: `Student`).
- **`ui/`** : Interface utilisateur (`MainActivity`).

## Installation et Utilisation
1. Cloner le dépôt.
2. Ouvrir le projet dans **Android Studio**.
3. Synchroniser avec Gradle (utilise `androidx.security:security-crypto`).
4. Lancer l'application sur un émulateur ou un appareil physique (API 24 minimum).

## Technologies Utilisées
- **Langage** : Java
- **Bibliothèque de Sécurité** : `androidx.security:security-crypto:1.1.0-alpha06`
- **Format de données** : JSON (org.json)
- **Minimum SDK** : 24 (Android 7.0)
