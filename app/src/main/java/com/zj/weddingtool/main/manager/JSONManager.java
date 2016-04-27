package com.zj.weddingtool.main.manager;

import android.util.Log;

import com.google.gson.Gson;
import com.zj.weddingtool.base.config.BmobConfig;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by stonekity.shi on 2015/4/6.
 */
public class JSONManager {

    private static final String TAG = "JSONManager";


    /**
     * 去掉Json字符串首位多余的双引号
     *
     * @param json
     * @return
     */
    private static String formatJsonString(String json) {
        if (json == null || json.equals(""))
            return "";
        json = json.replace("\\", "");
        StringBuilder sb = new StringBuilder(json);
        sb.deleteCharAt(0);
        sb.deleteCharAt(sb.length() - 1);
        Log.d(TAG, "json after format: " + sb.toString());
        return sb.toString();
    }

    /**
     * 获得用户登录结果
     *
     * @param httpResponse
     * @return
     */
    public static HashMap<String, String> getLoginResult(String httpResponse) {
        Log.d(TAG, "httpResponse: " + httpResponse);
        HashMap<String, String> map = new HashMap<String, String>();
        try {
            if (null != httpResponse) {
                JSONObject jsonObject = new JSONObject(httpResponse);
                if (jsonObject.has("Status")) {
                    map.put("Status", jsonObject.getString("Status"));
                } else {
                    map.put("Status", "");
                }
                if (jsonObject.has("Message")) {
                    map.put("Message", jsonObject.getString("Message"));
                } else {
                    map.put("Message", "");
                }
            }
        } catch (Exception e) {
        }
        if (BmobConfig.DEBUG) {
            Log.i(TAG, "Login Response HashMap: " + map.toString());
        }
        return map;
    }


}


//---------------------------------------  手动解析  ------------------------------------------------


