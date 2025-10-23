package org.sopt.app.common.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class SoptampDeepLinkBuilder {
	private static final String BASE =
		"home/soptamp/entire-part-ranking/part-ranking/missions/missionDetail";

	private SoptampDeepLinkBuilder() {}

	public static String buildStampDetailLink(long stampId, boolean isMine, String nickname, String part, long missionId) {
		return String.format("%s?id=%d&isMine=%s&nickname=%s&part=%s&missionId=%d",
			BASE, stampId, Boolean.toString(isMine), nickname, part, missionId);
	}
}
