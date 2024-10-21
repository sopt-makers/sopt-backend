package org.sopt.app.common.utils;

import java.util.*;
import java.util.stream.IntStream;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class RandomPicker {

    private static final Random random = new Random();

    public static <T> List<T> pickRandom(List<T> data, int pickLimit) {
        if (data.size() <= pickLimit) {
            return new ArrayList<>(data);
        }

        int[] indices = IntStream.range(0, data.size()).toArray();
        List<T> randomList = new ArrayList<>();
        for (int i = 0; i < pickLimit; ++i) {
            int randomIndex = random.nextInt(data.size() - i);
            randomList.add(data.get(indices[randomIndex]));
            indices[randomIndex] = indices[data.size() - 1 - i];
        }
        return randomList;
    }

}
