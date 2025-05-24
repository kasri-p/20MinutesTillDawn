package com.untilDawn.models.enums;

import com.untilDawn.models.App;

import java.util.Objects;

public enum Language {
    // Authentication
    Register("Register", "S'enregistrer"),
    SignUp("Sign Up", "S'inscrire"),
    Login("Login", "Connexion"),
    Username("Username:", "Nom d'utilisateur:"),
    Password("Password:", "Mot de passe:"),
    NewPassword("New Password:", "Nouveau mot de passe :"),
    ConfirmPassword("Confirm Password:", "Confirmer le mot de passe :"),
    ForgotPassword("Forgot Password?", "Mot de passe oublié ?"),
    ResetPassword("Reset Password", "Réinitialiser le mot de passe"),
    GuestLogin("Play as Guest", "Jouer en tant qu'invité"),
    EnterUsernameForSecurityQuestion("Enter your username to see your security question", "Entrez votre nom d'utilisateur pour voir votre question de sécurité"),
    Logout("Logout", "Se déconnecter"),
    PlayNewGame("Play New Game", "Commencer une nouvelle partie"),
    ContinueGame("Continue Game", "Continuer la partie"),

    // Menus
    Start("Start", "Commencer"),
    Quit("Quit", "Quitter"),
    MainMenu("Main Menu", "Menu principal"),
    Settings("Settings", "Paramètres"),
    Profile("Profile", "Profil"),
    Hint("Hint", "Indice"),
    Pause("Pause", "Pause"),
    Resume("Resume", "Reprendre"),
    GiveUp("Give Up", "Abandonner"),
    Continue("Continue", "Continuer"),
    SaveGame("Save Game", "Sauvegarder"),
    LoadGame("Load Game", "Charger"),
    Language("Language: English", "Langue : Français"),

    // Pre-game setup
    SelectCharacter("Select Character", "Choisir un personnage"),
    SelectWeapon("Select Weapon", "Choisir une arme"),
    SelectDuration("Select Game Duration", "Choisir la durée de jeu"),
    StartGame("Start Game", "Démarrer la partie"),

    // Profile settings
    ChangeUsername("Change Username", "Changer le nom d'utilisateur"),
    ChangePassword("Change Password", "Changer le mot de passe"),
    DeleteAccount("Delete Account", "Supprimer le compte"),
    ChangeAvatar("Change Avatar", "Changer d'avatar"),
    DropAvatarHere("Drop Avatar Here", "Déposez l'avatar ici"),

    // In-game UI
    Health("Health", "Santé"),
    TimeLeft("Time Left", "Temps restant"),
    AmmoLeft("Ammo Left", "Munitions restantes"),
    Kills("Kills", "Éliminations"),
    Level("Level", "Niveau"),
    XPBar("XP Progress", "Progression XP"),

    // End game
    GameOver("Game Over", "Jeu terminé"),
    Victory("Victory", "Victoire"),
    Defeat("Defeat", "Défaite"),
    FinalScore("Final Score", "Score final"),
    Retry("Retry", "Réessayer"),

    // Leaderboard
    Leaderboard("Leaderboard", "Classement"),
    SortByScore("Sort by Score", "Trier par score"),
    SortByKills("Sort by Kills", "Trier par éliminations"),
    SortByUsername("Sort by Username", "Trier par nom d'utilisateur"),
    SortBySurvivalTime("Sort by Survival Time", "Trier par temps de survie"),

    // Audio & visuals
    MusicVolume("Music Volume", "Volume de la musique"),
    ChangeMusic("Change Music", "Changer la musique"),
    SFXToggle("Toggle SFX", "Activer/Désactiver SFX"),
    AutoReload("Auto Reload", "Rechargement automatique"),
    BlackAndWhite("Black and White Mode", "Mode noir et blanc"),

