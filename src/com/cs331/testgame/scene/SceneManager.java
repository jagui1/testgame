package com.cs331.testgame.scene;

import org.andengine.engine.Engine;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

import com.cs331.testgame.ResourceManager;

public class SceneManager {
	private BaseScene menuScene;
	private BaseScene gameScene;

	private static final SceneManager INSTANCE = new SceneManager();

	private BaseScene currentScene;
	private Engine engine = ResourceManager.getInstance().engine;

	public enum SceneType {
		SCENE_MENU, SCENE_GAME,
	}
	
	public void setMenuScene(OnCreateSceneCallback cb) {
		ResourceManager.getInstance().loadMenuResources();
		menuScene = new MainMenuScene();
		setScene(menuScene);
		currentScene.createScene();
		cb.onCreateSceneFinished(menuScene);
	}
	
	public void setMenuScene() {
		ResourceManager.getInstance().loadMenuResources();
		menuScene = new MainMenuScene();
		setScene(menuScene);
		currentScene.createScene();
	}
	
	public void setGameScene() {
		ResourceManager.getInstance().loadGameResources();
		gameScene = new GameScene();
		setScene(gameScene);
		currentScene.createScene();
	}

	public void setScene(BaseScene scene) {
		if(currentScene != null)
			currentScene.disposeScene();
		engine.setScene(scene);
		currentScene = scene;
	}

	public void setScene(SceneType type) {

		switch (type) {
		case SCENE_MENU:
			setScene(menuScene);
			break;
		case SCENE_GAME:
			setScene(gameScene);
			break;
		}
	}

	public static SceneManager getInstance() {
		return INSTANCE;
	}
	
	public BaseScene getCurrentScene() {
		return currentScene;
	}
}
