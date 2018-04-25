package com.ni3.ag.navigator.client.domain.cache;

import org.apache.log4j.Logger;

import com.ni3.ag.navigator.shared.domain.GisOverlayGeometry;

public class OverlayCache extends AbstractObjectCache<GisOverlayGeometry>{

	private static final Logger log = Logger.getLogger(OverlayCache.class);

	private static final OverlayCache instance = new OverlayCache();

	private OverlayCache(){
	}

	public static OverlayCache getInstance(){
		return instance;
	}

	private String getFileName(final int overlayId, final Integer geometryId, final Integer version){
		final StringBuilder builder = new StringBuilder();
		builder.append(overlayId).append('-');
		builder.append(version).append('-');
		builder.append(geometryId);
		return builder.toString();
	}

	public GisOverlayGeometry getGeometry(final int overlayId, final Integer geometryId, final Integer version){
		return load(getFileName(overlayId, geometryId, version));
	}

	@Override
	protected String getName(){
		return "overlay";
	}

	public boolean saveGeometry(final int overlayId, final Integer geometryId, final Integer version,
	        final GisOverlayGeometry geometry){
		return save(getFileName(overlayId, geometryId, version), geometry);
	}
}
