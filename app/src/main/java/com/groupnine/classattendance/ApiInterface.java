package com.groupnine.classattendance;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {

    @GET("getusers.php")
    Call<List<User>> getUsers();

    @FormUrlEncoded
    @POST("login.php")
    Call<User> login(@Field("user_name") String username, @Field("user_password") String password);

    @FormUrlEncoded
    @POST("create_session.php")
    Call<Session> createSession(@Field("session_start") String start, @Field("session_end") String end, @Field("unit_id") String unit, @Field("venue_id") String venue, @Field("session_status") int status, @Field("user_id") int user);

    @FormUrlEncoded
    @POST("getunitsbylec.php")
    Call<List<Unit>> getUnitsByLec(@Field("user_id") int user);

    @FormUrlEncoded
    @POST("get_student_attendance.php")
    Call<List<User>> getStudentAttendance(@Field("session_id") int session);

    @FormUrlEncoded
    @POST("find_active_session.php")
    Call<List<Session>> findActiveSessionsForLec(@Field("user_id") int user);

    @GET("find_all_active_sessions.php")
    Call<List<Session>> findAllActiveSessions();

    @FormUrlEncoded
    @POST("find_all_sessions_by_lec.php")
    Call<List<Session>> findAllSessionsByLec(@Field("user_id") int user);

    @FormUrlEncoded
    @POST("find_all_closed_sessions_by_lec.php")
    Call<List<Session>> findAllClosedSessionsByLec(@Field("user_id") int user);

    @FormUrlEncoded
    @POST("close_session.php")
    Call<Session> closeSession(@Field("session_id") int session);

    @FormUrlEncoded
    @POST("register_attendance.php")
    Call<Session> registerAttendance(@Field("session_id") int session, @Field("user_id") int user);


}
