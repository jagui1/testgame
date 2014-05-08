package com.cs331.testgame.scene;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.shape.Shape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.cs331.testgame.ResourceManager;

public class GameScene extends BaseScene implements IOnSceneTouchListener,
		IOnAreaTouchListener {

	private static final int CAMERA_HEIGHT = 800;
	private static final float CAMERA_WIDTH = 527;
	private HUD gameHUD;
	private PhysicsWorld physicsWorld;
	private Body playerBody;
	private Sprite playerSprite;
	private int mFaceCount = 0;
	private static final FixtureDef FIXTURE_DEF = PhysicsFactory
			.createFixtureDef(1, 0.5f, 0.5f);
	private float startX;
	private float startY;
	private float lastX;
	private float lastY;
	private boolean isDrawing = false;
	private Line line;
	
	private static int charge;

	public static final short CATEGORY_PLAYER = 0x0001;
	public static final short CATEGORY_COLLECTABLE = 0x0002;
	public static final short CATEGORY_SCENERY = 0x0004;
	
	public static final short MASK_PLAYER = CATEGORY_COLLECTABLE | CATEGORY_SCENERY;
	public static final short MASK_COLLECTABLE = CATEGORY_PLAYER | CATEGORY_SCENERY;
	public static final short MASK_SCENERY = -1;
	
	
	@Override
	public void createScene() {
		setBackground();
		createHUD();
		// createControls();
		createPhysics();
		createWalls();
		preparePlayer();
		addItems();
		SceneManager.getInstance().getCurrentScene()
				.setOnSceneTouchListener(this);
		SceneManager.getInstance().getCurrentScene()
				.setOnAreaTouchListener(this);
		
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

	private void createHUD() {
		gameHUD = new HUD();
		camera.setHUD(gameHUD);
	}

	private void createPhysics() {
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 0), true);
		registerUpdateHandler(physicsWorld);
	}

	private void preparePlayer() {
		charge = 0;
		final FixtureDef playerFixtureDef = PhysicsFactory.createFixtureDef(
				0.5f, .99f, 0.75f);
		playerFixtureDef.filter.categoryBits = CATEGORY_COLLECTABLE;
		playerFixtureDef.filter.maskBits = MASK_PLAYER;
		playerSprite = createSprite(0, 0,
				ResourceManager.getInstance().player_region, vbom);
		playerBody = PhysicsFactory.createBoxBody(physicsWorld, playerSprite,
				BodyType.DynamicBody, playerFixtureDef);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(
				playerSprite, playerBody, true, false));
		playerBody.setUserData("player");

		// attachChild(playerSprite);
	}
	
	private void addItems() {
		Collectable newItem = new Collectable(50, 75);
		Collectable newItem2 = new Collectable(75, 500);
		Collectable newItem3 = new Collectable(250, 500);
		Collectable newItem4 = new Collectable(20, 700);
		
		
//		final FixtureDef joystickFixtureDef = PhysicsFactory.createFixtureDef(
//				0.5f, .99f, 0.75f);
//		joystickFixtureDef.filter.categoryBits = CATEGORY_COLLECTABLE;
//		joystickFixtureDef.filter.maskBits = MASK_COLLECTABLE;
//		joystickSprite = createSprite(50, 75,
//				ResourceManager.getInstance().control_knob, vbom);
//		joystickBody = PhysicsFactory.createBoxBody(physicsWorld, joystickSprite, BodyType.StaticBody,joystickFixtureDef );
//		joystickBody.setUserData("item");
		attachChild(newItem.getSprite());
		attachChild(newItem2.getSprite());
		attachChild(newItem3.getSprite());
		attachChild(newItem4.getSprite());
		
	}

	private void createWalls() {
		FixtureDef WALL_FIX = PhysicsFactory.createFixtureDef(0.0f, 0.0f, 0.0f);
		WALL_FIX.filter.categoryBits = CATEGORY_SCENERY;
		WALL_FIX.filter.maskBits = MASK_SCENERY;
		Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - 1, CAMERA_WIDTH,
				1, vbom);
		ground.setColor(new Color(0, 0, 0));
		ground.setUserData("ground");
		Body groundBody = PhysicsFactory.createBoxBody(physicsWorld, ground, BodyType.StaticBody,
				WALL_FIX);
		groundBody.setUserData("wall");
		attachChild(ground);

		Rectangle leftWall = new Rectangle(0, 0, 1, CAMERA_HEIGHT, vbom);
		leftWall.setColor(new Color(0, 0, 0));
		Body leftWallBody = PhysicsFactory.createBoxBody(physicsWorld, leftWall,
				BodyType.StaticBody, WALL_FIX);
		leftWall.setUserData("leftwall");
		leftWallBody.setUserData("wall");
		attachChild(leftWall);

		Rectangle rightWall = new Rectangle(CAMERA_WIDTH -1, 0, 1,
				CAMERA_HEIGHT, vbom);
		rightWall.setColor(new Color(0, 0, 0));
		Body rightWallBody = PhysicsFactory.createBoxBody(physicsWorld, rightWall,
				BodyType.StaticBody, WALL_FIX);
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
		
		return false;
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene,
			final TouchEvent pSceneTouchEvent) {

		if (physicsWorld != null ) {

			if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
				isDrawing = true;
				startX = pSceneTouchEvent.getX();
				startY = pSceneTouchEvent.getY();
				lastX = pSceneTouchEvent.getX();
				lastY = pSceneTouchEvent.getY();
				line = new Line(startX, startY, lastX, lastY, vbom);
				attachChild(line);
			}
			if (isDrawing && validMove(pSceneTouchEvent.getX(), pSceneTouchEvent.getY())) {
				detachChild(line);
				lastX = pSceneTouchEvent.getX();
				lastY = pSceneTouchEvent.getY();
				line = new Line(startX, startY, lastX, lastY, vbom);
				line.setLineWidth(4);
				line.setColor(0, 0, 0);
				attachChild(line);
			}
			if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
				if(mFaceCount < 1) {
					isDrawing = false;
					addPlayer(lastX - ResourceManager.getInstance().player_region.getWidth() / 2,
							lastY - ResourceManager.getInstance().player_region.getHeight() / 2);
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
	
	private boolean validMove(float x, float y){
		if(x >= (CAMERA_WIDTH - 32) || x <= 32){
			return false;
		}
		
		if(y >= (CAMERA_HEIGHT - 32) || y <= 32){
			return false;
		}
		
		return true;
	}

//	public MouseJoint createMouseJoint(final IAreaShape pFace,
//			final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
//		final Body body = (Body) pFace.getUserData();
//		final MouseJointDef mouseJointDef = new MouseJointDef();
//
//		final Vector2 localPoint = Vector2Pool.obtain(
//				(pTouchAreaLocalX - pFace.getWidth() * 0.5f)
//						/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
//				(pTouchAreaLocalY - pFace.getHeight() * 0.5f)
//						/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
//		playerBody.setTransform(localPoint, 0);
//
//		mouseJointDef.bodyA = playerBody;
//		mouseJointDef.bodyB = body;
//		mouseJointDef.dampingRatio = 0.95f;
//		mouseJointDef.frequencyHz = 60;
//		mouseJointDef.maxForce = (200.0f * body.getMass());
//		mouseJointDef.collideConnected = true;
//
//		mouseJointDef.target.set(body.getWorldPoint(localPoint));
//		Vector2Pool.recycle(localPoint);
//
//		return (MouseJoint) physicsWorld.createJoint(mouseJointDef);
//	}

	private void addPlayer(final float pX, final float pY) {
		this.mFaceCount++;

		playerSprite = new Sprite(pX, pY,
				ResourceManager.getInstance().player_region, vbom);
		playerBody = PhysicsFactory.createBoxBody(physicsWorld, playerSprite,
				BodyType.DynamicBody, FIXTURE_DEF);
		playerSprite.setUserData("player");
		playerBody.setUserData("player");
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(
				playerSprite, playerBody, true, true));
		//attachChild(playerSprite);
	}
	
	private void removeBody(final Body cBody) {
		engine.runOnUpdateThread(new Runnable()
		{
		    @Override
		    public void run()
		    {
		    	Shape cSprite = cBody.getAttachedSprite();
		    	physicsWorld.destroyBody(cBody);
		    	detachChild(cSprite);
		    	charge += 2;
		    	Debug.d("charge = " + charge);
		    	
		    }  
		});
	}
	
	private ContactListener cListener() {
		ContactListener contactListener = new ContactListener()
	    {
			
	            @Override
	            public void beginContact(Contact contact)
	            {   
	                Body x1 = contact.getFixtureA().getBody();
	                Body x2 = contact.getFixtureB().getBody();
	                
	                if(x1.getUserData().equals("item")) {
	                	x1.setActive(false);
	                	removeBody(x1);
	                } else if(x2.getUserData().equals("item")) {
	                	x2.setActive(false);
	                	removeBody(x2);
	                } else if(x1.getUserData().equals("player") && x2.getUserData().equals("wall")) {
	                	charge--;
	                	Debug.d("Charge = " + charge);
	                } else if(x2.getUserData().equals("player") && x1.getUserData().equals("wall")) {
	                	charge--;
	                	Debug.d("Charge = " + charge);
	                }
	            }
	
	            @Override
	            public void endContact(Contact contact)
	            {  

	            }
	
	            @Override
	            public void preSolve(Contact contact, Manifold oldManifold)
	            {
	            	
	            }
	
	            @Override
	            public void postSolve(Contact contact, ContactImpulse impulse)
	            {  

	            }
	    };
	    return contactListener;
	}
	
	private class Collectable {
		private AnimatedSprite cSprite;
		private Body cBody;
		
		public Collectable(int x,int y) {
			final FixtureDef joystickFixtureDef = PhysicsFactory.createFixtureDef(
					0.5f, .99f, 0.75f);
			joystickFixtureDef.filter.categoryBits = CATEGORY_COLLECTABLE;
			joystickFixtureDef.filter.maskBits = MASK_COLLECTABLE;
			cSprite = new AnimatedSprite((float)x, (float)y,
					ResourceManager.getInstance().items_region, vbom);
			cBody = PhysicsFactory.createBoxBody(physicsWorld, cSprite, BodyType.StaticBody,joystickFixtureDef );
			cBody.setUserData("item");
			cBody.setAttachedSprite(cSprite);	
			cSprite.animate(175);
		}
		
		Sprite getSprite() {
			return cSprite;
		}
		
		Body getBody() {
			return cBody;
		}
	
	}

}
