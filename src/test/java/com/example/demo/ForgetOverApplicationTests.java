package com.example.demo;

import com.example.demo.core.json.TelephoneName;
import com.example.demo.core.project.ChapterListInfo;
import com.example.demo.core.project.PayPalTrackerInfo;
import com.example.demo.core.project.QuestionBean;
import com.example.demo.core.project.QuestionInfo;
import com.example.demo.dao.mapper.QuestionMapper;
import com.example.demo.domain.bean.User;
import com.example.demo.service.UserService;
import com.example.demo.service.cache.RedisService;
import com.example.demo.service.common.aop.MethodDataLog;
import com.example.demo.service.message.activemq.ActiveMqMessageSender;
import com.example.demo.service.message.rabbitmq.RabbitMqMessageSender;
import com.example.demo.service.utils.CreateFileUtil;
import com.example.demo.service.utils.DesUtils;
import com.example.demo.service.utils.JSONUtils;
import com.example.demo.service.utils.RandomUtils;
import com.example.demo.tool.thread.ThreadPool;
import com.fasterxml.jackson.core.type.TypeReference;
import com.wandoujia.commons.httputils.model.HttpRequestMethod;
import com.wandoujia.commons.httputils.service.HttpService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.DigestUtils;
import sun.font.FontDesignMetrics;
import sun.misc.BASE64Encoder;
import sun.net.www.http.HttpClient;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ForgetOverApplicationTests {

    private static Map<Integer, Integer> map = new HashMap<>();


    @Autowired
    private ActiveMqMessageSender sender;

    @Autowired
    private RabbitMqMessageSender rabbitMqMessageSender;

    @Autowired
    private MethodDataLog methodDataLog;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserService userService;

    @Autowired(required = false)
    private QuestionMapper questionMapper;

    @Autowired(required = false)
    private HttpService httpService;

    private final static String CHARSET_UTF8 = "utf8";
    private final static String ALGORITHM = "UTF-8";
    private final static String SEPARATOR = "&";

    @Test
    public void contextLoads() {
        System.out.println(map.get(1));
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
        // 1. .csv文件的路径。注意只有一个\的要改成\\
        File csv = new File("/Users/ywj/Downloads/testShell/ugc/test_telepbone_read.csv"); // CSV文件路径
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(csv));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = "";
        String everyLine = "";
        try {
            List<String> allString = new ArrayList<>();
            while ((line = br.readLine()) != null) // 读取到的内容给line变量
            {
                everyLine = line;
                System.out.println(everyLine);
                allString.add(everyLine);
            }
            System.out.println("csv表格中所有行数：" + allString.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getRandomNick(String str) {
        if (str.length() == 0)
            return 0;
        char[] chars = str.toCharArray();
        // 判断是否存在符号位
        int flag = 0;
        if (chars[0] == '+')
            flag = 1;
        else if (chars[0] == '-')
            flag = 2;
        int start = flag > 0 ? 1 : 0;
        int res = 0;// 保存结果
        for (int i = start; i < chars.length; i++) {
            if (Character.isDigit(chars[i])) {// 调用Character.isDigit(char)方法判断是否是数字，是返回True，否则False
                int temp = chars[i] - '0';
                //res = res * 10 + temp;
                res = res + temp * (int) Math.pow(10, chars.length - start - 1);
            } else {
                return 0;
            }
        }
        return flag == 1 ? res : -res;
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
        String str = "460012323232";
        if (StringUtils.isBlank(str) || str.length() < 5) {
            System.out.println("test62 length is min");
        }
        String substring = StringUtils.substring(str, 3, 5);
        System.out.println("test62 " + substring);

        String str1 = "4600";
        if (StringUtils.isBlank(str) || str.length() < 5) {
            System.out.println("test63 length is min");
        }
        String substring1 = StringUtils.substring(str1, 3, 5);
        System.out.println("test63 " + substring1);
    }

    @Test
    public void test81() {
        int a = 31 % 1;
        System.out.println(a);
    }

    @Test
    public void test63() {
        try {
            String str = "P3VKQo9padxfuk1OacyXIs2jfFRBfMAOk0J/JvQjgKJOiB+1lIM1FGrIlokOf51BEiyGvvV8JQM0YE/BOvl3hGF+01A+xauyX7pNTmnMlyLNo3xUQXzADiOfDdNH3DBGgzzocog8H/8az8XvMZpDILuL8Zu41DlNs248jB3KAjJhftNQPsWrsl+6TU5pzJcizaN8VEF8wA6w1MOp5lkMD82E1hvjiTq/asiWiQ5/nUFjLPMe0hJNWVSxC74oOByzEOhKQKUPR3VegkmMRSR4Vgee5o+IwvuBj31c03XuaiLzoEdzD8dRCzHzrCguwiQtyIyufxlHfhffDKDxvIG8OBrubhmGA3Yf";
            String plaintext = DesUtils.decryptThreeDESECB(str);
            List<TelephoneName> telephoneNamePlaintext = JSONUtils.fromJSON(plaintext, new TypeReference<List<TelephoneName>>() {
            });
            System.out.println(telephoneNamePlaintext);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private int pxToFontSize(int pxHeight) {
        int h = estimateFontSize(pxHeight);//初始字号
        int correct = correctHeight(h);//字号换算的px高度
        int diff = 1;
        while (Math.abs(correct - pxHeight) > 3) {//3是差值（连续字号高度差值不会超过3像素）
            if (correct < pxHeight) {
                h = estimateFontSize(pxHeight) + diff;
                correct = correctHeight(h);
            } else if (correct > pxHeight) {
                h = (estimateFontSize(pxHeight) - diff);
                correct = correctHeight(h);
            }
            diff++;

        }
        return h;


    }

    // 根据字体px高度估算字号大小
    private int estimateFontSize(int h) {
        return h;
    }

    @Test
    public void test71() {
        Timer timer = new Timer();//实例化Timer类
        timer.schedule(new TimerTask() {
            public void run() {
                System.out.println("退出");
                this.cancel();
            }
        }, 20000);//五百毫秒
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
    public void test100() {
        System.out.println(fillUrlWithWatermark());
    }

    public static int getLength(String text) {
        int textLength = text.length();
        int length = textLength;
        for (int i = 0; i < textLength; i++) {
            if (String.valueOf(text.charAt(i)).getBytes().length > 1) {
                length++;
            }
        }
        return (length % 2 == 0) ? length / 2 : length / 2 + 1;
    }

    private String fillUrlWithWatermark() {
        String url = "http://img.kaiyanapp.com/cee019283a749c3cd4455383ed60ef94.png";
        int width = 734;
        int height = 736;
        String ws = "0.05";
        int fontSize = getWatermarkFontSize(width, height, ws);
        int text_height = fontSize / 20;
        int text_x = 20;
        int text_y = 9;
        int image_y = text_height + text_y + 3;
        url = url + "?watermark/3/image/aHR0cHM6Ly9pbWcua2FpeWFuYXBwLmNvbS9leWVwZXRpemVyLWxvZ28zLnBuZw==/dissolve/100/gravity/SouthEast/dx/5/dy/" + image_y + "/ws/" + ws + "/text/"
                + safeUrlBase64Encode("@一念尘") + "/font/5b6u6L2v6ZuF6buR/fontsize/" + fontSize + "/dissolve/60/fill/I0ZGRkZGRg==/gravity/SouthEast/dx/" + text_x + "/dy/" + text_y;
        return url;

    }

    private int getWatermarkFontSize(int width, int height, String ws) {
        if (width < 0)
            return 300;
        int min = Math.min(width, height);
        double image_width = 0.0d;
        double image_height = 0.0d;
        if (width < height) {
            image_width = Double.valueOf(ws) * min;//图片水印宽度
            image_height = 110.0d / 330.0d * image_width;//图片水印高度
        } else {
            image_height = Double.valueOf(ws) * min;
        }
        int text_height = (int) (image_height * 0.5); //文字水印高度
        if (text_height >= 28) {
            return (text_height - 6) * 20;
        } else if (text_height >= 23) {
            return (text_height - 5) * 20;
        } else if (text_height >= 18) {
            return (text_height - 4) * 20;
        } else if (text_height >= 14) {
            return (text_height - 3) * 20;
        } else {
            return (text_height - 2) * 20;
        }
    }

    @Test
    public void test1199() {
        double a = 12332.12323235;


        System.out.println(String.format("%.2f", a));
    }

    private int interfaceUtil() {
        int tryCount = 3;
        while (tryCount-- > 0) {
            if (tryCount == 2) {
                return tryCount;
            }
        }
        return -1;
    }

    private int getMin(int minute) {
        int curMinute = getField(new Date(), Calendar.MINUTE);
        int curSecond = getField(new Date(), Calendar.SECOND);
        if (curMinute < minute) {
            return minute * 60 - (curMinute * 60 + curSecond);
        }
        return 60 * 60 - (curMinute * 60 + curSecond - minute * 60);
    }

    public int getField(Date date, int field) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(field);
    }

    public <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private long setInitialDelayOfDay(int hour) {
        long current = System.currentTimeMillis();
        long configHour = getCurrentDayHourOfSecond(hour, current);

        if (configHour > current) {
            return (configHour - current) / 60000L;
        }
        if (configHour < current) {
            return (24 * 60 * 60 * 1000L - (current - configHour)) / 60000L;
        }
        return 24 * 60L;
    }

    private Long getCurrentDayHourOfSecond(int hour, long current) {
        long zero = current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();

        return zero + hour * 60 * 60 * 1000;
    }

    private String safeUrlBase64Encode(String originStr) {
        if (StringUtils.isEmpty(originStr))
            return originStr;
        String encodeBase64 = new BASE64Encoder().encode(originStr.getBytes());
        String safeBase64Str = encodeBase64.replace('+', '-');
        safeBase64Str = safeBase64Str.replace('/', '_');
        //safeBase64Str = safeBase64Str.replaceAll("=", "");
        return safeBase64Str;
    }

    // 根据字号大小精确计算字体px高度
    private static int correctHeight(int fontSize) {
        FontDesignMetrics fm = FontDesignMetrics.getMetrics(new Font("FZHei-B01S", Font.PLAIN, fontSize));
        return fm.getHeight();
    }

    private int test324(int size) {
        FontDesignMetrics fm = FontDesignMetrics.getMetrics(new Font("FZHei-B01S", Font.BOLD, size));
        return fm.getHeight();
    }

    @Test
    public void test24() {
        String _id = "5c0f27f34bade528b546c4d4";

        Date date = new Date(Long.parseLong(Integer.parseInt(_id.substring(0, 8), 16) + "000"));

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS", Locale.ROOT);
        System.out.println(dateFormat.format(date));
        //5b7690febb9f1aaa712efc6d
        //5b7d7cde0000000000000000
        //5b7a2c33bb9f1aaa714928f0

        //﻿ObjectId("5c0f27f34bade528b546c4d4")
    }

    @Test
    public void test23() {
        StringBuffer objectId = new StringBuffer("");
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
            Date date = sdf.parse("2018-08-27 00:00:00:000");
            objectId.append(Long.toHexString(date.getTime() / 1000)); //转换为16进制的字符串
            while (objectId.length() < 24) { //bson-3.6.4.jar 版本校验ObjectId的长度为24位，不足24位补0
                objectId.append("0");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println(objectId.toString());
        //return objectId.toString();
        //5b7d7cde0000000000000000
    }

    @Test
    public void test25() {
        Map<Integer, String> map = new HashMap<>(1000, 0.75f);
        //
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            Integer key = entry.getKey();
            String value = entry.getValue();
        }
    }

    @Test
    public void testCache() {
        User user = new User("kkk", 24);
        redisService.put("UserKey", user, 1, TimeUnit.DAYS);
    }

    @Test
    public void testGetCache() {
        User user = redisService.get("UserKey", User.class);
        System.out.println(user);
    }

    @Test
    public void testThread() {
        // 从线程池中取出一个空闲线程使用
        ThreadPool.getInstance().exec(() -> {

        });
    }

    @Test
    public void testCheckoutObject() {
        List<User> list = new ArrayList<>();
        User checkoutUser = new User(3, "ywj2", 20);
        list.add(new User(1, "ywj", 20));
        list.add(new User(2, "ywj1", 20));
        list.add(new User(3, "ywj2", 20));
        list.add(new User(4, "ywj3", 20));
        //testCheckoutObject1(list, checkoutUser);
        testCheckoutObject2(list, checkoutUser);
    }

    private void testCheckoutObject2(List<User> list, User checkoutUser) {
        long start = System.currentTimeMillis();
        list.stream().filter(Objects::nonNull).forEach(x -> {
            if (checkoutUser.getId() == x.getId()) {
                System.out.println("user is exist");
            }
        });
        long end = System.currentTimeMillis();
        System.out.println("time=" + (end - start));
    }

    private void testCheckoutObject1(List<User> list, User checkoutUser) {
        long start = System.currentTimeMillis();
        Map<Integer, User> userMap = list.stream().collect(Collectors.toMap(User::getId, Function.identity()));
        if (userMap.containsKey(checkoutUser.getId())) {
            System.out.println("user is exist");
        }
        long end = System.currentTimeMillis();
        System.out.println("time=" + (end - start));
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

    @Test
    public void query() {
        int[] a = new int[]{1, 3, 6, 7};
        int k = 1;
        boolean result = queryTwo(a, k, 0, a.length - 1);
        System.out.println(result);
    }

    private boolean queryTwo(int[] a, int k, int m, int n) {
        if (m >= n)
            return false;
        int mid = (m + n) / 2;
        int temp = a[mid];
        if (temp == k)
            return true;
        if (mid == 0) {
            return false;
        }
        if (mid + 1 == n) {

        }
        if (k < temp) {
            return queryTwo(a, k, m, mid);
        }
        if (k > temp) {
            return queryTwo(a, k, mid, n);
        }
        return false;
    }

    @Test
    public void quickSort() {
        int[] a = new int[]{2, 43, 1, 6, 3, 7};
        sort(a, 0, 5);
        for (int i : a) {
            System.out.print(i + " ");
        }
    }

    private void sort(int[] a, int low, int height) {
        if (low < height) {
            int mid = sorts(a, low, height);
            sort(a, low, mid - 1);
            sort(a, mid + 1, height);
        }
    }

    private int sorts(int[] a, int low, int height) {
        int temp = a[low];
        while (low < height) {
            while (low < height && temp < a[height]) {
                height--;
            }
            a[low] = a[height];
            while (low < height && temp > a[low]) {
                low++;
            }
            a[height] = a[low];
        }
        a[low] = temp;
        return low;
    }

    @Test
    public void getChapterListInfo() {
        ChapterListInfo chapterListInfo = userService.getChapterListInfo();
        String json = JSONUtils.toJSON(chapterListInfo);
        long time = System.currentTimeMillis();
        System.out.println("时间: " + time);
        CreateFileUtil.createJsonFile(json, "/Users/ywj/chapterFile", "chapter_" + time);
    }

    @Test
    public void getChapterListInfoAnther() {
        ChapterListInfo chapterListInfo = userService.getChapterListInfoOther();
        String json = JSONUtils.toJSON(chapterListInfo);
        long time = System.currentTimeMillis();
        System.out.println("时间: " + time);
        CreateFileUtil.createJsonFile(json, "/Users/ywj/chapterFile", "chapter2_" + time);
    }

    /**
     * 生成加法
     */
    @Test
    public void test103() {
        QuestionBean questionBean = new QuestionBean();
        questionBean.setType(1);

        //TODO 题目所属课程
        questionBean.setLessonId(60);

        List<String> questionList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            int[] ints = RandomUtils.randomArray(444, 999, 1);
            int[] ints1 = RandomUtils.randomArray(333, 888, 1);
            if (ints == null || ints1 == null)
                continue;
            int max = ints[0], min = ints1[0];
            String str = max + "#" + min;
            if (questionList.contains(str)) {
                i--;
                continue;
            } else {
                questionList.add(str);
            }
            questionBean.setNumberOne(max);
            questionBean.setNumberTwo(min);
            questionBean.setTips(generateTips(max, min, 1));
            questionBean.setResult(max + min);
            userService.insertQuestionBean(questionBean);
        }
    }

    /**
     * 生成减法
     */
    @Test
    public void test101() {
        QuestionBean questionBean = new QuestionBean();
        questionBean.setType(2);

        //TODO 题目所属课程
        questionBean.setLessonId(72);

        List<String> questionList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            int[] ints = RandomUtils.randomArray(1000, 8888, 1);
            int[] ints1 = RandomUtils.randomArray(1000, 8888, 1);
            if (ints == null || ints1 == null)
                continue;
            int max = ints[0], min = ints1[0];
            if (max < min) {
                int temp = max;
                max = min;
                min = temp;
            }
            String str = max + "#" + min;
            if (questionList.contains(str)) {
                i--;
                continue;
            } else {
                questionList.add(str);
            }
            questionBean.setNumberOne(max);
            questionBean.setNumberTwo(min);
            questionBean.setTips(generateTips(max, min, 2));
            questionBean.setResult(max - min);
            userService.insertQuestionBean(questionBean);
        }
    }

    /**
     * 生成乘法
     */
    @Test
    public void test102() {
        QuestionBean questionBean = new QuestionBean();
        questionBean.setType(3);
        //TODO 题目所属课程
        questionBean.setLessonId(84);

        List<String> questionList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            int[] ints = RandomUtils.randomArray(666, 999, 1);
            int[] ints1 = RandomUtils.randomArray(333, 666, 1);
            if (ints == null || ints1 == null)
                continue;
            int max = ints[0], min = ints1[0];
            String str = max + "#" + min;
            if (questionList.contains(str)) {
                i--;
                continue;
            } else {
                questionList.add(str);
            }
            questionBean.setNumberOne(max);
            questionBean.setNumberTwo(min);
            questionBean.setResult(max * min);
            questionBean.setTips(generateTips(max, min, 3));
            userService.insertQuestionBean(questionBean);
        }
    }

    /**
     * 生成除法
     */
    @Test
    public void test106() {
        QuestionBean questionBean = new QuestionBean();
        questionBean.setType(4);

        //TODO 题目所属课程
        questionBean.setLessonId(96);

        List<String> questionList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            int[] ints = RandomUtils.randomArray(6666, 9999, 1);
            int[] ints1 = RandomUtils.randomArray(33, 99, 1);
            if (ints == null || ints1 == null)
                continue;
            int max = ints[0], min = ints1[0];
            if (max < min) {
                int temp = max;
                max = min;
                min = temp;
            }
            String str = max + "#" + min;
            if (questionList.contains(str)) {
                i--;
                continue;
            } else {
                questionList.add(str);
            }
            questionBean.setNumberOne(max);
            questionBean.setNumberTwo(min);
            questionBean.setTips(generateTips(max, min, 4));
            questionBean.setResult(max / min);
            userService.insertQuestionBean(questionBean);
        }
    }

    @Test
    public void test1009() {
        List<QuestionBean> questionList = questionMapper.getQuestionList(37);
        for (QuestionBean questionBean : questionList) {
            String tips = generateTips(questionBean.getNumberOne(), questionBean.getNumberTwo(), 4);
            questionMapper.updateTips(tips, questionBean.getId());
        }
    }

    @Test
    public void test1010() {
        String s = generateTips(126, 59, 2);
        System.out.println(s);
    }


    private String generateTips(int one, int two, int type) {
        String tips = "";
        List<Integer> list = new ArrayList<>();
        switch (type) {
            case 1:
                int result = one + two;
                char[] charA = String.valueOf(one).toCharArray();
                char[] charB = String.valueOf(two).toCharArray();
                int a = charA.length;
                int b = charB.length;
                if (a == 1 && b == 1) {
                    return one + "+" + two + "=" + result;
                }
                int max = a > b ? a : b;
                int k = 1;
                for (int i = 0; i < max; i++) {
                    int tipA = 0;
                    int tipB = 0;
                    if (i < charA.length) {
                        tipA = get1(one, i) * k;
                    }
                    if (i < charB.length) {
                        tipB = get1(two, i) * k;
                    }
                    int c = tipA + tipB;
                    list.add(c);
                    k = k * 10;
                    tips = tips + tipA + "+" + tipB + "=" + c + ",";
                }
                String d = "";
                for (int i = 0; i < list.size(); i++) {
                    d = d + list.get(i);
                    if (i != list.size() - 1) {
                        d = d + "+";
                    }
                }
                tips = tips + d + "=" + result + ",";
                tips = tips + one + "+" + two + "=" + result;
                break;
            case 2:
                int result_2 = one - two;
                char[] charA_2 = String.valueOf(one).toCharArray();
                char[] charB_2 = String.valueOf(two).toCharArray();
                int a_2 = charA_2.length;
                int b_2 = charB_2.length;
                if (a_2 == 1 && b_2 == 1) {
                    return one + "-" + two + "=" + result_2;
                }
                int m = 0;
                int k_2 = 1;
                for (int i = 0; i < charA_2.length; i++) {
                    int i1 = get1(one, i) - m;
                    int i2 = 0;
                    if (i < charB_2.length) {
                        i2 = get1(two, i);
                    }
                    if (i1 < i2) {
                        i1 = i1 + 10;
                        m = 1;
                    }
                    int k1 = i1 * k_2;
                    int k2 = i2 * k_2;
                    int res = k1 - k2;
                    list.add(res);
                    tips = tips + k1 + "-" + k2 + "=" + res + ",";
                    k_2 = k_2 * 10;
                }
                String d_2 = "";
                for (int i = 0; i < list.size(); i++) {
                    d_2 = d_2 + list.get(i);
                    if (i != list.size() - 1) {
                        d_2 = d_2 + "+";
                    }
                }
                tips = tips + d_2 + "=" + result_2 + ",";
                tips = tips + one + "-" + two + "=" + result_2;
                break;
            case 3:
                int result_3 = one * two;
                char[] charA_3 = String.valueOf(one).toCharArray();
                char[] charB_3 = String.valueOf(two).toCharArray();
                int a_3 = charA_3.length;
                int b_3 = charB_3.length;
                if (a_3 == 1 && b_3 == 1) {
                    return one + "x" + two + "=" + result_3;
                }
                int k_3 = 1;
                for (int i = 0; i < charB_3.length; i++) {
                    int i1 = get1(two, i) * k_3;
                    int res = i1 * one;
                    list.add(res);
                    tips = tips + one + "x" + i1 + "=" + res + ",";
                    k_3 = k_3 * 10;
                }
                String d_3 = "";
                for (int i = 0; i < list.size(); i++) {
                    d_3 = d_3 + list.get(i);
                    if (i != list.size() - 1) {
                        d_3 = d_3 + "+";
                    }
                }
                tips = tips + d_3 + "=" + result_3 + ",";
                tips = tips + one + "x" + two + "=" + result_3;
                break;
            case 4:
                tips = userService.test1(one, two);
                break;
        }
        return tips;
    }

    @Test
    public void test1099() {
        try {
            int a = 1 / 0;
        } catch (Exception e) {
            System.out.println("呀呀呀呀");
        }
        System.out.println("哈哈哈哈");
    }

    @Test
    public void test99() {
        QuestionInfo questionBean = userService.getQuestionBean(9346, 12);
        System.out.println(questionBean);
    }

    @Test
    public void test10011() {
        System.out.println(getMac(1, 1000000000));
    }

    private int getMac(int x, int y) {
        if (x >= y) {
            return x - y;
        }
        int temp = y;
        if (temp % 2 == 0) {
            temp = temp / 2;
        } else {
            temp = temp + 1;
        }
        return 1 + getMac(x, temp);
    }

    @Test
    public void test998() {
        String str = "abcde";
        char[] chars = str.toCharArray();
        int length = chars.length;
        List<Character> list = new ArrayList<>();
        for (char i : chars) {
            list.add(i);
        }
        int total = getJieChen(length);
        Set<String> set = new HashSet<>();
        Set<List<Character>> set2 = new HashSet<>();
        int count = 0;
        while (count < total * 5) {
            if (set.size() == total) {
                break;
            }
            Collections.shuffle(list, new Random(count));
            boolean add = set2.add(list);
            if (add) {
                String sp = "";
                for (char s : list) {
                    sp = sp + s;
                }
                set.add(sp);
            }
            count++;
        }
        System.out.println("set total:" + set.size() + " count:" + count);
        for (String input : set) {
            System.out.println(input);
        }
    }

    private int getJieChen(int i) {
        return i == 1 ? 1 : i * getJieChen(i - 1);
    }

    @Test
    public void test234() {
        String str = "美食，旅行,健身,汽车";
        try {
            String[] split = str.split(",");
            System.out.println(split);
        } catch (Exception e) {
            System.out.println("false");
            return;
        }
        System.out.println("true");
    }

    @Test
    public void test1007() {
        String str = "abc";
        List<Character> list = new ArrayList<>();
        list.add('a');
        list.add('b');
        list.add('c');
        System.out.println(list.toString());
    }

    @Test
    public void test1008() {
        System.out.println(getAcs(13, 16));
    }

    private String getAcs(int a, int b) {
        boolean isA = a > b;
        List<String> list = new ArrayList<>();
        if (isA) {
            if (b * 2 + 2 < a) {
                System.out.println("无");
            }
            for (int i = 0; i < b; i++) {
                list.add("a");
                list.add("b");
            }

            int index = 1;
            for (int j = 0; j < a - b; j++) {
                index = index > list.size() - 1 ? list.size() : index;
                list.add(index, "a");
                index = index + 3;
            }
        } else {
            if (a * 2 + 2 < b) {
                System.out.println("无");
            }
            for (int i = 0; i < a; i++) {
                list.add("b");
                list.add("a");
            }
            int index = 1;
            for (int j = 0; j < b - a; j++) {
                index = index > list.size() - 1 ? list.size() : index;
                list.add(index, "b");
                index = index + 3;
            }
        }
        return getNude(list);
    }

    private String getNude(List<String> list) {
        String s = "";
        for (String s1 : list) {
            s = s + s1;
        }
        return s;
    }

    @Test
    public void test105() {
        int a = 4;
        int b = 6;
        a = a ^ b;
        b = a ^ b;
        a = a ^ b;

        System.out.println("a=" + a + " b=" + b);
    }

    @Test
    public void test1100() {
        //和为n的正数序列
        int number = 1000;

        int total = 2;

        List<String> list = new ArrayList<>();
        testxx(number, total, list);
        System.out.println(list);
    }

    @Test
    public void test1101() {
        String str = "abc";
        char[] chars = str.toCharArray();

        Map<Integer, Map<String, Integer>> map = new HashMap<>();
        int length = str.length();
        for (int i = 0; i < length; i++) {
            Map<String, Integer> childMap = new HashMap<>();
            for (int j = 0; j < length; j++) {
                childMap.put(String.valueOf(chars[j]), length - 1);
            }
            map.put(i, childMap);
        }

        int total = getJieChen(length);

        for (int i = 0; i < total; i++) {
            String word = "";
            for (int j = 0; j < length; j++) {
                Map<String, Integer> map1 = map.get(j);
                if (map1 == null)
                    break;
            }
        }

        System.out.println(map);
    }


    /**
     * 两个字符串之间的最长子串
     */
    @Test
    public void test1102() {
        String str2 = "abcedvdleedlvsfsd";
        String str1 = "kcedmdleedgrcedvdleedlvsfwew";
        char[] chars1 = str1.toCharArray();
        char[] chars2 = str2.toCharArray();

        int length1 = str1.length();
        int length2 = str2.length();
        String temp = "";
        String tempNext = "";
        for (int i = 0; i < length1; i++) {
            for (int j = 0; j < length2; j++) {
                if (chars1[i] == chars2[j]) {
                    String substring = str2.substring(j - tempNext.length(), j);
                    if (substring.equals(tempNext)) {
                        tempNext = tempNext + String.valueOf(chars1[i]);
                        if (i >= length1) {
                            break;
                        }
                        i++;
                    }
                }
            }
            if (tempNext.length() > temp.length()) {
                temp = tempNext;
                tempNext = "";
            }
        }
        System.out.println(temp);
    }

    @Test
    public void test1103() {
        int[] a = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 13, 15, 18, 21};
        List<Integer> list = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] % 2 != 0) {
                if (index > list.size()) {
                    list.add(a[i]);
                } else {
                    list.add(index, a[i]);
                }
                index++;
            } else {
                list.add(a[i]);
            }
        }
        System.out.println(list);
    }

    @Test
    public void test1104() {
        int[] a = new int[]{1, 1, 2, 2, 3, 3, 4, 5, 5};
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < a.length; i++) {
            Integer num = map.get(a[i]);
            if (num == null)
                map.put(a[i], 1);
            else {
                map.remove(a[i]);
            }
        }
        for (Map.Entry entry : map.entrySet()) {
            System.out.println(entry.getKey());
            break;
        }
    }


    @Test
    public void test1Quest() {
        int num = 100;
        for (int i = 0; i < num; i++) {
            long startTime = System.currentTimeMillis();
            String s = sendGet();
            System.out.println(s);
            System.out.println("第" + i + "次请求，时间:" + (System.currentTimeMillis() - startTime) / 1000d);
            System.out.println();
        }
    }

    /**
     * 向指定URL发送GET方法的请求
     * *            发送请求的URL
     * 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     *
     * @return URL 所代表远程资源的响应结果
     */
    public String sendGet() {
        //https://baobab.kaiyanapp.com/api/v5/index/tab/list
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = "https://baobab.kaiyanapp.com/api/v5/index/tab/list";
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段

            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    @Test
    public void test1105() {
        int[] a = new int[]{1, 2, 2, 4, 1, 4, 1, 5, 0, 4, 5, 4, 6, 4};

        test1106(a, 0, a.length - 1);
        int j = 1;
        int temp = 0;
        int total = 0;
        int num = 0;
        for (int i = 0; i < a.length; i++) {
            if (i >= a.length || j >= a.length) {
                break;
            }
            if (a[i] == a[j]) {
                j++;
                total++;
                i--;
            } else {
                if (total > temp) {
                    temp = total;
                    num = a[i];
                }
                i = j - 1;
                j = i + 2;
                total = 0;
            }
        }
        if (temp > 1) {
            temp = temp + 1;
        }
        System.out.println(temp + "次" + num);
    }

    @Test
    public void test1107() {
        int[] a = new int[]{1, -3, 2, -1, 1, -2, 1, 5, -1, 4, -6, 4, -5, 4};

        int maxSum = 0;
        int lastSum = 0;
        for (int i = 1; i <= a.length; i++) {
            int sum = 0;
            for (int j = 0; j <= a.length - i; j++) {
                int _i = i;
                while (_i > 0) {
                    sum = sum + a[j + _i - 1];
                    _i--;
                }
                if (sum > maxSum) {
                    maxSum = sum;
                }
                sum = 0;
            }
            if (maxSum > lastSum) {
                lastSum = maxSum;
                System.out.println("取到 " + i + " 个滑动窗口");
                //时间复杂度，O(nlogn)
            }
        }
        Integer k = 0;
        System.out.println(lastSum);
    }

    private void test1106(int[] a, int low, int height) {
        int i = low;
        int j = height;
        if (low < height) {
            while (low < height) {
                while (low < height && a[low] <= a[height]) {
                    height--;
                }
                int temp = a[low];
                a[low] = a[height];
                a[height] = temp;

                while (low < height && a[low] <= a[height]) {
                    low++;
                }
                temp = a[low];
                a[low] = a[height];
                a[height] = temp;
            }
            test1106(a, i, low - 1);
            test1106(a, low + 1, j);
        }
    }

    private void testxx(int number, int total, List<String> list) {
        int maxI = number / total;
        if (total > number / 2 + 1) {
            return;
        }
        for (int i = 1; i <= maxI; i++) {
            if (i + total - 1 > number) {
                continue;
            }
            int temp = (total * (total - 1)) / 2;
            if ((i * total + temp) == number) {
                int wei = i + total - 1;
                list.add(i + "-" + wei);
            }
        }
        testxx(number, ++total, list);
    }


    private int get1(int number, int i) {
        if (i == 0) {
            return number % 10;
        } else if (i == 1) {
            return number / 10 % 10;
        } else if (i == 2) {
            return number / 100 % 10;
        } else if (i == 3) {
            return number / 1000 % 10;
        }
        return 0;
    }
}