    // Tutorial & hints
    ShowKeyHints("Show Key Hints", "Afficher les touches"),
    ShowAbilityInfo("Show Ability Info", "Afficher les capacités"),
    ShowHeroTips("Show Hero Tips", "Astuces pour les héros"),
    ShowCheatCodes("Show Cheat Codes", "Afficher les codes de triche"),

    // Abilities / talents
    ChooseAbility("Choose an Ability", "Choisir une capacité"),
    SelectRandomAbility("Random Ability", "Capacité aléatoire"),
    SelectFromAbilities("Select From 3 Abilities", "Choisir parmi 3 capacités"),
    LevelUp("Level Up", "Niveau supérieur"),

    // Controls
    MoveUp("Move Up", "Monter"),
    MoveDown("Move Down", "Descendre"),
    MoveLeft("Move Left", "Gauche"),
    MoveRight("Move Right", "Droite"),
    Shoot("Shoot", "Tirer"),
    Reload("Reload", "Recharger"),
    ToggleAutoAim("Toggle Auto-Aim", "Activer/Désactiver visée auto"),

    // Character state
    TakeDamage("Take Damage", "Subir des dégâts"),
    Invincible("Invincible", "Invincible"),
    Died("Died", "Mort"),

    // Enemies
    SpawnEnemies("Spawn Enemies", "Générer des ennemis"),
    EnemiesLeft("Enemies Left", "Ennemis restants"),
    BossIncoming("Boss Incoming", "Boss en approche"),

    // Game events
    WaveSurvived("Wave Survived", "Vague survécue"),
    NextWave("Next Wave", "Vague suivante"),
    TimeExpired("Time Expired", "Temps écoulé"),
    GamePaused("Game Paused", "Jeu en pause"),
    GameResumed("Game Resumed", "Jeu repris"),

    // Save system
    Saving("Saving...", "Sauvegarde..."),
    SavedSuccessfully("Saved Successfully", "Sauvegardé avec succès"),
    LoadSuccessful("Game Loaded", "Partie chargée"),

    // Debug & cheats
    CheatMode("Cheat Mode", "Mode triche"),
    AddXP("Add XP", "Ajouter de l'XP"),
    AddHealth("Add Health", "Ajouter de la santé"),
    TriggerBoss("Trigger Boss Fight", "Déclencher le boss"),
    ReduceTime("Reduce Time", "Réduire le temps"),
    UnlockAll("Unlock All", "Tout débloquer"),

    // Common
    Confirm("Confirm", "Confirmer"),
    Cancel("Cancel", "Annuler"),
    OK("OK", "OK"),
    Reset("Reset", "Réinitialiser"),
    Error("Error", "Erreur"),
    Warning("Warning", "Avertissement"),
    Info("Info", "Info"),

    // Errors
    PleaseEnterUsername("Please enter a username", "Veuillez entrer un nom d'utilisateur"),
    UserNotFound("User not found", "Utilisateur non trouvé"),
    SecurityQuestionNotAvailable("Security question not available", "Question de sécurité non disponible"),
    FindUserFirst("Please find a user first", "Veuillez d'abord rechercher un utilisateur"),
    AnswerSecurityQuestion("Please answer the security question", "Veuillez répondre à la question de sécurité"),
    IncorrectSecurityAnswer("Incorrect security answer", "Réponse de sécurité incorrecte"),
    EnterNewPassword("Please enter a new password", "Veuillez entrer un nouveau mot de passe"),
    PasswordTooShort("Password must be at least 6 characters", "Le mot de passe doit contenir au moins 6 caractères"),
    PasswordsDoNotMatch("Passwords do not match", "Les mots de passe ne correspondent pas"),
    PasswordResetSuccess("Password reset successful! You can now log in.", "Réinitialisation réussie ! Vous pouvez maintenant vous connecter."),
    ;

    private final String english;
    private final String french;

    Language(String english, String french) {
        this.english = english;
        this.french = french;
    }

    public String getText() {
        return Objects.equals(App.getLanguage(), "en") ? english : french;
    }
}
