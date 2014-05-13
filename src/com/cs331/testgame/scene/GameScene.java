package com.cs331.testgame.scene;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.shape.Shape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.cs331.testgame.ResourceManager;

public class GameScene extends BaseScene implements IOnSceneTouchListener,
		IOnAreaTouchListener {

	private static final int CAMERA_HEIGHT = 800;
	private static final float CAMERA_WIDTH = 527;
	private HUD gameHUD;
	private PhysicsWorld physicsWorld;
	private Body playerBody;
	private Sprite playerSprite;
	private Sprite chargeBarSprite;
	private Sprite postGameSprite;
	private Sprite nextButton;
	private Rectangle cCharge;
	private static int playerCount;
	private static int itemCount;
	
	private static final FixtureDef FIXTURE_DEF = PhysicsFactory
			.createFixtureDef(1, 0.5f, 0.5f);
	private float startX;
	private float startY;
	private float lastX;
	private float lastY;
	private boolean isDrawing = false;
	private Line line;
	private static int charge;
	private static boolean levelReady;
	
	private static int cLevelI;
	private final int [][] levelsX = 	{   {200, 200, 200, 200, 200}, //level 1
											{50, 75, 250, 20, 200}, //level 2
											{50, 55, 50, 20, 200},	//level 3
											{50, 75, 250, 20, 200}, //level 4
											{50, 75, 250, 20, 200}, //level 5
											{50, 75, 250, 20, 200}, //level 6
											{50, 75, 250, 20, 200}, //level 7
											{50, 75, 250, 20, 200}, //level 8
											{50, 75, 250, 20, 200}, //level 9
											{50, 75, 250, 20, 200}, //level 10
								        };
	
	private final int [][] levelsY =  {		{75, 225, 375, 525, 675}, //level 1
		 									{75, 500, 500, 700, 700}, //level 2
											{75, 500, 500, 700, 700}, //level 3
											{75, 500, 500, 700, 700}, //level 4
											{75, 500, 500, 700, 700}, //level 5
											{75, 500, 500, 700, 700}, //level 6
											{75, 500, 500, 700, 700}, //level 7
											{75, 500, 500, 700, 700}, //level 8
											{75, 500, 500, 700, 700}, //level 9
											{75, 500, 500, 700, 700}, //level 10
									  };


	public static final short CATEGORY_PLAYER = 0x0001;
	public static final short CATEGORY_COLLECTABLE = 0x0002;
	public static final short CATEGORY_SCENERY = 0x0004;
	public static final short CATEGORY_HUD = 0x0008;

	public static final short MASK_PLAYER = CATEGORY_COLLECTABLE
			| CATEGORY_SCENERY;
	public static final short MASK_COLLECTABLE = CATEGORY_PLAYER
			| CATEGORY_SCENERY;
	public static final short MASK_SCENERY = -1;

	@Override
	public void createScene() {
		cLevelI = 0;
		levelReady = true;
		playerCount = 0;
		charge = 0;
		setBackground();
		createHUD();
		createPhysics();
		createWalls();
		preparePlayer();
		addItems(0);
		createTouchListeners();

		physicsWorld.setContactListener(cListener());
	}

	@Override
	public void onBackPressed() {
		SceneManager.getInstance().setMenuScene();
	}

	@Override
	public void disposeScene() {
		ResourceManager.getInstance().unloadGameResources();
	}

	private void setBackground() {
		setBackground(new Background(Color.WHITE));

	}

	private void createTouchListeners() {
		SceneManager.getInstance().getCurrentScene()
		.setOnSceneTouchListener(this);
		SceneManager.getInstance().getCurrentScene()
		.setOnAreaTouchListener(this);
		
	}
	private void createHUD() {
		chargeBarSprite = createSprite(10, 10,
				ResourceManager.getInstance().charge_bar_region, vbom);
		attachChild(chargeBarSprite);
		cCharge = new Rectangle(100, 25, 12, 12, vbom);
		cCharge.setColor(Color.RED);
		attachChild(cCharge);
		
		//POST MENU STUFF
		postGameSprite = new Sprite(0, 0, ResourceManager.getInstance().post_menu_overlay_region, vbom);
		nextButton = new Sprite(150, 700, ResourceManager.getInstance().next_button_region, vbom);
		SceneManager.getInstance().getCurrentScene().registerTouchArea(nextButton);
	}

	private void createPhysics() {
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 0), true);
		registerUpdateHandler(physicsWorld);
	}

	private void preparePlayer() {
		charge = 0;
		playerCount = 0;
		final FixtureDef playerFixtureDef = PhysicsFactory.createFixtureDef(
				0.5f, .99f, 0.75f);
		playerFixtureDef.filter.categoryBits = CATEGORY_COLLECTABLE;
		playerFixtureDef.filter.maskBits = MASK_PLAYER;
	}

	private void addItems(int i) {
		itemCount = 0;

		Collectable newItem = new Collectable(levelsX[i][0], levelsY[i][0]);
		itemCount++;
		attachChild(newItem.getSprite());

		Collectable newItem2 = new Collectable(levelsX[i][1], levelsY[i][1]);
		itemCount++;
		attachChild(newItem2.getSprite());

		Collectable newItem3 = new Collectable(levelsX[i][2], levelsY[i][2]);
		itemCount++;
		attachChild(newItem3.getSprite());

		Collectable newItem4 = new Collectable(levelsX[i][3], levelsY[i][3]);
		itemCount++;
		attachChild(newItem4.getSprite());

		Collectable newItem5 = new Collectable(levelsX[i][4], levelsY[i][4]);
		itemCount++;
		attachChild(newItem5.getSprite());

	}

	private void createWalls() {
		FixtureDef WALL_FIX = PhysicsFactory.createFixtureDef(0.0f, 0.0f, 0.0f);
		WALL_FIX.filter.categoryBits = CATEGORY_SCENERY;
		WALL_FIX.filter.maskBits = MASK_SCENERY;
		Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - 1, CAMERA_WIDTH, 1,
				vbom);
		ground.setColor(new Color(0, 0, 0));
		ground.setUserData("ground");
		Body groundBody = PhysicsFactory.createBoxBody(physicsWorld, ground,
				BodyType.StaticBody, WALL_FIX);
		groundBody.setUserData("wall");
		attachChild(ground);

		Rectangle leftWall = new Rectangle(0, 0, 1, CAMERA_HEIGHT, vbom);
		leftWall.setColor(new Color(0, 0, 0));
		Body leftWallBody = PhysicsFactory.createBoxBody(physicsWorld,
				leftWall, BodyType.StaticBody, WALL_FIX);
		leftWall.setUserData("leftwall");
		leftWallBody.setUserData("wall");
		attachChild(leftWall);

		Rectangle rightWall = new Rectangle(CAMERA_WIDTH - 1, 0, 1,
				CAMERA_HEIGHT, vbom);
		rightWall.setColor(new Color(0, 0, 0));
		Body rightWallBody = PhysicsFactory.createBoxBody(physicsWorld,
				rightWall, BodyType.StaticBody, WALL_FIX);
		rightWall.setUserData("rightwall");
		rightWallBody.setUserData("wall");
		attachChild(rightWall);

		Rectangle ceiling = new Rectangle(0, 0, CAMERA_WIDTH, 1, vbom);
		ceiling.setColor(new Color(0, 0, 0));
		Body ceilingBody = PhysicsFactory.createBoxBody(physicsWorld, ceiling,
				BodyType.StaticBody, WALL_FIX);
		ceiling.setUserData("ceiling");
		ceilingBody.setUserData("wall");
		attachChild(ceiling);

	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			ITouchArea pTouchArea, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
		if(pSceneTouchEvent.isActionUp()) {
			if((pTouchAreaLocalX > nextButton.getScaleX()) && 
					(pTouchAreaLocalX < nextButton.getScaleX() + nextButton.getWidth()) &&
					(pTouchAreaLocalY > nextButton.getScaleY()) &&
					(pTouchAreaLocalY < nextButton.getScaleY() + nextButton.getHeight())) {

				loadNextLevel();
			}
		}

		return false;
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene,
			final TouchEvent pSceneTouchEvent) {

		if (physicsWorld != null && levelReady) {
			charge = 0;

			if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
				isDrawing = true;
				startX = pSceneTouchEvent.getX();
				startY = pSceneTouchEvent.getY();
				lastX = pSceneTouchEvent.getX();
				lastY = pSceneTouchEvent.getY();
				line = new Line(startX, startY, lastX, lastY, vbom);
				attachChild(line);
			}
			if (isDrawing
					&& validMove(pSceneTouchEvent.getX(),
							pSceneTouchEvent.getY())) {
				detachChild(line);
				lastX = pSceneTouchEvent.getX();
				lastY = pSceneTouchEvent.getY();
				line = new Line(startX, startY, lastX, lastY, vbom);
				line.setLineWidth(4);
				line.setColor(0, 0, 0);
				attachChild(line);
			}
			if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP && isDrawing ) {
				Debug.d("playerCount = " + playerCount);
				if (playerCount < 1) {
					isDrawing = false;
					addPlayer(
							lastX
									- ResourceManager.getInstance().player_region.getWidth()
									/ 2,
							lastY
									- ResourceManager.getInstance().player_region
											.getHeight() / 2);
					Vector2 vec = new Vector2(startX - lastX, startY - lastY);
					attachChild(playerSprite);
					playerBody.setLinearVelocity(vec);
					playerBody.setLinearDamping(0.2f);
					playerBody.setAngularDamping(0.2f);
					playerBody.setUserData("player");
				}
				detachChild(line);

			}
			return true;
		}
		return false;
	}

	private boolean validMove(float x, float y) {
		if (x >= (CAMERA_WIDTH - 32) || x <= 32) {
			return false;
		}

		if (y >= (CAMERA_HEIGHT - 32) || y <= 32) {
			return false;
		}

		return true;
	}

	private void addPlayer(final float pX, final float pY) {
		playerSprite = new Sprite(pX, pY,
				ResourceManager.getInstance().player_region, vbom);
		playerBody = PhysicsFactory.createBoxBody(physicsWorld, playerSprite,
				BodyType.DynamicBody, FIXTURE_DEF);
		playerSprite.setUserData("player");
		playerBody.setUserData("player");
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(
				playerSprite, playerBody, true, true));
		playerCount++;
	}

	private void removeBody(final Body cBody) {
		engine.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				Shape cSprite = cBody.getAttachedSprite();
				physicsWorld.destroyBody(cBody);
				itemCount--;
				charge++;
				cCharge.setX(cCharge.getX() + 32);
				detachChild(cSprite);
				if (itemCount == 0) {
					playerBody.setAwake(false);
					displayPostMenu();
				}
					

			}
		});
	}
	
	private void displayLoserPostMenu() {
		
		engine.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {

				levelReady = false;
				attachChild(postGameSprite);
				attachChild(nextButton);
			}
		});

	}
	
	private void loadNextLevel() {
		engine.runOnUpdateThread(new Runnable() {

			@Override
			public void run() {
			    getTouchAreas().clear();
		        detachChildren();
		        physicsWorld.clearPhysicsConnectors();
		        unregisterUpdateHandler(physicsWorld);
			    disposeScene();
			    ResourceManager.getInstance().loadGameResources();
				cCharge.setX(100);
				playerCount = 0;
				charge = 0;
				
				setBackground();
				createHUD();
				createPhysics();
				createWalls();
				preparePlayer();
				addItems(cLevelI);
				createTouchListeners();

				physicsWorld.setContactListener(cListener());
				levelReady = true;
			}
		});
		
	}
	
	private void displayPostMenu() {
		//Check current game status
		//display some buttons and then load the next level or reload the current one
		
		levelReady = false;
		attachChild(postGameSprite);
		attachChild(nextButton);
		cLevelI++;
	}


	private ContactListener cListener() {
		ContactListener contactListener = new ContactListener() {

			@Override
			public void beginContact(Contact contact) {
				Body x1 = contact.getFixtureA().getBody();
				Body x2 = contact.getFixtureB().getBody();

				if (x1.getUserData().equals("item")) {
					x1.setActive(false);
					removeBody(x1);
					Debug.d("itemCount = " + itemCount);
				} else if (x2.getUserData().equals("item")) {
					x2.setActive(false);
					removeBody(x2);
				} else if (x1.getUserData().equals("player")
						&& x2.getUserData().equals("wall")) {
					charge--;
					cCharge.setX(cCharge.getX() - 32);
					Debug.d("Charge = " + charge);
				} else if (x2.getUserData().equals("player")
						&& x1.getUserData().equals("wall")) {
					charge--;
					cCharge.setX(cCharge.getX() - 32);
					Debug.d("Charge = " + charge);
				}
			}

			@Override
			public void endContact(Contact contact) {
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				if (itemCount == 0 || playerBody.getLinearVelocity().len() < 2
						|| charge >= 3 || charge <= -3) {
					playerBody.setAwake(false);
					loadNextLevel();
				}
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
			}
		};
		return contactListener;
	}

	// ///////////////////////////////////////////////////
	// WRAPPER CLASS FOR THE COLLECTABLE ITEMS
	//
	private class Collectable {
		private AnimatedSprite cSprite;
		private Body cBody;

		public Collectable(int x, int y) {
			final FixtureDef cFixtureDef = PhysicsFactory.createFixtureDef(
					0.5f, .99f, 0.75f);
			cFixtureDef.filter.categoryBits = CATEGORY_COLLECTABLE;
			cFixtureDef.filter.maskBits = MASK_COLLECTABLE;
			cSprite = new AnimatedSprite((float) x, (float) y,
					ResourceManager.getInstance().items_region, vbom);
			cBody = PhysicsFactory.createBoxBody(physicsWorld, cSprite,
					BodyType.StaticBody, cFixtureDef);
			cBody.setUserData("item");
			cBody.setAttachedSprite(cSprite);
			cSprite.animate(175);
		}

		Sprite getSprite() {
			return cSprite;
		}
	}

}