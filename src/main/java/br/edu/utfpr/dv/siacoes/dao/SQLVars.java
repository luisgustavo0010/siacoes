package br.edu.utfpr.dv.siacoes.dao;

public abstract class SQLVars {

    public final void teste(){
        connection();
        statement();
        prestatement();
        resultset();
    }

    public void connection(){

    }

    public abstract void statement() {

    }

    public abstract void prestatement() {

    }

    public abstract void resultset() {

    }

}
