package org.sopt.app.common.utils;

import java.util.List;

public interface AnonymousNameGenerator {
    String generate();
    List<String> generateMultiple(int count);
}
