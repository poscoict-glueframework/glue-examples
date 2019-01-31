package com.poscoict.sample.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity( name = "Emp" )
@Table( name = "EMP" )
public class EmpJpo
{
    @Id
    private int empno;
    private String ename;

    public EmpJpo()
    {}

    public EmpJpo( int empno, String ename )
    {
        this.empno = empno;
        this.ename = ename;
    }

    public int getEmpno()
    {
        return empno;
    }

    public void setEmpno( int empno )
    {
        this.empno = empno;
    }

    public String getEname()
    {
        return ename;
    }

    public void setEname( String ename )
    {
        this.ename = ename;
    }
}
