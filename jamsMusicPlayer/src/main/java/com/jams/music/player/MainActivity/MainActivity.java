/*
 * Copyright (C) 2014 Saravan Pantham
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jams.music.player.MainActivity;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;

import com.jams.music.player.Drawers.NavigationDrawerFragment;
import com.jams.music.player.Drawers.QueueDrawerFragment;
import com.jams.music.player.FoldersFragment.FilesFoldersFragment;
import com.jams.music.player.GridViewFragment.GridViewFragment;
import com.jams.music.player.Helpers.UIElementsHelper;
import com.jams.music.player.ListViewFragment.ListViewFragment;
import com.jams.music.player.R;
import com.jams.music.player.Utils.Common;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import android.support.design.widget.TabLayout;

public class MainActivity extends AppCompatActivity {

	//Context and Common object(s).
	private Context mContext;
	private Common mApp;

	//UI elements.
	//private FrameLayout mDrawerParentLayout;
	//private DrawerLayout mDrawerLayout;
	//private RelativeLayout mNavDrawerLayout;
	//private RelativeLayout mCurrentQueueDrawerLayout;
	//private ActionBarDrawerToggle mDrawerToggle;
    //private QueueDrawerFragment mQueueDrawerFragment;
    private Menu mMenu;
	
	//Current fragment params.
	private Fragment mCurrentFragment;
	public static int mCurrentFragmentId;
	public static int mCurrentFragmentLayout;
	
	//Layout flags.
	public static final String CURRENT_FRAGMENT = "CurrentFragment";
	public static final String ARTISTS_FRAGMENT_LAYOUT = "ArtistsFragmentLayout";
	public static final String ALBUM_ARTISTS_FRAGMENT_LAYOUT = "AlbumArtistsFragmentLayout";
	public static final String ALBUMS_FRAGMENT_LAYOUT = "AlbumsFragmentLayout";
	public static final String PLAYLISTS_FRAGMENT_LAYOUT = "PlaylistsFragmentLayout";
	public static final String GENRES_FRAGMENT_LAYOUT = "GenresFragmentLayout";
	public static final String FOLDERS_FRAGMENT_LAYOUT = "FoldersFragmentLayout";
    public static final String FRAGMENT_HEADER = "FragmentHeader";
	public static final int LIST_LAYOUT = 0;
	public static final int GRID_LAYOUT = 1;

	private String[] navMenuTitles;
	private SlidingMenu menu;
	private Toolbar toolbar;
	private ViewPager viewPager;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		//Context and Common object(s).
        mContext = getApplicationContext();
        mApp = (Common) getApplicationContext();
        
        //Set the theme and inflate the layout.
		//setTheme();
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		// As we're using a Toolbar, we should retrieve it and set it
		// to be our ActionBar
		toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				menu.showMenu(true);
			}
		});

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setIcon(R.drawable.icon_launcher4);

        //Init the UI elements.

        //Load the drawer fragments.
		InitializeTabPager();
		loadDrawerFragments();
        //Load the fragment.
        loadFragment(savedInstanceState);

    	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Check if this is the first time the app is being started.
        if (mApp.getSharedPreferences().getBoolean(Common.FIRST_RUN, true)==true) {
            showAlbumArtScanningDialog();
            mApp.getSharedPreferences().edit().putBoolean(Common.FIRST_RUN, false).commit();
        }
    	
	}

	private void InitializeTabPager()
	{
		TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
		tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.artists)));
		tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.album_artists)));
		tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.albums)));
		tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.songs)));
		tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.playlists)));
		tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.genres)));
		tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.folders)));
		tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
		tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

		viewPager = (ViewPager) findViewById(R.id.pager);
		final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),mContext,mApp);
		viewPager.setAdapter(adapter);
		viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

		tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				viewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {

			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

			}
		});
	}
	/**
	 * Sets the entire activity-wide theme.
	 */
	private void setTheme() {
    	//Set the UI theme.
    	if (mApp.getCurrentTheme()==Common.DARK_THEME) {
    		//setTheme(R.style.AppTheme);
    	} else {
    		//setTheme(R.style.AppThemeLight);
    	}
    	
	}

	/**
	 * Loads the correct fragment based on the selected browser.
	 */
	public void loadFragment(Bundle savedInstanceState) {
		//Get the target fragment from savedInstanceState if it's not null (orientation changes?).
		if (savedInstanceState!=null) {
			mCurrentFragmentId = savedInstanceState.getInt(CURRENT_FRAGMENT);
			invalidateOptionsMenu();
			
		} else {
			//Set the current fragment based on the intent's extras.
    		if (getIntent().hasExtra(CURRENT_FRAGMENT)) {
    			mCurrentFragmentId = getIntent().getExtras().getInt(CURRENT_FRAGMENT);
    		}

			int tabPossition = 0;
    		switch (mCurrentFragmentId) {
    		case Common.ARTISTS_FRAGMENT:
//    			mCurrentFragment = getLayoutFragment(Common.ARTISTS_FRAGMENT);
				tabPossition = 0;
    			break;
    		case Common.ALBUM_ARTISTS_FRAGMENT:
//    			mCurrentFragment = getLayoutFragment(Common.ALBUM_ARTISTS_FRAGMENT);
				tabPossition = 1;
    			break;
    		case Common.ALBUMS_FRAGMENT:
//    			mCurrentFragment = getLayoutFragment(Common.ALBUMS_FRAGMENT);
				tabPossition = 2;
				break;
    		case Common.SONGS_FRAGMENT:
//    			mCurrentFragment = getLayoutFragment(Common.SONGS_FRAGMENT);
				tabPossition = 3;
    			break;
    		case Common.PLAYLISTS_FRAGMENT:
//    			mCurrentFragment = getLayoutFragment(Common.PLAYLISTS_FRAGMENT);
				tabPossition = 4;
				break;
    		case Common.GENRES_FRAGMENT:
//    			mCurrentFragment = getLayoutFragment(Common.GENRES_FRAGMENT);
				tabPossition = 5;
				break;
    		case Common.FOLDERS_FRAGMENT:
//    			mCurrentFragment = new FilesFoldersFragment();
				tabPossition = 6;
				break;
			}

			switchContent(tabPossition);
		}
		
	}

	/**
	 * Loads the specified fragment into the target layout.
	 */
	public void switchContent(int tabPossition) {
        // Reset action bar
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayShowCustomEnabled(false);

		if(menu.isMenuShowing()) menu.toggle(true);
		viewPager.setCurrentItem(tabPossition);
		invalidateOptionsMenu();

	}
	
	/**
	 * Loads the drawer fragments.
	 */
	private void loadDrawerFragments() {

		// configure the SlidingMenu
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.menu);

		//Load the navigation drawer.
