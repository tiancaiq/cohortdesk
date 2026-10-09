package com.github.tiancaiq.tliaswebmanagement.mapper;

import com.github.tiancaiq.tlias_pojo.Emp;
import com.github.tiancaiq.tlias_pojo.dto.EmpQueryParam;
import com.github.tiancaiq.tlias_pojo.vo.JobCountVO;
import com.github.tiancaiq.tlias_pojo.vo.OptionVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface EmpMapper {
    @Select( """
            SELECT
                e.id, e.username, e.name, e.gender, e.phone, e.job, e.salary, e.image, e.entry_date, e.dept_id, e.create_time, e.update_time,
                d.name AS deptName
            FROM emp AS e
                LEFT JOIN dept AS d
                ON e.dept_id = d.id
            ORDER BY CONVERT( d.name USING gbk ) ASC, CONVERT( e.name USING gbk ) ASC, e.id ASC
            """ )
    List< Emp > selectAll();

    List< Emp > selectByQuery( EmpQueryParam queryParam );

    @Options(useGeneratedKeys = true,
            keyProperty = "id")
    @Insert( """
            insert into emp(username, name, gender, phone, job, salary, image, entry_date, dept_id, create_time, update_time) \
            values (#{username},#{name},#{gender},#{phone},#{job},#{salary},#{image},#{entryDate},#{deptId},#{createTime},#{updateTime})""" )
    int insert(Emp emp);

    int deleteBatch( @Param( "ids" ) List< Integer > ids );

    @Select( "SELECT * FROM emp WHERE id = #{id}" )
    Emp selectById( @Param( "id" ) Integer id );

    int updateById( Emp emp );

    List< JobCountVO > countEmpJobData();

    List< OptionVO > countEmpGenderData();

    @Select( """
            SELECT *
            FROM emp
            WHERE dept_id = #{deptId}
            ORDER BY name ASC, id ASC
            """ )
    List< Emp> selectByDeptId( @Param( "deptId" ) Integer deptId );

    @Select( "SELECT * FROM emp WHERE username = #{username} AND password = #{password}" )
    Emp selectByUsernameAndPassword( @Param( "username" ) String username, @Param( "password" ) String password );

    @Update( """
            UPDATE emp SET password = #{newPassword} WHERE id = #{empId}
            """ )
    int updatePasswordById( @Param( "empId" ) Integer empId, @Param( "newPassword" ) String newPassword );
}
