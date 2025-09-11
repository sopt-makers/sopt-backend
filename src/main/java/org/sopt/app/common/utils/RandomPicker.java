package org.sopt.app.common.utils;

import java.util.*;
import java.util.stream.IntStream;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class RandomPicker {

    public static <T> List<T> pickRandom(List<T> data, int pickLimit) {
        int n = data.size();
        if (pickLimit <= 0 || n == 0) return List.of();     // 가드
        if (pickLimit >= n) return new ArrayList<>(data);   // 전부 반환

        int[] indices = IntStream.range(0, n).toArray();
        List<T> randomList = new ArrayList<>(pickLimit);
        for (int i = 0; i < pickLimit; ++i) {
            int randomIndex = java.util.concurrent.ThreadLocalRandom.current().nextInt(n - i);
            randomList.add(data.get(indices[randomIndex]));
            // Fisher–Yates 선택: 사용한 슬롯을 뒤쪽 미사용 구간의 마지막으로 대체
            indices[randomIndex] = indices[n - 1 - i];
        }
        return randomList;
    }
}
