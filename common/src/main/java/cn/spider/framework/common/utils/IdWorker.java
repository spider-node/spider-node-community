package cn.spider.framework.common.utils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import java.util.LinkedHashMap;
import java.util.Map;


public class IdWorker {
    private static final Integer DATA_SIZE = 32;
    private static final String[] RADIX_STR = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v"};
    private static Map<String, Integer> RADIX_MAP = new LinkedHashMap<>();

    private final static String SNOW = "SPIDER_SNOW";

    static {
        for (int i = 0; i < DATA_SIZE; i++) {
            RADIX_MAP.put(RADIX_STR[i], i);
        }
    }

    public static SnowIdDto calculateDataIdAndWorkId2(RedissonClient redissonClient, String appName) {
        String key = SNOW + appName;
        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
        long andIncrement = atomicLong.getAndIncrement();
        // result在0~1023之间
        long result = andIncrement % (DATA_SIZE * DATA_SIZE);
        //将result转化为32进制数，个位为worlId，十位为dataId
        String strResult = Integer.toString(Math.toIntExact(result), DATA_SIZE);
        String strResult2 = StringUtils.leftPad(strResult, 2, "0");
        String substring1 = strResult2.substring(0, 1);
        String substring2 = strResult2.substring(1, 2);
        Integer dataId = RADIX_MAP.get(substring1);
        Integer workId = RADIX_MAP.get(substring2);
        SnowIdDto snowIdDto = new SnowIdDto(System.currentTimeMillis(), dataId, workId);
        return snowIdDto;
    }


    public static int GetRandomNum(int num1, int num2) {
        int result = (int) (num1 + Math.random() * (num2 - num1 + 1));
        return result;
    }
}
