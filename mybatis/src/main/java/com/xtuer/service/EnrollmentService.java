package com.xtuer.service;

import com.xtuer.bean.Enrollment;
import com.xtuer.editor.EnrollmentEditor;
import com.xtuer.mapper.EnrollmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Service
public class EnrollmentService {
    @Autowired
    private EnrollmentMapper mapper;

    public void insertEnrollment(Enrollment enrollment) {
        mapper.insertEnrollment(enrollment);
    }

    @Transactional
    public void insertEnrollmentWithTransaction(Enrollment enrollment) {
        mapper.insertEnrollment(enrollment);
    }

    @Transactional
    public void insertEnrollmentsWithTransaction(List<Enrollment> enrollments) {
        for (Enrollment enrollment : enrollments) {
            mapper.insertEnrollment(enrollment);
        }
    }

    /**
     * 从输入流中读取 enrollments
     *
     * @param in enrollments 的输入流
     * @return 返回从文件读取 enrollments list
     */
    public List<Enrollment> readEnrollments(InputStream in) {
        List<Enrollment> enrollments = new ArrayList<>();

        Scanner scanner = new Scanner(in);
        while (scanner.hasNextLine()) {
            EnrollmentEditor editor = new EnrollmentEditor();
            editor.setAsText(scanner.nextLine().trim());
            Enrollment enrollment = (Enrollment) editor.getValue();

            if (enrollment == null) {
                continue;
            }

            enrollments.add(enrollment);
        }

        return enrollments;
    }
}
