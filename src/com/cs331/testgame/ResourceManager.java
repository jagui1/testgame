package com.cs331.testgame;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

public class ResourceManager {
	private static final ResourceManager INSTANCE = new ResourceManager();

	public Engine engine;
	public GameActivity activity;
	public VertexBufferObjectManager vbom;
	public Camera camera;

	private BuildableBitmapTextureAtlas menuTextureAtlas;
	public ITextureRegion play_button_region;
	public ITextureRegion exit_button_region;
	public ITextureRegion info_button_region;	
	
	private BuildableBitmapTextureAtlas infoTextureAtlas;
	public ITextureRegion info_page_region;
	
	private BuildableBitmapTextureAtlas gameTextureAtlas;
	public ITextureRegion player_region;
	public ITiledTextureRegion items_region;
	public ITextureRegion charge_bar_region;
	public ITextureRegion post_menu_overlay_region;
	
	private BuildableBitmapTextureAtlas postGameTextureAtlas;
	public ITextureRegion next_button_region;
	public ITextureRegion retry_button_region;

	public void loadMenuResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		menuTextureAtlas = new BuildableBitmapTextureAtlas(
				activity.getTextureManager(), 1024, 1024,
				TextureOptions.BILINEAR);
		play_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "play.png");
		exit_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "quit.png");
		//NEED INFO GRAPHIC
		info_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "info.png");

		try {
			menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			menuTextureAtlas.load();
		} catch (Exception e) {
			Debug.e(e);
		}
	}
	
	public void unloadMenuResources() {
		menuTextureAtlas.unload();
		menuTextureAtlas = null;
	}

	public void loadGameResources() {
		gameTextureAtlas = new BuildableBitmapTextureAtlas(
				activity.getTextureManager(), 1024, 1024,
				TextureOptions.BILINEAR);
		player_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "Player.png");
		items_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "targetsprite.png", 12, 1);
		charge_bar_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "charge.png");
		post_menu_overlay_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "overlay.png");
		next_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "next.png");

		try {
			gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			gameTextureAtlas.load();
		} catch (Exception e) {
			Debug.e(e);
		}
	}

	public void unloadGameResources() {
		gameTextureAtlas.unload();
		gameTextureAtlas = null;
	}
	
	public void loadPostGameResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		postGameTextureAtlas = new BuildableBitmapTextureAtlas(
				activity.getTextureManager(), 1024, 1024,
				TextureOptions.BILINEAR);
		next_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(postGameTextureAtlas, activity, "next.png");
		retry_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(postGameTextureAtlas, activity, "retry.png");

		try {
			menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			menuTextureAtlas.load();
		} catch (Exception e) {
			Debug.e(e);
		}
	}
	
	public void unloadPostGameResources() {
		
	}

	public void loadFonts() {

	}
	
	public void loadInfoResources() {
		infoTextureAtlas = new BuildableBitmapTextureAtlas(
				activity.getTextureManager(), 1024, 1024,
				TextureOptions.BILINEAR);

		info_page_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(infoTextureAtlas, activity, "infobackground.png");

		
		try {
			infoTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			infoTextureAtlas.load();
			
		} catch (Exception e) {
			Debug.e(e);
		}
	}
	
	public void unloadInfoResources() {

		infoTextureAtlas.unload();
		infoTextureAtlas = null;
	}
	

	public static void prepareManager(Engine engine, GameActivity activity,
			Camera camera, VertexBufferObjectManager vbom) {
		getInstance().engine = engine;
		getInstance().activity = activity;
		getInstance().camera = camera;
		getInstance().vbom = vbom;

	}

	public static ResourceManager getInstance() {
		return INSTANCE;
	}

}