/*    *//**
 * 解析学生所有课程信息
 *
 * @param json
 * @return
 *//*
    private static StudentSchedule parseJsonStudentSchedule(String json) {
        StudentSchedule ss = new StudentSchedule();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = null;
            if (jsonObject.has("Title")) {
                ss.setTitle(jsonObject.getString("Title"));
            }
            if (jsonObject.has("Content")) {
                ss.setContent(jsonObject.getString("Content"));
            }
            if (jsonObject.has("IsTeacher")) {
                ss.setIsTeacher(jsonObject.getBoolean("IsTeacher"));
            }
            if (jsonObject.has("TimeScheduleList")) {
                String arrayJson = jsonObject.getString("TimeScheduleList");
                jsonArray = new JSONArray(arrayJson);
            }
            parseJsonSingleSchedule(ss, jsonArray);

            return ss;
        } catch (Exception e) {
            ToastUtils.showToast("数据异常1-1，请稍后再试");
            return null;
        }
    }

    *//**
 * 解析单条课表信息
 *
 * @param ss
 * @param jsonArray
 *//*
    private static void parseJsonSingleSchedule(StudentSchedule ss, JSONArray jsonArray) {
        if (ss == null || jsonArray == null || jsonArray.toString().equals(""))
            return;

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                SingleSchedule singleSchedule = new SingleSchedule();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.has("Classes")) {
                    singleSchedule.setClasses(jsonObject.getString("Classes"));
                }
                if (jsonObject.has("CurName")) {
                    singleSchedule.setCurName(jsonObject.getString("CurName"));
                }
                if (jsonObject.has("Day")) {
                    singleSchedule.setDay(jsonObject.getInt("Day"));
                }
                if (jsonObject.has("DayStr")) {
                    singleSchedule.setDayStr(jsonObject.getString("DayStr"));
                }
                if (jsonObject.has("DayTime")) {
                    singleSchedule.setDayTime(jsonObject.getInt("DayTime"));
                }
                if (jsonObject.has("DayTimeStr")) {
                    singleSchedule.setDayTimeStr(jsonObject.getString("DayTimeStr"));
                }
                if (jsonObject.has("Place")) {
                    singleSchedule.setPlace(jsonObject.getString("Place"));
                }
                if (jsonObject.has("TaskTimeType")) {
                    singleSchedule.setTaskTimeType(jsonObject.getInt("TaskTimeType"));
                }
                if (jsonObject.has("Teacher")) {
                    singleSchedule.setTeacher(jsonObject.getString("Teacher"));
                }
                if (jsonObject.has("Week")) {
                    singleSchedule.setWeek(jsonObject.getString("Week"));
                }
                ss.getTimeScheduleList().add(singleSchedule);
            }
        } catch (Exception e) {
            ToastUtils.showToast("数据异常1-2，请稍后再试");
            e.printStackTrace();
        }

    }


    *//**
 * 解析学生所有成绩信息
 *
 * @param json
 * @return
 *//*
    private static StudentGrade parseJsonStudentGrade(String json) {
        StudentGrade sg = new StudentGrade();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = null;
            if (jsonObject.has("AverageGradePoint")) {
                sg.setAverageGradePoint(jsonObject.getDouble("AverageGradePoint"));
            }
            if (jsonObject.has("TotalGradePoint")) {
                sg.setTotalGradePoint(jsonObject.getDouble("TotalGradePoint"));
            }
            if (jsonObject.has("Title")) {
                sg.setTitle(jsonObject.getString("IsTeacher"));
            }
            if (jsonObject.has("Name")) {
                sg.setName(jsonObject.getString("Name"));
            }
            if (jsonObject.has("IsViewGradeAfterEvaluating")) {
                sg.setIsViewGradeAfterEvaluating(jsonObject.getBoolean("IsViewGradeAfterEvaluating"));
            }
            if (jsonObject.has("StuGradeList")) {
                String arrayJson = jsonObject.getString("StuGradeList");
                jsonArray = new JSONArray(arrayJson);
            }
            parseJsonSingleGrade(sg, jsonArray);

            return sg;
        } catch (Exception e) {
            ToastUtils.showToast("数据异常2-1，请稍后再试");
            return null;
        }
    }


    *//**
 * 解析单条成绩信息
 *
 * @param sg
 * @param jsonArray
 *//*
    private static void parseJsonSingleGrade(StudentGrade sg, JSONArray jsonArray) {
        if (sg == null || jsonArray == null || jsonArray.toString().equals(""))
            return;

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                SingleGrade singleGrade = new SingleGrade();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.has("TaskNO")) {
                    singleGrade.setTaskNO(jsonObject.getString("TaskNO"));
                }
                if (jsonObject.has("CourseName")) {
                    singleGrade.setCourseName(jsonObject.getString("CourseName"));
                }
                if (jsonObject.has("CourseType")) {
                    singleGrade.setCourseType(jsonObject.getString("CourseType"));
                }
                if (jsonObject.has("CourseCredit")) {
                    singleGrade.setCourseCredit(jsonObject.getDouble("CourseCredit"));
                }
                if (jsonObject.has("Grade")) {
                    singleGrade.setGrade(jsonObject.getInt("Grade"));
                }
                if (jsonObject.has("GradePoint")) {
                    singleGrade.setGradePoint(jsonObject.getDouble("GradePoint"));
                }
                if (jsonObject.has("CanModifyScore")) {
                    singleGrade.setCanModifyScore(jsonObject.getInt("CanModifyScore"));
                }
                if (jsonObject.has("IsShowScore")) {
                    singleGrade.setIsShowScore(jsonObject.getBoolean("IsShowScore"));
                }
                if (jsonObject.has("HasEvaludated")) {
                    singleGrade.setHasEvaludated(jsonObject.getBoolean("HasEvaludated"));
                }
                if (jsonObject.has("IsCurrentSemester")) {
                    singleGrade.setIsCurrentSemester(jsonObject.getBoolean("IsCurrentSemester"));
                }
                sg.getStuGradeList().add(singleGrade);
            }
        } catch (Exception e) {
            ToastUtils.showToast("数据异常2-2，请稍后再试");
            e.printStackTrace();
        }
    }*/

