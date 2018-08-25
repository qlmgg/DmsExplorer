/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.dmsexplorer.core;

import net.mm2d.android.upnp.AvControlPointManager;
import net.mm2d.dmsexplorer.core.domain.ServerRepositories;
import net.mm2d.dmsexplorer.core.infrastructure.dlna.DlnaServerRepository;
import net.mm2d.log.Log;

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
public class MainRepository {
    private final AvControlPointManager mAvControlPointManager;
    private final ServerRepositories mServerRepositories;
    public MainRepository() {
        mAvControlPointManager = new AvControlPointManager();
        mServerRepositories = new ServerRepositories(new DlnaServerRepository(mAvControlPointManager));
        mServerRepositories.getDiscoveryObservable()
                .subscribe(discoveryEvent -> {
                    Log.e(discoveryEvent.getType() + " " + discoveryEvent.getServer());
                });
    }

    public void search() {
        mServerRepositories.startSearch();
    }
}
