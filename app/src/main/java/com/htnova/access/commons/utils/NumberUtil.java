package com.htnova.access.commons.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;

/** 数字处理工具类型，处理各种数字类型的转换，处理字符串与数字的转换。 */
public class NumberUtil {
    public static int byte2Int(byte abyte) {
        return (abyte & 0xff);
    }

    public static int bytes2Int(byte[] bytes, boolean isBigEndian) {
        if (bytes == null || bytes.length == 0) {
            return 0;
        }

        if (isBigEndian) {
            int digit = 0;
            for (int i = 0; i < bytes.length; i++) {
                byte abyte = bytes[i];
                digit = digit << 8;
                digit = digit + byte2Int(abyte);
            }
            return digit;
        } else {
            int digit = 0;
            for (int i = bytes.length - 1; i >= 0; i--) {
                byte abyte = bytes[i];
                digit = digit << 8;
                digit = digit + byte2Int(abyte);
            }
            return digit;
        }
    }

    public static int bytes2Int(byte[] bytes, int beginPos, int length, boolean isBigEndian) {
        if (bytes == null || bytes.length == 0) {
            return 0;
        }

        byte[] payload = new byte[length];
        System.arraycopy(bytes, beginPos, payload, 0, payload.length);
        if (isBigEndian) {
            int digit = 0;
            for (int i = 0; i < payload.length; i++) {
                byte abyte = payload[i];
                digit = digit << 8;
                digit = digit + byte2Int(abyte);
            }
            return digit;
        } else {
            int digit = 0;
            for (int i = payload.length - 1; i >= 0; i--) {
                byte abyte = payload[i];
                digit = digit << 8;
                digit = digit + byte2Int(abyte);
            }
            return digit;
        }
    }

    public static long bytes2Long(byte[] bytes, boolean isBigEndian) {
        if (bytes == null || bytes.length == 0) {
            return 0;
        }

        if (isBigEndian) {
            long digit = 0;
            for (int i = 0; i < bytes.length; i++) {
                byte abyte = bytes[i];
                digit = digit << 8;
                digit = digit + byte2Int(abyte);
            }
            return digit;
        } else {
            long digit = 0;
            for (int i = bytes.length - 1; i >= 0; i--) {
                byte abyte = bytes[i];
                digit = digit << 8;
                digit = digit + byte2Int(abyte);
            }
            return digit;
        }
    }

    public static long bytes2Long(byte[] bytes, int beginPos, int length, boolean isBigEndian) {
        if (bytes == null || bytes.length == 0) {
            return 0;
        }

        byte[] payload = new byte[length];
        System.arraycopy(bytes, beginPos, payload, 0, payload.length);
        if (isBigEndian) {
            long digit = 0;
            for (int i = 0; i < payload.length; i++) {
                byte abyte = payload[i];
                digit = digit << 8;
                digit = digit + byte2Int(abyte);
            }
            return digit;
        } else {
            long digit = 0;
            for (int i = payload.length - 1; i >= 0; i--) {
                byte abyte = payload[i];
                digit = digit << 8;
                digit = digit + byte2Int(abyte);
            }
            return digit;
        }
    }

    public static float bytes2Float(byte[] bytes, boolean isBigEndian) {
        if (isBigEndian) {
            return ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getFloat();
        } else {
            return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        }
    }

    public static float bytes2Float(byte[] bytes, int beginPos, boolean isBigEndian) {
        byte[] payload = new byte[4];
        System.arraycopy(bytes, beginPos, payload, 0, payload.length);
        if (isBigEndian) {
            return ByteBuffer.wrap(payload).order(ByteOrder.BIG_ENDIAN).getFloat();
        } else {
            return ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        }
    }

    public static float bytes2Float(byte[] bytes, int beginPos, int length, boolean isBigEndian) {
        if (bytes == null || bytes.length == 0) {
            return 0;
        }

        byte[] payload = new byte[length];
        System.arraycopy(bytes, beginPos, payload, 0, payload.length);
        if (isBigEndian) {
            return ByteBuffer.wrap(payload).order(ByteOrder.BIG_ENDIAN).getFloat();
        } else {
            return ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        }
    }

    public static double bytes2Double(byte[] bytes, boolean isBigEndian) {
        if (isBigEndian) {
            return ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getDouble();
        } else {
            return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        }
    }

    public static double bytes2Double(byte[] bytes, int beginPos, boolean isBigEndian) {
        byte[] palyoad = new byte[8];
        System.arraycopy(bytes, beginPos, palyoad, 0, palyoad.length);
        if (isBigEndian) {
            return ByteBuffer.wrap(palyoad).order(ByteOrder.BIG_ENDIAN).getDouble();
        } else {
            return ByteBuffer.wrap(palyoad).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        }
    }

