package br.com.alura.agenda.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import br.com.alura.agenda.modelo.Aluno;

/**
 * Created by glga on 09/12/2017.
 */

public class AlunoDAO extends SQLiteOpenHelper{


    public AlunoDAO(Context context) {
        super(context, "db_Agenda", null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE Alunos " +
                "(id INTEGER PRIMARY KEY, " +
                "nome TEXT NOT NULL, " +
                "endereco TEXT, " +
                "telefone TEXT, " +
                "site TEXT, " +
                "nota REAL, " +
                "caminhoFoto TEXT)";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        String sql = "";

        switch (oldVersion){
            case 1:
                sql = "ALTER TABLE Alunos ADD COLUMN caminhoFoto TEXT";
                sqLiteDatabase.execSQL(sql);

            case 2:
                String criandoTabelaNova = "CREATE TABLE Alunos_novo " +
                        "(id CHAR(36) PRIMARY KEY, " +
                        "nome TEXT NOT NULL, " +
                        "endereco TEXT, " +
                        "telefone TEXT, " +
                        "site TEXT, " +
                        "nota REAL, " +
                        "caminhoFoto TEXT)";
                sqLiteDatabase.execSQL(criandoTabelaNova);

                String inserindoAlunosNaTabelaNova = "INSERT INTO Alunos_novo " +
                        "(id, nome, endereco, telefone, site, nota, caminhoFoto) " +
                        "select id, nome, endereco, telefone, site, nota, caminhoFoto FROM Alunos ";
                sqLiteDatabase.execSQL(inserindoAlunosNaTabelaNova);

                String removendoTabelaAntiga = "DROP TABLE Alunos";
                sqLiteDatabase.execSQL(removendoTabelaAntiga);

                String alterandoNomeTabelaNova = "ALTER TABLE Alunos_novo RENAME TO Alunos ";
                sqLiteDatabase.execSQL(alterandoNomeTabelaNova);


        }

    }

    public void insereAluno(Aluno aluno) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues dados = getDadosAluno(aluno);

        long id = db.insert("Alunos", null, dados);

        aluno.setId(id);

    }

    @NonNull
    private ContentValues getDadosAluno(Aluno aluno) {
        ContentValues dados = new ContentValues();
        dados.put("nome", aluno.getNome());
        dados.put("endereco", aluno.getEndereco());
        dados.put("telefone", aluno.getTelefone());
        dados.put("site", aluno.getSite());
        dados.put("nota", aluno.getNota());
        dados.put("caminhoFoto", aluno.getCaminhoFoto());
        return dados;
    }


    public List<Aluno> buscaAlunos() {

        String sql = "SELECT * FROM Alunos;";
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(sql, null);

        List<Aluno> alunos = new ArrayList<Aluno>();

        while(c.moveToNext()){

            Aluno aluno = new Aluno();

            aluno.setId(c.getLong(c.getColumnIndex("id")));
            aluno.setNome(c.getString(c.getColumnIndex("nome")));
            aluno.setEndereco(c.getString(c.getColumnIndex("endereco")));
            aluno.setTelefone(c.getString(c.getColumnIndex("telefone")));
            aluno.setSite(c.getString(c.getColumnIndex("site")));
            aluno.setNota(c.getDouble(c.getColumnIndex("nota")));
            aluno.setCaminhoFoto(c.getString(c.getColumnIndex("caminhoFoto")));
            alunos.add(aluno);
        };

        c.close();
        return alunos;
    }

    public void deletar(Aluno aluno) {

        SQLiteDatabase db = getWritableDatabase();

        String[] params = {aluno.getId().toString()};

        db.delete("Alunos", "id = ?", params);

    }

    public void alteraAluno(Aluno aluno) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues dados = getDadosAluno(aluno);

        String[] params = {aluno.getId().toString()};

        db.update("Alunos", dados, "id = ?", params);

    }

    public boolean existeAluno(String telefone){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Alunos WHERE telefone = ?", new String[]{telefone});

        int result = c.getCount();

        c.close();

        return result > 0; // true or false

    }
}
