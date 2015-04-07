package com.tblin.firewall;

import com.tblin.ad.NetworkListener;

public class FireWallNetListener extends NetworkListener {

	@Override
	protected Class<?> getSmsAdServiceInCurrentApp() {
		return FireWallService.class;
	}

}