//		getSupportFragmentManager().beginTransaction()
//		   						   .replace(R.id.nav_drawer_container, new NavigationDrawerFragment())
//		   						   .commit();
//
//		//Load the current queue drawer.
//        mQueueDrawerFragment = new QueueDrawerFragment();
//		getSupportFragmentManager().beginTransaction()
//		   						   .replace(R.id.current_queue_drawer_container, mQueueDrawerFragment)
//				.commit();
//
	}

	/**
	 * Called when the user taps on the "Play all" or "Shuffle all" action button.
	 */
	public void playAll(boolean shuffle) {
		//Start the playback sequence.
		mApp.getPlaybackKickstarter().initPlayback(mContext, "", Common.PLAY_ALL_SONGS, 0, true, true);

	}

    /**
     * Displays the message dialog for album art processing.
     */
    private void showAlbumArtScanningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.album_art);
        builder.setMessage(R.string.scanning_for_album_art_details);
        builder.setPositiveButton(R.string.got_it, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}

		});

        builder.create().show();
    }

    /**
     * Inflates the generic MainActivity ActionBar layout.
     *
     * @param inflater The ActionBar's menu inflater.
     * @param menu The ActionBar menu to work with.
     */
    private void showMainActivityActionItems(MenuInflater inflater, Menu menu) {
        //Inflate the menu.
        getMenu().clear();
        inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity, menu);

        //Set the ActionBar background
		getSupportActionBar().setBackgroundDrawable(UIElementsHelper.getGeneralActionBarBackground(mContext));
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(true);

        //Set the ActionBar text color.
        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        if (actionBarTitleId > 0) {
            TextView title = (TextView) findViewById(actionBarTitleId);
            if (title != null) {
                title.setTextColor(0x00FFFFFF);
            }

        }

    }

    /**
     * Displays the folder fragment's action items.
     *
     * @param filePath The file path to set as the ActionBar's title text.
     * @param inflater The ActionBar's menu inflater.
     * @param menu The ActionBar menu to work with.
     * @param showPaste Pass true if the ActionBar is being updated for a copy/move operation.
	 */
    public void showFolderFragmentActionItems(String filePath,
											  MenuInflater inflater,
											  Menu menu,
                                              boolean showPaste) {
        getMenu().clear();
        inflater.inflate(R.menu.files_folders_fragment, menu);


		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setLogo(0);
		getSupportActionBar().setIcon(0);

        if (showPaste) {
            //Change the ActionBar's background and show the Paste Here option.
            menu.findItem(R.id.action_paste).setVisible(true);
            menu.findItem(R.id.action_cancel).setVisible(true);
			getSupportActionBar().setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.cab_background_top_apptheme));

            //Change the KitKat system bar color.
            if (Build.VERSION.SDK_INT==Build.VERSION_CODES.KITKAT)
                getWindow().setBackgroundDrawable(new ColorDrawable(0xFF002E3E));

        } else {
            //Hide the Paste Here option and set the default ActionBar background.
            menu.findItem(R.id.action_paste).setVisible(false);
            menu.findItem(R.id.action_cancel).setVisible(false);
			getSupportActionBar().setBackgroundDrawable(UIElementsHelper.getGeneralActionBarBackground(mContext));

            //Change the KitKat system bar color.
            if (Build.VERSION.SDK_INT==Build.VERSION_CODES.KITKAT)
                getWindow().setBackgroundDrawable(UIElementsHelper.getGeneralActionBarBackground(mContext));

        }

        LayoutInflater inflator = LayoutInflater.from(this);
        View view = inflator.inflate(R.layout.custom_actionbar_layout, null);

        TextView titleText = (TextView) view.findViewById(R.id.custom_actionbar_title);
        titleText.setText(filePath);
        titleText.setSelected(true);
        titleText.setTextColor(0xFFFFFFFF);

        //Inject the custom view into the ActionBar.
		getSupportActionBar().setCustomView(view);

    }
	
	/**
	 * Initializes the ActionBar.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        if (mCurrentFragmentId==Common.FOLDERS_FRAGMENT)
            showFolderFragmentActionItems(FilesFoldersFragment.currentDir,
                                          getMenuInflater(), menu, false);
        else
            showMainActivityActionItems(getMenuInflater(), menu);

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * ActionBar item selection listener.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
//		if (mDrawerToggle.onOptionsItemSelected(item)) {
//			return true;
//	    }
		
		switch (item.getItemId()) {
		case R.id.action_search:
//			ArtistsFragment.showSearch();
			return true;
	    case R.id.action_queue_drawer:
			menu.toggle(true);
//	    	if (mDrawerLayout!=null && mCurrentQueueDrawerLayout!=null) {
//		    	if (mDrawerLayout.isDrawerOpen(mCurrentQueueDrawerLayout)) {
//		    		mDrawerLayout.closeDrawer(mCurrentQueueDrawerLayout);
//		    	} else {
//		    		mDrawerLayout.openDrawer(mCurrentQueueDrawerLayout);
//		    	}
//
//	    	}
	    	return true;
        case R.id.action_up:
            ((FilesFoldersFragment) mCurrentFragment).getParentDir();
            return true;
        case R.id.action_paste:
            ((FilesFoldersFragment) mCurrentFragment).pasteIntoCurrentDir(((FilesFoldersFragment) mCurrentFragment).copyMoveSourceFile);
            showMainActivityActionItems(getMenuInflater(), getMenu());
            return true;
        case R.id.action_cancel:
            ((FilesFoldersFragment) mCurrentFragment).copyMoveSourceFile = null;
            if (((FilesFoldersFragment) mCurrentFragment).shouldMoveCopiedFile)
                Toast.makeText(mContext, R.string.move_canceled, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(mContext, R.string.copy_canceled, Toast.LENGTH_LONG).show();
            return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
		
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        mDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {

        if(menu.isMenuShowing()) { // Close left drawer if opened
            menu.toggle(true);
        } else
		if (getCurrentFragmentId()==Common.FOLDERS_FRAGMENT) {
            if (((FilesFoldersFragment) mCurrentFragment).getCurrentDir().equals("/"))
                super.onBackPressed();
            else
                ((FilesFoldersFragment) mCurrentFragment).getParentDir();

        } else {
            super.onBackPressed();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT==Build.VERSION_CODES.KITKAT) {
			getSupportActionBar().setBackgroundDrawable(UIElementsHelper.getGeneralActionBarBackground(mContext));
            getWindow().setBackgroundDrawable(UIElementsHelper.getGeneralActionBarBackground(mContext));
        }

    }
	
	/**
	 * Getters/Setters.
	 */
	
	public int getCurrentFragmentId() {
		return mCurrentFragmentId;
	}
	
	public void setCurrentFragmentId(int id) {
		mCurrentFragmentId = id;
	}

    public Menu getMenu() {
        return mMenu;
    }
	
}