    public static byte[] int2Bytes4(int aint, boolean isBigEndian) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        if (isBigEndian) {
            buffer.order(ByteOrder.BIG_ENDIAN);
        } else {
            buffer.order(ByteOrder.LITTLE_ENDIAN);
        }
        buffer.putInt(aint);
        return buffer.array();
    }

    public static byte[] int2Bytes2(int aint, boolean isBigEndian) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        if (isBigEndian) {
            buffer.order(ByteOrder.BIG_ENDIAN);
        } else {
            buffer.order(ByteOrder.LITTLE_ENDIAN);
        }
        buffer.putInt(aint);
        byte[] result = buffer.array();
        if (isBigEndian) {
            return new byte[] {result[2], result[3]};
        } else {
            return new byte[] {result[0], result[1]};
        }
    }

    public static byte[] long2Bytes8(long along, boolean isBigEndian) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        if (isBigEndian) {
            buffer.order(ByteOrder.BIG_ENDIAN);
        } else {
            buffer.order(ByteOrder.LITTLE_ENDIAN);
        }
        buffer.putLong(along);
        return buffer.array();
    }

    public static byte[] long2Bytes4(long along, boolean isBigEndian) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        if (isBigEndian) {
            buffer.order(ByteOrder.BIG_ENDIAN);
        } else {
            buffer.order(ByteOrder.LITTLE_ENDIAN);
        }
        buffer.putLong(along);
        byte[] result = buffer.array();
        if (isBigEndian) {
            return new byte[] {result[4], result[5], result[6], result[7]};
        } else {
            return new byte[] {result[0], result[1], result[2], result[3]};
        }
    }

    public static byte[] float2Bytes4(float afloat, boolean isBigEndian) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        if (isBigEndian) {
            buffer.order(ByteOrder.BIG_ENDIAN);
        } else {
            buffer.order(ByteOrder.LITTLE_ENDIAN);
        }
        buffer.putFloat(afloat);
        return buffer.array();
    }

    public static byte[] double2Bytes8(double adouble, boolean isBigEndian) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        if (isBigEndian) {
            buffer.order(ByteOrder.BIG_ENDIAN);
        } else {
            buffer.order(ByteOrder.LITTLE_ENDIAN);
        }
        buffer.putDouble(adouble);
        return buffer.array();
    }

    /**
     * 截取相应的小数位数（四舍五入）。
     *
     * @param adouble
     *            要截取的double数。
     * @param dotLength
     *            要截取的小数位数。
     * @return 截取后的double数。
     */
    public static double getDoublePrecise(double adouble, int dotLength) {
        BigDecimal bd = new BigDecimal(adouble);
        return bd.setScale(dotLength, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static float getFloatPrecise(float afloat, int dotLength) {
        BigDecimal bd = new BigDecimal(afloat);
        return bd.setScale(dotLength, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * 把字节数组转换成16进制字符串。
     *
     * @param bytes
     *            要转换的字节数组。
     * @return 转换后的16进制字符串。
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder buf = new StringBuilder(bytes.length);
        String sTemp;
        for (byte abyte : bytes) {
            sTemp = Integer.toHexString(0xFF & abyte);
            if (sTemp.length() < 2) {
                buf.append(0);
            }
            buf.append(sTemp.toUpperCase());
            buf.append(" ");
        }
        return buf.toString();
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] hexBytes = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            hexBytes[i] = (byte)(charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return hexBytes;
    }

    private static byte charToByte(char c) {
        return (byte)"0123456789ABCDEF".indexOf(c);
    }

    /**
     * 将byte[]转为各种进制的字符串
     *
     * @param bytes
     *            要转换的字节数组。
     * @param radix
     *            基数可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制。
     * @return 转换后的字符串。
     */
    public static String binary(byte[] bytes, int radix) {
        return new BigInteger(1, bytes).toString(radix);
    }

    /**
     * 将byte转换为一个长度为8的byte数组，数组每个值代表bit
     */
    public static byte[] getBits(byte b, boolean isBigEndian) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte)(b & 1);
            b = (byte)(b >> 1);
        }
        if (isBigEndian) {
            return array;
        }

        byte[] bigArray = new byte[8];
        for (int i = 0; i < 8; i++) {
            bigArray[i] = array[8 - i - 1];
        }
        return bigArray;
    }

    public static byte byte2Bcd(byte abyte) {
        // 高四位
        byte bhigh = (byte)(abyte / 10);

        // 低四位
        byte blow = (byte)(abyte % 10);

        return (byte)((bhigh << 4) | blow);
    }

    public static byte bcd2Byte(byte abyte) {
        // 高四位
        byte bhigh = (byte)((abyte >> 4) & 0xF);

        // 低四位
        byte blow = (byte)(abyte & 0xF);

        return (byte)(bhigh * 10 + blow);
    }

    /**
     * 是否包含数值。
     *
     * @param value
     *            验证值。
     * @param values
     *            值数组。
     * @return 包含返回true，否则返回false。
     */
    public static boolean isValueIn(Number value, Map<String, Number> values) {
        if (values.containsKey(Long.toString(value.longValue()))) {
            return true;
        }

        return false;
    }

    // 工具类不需要外部构造。
    private NumberUtil() {}
}
