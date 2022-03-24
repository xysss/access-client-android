package com.htnova.access.commons.utils;

import java.util.Random;

/** 随机数据生成类。 */
public class RandomUtil {
    public static float getRandomFloat(float min, float max, int dotLength) {
        float floatBounded = min + new Random().nextFloat() * (max - min);
        floatBounded = NumberUtil.getFloatPrecise(floatBounded, 2);
        return floatBounded;
    }

    public static double getRandomDouble(double min, double max, int dotLength) {
        double doubleBounded = min + new Random().nextDouble() * (max - min);
        doubleBounded = NumberUtil.getDoublePrecise(doubleBounded, 2);
        return doubleBounded;
    }

    public static int getRandomInt(int min, int max) {
        int intBounded = min + ((int)(new Random().nextFloat() * (max - min)));
        return intBounded;
    }

    // 工具类不需要外部构造。
    private RandomUtil() {}
}
