package com.cs331.testgame.scene;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.Color;

import com.cs331.testgame.ResourceManager;
import com.cs331.testgame.scene.SceneManager.SceneType;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener {

	private MenuScene menu;
	private final int MENU_PLAY = 0;
	private final int MENU_INFO = 1;
	private final int MENU_EXIT = 2;
	private Sprite logoSprite;

	@Override
	public void createScene() {
		setBackground(new Background(Color.WHITE));
		logoSprite = createSprite(32, 32, ResourceManager.getInstance().logo_region, vbom);
		attachChild(logoSprite);
		createMenu();

	}

	@Override
	public void onBackPressed() {
		System.exit(0);
	}

	@Override
	public void disposeScene() {
		ResourceManager.getInstance().unloadMenuResources();
	}

	private void createMenu() {
		menu = new MenuScene(camera);
		menu.setPosition(0, 0);
		final IMenuItem playItem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(MENU_PLAY,
						ResourceManager.getInstance().play_button_region, vbom),
				1.2f, 1);
		final IMenuItem infoItem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(MENU_INFO,
						ResourceManager.getInstance().info_button_region, vbom),
						1.2f, 1);
		final IMenuItem exitItem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(MENU_EXIT,
						ResourceManager.getInstance().exit_button_region, vbom),
				1.2f, 1);		
		
		menu.addMenuItem(playItem);
		menu.addMenuItem(infoItem);
		menu.addMenuItem(exitItem);
		
		menu.buildAnimations();
		menu.setBackgroundEnabled(false);
		playItem.setPosition(playItem.getX(), playItem.getY() + 60);
		infoItem.setPosition(infoItem.getX(), infoItem.getY() + 80);
		exitItem.setPosition(exitItem.getX(), exitItem.getY() + 100);
		
		menu.setOnMenuItemClickListener(this);
		setChildScene(menu);
	}

	@Override
	public boolean onMenuItemClicked(MenuScene scene, IMenuItem item,
			float localX, float localY) {
		
		switch(item.getID()) {
		case MENU_PLAY:
			SceneManager.getInstance().setGameScene();
			return true;
		case MENU_INFO:
			SceneManager.getInstance().setInfoScene();
			return true;
		case MENU_EXIT:
			System.exit(0);
			return true;
		}
		return false;
	}
}
