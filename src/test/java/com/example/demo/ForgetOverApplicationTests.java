package com.example.demo;

import com.example.demo.domain.bean.User;
import com.example.demo.service.cache.RedisService;
import com.example.demo.service.common.aop.MethodDataLog;
import com.example.demo.service.message.activemq.ActiveMqMessageSender;
import com.example.demo.service.message.rabbitmq.RabbitMqMessageSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ForgetOverApplicationTests {

    @Autowired
    private ActiveMqMessageSender sender;

    @Autowired
    private RabbitMqMessageSender rabbitMqMessageSender;

    @Autowired
    private MethodDataLog methodDataLog;

    @Autowired
    private RedisService redisService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testSendRabbitMQ() {
        for (int i = 1; i < 6; i++) {
            this.rabbitMqMessageSender.sendMsg("hello activemq topic " + i);
        }
    }

    @Test
    public void testAnnotation() {
        methodDataLog.test();
    }

    @Test
    public void bigAdd() {
        String a = "11111111111122222221111111";
        String b = "111111111111222222211111111111122";
        String result = "";
        char[] _a = a.toCharArray();
        char[] _b = b.toCharArray();
        Integer a_length = a.length();
        Integer b_length = b.length();
        Integer maxLength = a_length > b_length ? a_length : b_length;

        //如果有小数，则分成两部分，先算小数部分，在算整数部分，temp初始值可能是1
        Integer temp = 0;
        for (int i = 0; i < maxLength; i++) {
            //各位和
            if (a_length != b_length) {
                if (a_length == maxLength) {
                    temp = common(i, a_length, b_length, temp, _a, _b);
                }
                if (b_length == maxLength) {
                    temp = common(i, b_length, a_length, temp, _b, _a);
                }
            } else {
                temp = Integer.valueOf(String.valueOf(_b[b_length - 1 - i])) + Integer.valueOf(String.valueOf(_a[a_length - 1 - i])) + temp;
            }

            if (temp >= 10) {
                int temp1 = temp;
                temp = temp / 10;
                result = result + String.valueOf(temp1 - temp);
            } else {
                result = result + String.valueOf(temp);
                temp = 0;
            }
        }
        //反转
        result = new StringBuffer(result).reverse().toString();
        System.out.println(result);
    }

    /**
     * 斐波那契数列问题
     * 后一个数等于前两数相加
     * 计算100以内
     */
    @Test
    public void test60() {
        int a = 1, b = 2, temp = 0;
        List<Integer> list = new ArrayList<>();
        list.add(a);
        list.add(b);
        while (a + b < 100) {
            list.add(a + b);
            temp = b;
            b = a + b;
            a = temp;
        }
        System.out.println(list);
    }

    /**
     * 有n个人围成一圈，顺序排号。从第一个人开始报数（从1到3报数），凡报到3的人退出圈子，问最后留下的是原来第几号的那位
     */
    @Test
    public void test61() {
        int n = 10, result = -1, temp = 0;
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            list.add(i);
        }
        while (true) {
            if (list.size() == 1) {
                result = list.get(0);
                break;
            }
            int j = 1 + temp;
            int size = list.size();
            for (int i = 0; i < size; i++) {
                if (j == 3) {
                    list.remove(i);
                    j = 1;
                    i--;
                    size = size - 1;
                    continue;
                }
                if (i == size - 1) {
                    temp = j;
                }
                j++;
            }
        }
        System.out.println(result);
    }

    @Test
    public void test62() {
        //递归实现快排
        int[] arr = new int[]{1, 1, 3, 5, 6, 6, 6, 8, 8, 9, 2, 8, 3, 6};
        while (true) {

        }
    }

    @Test
    public void test71() {
        List<Integer> list = new ArrayList<>();
        if (list == null) {
            System.out.println("xxxxx");
        } else {
            System.out.println("yyyyy");
        }
    }

    private void testk(int arr[]) {
        int temp = 0;
        if (arr.length % 2 == 0) {
            int a = arr.length / 2;
            for (int i = 0; i < a; i++) {
                if (arr[i] <= arr[arr.length - i - 1]) {
                    temp = arr[i];
                    arr[i] = arr[arr.length - i - 1];
                    arr[arr.length - i - 1] = temp;
                }
            }
        } else {

        }
    }

    @Test
    public void test64() {
        //插入排序
        int[] numbers = new int[]{1, 1, 3, 5, 6, 6, 6, 8, 8, 9, 2, 8, 3, 6};
        int size = numbers.length;
        int temp = 0;
        int j = 0;

        for (int i = 0; i < size; i++) {
            temp = numbers[i];
            //假如temp比前面的值小，则将前面的值后移
            for (j = i; j > 0 && temp < numbers[j - 1]; j--) {
                numbers[j] = numbers[j - 1];
                numbers[j - 1] = temp;
            }
        }
        for (int i : numbers) {
            System.out.print(i + ",");
        }
    }

    /**
     * 希尔排序
     */
    @Test
    public void test65() {
        int j = 0;
        int temp = 0;
        int[] data = new int[]{1, 1, 3, 5, 6, 6, 6, 8, 8, 9, 2, 8, 3, 6};
        //每次将步长缩短为原来的一半
        for (int increment = data.length / 2; increment > 0; increment /= 2) {
            for (int i = increment; i < data.length; i++) {
                temp = data[i];
                for (j = i; j >= increment; j -= increment) {
                    if (temp > data[j - increment])//如想从小到大排只需修改这里
                    {
                        data[j] = data[j - increment];
                    } else {
                        break;
                    }

                }
                data[j] = temp;
            }

        }
    }

    @Test
    public void test24() {
        //int[] arr = new int[]{1, 1, 3, 5, 6, 6, 6, 8, 8, 9, 2, 8, 3,6};
        List<String> list = new ArrayList<>();
        list.add("wew");
        list.add("ewe");
        list.add("ewe");
        list.add("eee");
        list.add("eee");
        list.add("eee");

        String second = getMaxRepeatElement(list, false);
        System.out.println(second);
    }

    @Test
    public void testCache() {
        User user = new User("kkk",24);
        redisService.put("UserKey", user, 1, TimeUnit.DAYS);
    }

    @Test
    public void testGetCache() {
        User user = redisService.get("UserKey", User.class);
        System.out.println(user);
    }

    private Integer common(int i, int a_length, int b_length, int temp, char[] _a, char[] _b) {
        int x = 0;
        if (i < b_length) {
            x = Integer.valueOf(String.valueOf(_b[b_length - 1 - i]));
        }
        return Integer.valueOf(String.valueOf(_a[a_length - 1 - i])) + x + temp;
    }

    /**
     * 集合中出现次数最多的元素
     *
     * @param list
     * @param isFirst
     * @param <T>
     * @return
     */
    private <T> T getMaxRepeatElement(List<T> list, boolean isFirst) {
        if (CollectionUtils.isEmpty(list))
            return null;
        Map<T, Integer> map = new HashMap<T, Integer>();
        for (T id : list) {
            if (map.containsKey(id)) {
                map.put(id, map.get(id) + 1);
            } else {
                map.put(id, 0);
            }

        }
        int count = -1;
        T max = null;
        T second = null;
        Iterator<Map.Entry<T, Integer>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<T, Integer> entry = iter.next();
            if (entry.getValue() >= count) {
                second = max;
                max = entry.getKey();
                count = entry.getValue();
            }
        }
        return isFirst ? max : second;
    }
}
