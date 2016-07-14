package com.xtuer.service;

import com.xtuer.bean.Enrollment;
import com.xtuer.mapper.EnrollmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnrollmentService {
    @Autowired
    private EnrollmentMapper enrollmentMapper;

    public void insertEnrollment(Enrollment enrollment) {
        enrollmentMapper.insertEnrollment(enrollment);
    }

    @Transactional
    public void insertEnrollmentWithTransaction(Enrollment enrollment) {
        enrollmentMapper.insertEnrollment(enrollment);
    }

    @Transactional
    public void insertEnrollmentsWithTransaction(List<Enrollment> enrollments) {
        for (Enrollment enrollment : enrollments) {
            enrollmentMapper.insertEnrollment(enrollment);
        }
    }
}
