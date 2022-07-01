package com.ljt.study.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.ljt.study.protobuf.dto.CourseProto;
import com.ljt.study.protobuf.dto.StudentProto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author LiJingTang
 * @date 2022-07-01 14:24
 */
@Slf4j
class ProtoBufTest {

    @Test
    void serializable() throws InvalidProtocolBufferException {
        CourseProto.course.Builder courseBuilder1 = CourseProto.course.newBuilder();
        courseBuilder1.setName("Java");
        courseBuilder1.setScore(99);
        CourseProto.course course1 = courseBuilder1.build();

        CourseProto.course.Builder courseBuilder2 = CourseProto.course.newBuilder();
        courseBuilder2.setName("语文");
        courseBuilder2.setScore(100);
        CourseProto.course course2 = courseBuilder2.build();

        StudentProto.student.Builder studentBuilder = StudentProto.student.newBuilder();
        studentBuilder.setName("Lucy");
        studentBuilder.setAge(23);
        studentBuilder.addCourse(0, course1);
        studentBuilder.addCourse(1, course2);
        StudentProto.student student = studentBuilder.build();

        log.info("学生信息\n{}", student);

        byte[] bytes = student.toByteArray();
        log.info("学生信息序列化 {}", Arrays.toString(bytes));

        StudentProto.student newStudent = StudentProto.student.parseFrom(bytes);
        log.info("反序列化学生信息\n{}", JsonFormat.printer().print(newStudent));
    }

}
