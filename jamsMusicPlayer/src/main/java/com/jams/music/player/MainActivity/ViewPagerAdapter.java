package com.jams.music.player.MainActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.jams.music.player.GridViewFragment.GridViewFragment;
import com.jams.music.player.ListViewFragment.ListViewFragment;
import com.jams.music.player.R;
import com.jams.music.player.Utils.Common;

/**
 * Created by Oswaldo Zapata on 10/10/2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter{

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

    private int mNumOfTabs;
    private Context myContext;
    private Common  mApp;
    public ViewPagerAdapter(FragmentManager fm, int NumOfTabs,Context context,Common appContext) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        myContext = context;
        mApp = appContext;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment tabFragment;
        Bundle bundle = new Bundle();
        switch (position) {
            case 0: //
                mCurrentFragmentLayout = mApp.getSharedPreferences().getInt(ARTISTS_FRAGMENT_LAYOUT, GRID_LAYOUT);
                bundle.putInt(Common.FRAGMENT_ID, Common.ARTISTS_FRAGMENT);
                bundle.putString(FRAGMENT_HEADER, myContext.getResources().getString(R.string.artists));
                break;
            case 1:
                mCurrentFragmentLayout = mApp.getSharedPreferences().getInt(ALBUM_ARTISTS_FRAGMENT_LAYOUT, GRID_LAYOUT);
                bundle.putInt(Common.FRAGMENT_ID, Common.ALBUM_ARTISTS_FRAGMENT);
                bundle.putString(FRAGMENT_HEADER, myContext.getResources().getString(R.string.album_artists));
                break;
            case 2:
                mCurrentFragmentLayout = mApp.getSharedPreferences().getInt(ALBUMS_FRAGMENT_LAYOUT, GRID_LAYOUT);
                bundle.putInt(Common.FRAGMENT_ID, Common.ALBUMS_FRAGMENT);
                bundle.putString(FRAGMENT_HEADER, myContext.getResources().getString(R.string.albums));
                break;
            case 3:
                mCurrentFragmentLayout = LIST_LAYOUT;
                bundle.putInt(Common.FRAGMENT_ID, Common.SONGS_FRAGMENT);
                bundle.putString(FRAGMENT_HEADER, myContext.getResources().getString(R.string.songs));
                break;
            case 4:
                mCurrentFragmentLayout = mApp.getSharedPreferences().getInt(PLAYLISTS_FRAGMENT_LAYOUT, LIST_LAYOUT);
                bundle.putInt(Common.FRAGMENT_ID, Common.PLAYLISTS_FRAGMENT);
                bundle.putString(FRAGMENT_HEADER, myContext.getResources().getString(R.string.playlists));
                break;
            case 5:
                mCurrentFragmentLayout = mApp.getSharedPreferences().getInt(GENRES_FRAGMENT_LAYOUT, GRID_LAYOUT);
                bundle.putInt(Common.FRAGMENT_ID, Common.GENRES_FRAGMENT);
                bundle.putString(FRAGMENT_HEADER, myContext.getResources().getString(R.string.genres));
                break;
            case 6:
                mCurrentFragmentLayout = mApp.getSharedPreferences().getInt(FOLDERS_FRAGMENT_LAYOUT, LIST_LAYOUT);
                bundle.putInt(Common.FRAGMENT_ID, Common.FOLDERS_FRAGMENT);
                bundle.putString(FRAGMENT_HEADER, myContext.getResources().getString(R.string.folders));
                break;
            default:
                return null;
        }

        //Return the correct layout fragment.
        if (mCurrentFragmentLayout==GRID_LAYOUT) {
            tabFragment = new GridViewFragment();
            tabFragment.setArguments(bundle);
        } else {
            tabFragment = new ListViewFragment();
            tabFragment.setArguments(bundle);
        }
        return tabFragment;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
