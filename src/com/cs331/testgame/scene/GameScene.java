package com.cs331.testgame.scene;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.Color;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.cs331.testgame.ResourceManager;

public class GameScene extends BaseScene implements IOnSceneTouchListener,
		IOnAreaTouchListener {

	private static final int CAMERA_HEIGHT = 800;
	private static final float CAMERA_WIDTH = 480;
	private HUD gameHUD;
	private PhysicsWorld physicsWorld;
	private Body playerBody;
	private Sprite playerSprite;
	private MouseJoint mMouseJointActive;
	private int mFaceCount = 0;
	private static final FixtureDef FIXTURE_DEF = PhysicsFactory
			.createFixtureDef(1, 0.5f, 0.5f);
	private float startX;
	private float startY;
	private float lastX;
	private float lastY;
	private boolean isDrawing = false;
	private Line line;

	@Override
	public void createScene() {
		setBackground();
		createHUD();
		// createControls();
		createPhysics();
		createWalls();
		addPlayer();
		SceneManager.getInstance().getCurrentScene()
				.setOnSceneTouchListener(this);
		SceneManager.getInstance().getCurrentScene()
				.setOnAreaTouchListener(this);
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

	private void addPlayer() {
		final FixtureDef playerFixtureDef = PhysicsFactory.createFixtureDef(
				0.5f, .99f, 0.75f);
		playerSprite = createSprite(0, 0,
				ResourceManager.getInstance().player_region, vbom);
		playerBody = PhysicsFactory.createBoxBody(physicsWorld, playerSprite,
				BodyType.DynamicBody, playerFixtureDef);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(
				playerSprite, playerBody, true, false));

		// attachChild(playerSprite);
	}

	private void createWalls() {
		FixtureDef WALL_FIX = PhysicsFactory.createFixtureDef(0.0f, 0.0f, 0.0f);
		Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - 15, CAMERA_WIDTH,
				15, vbom);
		ground.setColor(new Color(15, 50, 0));
		PhysicsFactory.createBoxBody(physicsWorld, ground, BodyType.StaticBody,
				WALL_FIX);
		attachChild(ground);

		Rectangle leftWall = new Rectangle(0, 0, 15, CAMERA_HEIGHT, vbom);
		leftWall.setColor(new Color(15, 50, 0));
		PhysicsFactory.createBoxBody(physicsWorld, leftWall,
				BodyType.StaticBody, WALL_FIX);
		attachChild(leftWall);

		Rectangle rightWall = new Rectangle(CAMERA_WIDTH - 15, 0, 15,
				CAMERA_HEIGHT, vbom);
		rightWall.setColor(new Color(15, 50, 0));
		PhysicsFactory.createBoxBody(physicsWorld, rightWall,
				BodyType.StaticBody, WALL_FIX);
		attachChild(rightWall);

		Rectangle ceiling = new Rectangle(0, 0, CAMERA_WIDTH, 15, vbom);
		ceiling.setColor(new Color(15, 50, 0));
		PhysicsFactory.createBoxBody(physicsWorld, ceiling,
				BodyType.StaticBody, WALL_FIX);
		attachChild(ceiling);

		Rectangle angleWall = new Rectangle(-1, CAMERA_HEIGHT - 50, 100, 15,
				vbom);
		angleWall.setColor(new Color(15, 50, 0));
		angleWall.setRotation(40);
		PhysicsFactory.createBoxBody(physicsWorld, angleWall,
				BodyType.StaticBody, WALL_FIX);
		attachChild(angleWall);
	}


	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			ITouchArea pTouchArea, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {

		if (pSceneTouchEvent.isActionDown()) {
			final IAreaShape face = (IAreaShape) pTouchArea;
			/*
			 * If we have a active MouseJoint, we are just moving it around
			 * instead of creating a second one.
			 */
			if (mMouseJointActive == null) {
				mMouseJointActive = createMouseJoint(face, pTouchAreaLocalX,
						pTouchAreaLocalY);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene,
			final TouchEvent pSceneTouchEvent) {

		if (physicsWorld != null) {

			if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
				isDrawing = true;
				startX = pSceneTouchEvent.getX();
				startY = pSceneTouchEvent.getY();
				lastX = pSceneTouchEvent.getX();
				lastY = pSceneTouchEvent.getY();
				line = new Line(startX, startY, lastX, lastY, vbom);
				attachChild(line);
			}
			if (isDrawing) {
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
					addFace(lastX - ResourceManager.getInstance().player_region.getWidth() / 2,
							lastY - ResourceManager.getInstance().player_region.getHeight() / 2);
					Vector2 vec = new Vector2(startX - lastX, startY - lastY);
					playerBody.setLinearVelocity(vec);
					playerBody.setLinearDamping(0.2f);
					playerBody.setAngularDamping(0.2f);
				}
				detachChild(line);

			}
			return true;
		}
		return false;
	}

	public MouseJoint createMouseJoint(final IAreaShape pFace,
			final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		final Body body = (Body) pFace.getUserData();
		final MouseJointDef mouseJointDef = new MouseJointDef();

		final Vector2 localPoint = Vector2Pool.obtain(
				(pTouchAreaLocalX - pFace.getWidth() * 0.5f)
						/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
				(pTouchAreaLocalY - pFace.getHeight() * 0.5f)
						/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
		playerBody.setTransform(localPoint, 0);

		mouseJointDef.bodyA = playerBody;
		mouseJointDef.bodyB = body;
		mouseJointDef.dampingRatio = 0.95f;
		mouseJointDef.frequencyHz = 60;
		mouseJointDef.maxForce = (200.0f * body.getMass());
		mouseJointDef.collideConnected = true;

		mouseJointDef.target.set(body.getWorldPoint(localPoint));
		Vector2Pool.recycle(localPoint);

		return (MouseJoint) physicsWorld.createJoint(mouseJointDef);
	}

	private void addFace(final float pX, final float pY) {
		this.mFaceCount++;

		playerSprite = new Sprite(pX, pY,
				ResourceManager.getInstance().player_region, vbom);
		playerBody = PhysicsFactory.createBoxBody(physicsWorld, playerSprite,
				BodyType.DynamicBody, FIXTURE_DEF);
		playerSprite.setUserData(playerBody);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(
				playerSprite, playerBody, true, true));
		registerTouchArea(playerSprite);
		attachChild(playerSprite);
	}

}
