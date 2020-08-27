package com.example.demo.service.impl;

import com.example.demo.core.project.*;
import com.example.demo.dao.mapper.ChapterMapper;
import com.example.demo.dao.mapper.LessonMapper;
import com.example.demo.dao.mapper.QuestionMapper;
import com.example.demo.dao.mapper.UserMapper;
import com.example.demo.domain.bean.User;
import com.example.demo.service.UserService;
import com.example.demo.service.utils.RandomUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
public class UserServiceImpl implements UserService {

    @Autowired(required = false)
    private UserMapper userMapper;

    @Autowired(required = false)
    private ChapterMapper chapterMapper;

    @Autowired(required = false)
    private LessonMapper lessonMapper;

    @Autowired(required = false)
    private QuestionMapper questionMapper;

    @Override
    @Transactional
    public User get(int id) {
        return userMapper.get(id);
    }

    @Override
    @Transactional
    public void insert(String name, int age) {
        User user = new User(name, age);
        userMapper.insert(user);
    }

    @Override
    public User getUserById(int name) {
        System.out.println("3333");
        return userMapper.get(name);
    }

    @Override
    public ChapterListInfo getChapterListInfo() {
        ChapterListInfo chapterListInfo = new ChapterListInfo();
        List<ChapterBean> chapterList = chapterMapper.getAll();
        for (ChapterBean chapterBean : chapterList) {
            //课程
            List<LessonBean> lessonList = lessonMapper.getLessonList(chapterBean.getId());

            for (LessonBean lessonBean : lessonList) {
                List<QuestionBean> questionList = questionMapper.getQuestionList(lessonBean.getId());
                List<QuestionInfo> questionInfoList = new ArrayList<>();

                for (QuestionBean questionBean : questionList) {
                    QuestionInfo questionInfo = new QuestionInfo();
                    questionInfo.setId(questionBean.getId());
                    questionInfo.setLessonId(questionBean.getLessonId());
                    questionInfo.setType(OperatorType.valueOf(questionBean.getType()));
                    questionInfo.setTips(StringUtils.replace(questionBean.getTips(), ",", "\n"));

                    setQuestionInfo(questionInfo, questionBean);

                    questionInfoList.add(questionInfo);
                }
                lessonBean.setQuestionList(questionInfoList);
            }
            chapterBean.setLessonList(lessonList);
        }
        chapterListInfo.setChapterList(chapterList);
        return chapterListInfo;
    }

    @Override
    public ChapterListInfo getChapterListInfoOther() {
        ChapterListInfo chapterListInfo = new ChapterListInfo();
        List<ChapterBean> chapterList = new ArrayList<>();
        chapterListInfo.setChapterList(chapterList);

        ChapterBean chapterBean1 = new ChapterBean();
        chapterBean1.setId(1);
        chapterBean1.setTitle("算数-1");
        chapterBean1.setLessonList(getOtherLessonBeanList(1));
        chapterList.add(chapterBean1);


        ChapterBean chapterBean2 = new ChapterBean();
        chapterBean2.setId(2);
        chapterBean2.setTitle("算数-2");
        chapterBean2.setLessonList(getOtherLessonBeanList(2));
        chapterList.add(chapterBean2);

        return chapterListInfo;
    }

