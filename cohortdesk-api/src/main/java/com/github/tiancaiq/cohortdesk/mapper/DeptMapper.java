package com.github.tiancaiq.cohortdesk.mapper;

import com.github.tiancaiq.cohortdesk.model.Dept;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DeptMapper {
    @Results( id = "deptMap", value = {
            @Result( property = "createTime", column = "create_time" ),
            @Result( property = "updateTime", column = "update_time" )
    } )
    @Select( """
            SELECT *
            FROM dept
            ORDER BY CONVERT( name USING gbk ) ASC , id ASC
            """ )
    List< Dept > selectAll();

    @ResultMap( "deptMap" )
    @Select( """
            SELECT * FROM dept WHERE id = #{id}
            """ )
    Dept selectById( @Param( "id" ) Integer id );

    @ResultMap( "deptMap" )
    @Delete( """
            DELETE FROM dept WHERE id = #{id}
            """ )
    int deleteById( @Param( "id" ) Integer id );

    @Insert( """
            INSERT INTO dept
            ( dept.name, dept.create_time, dept.update_time)
            values( #{name}, #{ createTime }, #{ updateTime } )
            """ )
    int insert(  Dept dept );

    @Update( """
            UPDATE dept
            SET dept.name = #{ name }, dept.update_time = #{ updateTime }
            WHERE id = #{ id }
            """ )
    int updateById( Dept dept );
}
