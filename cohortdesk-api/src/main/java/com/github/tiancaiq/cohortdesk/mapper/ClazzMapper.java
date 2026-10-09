package com.github.tiancaiq.cohortdesk.mapper;

import com.github.tiancaiq.cohortdesk.model.Clazz;
import com.github.tiancaiq.cohortdesk.model.dto.ClazzQueryParam;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ClazzMapper {
    List< Clazz > selectByQuery( ClazzQueryParam queryParam );

    @Insert( """
            INSERT INTO clazz
            ( name, room, begin_date, end_date, master_id, subject, create_time, update_time )
            VALUES ( #{name}, #{room}, #{beginDate}, #{endDate}, #{masterId}, #{subject}, #{createTime}, #{updateTime} )""" )
    int insert( Clazz clazz );

    Clazz selectById( @Param( "id" ) Integer id );

    int updateById( Clazz clazz );

    @Delete( "DELETE FROM clazz WHERE id = #{id}" )
    int deleteById( @Param( "id" ) Integer id );

    List< Clazz> selectAll();

    @Select( "SELECT id, name, room, begin_date, end_date, master_id, subject, create_time, update_time FROM clazz WHERE name = #{name}" )
    Clazz selectByName( @Param( "name" ) String name );

    @Select( "SELECT count( 0 ) FROM clazz" )
    Integer countAll();

    @Select( """
            SELECT count( 0 )
            FROM clazz
            WHERE begin_date <= now() AND now() <= end_date
            """ )
    Integer countOpenClazz();

    @Select( """
            SELECT count( 0 )
            FROM clazz
            WHERE end_date < now()
            """  )
    Integer countEndClazz();

    @Select( """
            SELECT count( 0 )
            FROM clazz
            WHERE begin_date > now()
            """  )
    Integer countNotStart();

}
