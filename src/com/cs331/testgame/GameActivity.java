package com.cs331.testgame;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.controller.MultiTouch;
import org.andengine.ui.activity.BaseGameActivity;

import com.cs331.testgame.scene.SceneManager;

import android.view.KeyEvent;
import android.widget.Toast;

public class GameActivity extends BaseGameActivity {

	private Camera camera;

	@Override
	public EngineOptions onCreateEngineOptions() {
		camera = new Camera(0, 0, 480 , 800);
		EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(
						480, 800), this.camera);
		engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true); //ALLOWS FOR SOUNDS AND SUCH
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON); //KEEPS SCREEN ON
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);
		
		if (MultiTouch.isSupported(this)) {
			Toast.makeText(this, "Sorry your device does not support multitouch!", Toast.LENGTH_LONG);
		}
		return engineOptions;
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback cb)
			throws Exception {
		
		ResourceManager.prepareManager(getEngine(), this, camera, getVertexBufferObjectManager());
		cb.onCreateResourcesFinished();
		
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback cb)
			throws Exception {
		SceneManager.getInstance().setMenuScene(cb);

	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback cb) throws Exception {
		cb.onPopulateSceneFinished();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.exit(0);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			SceneManager.getInstance().getCurrentScene().onBackPressed();
		}
		return false;
	}
}
