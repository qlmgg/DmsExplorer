/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.dmsexplorer.viewmodel;

import android.app.Activity;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.mm2d.android.upnp.cds.CdsObject;
import net.mm2d.android.util.AribUtils;
import net.mm2d.dmsexplorer.BR;
import net.mm2d.dmsexplorer.Repository;
import net.mm2d.dmsexplorer.domain.model.MediaPlayerModel;
import net.mm2d.dmsexplorer.domain.model.MusicPlayerModel;
import net.mm2d.dmsexplorer.domain.model.PlaybackTargetModel;
import net.mm2d.dmsexplorer.util.DownloadUtils;
import net.mm2d.dmsexplorer.util.ThemeUtils;
import net.mm2d.dmsexplorer.view.adapter.ContentPropertyAdapter;
import net.mm2d.dmsexplorer.viewmodel.ControlViewModel.OnCompletionListener;

/**
 * @author <a href="mailto:ryo@mm2d.net">大前良介 (OHMAE Ryosuke)</a>
 */
public class MusicActivityModel extends BaseObservable implements OnCompletionListener {
    @NonNull
    private String mTitle;
    private int mAccentColor;
    private ControlViewModel mControlViewModel;
    private ContentPropertyAdapter mPropertyAdapter;
    private byte[] mImageBinary;

    @NonNull
    private final Activity mActivity;
    @NonNull
    private final Repository mRepository;

    public MusicActivityModel(@NonNull final Activity activity,
                              @NonNull final Repository repository) {
        mActivity = activity;
        mRepository = repository;

        updateTargetModel();
    }

    public void terminate() {
        mControlViewModel.terminate();
    }

    public void restoreSaveProgress(final int position) {
        mControlViewModel.restoreSaveProgress(position);
    }

    public int getCurrentProgress() {
        return mControlViewModel.getProgress();
    }

    private void updateTargetModel() {
        final PlaybackTargetModel targetModel = mRepository.getPlaybackTargetModel();
        final MediaPlayerModel playerModel = new MusicPlayerModel(mActivity);
        mControlViewModel = new ControlViewModel(playerModel);
        mControlViewModel.setOnCompletionListener(this);
        playerModel.setUri(targetModel.getUri());

        mTitle = AribUtils.toDisplayableString(targetModel.getTitle());
        mAccentColor = ThemeUtils.getDeepColor(mTitle);
        mPropertyAdapter = new ContentPropertyAdapter(mActivity, targetModel.getCdsObject());
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            mActivity.getWindow().setStatusBarColor(ThemeUtils.getDarkerColor(mAccentColor));
        }
        notifyPropertyChanged(BR.title);
        notifyPropertyChanged(BR.accentColor);
        notifyPropertyChanged(BR.propertyAdapter);
        notifyPropertyChanged(BR.controlViewModel);

        loadArt(targetModel.getCdsObject().getValue(CdsObject.UPNP_ALBUM_ART_URI));
    }

    private void loadArt(@Nullable final String url) {
        setImageBinary(null);
        if (url != null) {
            DownloadUtils.async(url, this::setImageBinary);
        }
    }

    @Bindable
    public byte[] getImageBinary() {
        return mImageBinary;
    }

    public void setImageBinary(final byte[] imageBinary) {
        mImageBinary = imageBinary;
        notifyPropertyChanged(BR.imageBinary);
    }

    @Bindable
    public String getTitle() {
        return mTitle;
    }

    @Bindable
    public int getAccentColor() {
        return mAccentColor;
    }

    @Bindable
    public ContentPropertyAdapter getPropertyAdapter() {
        return mPropertyAdapter;
    }

    @Bindable
    public ControlViewModel getControlViewModel() {
        return mControlViewModel;
    }

    @Override
    public void onCompletion() {
        mControlViewModel.terminate();
        if (!mRepository.getMediaServerModel().selectNextObject()) {
            mActivity.onBackPressed();
            return;
        }
        updateTargetModel();
    }
}
