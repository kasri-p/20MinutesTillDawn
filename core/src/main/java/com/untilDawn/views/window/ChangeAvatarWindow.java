package com.untilDawn.views.window;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.models.User;
import com.untilDawn.models.utils.GameAssetManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ChangeAvatarWindow extends Window {
    // Constants
    private static final float WINDOW_WIDTH = 800f;
    private static final float WINDOW_HEIGHT = 600f;
    private static final float PADDING = 20f;
    private static final float AVATAR_SIZE = 80f;
    private static final float PREVIEW_SIZE = 150f;

    // Colors
    private static final Color PANEL_COLOR = new Color(0.1f, 0.1f, 0.15f, 0.98f);
    private static final Color SELECTED_COLOR = new Color(0.4f, 0.8f, 1.0f, 1f);
    private static final Color HOVER_COLOR = new Color(0.3f, 0.6f, 0.8f, 0.8f);
    private static final Color SUCCESS_COLOR = new Color(0.3f, 0.8f, 0.3f, 1f);
    private static final Color INFO_COLOR = new Color(0.7f, 0.7f, 0.7f, 1f);
    // Data
    private final Stage parentStage;
    private final User currentUser;
    private final Skin skin;
    // UI Components
    private Table contentTable;
    private Table avatarGrid;
    private Table previewSection;
    private Table uploadSection;
    private Image currentAvatarPreview;
    private Image newAvatarPreview;
    private Label statusLabel;
    private TextButton confirmButton;
    private TextButton cancelButton;
    private TextButton uploadButton;
    private Texture panelTexture;
    private Array<AvatarOption> avatarOptions;
    private AvatarOption selectedAvatar;
    private Runnable onComplete;

    private boolean isDragOver = false;
    private Table dropZone;

    private Stage stage;

    public ChangeAvatarWindow(Skin skin, Stage stage) {
        super("", skin);
        this.skin = skin;
        this.parentStage = stage;
        this.currentUser = App.getLoggedInUser();
        this.panelTexture = GameAssetManager.getGameAssetManager().getPanel();
        this.avatarOptions = new Array<>();
        this.stage = stage;

        setupWindow();
        loadAvatarOptions();
        createContent();
        animateIn();
    }

    private void setupWindow() {
        getTitleTable().clear();
        setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        setColor(PANEL_COLOR);

        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        float centerX = parentStage.getWidth() / 2 - getWidth() / 2;
        float centerY = parentStage.getHeight() / 2 - getHeight() / 2;
        setPosition(centerX, centerY);

        setModal(true);
        setMovable(false);
        setResizable(false);
    }

    private void loadAvatarOptions() {
        for (int i = 1; i <= 6; i++) {
            String filename = "avatar" + i + ".png";
            String path = "images/avatars/" + filename;
            if (Gdx.files.internal(path).exists()) {
                Texture texture = new Texture(Gdx.files.internal(path));
                avatarOptions.add(new AvatarOption(filename, texture, false));
            }
        }

        FileHandle customDir = Gdx.files.local("avatars/");
        if (customDir.exists() && customDir.isDirectory()) {
            for (FileHandle file : customDir.list()) {
                if (isImageFile(file.name())) {
                    try {
                        Texture texture = new Texture(file);
                        avatarOptions.add(new AvatarOption(file.name(), texture, true));
                    } catch (Exception e) {
                        Gdx.app.error("ChangeAvatarWindow", "Failed to load custom avatar: " + file.name());
                    }
                }
            }
        }
    }

    private void createContent() {
        clear();
        defaults().pad(PADDING);

        contentTable = new Table();
        contentTable.defaults().padBottom(20);

        createTitleSection();

        Table mainContent = new Table();
        mainContent.defaults().padRight(20);

        Table leftSide = new Table();
        leftSide.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        leftSide.pad(15);
        createAvatarSelectionSection(leftSide);

        Table rightSide = new Table();
        rightSide.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        rightSide.pad(15);
        createPreviewSection(rightSide);

        mainContent.add(leftSide).width(450).height(400).top();
        mainContent.add(rightSide).width(280).height(400).top();

        contentTable.add(mainContent).row();

        createStatusSection();
        createButtonsSection();

        add(contentTable).expand().fill();
    }

    private void createTitleSection() {
        Table titleTable = new Table();

        Label titleLabel = new Label("Change Avatar", skin);
        titleLabel.setFontScale(2.0f);
        titleLabel.setColor(Color.WHITE);
        titleLabel.setAlignment(Align.center);

        Label subtitleLabel = new Label("Select from existing avatars or upload your own", skin);
        subtitleLabel.setFontScale(1.0f);
        subtitleLabel.setColor(INFO_COLOR);
        subtitleLabel.setAlignment(Align.center);

        titleTable.add(titleLabel).row();
        titleTable.add(subtitleLabel).padTop(5);

        contentTable.add(titleTable).row();
    }

    private void createAvatarSelectionSection(Table container) {
        Label sectionLabel = new Label("Available Avatars", skin);
        sectionLabel.setFontScale(1.3f);
        sectionLabel.setColor(Color.WHITE);
        container.add(sectionLabel).left().padBottom(15).row();

        avatarGrid = new Table();
        avatarGrid.defaults().pad(5);

        int col = 0;
        for (AvatarOption option : avatarOptions) {
            if (!option.isCustom) {
                Table avatarCell = createAvatarCell(option);
                avatarGrid.add(avatarCell).size(AVATAR_SIZE + 10);

                col++;
                if (col >= 4) {
                    avatarGrid.row();
                    col = 0;
                }
            }
        }

        ScrollPane scrollPane = new ScrollPane(avatarGrid, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        container.add(scrollPane).expand().fill().row();

        if (hasCustomAvatars()) {
            container.add(new Label("Custom Avatars", skin)).left().padTop(15).padBottom(10).row();

            Table customGrid = new Table();
            customGrid.defaults().pad(5);

            col = 0;
            for (AvatarOption option : avatarOptions) {
                if (option.isCustom) {
                    Table avatarCell = createAvatarCell(option);
                    customGrid.add(avatarCell).size(AVATAR_SIZE + 10);

                    col++;
                    if (col >= 4) {
                        customGrid.row();
                        col = 0;
                    }
                }
            }

            container.add(customGrid).left();
        }
    }

    private Table createAvatarCell(final AvatarOption option) {
        final Table cell = new Table();
        cell.setBackground(createCellBackground());
        cell.pad(5);

        Image avatarImage = new Image(option.texture);
        cell.add(avatarImage).size(AVATAR_SIZE);

        if (currentUser.getAvatarPath() != null && currentUser.getAvatarPath().equals(option.filename)) {
            cell.setColor(SELECTED_COLOR);
        }

        cell.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectAvatar(option);
                updateAvatarSelection();
                playClick();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (selectedAvatar != option) {
                    cell.setColor(HOVER_COLOR);
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (selectedAvatar != option) {
                    cell.setColor(Color.WHITE);
                }
            }
        });

        return cell;
    }

    private void createPreviewSection(Table container) {
        previewSection = new Table();
        previewSection.defaults().padBottom(15);

        Label previewLabel = new Label("Preview", skin);
        previewLabel.setFontScale(1.3f);
        previewLabel.setColor(Color.WHITE);
        previewSection.add(previewLabel).row();

        Table currentSection = new Table();
        currentSection.setBackground(createCellBackground());
        currentSection.pad(10);

        Label currentLabel = new Label("Current Avatar", skin);
        currentLabel.setFontScale(0.9f);
        currentLabel.setColor(INFO_COLOR);
        currentSection.add(currentLabel).padBottom(5).row();

        currentAvatarPreview = new Image();
        updateCurrentAvatarPreview();
        currentSection.add(currentAvatarPreview).size(PREVIEW_SIZE / 2);

        previewSection.add(currentSection).row();

        Table newSection = new Table();
        newSection.setBackground(createCellBackground());
        newSection.pad(10);

        Label newLabel = new Label("New Avatar", skin);
        newLabel.setFontScale(0.9f);
        newLabel.setColor(SUCCESS_COLOR);
        newSection.add(newLabel).padBottom(5).row();

        newAvatarPreview = new Image();
        newSection.add(newAvatarPreview).size(PREVIEW_SIZE);

        previewSection.add(newSection).row();

        container.add(previewSection).expandX().fillX().row();

        createUploadSection(container);
    }

    private void createUploadSection(Table container) {
        uploadSection = new Table();
        uploadSection.pad(10);

        Label uploadLabel = new Label("Upload Custom Avatar", skin);
        uploadLabel.setFontScale(1.1f);
        uploadLabel.setColor(Color.WHITE);
        uploadSection.add(uploadLabel).padBottom(10).row();

        uploadButton = new TextButton("Choose File", skin);
        styleButton(uploadButton, new Color(0.4f, 0.7f, 1f, 1f));
        uploadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                openFileChooser();
            }
        });

        uploadSection.add(uploadButton).width(180).height(35).padBottom(10).row();

        createDragDropZone();
        uploadSection.add(dropZone).size(200, 100);

        container.add(uploadSection).padTop(20);
    }

    private void createDragDropZone() {
        dropZone = new Table();
        dropZone.setBackground(createDragDropBackground());
        dropZone.pad(20);

        Label dropLabel = new Label("Or drag & drop\nimage here", skin);
        dropLabel.setAlignment(Align.center);
        dropLabel.setFontScale(0.9f);
        dropLabel.setColor(INFO_COLOR);

        dropZone.add(dropLabel);

        dropZone.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                dropZone.setColor(HOVER_COLOR);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                dropZone.setColor(Color.WHITE);
            }
        });
    }

    private void createStatusSection() {
        statusLabel = new Label("", skin);
        statusLabel.setAlignment(Align.center);
        statusLabel.setWrap(true);
        contentTable.add(statusLabel).width(WINDOW_WIDTH - PADDING * 4).row();
    }

    private void createButtonsSection() {
        Table buttonTable = new Table();
        buttonTable.defaults().width(150).height(40).pad(5);

        confirmButton = new TextButton("Confirm", skin);
        cancelButton = new TextButton("Cancel", skin);

        styleButton(confirmButton, SUCCESS_COLOR);
        styleButton(cancelButton, new Color(0.8f, 0.3f, 0.3f, 1f));

        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                if (saveAvatar()) {
                    animateOut(() -> {
                        remove();
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    });
                }
            }
        });

        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                animateOut(() -> remove());
            }
        });

        buttonTable.add(confirmButton).padRight(10);
        buttonTable.add(cancelButton);

        contentTable.add(buttonTable);
    }

    private void selectAvatar(AvatarOption option) {
        selectedAvatar = option;
        newAvatarPreview.setDrawable(new TextureRegionDrawable(new TextureRegion(option.texture)));
        statusLabel.setText("Selected: " + getDisplayName(option.filename));
        statusLabel.setColor(SUCCESS_COLOR);
    }

    private void updateAvatarSelection() {
        for (Actor actor : avatarGrid.getChildren()) {
            if (actor instanceof Table) {
                actor.setColor(Color.WHITE);
            }
        }

        if (selectedAvatar != null) {
            int index = 0;
            for (AvatarOption option : avatarOptions) {
                if (option == selectedAvatar && index < avatarGrid.getChildren().size) {
                    avatarGrid.getChildren().get(index).setColor(SELECTED_COLOR);
                    break;
                }
                if (!option.isCustom) {
                    index++;
                }
            }
        }
    }

    private void updateCurrentAvatarPreview() {
        String avatarPath = currentUser.getAvatarPath();
        if (avatarPath != null && !avatarPath.isEmpty()) {
            String fullPath = "Images/avatars/" + avatarPath;
            if (Gdx.files.internal(fullPath).exists()) {
                currentAvatarPreview.setDrawable(new TextureRegionDrawable(
                    new TextureRegion(new Texture(Gdx.files.internal(fullPath)))));
            }
        } else {
            currentAvatarPreview.setDrawable(new TextureRegionDrawable(
                new TextureRegion(new Texture(Gdx.files.internal("Images/avatars/avatar1.png")))));
        }
    }

    private void openFileChooser() {
        FileChooser.setDefaultPrefsName("select an image");

        FileChooser fileChooser = new FileChooser(FileChooser.Mode.OPEN);
        fileChooser.setSelectionMode(FileChooser.SelectionMode.FILES);
        fileChooser.setFileFilter(file -> {
            String name = file.getName().toLowerCase();
            return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif");
        });

        fileChooser.setListener(new FileChooserAdapter() {
            @Override
            public void selected(Array<FileHandle> files) {
                for (FileHandle file : files) {
                    handleFileSelection(file.file());
                }
            }
        });

        stage.addActor(fileChooser);
    }

    private void handleFileSelection(File file) {
        try {
            if (!isImageFile(file.getName())) {
                showError("Please select a valid image file (PNG, JPG, GIF, BMP)");
                return;
            }

            long fileSize = file.length();
            if (fileSize > 5 * 1024 * 1024) {
                showError("File size must be less than 5MB");
                return;
            }

            FileHandle localDir = Gdx.files.local("Images/avatars/");
            localDir.mkdirs();

            String newFilename = "custom_" + getFileExtension(file.getName());
            FileHandle destFile = localDir.child(newFilename);

            Files.copy(file.toPath(), destFile.file().toPath(), StandardCopyOption.REPLACE_EXISTING);

            Texture newTexture = new Texture(destFile);
            AvatarOption newOption = new AvatarOption(newFilename, newTexture, true);
            avatarOptions.add(newOption);

            selectAvatar(newOption);

            showSuccess("Avatar uploaded successfully!");
        } catch (IOException e) {
            showError("Failed to upload avatar: " + e.getMessage());
        } catch (Exception e) {
            showError("Error loading avatar: " + e.getMessage());
        }
    }

    private boolean saveAvatar() {
        if (selectedAvatar == null) {
            showError("Please select an avatar");
            return false;
        }

        currentUser.setAvatarPath(selectedAvatar.filename);
        App.save();

        showSuccess("Avatar changed successfully!");
        return true;
    }

    private boolean isImageFile(String filename) {
        String lower = filename.toLowerCase();
        return lower.endsWith(".png") || lower.endsWith(".jpg") ||
            lower.endsWith(".jpeg") || lower.endsWith(".gif") ||
            lower.endsWith(".bmp");
    }

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot >= 0 ? filename.substring(lastDot) : "";
    }

    private String getDisplayName(String filename) {
        String name = filename.substring(0, filename.lastIndexOf('.'));
        name = name.replace("_", " ");
        name = name.replace("avatar", "Avatar ");
        return name;
    }

    private boolean hasCustomAvatars() {
        for (AvatarOption option : avatarOptions) {
            if (option.isCustom) return true;
        }
        return false;
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setColor(new Color(0.8f, 0.3f, 0.3f, 1f));
    }

    private void showSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setColor(SUCCESS_COLOR);
    }

    private TextureRegionDrawable createCellBackground() {
        // Create a semi-transparent background
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.2f, 0.2f, 0.3f, 0.5f));
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    private TextureRegionDrawable createDragDropBackground() {
        Pixmap pixmap = new Pixmap(200, 100, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.2f, 0.2f, 0.3f, 0.3f));
        pixmap.fill();

        pixmap.setColor(new Color(0.5f, 0.5f, 0.6f, 0.8f));
        for (int x = 0; x < 200; x += 10) {
            pixmap.fillRectangle(x, 0, 5, 2);
            pixmap.fillRectangle(x, 98, 5, 2);
        }

        for (int y = 0; y < 100; y += 10) {
            pixmap.fillRectangle(0, y, 2, 5);
            pixmap.fillRectangle(198, y, 2, 5);
        }

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    private void styleButton(TextButton button, Color accentColor) {
        button.getLabel().setColor(accentColor);
        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                button.addAction(Actions.scaleTo(1.05f, 1.05f, 0.1f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                button.addAction(Actions.scaleTo(1f, 1f, 0.1f));
            }
        });
    }

    private void animateIn() {
        setScale(0.9f);
        setColor(1f, 1f, 1f, 0f);
        addAction(Actions.parallel(
            Actions.scaleTo(1f, 1f, 0.3f),
            Actions.fadeIn(0.3f)
        ));
    }

    private void animateOut(Runnable onComplete) {
        addAction(Actions.sequence(
            Actions.parallel(
                Actions.scaleTo(0.9f, 0.9f, 0.2f),
                Actions.fadeOut(0.2f)
            ),
            Actions.run(onComplete)
        ));
    }

    private void playClick() {
        if (App.isSFX()) {
            Main.getMain().getClickSound().play();
        }
    }

    public void setOnComplete(Runnable onComplete) {
        this.onComplete = onComplete;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Check for drag and drop events (platform-specific implementation needed)
        // This is where you would handle actual drag-drop functionality
    }

    private static class AvatarOption {
        String filename;
        Texture texture;
        boolean isCustom;

        AvatarOption(String filename, Texture texture, boolean isCustom) {
            this.filename = filename;
            this.texture = texture;
            this.isCustom = isCustom;
        }
    }
}
