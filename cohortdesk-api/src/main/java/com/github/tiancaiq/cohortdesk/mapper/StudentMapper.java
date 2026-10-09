package com.github.tiancaiq.cohortdesk.mapper;

import com.github.tiancaiq.cohortdesk.model.Student;
import com.github.tiancaiq.cohortdesk.model.dto.StudentQueryParam;
import com.github.tiancaiq.cohortdesk.model.vo.ClazzStudentCountVO;
import com.github.tiancaiq.cohortdesk.model.vo.OptionVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface StudentMapper {
    List< Student > selectByQuery( StudentQueryParam queryParam );

    @Insert( "INSERT INTO student " +
            " ( name, no, gender, phone, id_card, is_college, address, degree, graduation_date, clazz_id, violation_count, violation_score, create_time, update_time )  " +
            " VALUES ( #{name}, #{no}, #{gender}, #{phone}, #{idCard}, #{isCollege}, #{address}, #{degree}, #{graduationDate}, #{clazzId}, #{violationCount}, #{violationScore}, #{createTime}, #{updateTime} )" )
    int insert( Student student );

    @Select( """
            SELECT
                s.id, s.name, s.no, s.gender, s.phone, s.id_card, s.is_college, s.address, s.degree, s.graduation_date, s.clazz_id, s.violation_count, s.violation_score, s.create_time, s.update_time,
                c.name AS clazz_name
            FROM student AS s
            LEFT JOIN clazz AS c
            ON s.clazz_id = c.id
            WHERE s.id = #{id}
            """ )
    Student selectById( @Param( "id" ) Integer id );

    int update( Student student );

    int deleteByIds( @Param( "ids" ) List< Integer > ids );

    @Select( """
            SELECT
                s.id, s.name, s.no, s.gender, s.phone, s.id_card, s.is_college, s.address, s.degree, s.graduation_date, s.clazz_id, s.violation_count, s.violation_score, s.create_time, s.update_time,
                c.name AS clazz_name
            FROM student AS s
            LEFT JOIN clazz AS c
            ON s.clazz_id = c.id 
            ORDER BY CONVERT( c.name USING gbk ) ASC, CONVERT( s.name USING gbk ) ASC, s.id ASC
            """ )
    List< Student > selectAll();

    @Update( "UPDATE student " +
            "SET violation_count = #{violationCount}, violation_score = #{violationScore}, update_time = #{updateTime} " +
            "WHERE id = #{id}" )
    int updateViolation( Student student );

    @Select( """
            SELECT id, name, no, gender, phone, id_card, is_college, address, degree, graduation_date, clazz_id, violation_count, violation_score, create_time, update_time 
            FROM student WHERE clazz_id = #{clazzId} 
            ORDER BY name ASC, id ASC
        """ )
    List< Student > selectByClazzId( @Param( "clazzId" ) Integer clazzId );

    List< ClazzStudentCountVO > countStudentCountData();

    List< OptionVO > countStudentDegreeData();

    @Select( "SELECT count( 0 ) FROM student" )
    Integer countAll();

    @Select( """
            SELECT count( 0 )
            FROM student AS s LEFT JOIN clazz AS c
            ON s.clazz_id = c.id
            WHERE c.name IS NULL
            """ )
    Integer countStudentHaveNoClazz();
}
