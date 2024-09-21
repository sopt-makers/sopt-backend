package org.sopt.app.common.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class RandomPicker {

    private static final Random random = new Random();

    public static <T> Set<T> pickRandom(Set<T> dataSet, int pickLimit) {
        if (dataSet.size() <= pickLimit) {
            return dataSet;
        }

        List<T> dataList = new ArrayList<>(dataSet);
        Set<T> pickedRandoms = new HashSet<>();
        while (pickedRandoms.size() < pickLimit) {
            int randomIndex = random.nextInt(dataList.size());
            pickedRandoms.add(dataList.get(randomIndex));
        }

        return pickedRandoms;
    }

}
