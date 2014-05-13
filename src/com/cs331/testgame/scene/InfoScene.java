package com.cs331.testgame.scene;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.Color;

import com.cs331.testgame.ResourceManager;

public class InfoScene extends BaseScene{
	private Sprite infoButton;

	@Override
	public void createScene() {
		setBackground(new Background(Color.BLUE));
		infoButton = new Sprite(0, 0, ResourceManager.getInstance().info_page_region, vbom);
		attachChild(infoButton);
	}

	@Override
	public void onBackPressed() {
		SceneManager.getInstance().setMenuScene();
	}

	@Override
	public void disposeScene() {
		ResourceManager.getInstance().unloadInfoResources();
	}

	

}