    private List<LessonBean> getOtherLessonBeanList(int chapterId) {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "一");
        map.put(2, "二");
        map.put(3, "三");
        map.put(4, "四");
        map.put(5, "五");
        map.put(6, "六");
        map.put(7, "七");
        map.put(8, "八");
        List<LessonBean> list = new ArrayList<>();
        for (int lessonCount = 1; lessonCount <= 8; lessonCount++) {
            LessonBean lessonBean = new LessonBean();
            lessonBean.setId(lessonCount);
            lessonBean.setTitle("第" + map.get(lessonCount) + "课程");
            if (lessonCount != 1) {
                lessonBean.setLocked(1);
            }
            lessonBean.setChapterId(chapterId);
            List<QuestionInfo> questionList = generateOtherQuestionInfo(lessonCount, chapterId);
            lessonBean.setQuestionList(questionList);
            list.add(lessonBean);
        }
        return list;
    }

    private List<QuestionInfo> generateOtherQuestionInfo(int lessonCount, int chapterId) {
        List<QuestionInfo> list = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            QuestionInfo questionInfo = new QuestionInfo();
            questionInfo.setId(i);
            questionInfo.setLessonId(lessonCount);

            generateTitleAndOther(questionInfo, chapterId);

            list.add(questionInfo);
        }
        return list;
    }

    private void generateTitleAndOther(QuestionInfo questionInfo, int chapterId) {
        OperatorType type = OperatorType.valueOf(RandomUtils.randomArray(1, 3, 1)[0]);
        switch (type) {
            case plus:
                int[] ints = new int[2];
                if (chapterId == 1) {
                    ints = RandomUtils.randomArray(1, 10, 2);
                } else {
                    ints = RandomUtils.randomArray(2, 49, 2);
                }

                if (ints == null || ints.length < 2)
                    return;
                questionInfo.setTitle(ints[0] + "+" + ints[1] + "=");
                int result = ints[0] + ints[1];
                List<Integer> selectAnswerList = new ArrayList<>();
                selectAnswerList.add(result);
                //混入3个错误答案
                int[] intsC = new int[3];
                if (chapterId == 1) {
                    intsC = getArrayC(3, 8, result);
                } else {
                    intsC = getArrayC(6, 99, result);
                }

                selectAnswerList.add(intsC[0]);
                selectAnswerList.add(intsC[1]);
                selectAnswerList.add(intsC[2]);

                Collections.shuffle(selectAnswerList, new Random(System.currentTimeMillis() * chapterId * questionInfo.getId()));
                int index = selectAnswerList.indexOf(result);
                questionInfo.setSelectAnswerList(selectAnswerList);
                questionInfo.setRightIndex(index);
                break;
            case subtract:
                int[] intsS = new int[2];
                if (chapterId == 1) {
                    intsS = RandomUtils.randomArray(1, 10, 2);
                } else {
                    intsS = RandomUtils.randomArray(2, 49, 2);
                }
                if (intsS == null || intsS.length < 2)
                    return;
                int max = intsS[0], min = intsS[1];
                if (max < min) {
                    int temp = max;
                    max = min;
                    min = temp;
                }
                questionInfo.setTitle(max + "-" + min + "=");
                int resultS = max - min;
                List<Integer> selectAnswerListS = new ArrayList<>();
                selectAnswerListS.add(resultS);
                //混入3个错误答案
                int[] intsCS = new int[3];
                if (chapterId == 1) {
                    intsCS = getArrayC(2, 9, resultS);
                } else {
                    intsCS = getArrayC(6, 48, resultS);
                }
                selectAnswerListS.add(intsCS[0]);
                selectAnswerListS.add(intsCS[1]);
                selectAnswerListS.add(intsCS[2]);

                Collections.shuffle(selectAnswerListS, new Random(System.currentTimeMillis() * chapterId * questionInfo.getId()));
                int indexS = selectAnswerListS.indexOf(resultS);
                questionInfo.setSelectAnswerList(selectAnswerListS);
                questionInfo.setRightIndex(indexS);
                break;
            case multiply:
                int[] intsM = new int[2];
                if (chapterId == 1) {
                    intsM = RandomUtils.randomArray(2, 9, 2);
                } else {
                    intsM = RandomUtils.randomArray(10, 50, 2);
                }
                if (intsM == null || intsM.length < 2)
                    return;
                questionInfo.setTitle(intsM[0] + "×" + intsM[1] + "=");
                int resultM = intsM[0] * intsM[1];
                List<Integer> selectAnswerListM = new ArrayList<>();
                selectAnswerListM.add(resultM);
                //混入3个错误答案
                int[] intsCM = new int[3];
                if (chapterId == 1) {
                    intsCM = getArrayC(6, 80, resultM);
                } else {
                    intsCM = getArrayC(200, 1500, resultM);
                }
                selectAnswerListM.add(intsCM[0]);
                selectAnswerListM.add(intsCM[1]);
                selectAnswerListM.add(intsCM[2]);

                Collections.shuffle(selectAnswerListM, new Random(System.currentTimeMillis() * chapterId * questionInfo.getId()));
                int indexM = selectAnswerListM.indexOf(resultM);
                questionInfo.setSelectAnswerList(selectAnswerListM);
                questionInfo.setRightIndex(indexM);
                break;
        }
    }

    private int[] getArrayC(int min, int max, int result) {
        int[] intsC = RandomUtils.randomArray(min, max, 3);
        if (intsC[0] == result || intsC[1] == result || intsC[2] == result)
            return getArrayC(min, max, result);
        return intsC;
    }

    @Override
    public void insertQuestionBean(QuestionBean questionBean) {
        questionMapper.insert(questionBean);
    }

    private void setQuestionInfo(QuestionInfo questionInfo, QuestionBean questionBean) {
        char[] oneChars = String.valueOf(questionBean.getNumberOne()).toCharArray();
        char[] twoChars = String.valueOf(questionBean.getNumberTwo()).toCharArray();
        char[] resultChars = String.valueOf(questionBean.getResult()).toCharArray();

        int oneLength = oneChars.length;
        int twoLength = twoChars.length + 1;
        int resultLength = resultChars.length;
        int maxLength = 0;
        if (oneLength > twoLength) {
            maxLength = oneLength;
        } else {
            maxLength = twoLength;
        }
        if (maxLength < resultLength) {
            maxLength = resultLength;
        }
        List<List<String>> questionItems = new ArrayList<>();
        List<List<String>> resultItems = new ArrayList<>();
        switch (questionInfo.getType()) {
            case plus:
            case subtract:
                // 加法,减法
                String op = "";
                if (questionInfo.getType() == OperatorType.plus) {
                    op = "+";
                } else {
                    op = "-";
                }
                // 题目区域
                questionItems.add(getStringList(maxLength, oneLength, String.valueOf(questionBean.getNumberOne()), ""));
                questionItems.add(getStringList(maxLength, twoLength, String.valueOf(questionBean.getNumberTwo()), op));
                questionInfo.setQuestionItems(questionItems);
                // 无草稿区域
                // 结果区域
                resultItems.add(getStringList(maxLength, resultLength, String.valueOf(questionBean.getResult()), ""));
                questionInfo.setResultItems(resultItems);
                break;
            case multiply:
                // 乘法
                questionItems.add(getStringList(maxLength, oneLength, String.valueOf(questionBean.getNumberOne()), ""));
                questionItems.add(getStringList(maxLength, twoLength, String.valueOf(questionBean.getNumberTwo()), "x"));
                questionInfo.setQuestionItems(questionItems);
                // 草稿区域
                setDraftItems(questionInfo, maxLength, questionBean.getNumberOne(), questionBean.getNumberTwo());
                // 结果区域
                resultItems.add(getStringList(maxLength, resultLength, String.valueOf(questionBean.getResult()), ""));
                questionInfo.setResultItems(resultItems);
                break;
            case division:
                // 除法
                questionItems.add(getStringList(oneLength, oneLength, String.valueOf(questionBean.getNumberOne()), ""));
                questionItems.add(getStringList(twoLength, twoLength, String.valueOf(questionBean.getNumberTwo()), ""));
                questionInfo.setQuestionItems(questionItems);

                resultItems.add(getStringList(oneLength, resultLength, String.valueOf(questionBean.getResult()), ""));
                questionInfo.setResultItems(resultItems);

                test1(questionInfo, questionBean.getNumberOne(), questionBean.getNumberTwo(), questionBean.getResult());
                break;
            default:
        }
    }

    @Override
    public QuestionInfo getQuestionBean(int numberOne, int numberTwo) {

        QuestionBean questionBean = new QuestionBean();
        questionBean.setId(1);
        questionBean.setLessonId(1);
        questionBean.setTips("");
        questionBean.setType(4);

        questionBean.setNumberOne(numberOne);
        questionBean.setNumberTwo(numberTwo);

        int result = numberOne / numberTwo;
        questionBean.setResult(result);

        QuestionInfo questionInfo = new QuestionInfo();
        questionInfo.setId(1);
        questionInfo.setType(OperatorType.valueOf(4));
        questionInfo.setTips("");
        String str = String.valueOf(result);

        List<List<String>> list = new ArrayList<>();
        list.add(getStringList(str.length(), str.length(), str, ""));
        questionInfo.setResultItems(list);

        setQuestionInfo(questionInfo, questionBean);
        return questionInfo;
    }

    private void test1(QuestionInfo questionInfo, int numberOne, int numberTwo, int result) {
        int oneLength = String.valueOf(numberOne).length();
        int resultLength = String.valueOf(result).length();

        char[] charsOne = String.valueOf(numberOne).toCharArray();
        char[] charsResult = String.valueOf(result).toCharArray();

        int start = 0;
        int end = 0;
        int yu = 0;
        int fuhao = 0;

        List<List<String>> list = new ArrayList<>();
        for (int i = 0; i < resultLength; i++) {
            if (i == 0) {
                //TODO i2加入草稿,注意填充#
                int i1 = Integer.valueOf(charsResult[i] + "");
                int i2 = i1 * numberTwo;
                list.add(getDrf(i2, String.valueOf(i2).length(), oneLength));

                int lengthI2 = String.valueOf(i2).length();
                end = start + lengthI2 - 1;
                int i3 = test3(charsOne, start, end);

                if (i3 < i2 && end < oneLength - 1) {
                    i3 = test3(charsOne, start, end + 1);
                    int i4 = i3 - i2;
                    if (i + 1 == resultLength) {
                        //TODO i4是余数，加入结果区
                        questionInfo.getResultItems().add(getYu(i4, oneLength));
                        break;
                    } else {
                        yu = i4;
                    }
                    end = end + 1;
                } else {
                    int i4 = i3 - i2;
                    if (end >= oneLength - 1) {
                        //TODO i4加入结果区
                        questionInfo.getResultItems().add(getYu(i4, oneLength));
                        break;
                    } else {
                        yu = i4;
                    }
                }
                //
                if (yu == 0) {
                    fuhao = String.valueOf(i2).length();
                } else {
                    fuhao = String.valueOf(i3).length() - String.valueOf(yu).length();
                }
            } else {
                end++;
                if (yu == 0) {
                    yu = Integer.valueOf(charsOne[end] + "");
                } else {
                    yu = Integer.valueOf(String.valueOf(yu) + charsOne[end] + "");
                }

                if (yu < numberTwo && end >= charsOne.length - 1) {
                    //TODO yu加入结果区
                    questionInfo.getResultItems().add(getYu(yu, oneLength));
                    break;
                }

                //结果中有0
                Integer results = Integer.valueOf(charsResult[i] + "");
                if (results == 0) {
                    if (yu == 0) {
                        yu = Integer.valueOf(charsOne[end] + "");
                    } else {
                        yu = Integer.valueOf(String.valueOf(yu) + charsOne[end] + "");
                    }
                    end++;
                    continue;
                }

                //TODO i6加入草稿
                int i6 = Integer.valueOf(yu);
                int lengthI6 = String.valueOf(i6).length();
                String numberStrI6 = String.valueOf(i6);
                //
                //getStringList(fuhao + lengthI6, lengthI6, numberStrI6, "")
                list.add(getDrf(i6, fuhao + lengthI6, oneLength));

                //TODO i5加入草稿
                int i5 = results * numberTwo;
                int lengthI5 = String.valueOf(i5).length();
                String numberStrI5 = String.valueOf(i5);
                //getStringList(fuhao + lengthI6, lengthI5, numberStrI5, "")
                list.add(getDrf(i5, fuhao + lengthI5, oneLength));

                yu = i6 - i5;
                fuhao = fuhao + lengthI6 - String.valueOf(yu).length();
                if (yu < numberTwo && end >= charsOne.length - 1) {
                    //TODO yu与加入结果集
                    questionInfo.getResultItems().add(getYu(yu, oneLength));
                    break;
                }
            }
        }
        questionInfo.setDraftItems(list);
    }

    private List<String> getYu(int i4, int oneLength) {
        String numberStr = String.valueOf(i4);
        int le = numberStr.length();
        return getStringList(oneLength, le, numberStr, "");
    }

    private List<String> getDrf(int i4, int maxLength, int oneLength) {
        String numberStr = String.valueOf(i4);
        int le = numberStr.length();
        List<String> stringList = getStringList(maxLength, le, numberStr, "");
        int i = oneLength - maxLength;
        while (i > 0) {
            stringList.add("#");
            i--;
        }
        return stringList;
    }

    private int test3(char[] chars, int start, int end) {
        if (start > end || end >= chars.length)
            return 0;
        String str = "";
        for (int i = start; i <= end; i++) {
            str = str + chars[i];
        }
        return Integer.valueOf(str);
    }

    private void setDraftItems(QuestionInfo questionInfo, int maxLength, int numberOne, int numberTwo) {
        int index = 0;
        int[] a = new int[4];
        if (numberTwo >= 10 && numberTwo < 100) {
            index = 2;
            a[0] = numberTwo % 10;
            a[1] = numberTwo / 10;
        } else if (numberTwo >= 100 && numberTwo < 1000) {
            index = 3;
            a[0] = numberTwo % 10;
            a[1] = numberTwo / 10 % 10;
            a[2] = numberTwo / 100;
        } else if (numberTwo >= 1000 && numberTwo < 10000) {
            index = 4;
            a[0] = numberTwo % 10;
            a[1] = numberTwo / 10 % 10;
            a[2] = numberTwo / 100 % 10;
            a[3] = numberTwo / 1000;
        } else {
            // 大于10000乘法不统计，小于10没有草稿区域
            return;
        }
        List<List<String>> draftItems = new ArrayList<>();
        int k = 1;
        for (int i = 0; i < index; i++) {
            int draft = a[i] * k * numberOne;
            String str = String.valueOf(draft);
            draftItems.add(getStringList(maxLength, str.length(), str, ""));
            k = k * 10;
        }
        questionInfo.setDraftItems(draftItems);
    }

    private List<String> getStringList(int maxLength, int length, String numberStr, String op) {
        if (StringUtils.isNotEmpty(op)) {
            numberStr = op + numberStr;
        }
        if (length < maxLength) {
            int n = maxLength - length;
            String spi = "";
            for (int i = 0; i < n; i++) {
                spi = spi + "#";
            }
            numberStr = spi + numberStr;
        }
        char[] chars = numberStr.toCharArray();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < chars.length; i++) {
            list.add(String.valueOf(chars[i]));
        }
        return list;
    }

    @Override
    public String test1(int numberOne, int numberTwo) {

        List<String> tips = new ArrayList<>();
        int result = numberOne / numberTwo;

        int oneLength = String.valueOf(numberOne).length();
        int resultLength = String.valueOf(result).length();

        char[] charsOne = String.valueOf(numberOne).toCharArray();
        char[] charsResult = String.valueOf(result).toCharArray();

        int start = 0;
        int end = 0;
        int yu = 0;
        int fuhao = 0;

        List<Integer> listDf = new ArrayList<>();
        for (int i = 0; i < resultLength; i++) {
            if (i == 0) {
                //TODO i2加入草稿,注意填充#
                int i1 = Integer.valueOf(charsResult[i] + "");
                int i2 = i1 * numberTwo;

                int lengthI2 = String.valueOf(i2).length();
                end = start + lengthI2 - 1;
                int i3 = test3(charsOne, start, end);

                if (i3 < i2 && end < oneLength - 1) {
                    i3 = test3(charsOne, start, end + 1);
                    listDf.add(i3);
                    listDf.add(i2);
                    int i4 = i3 - i2;
                    if (i + 1 == resultLength) {
                        //TODO i4是余数，加入结果区
                        yu = i4;
                        break;
                    } else {
                        yu = i4;
                    }
                    end = end + 1;
                } else {
                    listDf.add(i3);
                    listDf.add(i2);
                    int i4 = i3 - i2;
                    if (end >= oneLength - 1) {
                        //TODO i4加入结果区
                        yu = i4;
                        break;
                    } else {
                        yu = i4;
                    }
                }
                if (yu == 0) {
                    fuhao = String.valueOf(i2).length();
                } else {
                    fuhao = String.valueOf(i3).length() - String.valueOf(yu).length();
                }
            } else {
                end++;
                if (yu == 0) {
                    yu = Integer.valueOf(charsOne[end] + "");
                } else {
                    yu = Integer.valueOf(String.valueOf(yu) + charsOne[end] + "");
                }

                if (yu < numberTwo && end >= charsOne.length - 1) {
                    //TODO yu加入结果区
                    break;
                }

                //结果中有0
                Integer results = Integer.valueOf(charsResult[i] + "");
                if (results == 0) {
                    if (yu == 0) {
                        yu = Integer.valueOf(charsOne[end] + "");
                    } else {
                        yu = Integer.valueOf(String.valueOf(yu) + charsOne[end] + "");
                    }
                    end++;
                    continue;
                }

                //TODO i6加入草稿
                int i6 = Integer.valueOf(yu);
                int lengthI6 = String.valueOf(i6).length();
                listDf.add(i6);

                //TODO i5加入草稿
                int i5 = results * numberTwo;
                listDf.add(i5);

                yu = i6 - i5;
                fuhao = fuhao + lengthI6 - String.valueOf(yu).length();
                if (yu < numberTwo && end >= charsOne.length - 1) {
                    //TODO yu与加入结果集
                    break;
                }
            }
        }
        int size = listDf.size();
        if (size % 2 == 0) {
            for (int i = 0; i < size / 2; i++) {
                testTips(listDf, numberTwo, tips, i);
            }
        }
        tips.add(numberOne + "÷" + numberTwo + "=" + result + "余" + yu);
        return toString(tips);
    }

    private <T> String toString(List<T> list) {
        if (CollectionUtils.isEmpty(list))
            return "";
        return StringUtils.join(list.toArray(), ",");
    }

    private void testTips(List<Integer> listDf, int numberTwo, List<String> tips, int i) {
        int a0 = listDf.get(2 * i);
        int a1 = listDf.get(2 * i + 1);
        int results = a0 / numberTwo;
        int yu1 = a0 - results * numberTwo;
        tips.add(a0 + "÷" + numberTwo + "=" + results + "余" + yu1);
        tips.add(numberTwo + "x" + results + "=" + a1);
        tips.add(a0 + "-" + a1 + "=" + yu1);
    }
}
