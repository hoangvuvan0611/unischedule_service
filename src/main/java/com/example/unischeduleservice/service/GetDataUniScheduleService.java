package com.example.unischeduleservice.service;

/**
 * @author vuvanhoang
 * @created 25/10/25 08:08
 * @project unischedule_service
 */
public interface GetDataUniScheduleService {
    void getDataSchedule(String username, String password);
    void getDataScheduleTest(String username, String password);
    void getDataScore(String username, String password);
    void getDataTuition(String username, String password);
    void getDataTuitionDetail(String username, String password);
}
