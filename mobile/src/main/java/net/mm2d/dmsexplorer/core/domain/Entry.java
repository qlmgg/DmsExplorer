/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.dmsexplorer.core.domain;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.mm2d.dmsexplorer.domain.entity.ContentType;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * @author <a href="mailto:ryo@mm2d.net">大前良介 (OHMAE Ryosuke)</a>
 */
public interface Entry {
    boolean isContent();

    boolean isContainer();

    boolean isDeletable();

    @NonNull
    Single<Result> delete();

    @NonNull
    ContentType getType();

    @NonNull
    Server getServer();

    @Nullable
    Entry getParent();

    boolean isRoot();

    @NonNull
    String getName();

    @NonNull
    Observable<? extends Entry> readEntries(boolean noCache);

    @NonNull
    PlayList createPlayList(ContentType type);
}
