package com.github.tiancaiq.cohortdesk.mapper;

import com.github.tiancaiq.cohortdesk.model.EmpExpr;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmpExprMapper {
    @Insert( "INSERT INTO emp_expr " +
            "( emp_id, begin, end, company, job ) " +
            "VALUES( #{empId}, #{begin}, #{end}, #{company}, #{job} )" )
    void insert( EmpExpr empExpr );

    void insertBatch( List< EmpExpr > exprList );

    void deleteBatchByEmpIds( @Param( "empIds" ) List< Integer > empIds );

    @Select( """
            SELECT *
            FROM emp_expr
            WHERE emp_id = #{empId}
            ORDER BY begin DESC
            """ )
    List< EmpExpr > findByEmpId( @Param( "empId" ) Integer empId );

}
