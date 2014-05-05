package com.cs331.testgame.scene;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.util.color.Color;

import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.cs331.testgame.ResourceManager;

public class PostGameScene extends BaseScene implements IOnMenuItemClickListener{

	private static final int CAMERA_HEIGHT = 480;
	private static final float CAMERA_WIDTH = 800;
	private HUD gameHUD;
	private PhysicsWorld physicsWorld;
	private Body playerBody;
	private Sprite playerSprite;

	private final int MENU_NEXT = 0;
	private final int MENU_RETRY = 1;
	
	@Override
	public void createScene() {
		setBackground();
		createHUD();
	}

	@Override
	public void onBackPressed() {
		SceneManager.getInstance().setScene(SceneManager.SceneType.SCENE_MENU);
	}

	@Override
	public void disposeScene() {
		ResourceManager.getInstance().unloadGameResources();
	}

	private void setBackground() {
		setBackground(new Background(Color.WHITE));

	}

	private void createHUD() {
		gameHUD = new HUD();
		camera.setHUD(gameHUD);
	}
	
	@Override
	public boolean onMenuItemClicked(MenuScene scene, IMenuItem item,
			float localX, float localY) {
		
		switch(item.getID()) {
		case MENU_NEXT:
			SceneManager.getInstance().setGameScene();
			return true;
		case MENU_RETRY:
			System.exit(0);
			return true;
		}
		return false;
	}

}
