/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.dmsexplorer.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

/**
 * @author <a href="mailto:ryo@mm2d.net">大前良介 (OHMAE Ryosuke)</a>
 */
public class Settings {
    private static SharedPreferences sPref;

    private static SharedPreferences getPref(@NonNull final Context context) {
        if (sPref != null) {
            return sPref;
        }
        sPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sPref;
    }

    public static void initialize(@NonNull final Context context) {
        Maintainer.maintain(getPref(context));
    }

    private final Context mContext;

    public Settings(@NonNull final Context context) {
        mContext = context;
    }

    private SharedPreferences getPref() {
        return getPref(mContext);
    }

    public boolean isPlayMovieMyself() {
        return getPref().getBoolean(Key.LAUNCH_APP_MOVIE.name(), true);
    }

    public boolean isPlayMusicMyself() {
        return getPref().getBoolean(Key.LAUNCH_APP_MUSIC.name(), true);
    }

    public boolean isPlayPhotoMyself() {
        return getPref().getBoolean(Key.LAUNCH_APP_PHOTO.name(), true);
    }

    public boolean isMusicAutoPlay() {
        return getPref().getBoolean(Key.MUSIC_AUTO_PLAY.name(), false);
    }
}
