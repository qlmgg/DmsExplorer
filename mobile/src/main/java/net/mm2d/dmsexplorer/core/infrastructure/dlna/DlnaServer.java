package net.mm2d.dmsexplorer.core.infrastructure.dlna;

import android.support.annotation.NonNull;

import net.mm2d.android.upnp.cds.CdsObject;
import net.mm2d.android.upnp.cds.MediaServer;
import net.mm2d.android.upnp.cds.RootCdsObject;
import net.mm2d.dmsexplorer.core.domain.Entry;
import net.mm2d.dmsexplorer.core.domain.Result;
import net.mm2d.dmsexplorer.core.domain.Server;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
public class DlnaServer implements Server {
    private final MediaServer mMediaServer;
    private final DlnaEntry mRootEntry;

    public DlnaServer(@NonNull final MediaServer server) {
        mMediaServer = server;
        mRootEntry = new DlnaEntry(this, null, new RootCdsObject(server.getUdn()));
    }

    @NonNull
    Observable<CdsObject> browse(@NonNull final String objectId) {
        return mMediaServer.browse(objectId);
    }

    boolean hasDeleteFunction() {
        return mMediaServer.hasDestroyObject();
    }

    Single<Result> delete(@NonNull final String objectId) {
        return mMediaServer.destroyObject(objectId)
                .map(i -> i == MediaServer.NO_ERROR ? Result.SUCCESS : Result.ERROR);
    }

    @NonNull
    @Override
    public String getName() {
        return mMediaServer.getFriendlyName();
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @NonNull
    @Override
    public Entry getRoot() {
        return mRootEntry;
    }

    @Override
    public void setActive(final boolean active) {
    }
}
