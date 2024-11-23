package org.sopt.app.common.utils;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class HtmlTagWrapper {
    public static String wrapWithTag(String content, String tag) {
        return "<" + tag + ">" + content + "</" + tag + ">";
    }
}
