package br.com.alura.agenda.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.alura.agenda.modelo.Aluno;

/**
 * Created by glga on 09/12/2017.
 */

public class AlunoDAO extends SQLiteOpenHelper{


    public AlunoDAO(Context context) {
        super(context, "db_Agenda", null, 6);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE Alunos " +
                "(id CHAR(36) PRIMARY KEY, " +
                "nome TEXT NOT NULL, " +
                "endereco TEXT, " +
                "telefone TEXT, " +
                "site TEXT, " +
                "nota REAL, " +
                "caminhoFoto TEXT, " +
                "sincronizado INT DEFAULT 0, " +
                "desativado INT DEFAULT 0)";
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

            case 3:

                String buscaAlunos = "SELECT * FROM Alunos WHERE desativado = 0 ";
                Cursor cursor = sqLiteDatabase.rawQuery(buscaAlunos, null);

                List<Aluno> alunos = populaAlunos(cursor);

                String atualizaIdAluno = "UPDATE Alunos SET id=? WHERE id=? ";

                for (Aluno aluno : alunos) {
                    sqLiteDatabase.execSQL(atualizaIdAluno, new String[] { geraUUID(), aluno.getId()});
                    Log.i("aluno update: ", aluno.getId());
                }
            case 4:
                String adicionaCampoSincronizado = "ALTER TABLE Alunos " +
                        "ADD COLUMN sincronizado INT DEFAULT 0";
                sqLiteDatabase.execSQL(adicionaCampoSincronizado);

            case 5:
                String adicionaCampoDesativado = "ALTER TABLE Alunos " +
                        "ADD COLUMN desativado INT DEFAULT 0";
                sqLiteDatabase.execSQL(adicionaCampoDesativado);

        }

    }

    private String geraUUID() {
        return UUID.randomUUID().toString();
    }

    public void sincronizaAluno(List<Aluno> alunos) {
        for (Aluno aluno :
                alunos) {
            aluno.sincroniza();
            if(existe(aluno)) {
                if(aluno.estaDesativado()){
                    deletaAluno(aluno);
                }else {
                    alteraAluno(aluno);
                }
            }else if(!aluno.estaDesativado()){
                insereAluno(aluno);
            }
        }
    }

    public void insereAluno(Aluno aluno) {

        SQLiteDatabase db = getWritableDatabase();

        insereIdSeNecessario(aluno);

        ContentValues dados = getDadosAluno(aluno);

        db.insert("Alunos", null, dados);
    }

    private void insereIdSeNecessario(Aluno aluno) {
        if(aluno.getId() == null) {
            aluno.setId(geraUUID());
        }
    }

    @NonNull
    private ContentValues getDadosAluno(Aluno aluno) {
        ContentValues dados = new ContentValues();
        dados.put("id", aluno.getId());
        dados.put("nome", aluno.getNome());
        dados.put("endereco", aluno.getEndereco());
        dados.put("telefone", aluno.getTelefone());
        dados.put("site", aluno.getSite());
        dados.put("nota", aluno.getNota());
        dados.put("caminhoFoto", aluno.getCaminhoFoto());
        dados.put("sincronizado", aluno.getSincronizado());
        dados.put("desativado", aluno.getDesativado());
        return dados;
    }


    public List<Aluno> buscaAlunos() {

        String sql = "SELECT * FROM Alunos;";
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(sql, null);

        List<Aluno> alunos = populaAlunos(c);

        c.close();
        return alunos;
    }

    @NonNull
    private List<Aluno> populaAlunos(Cursor c) {
        List<Aluno> alunos = new ArrayList<Aluno>();

        while(c.moveToNext()){

            Aluno aluno = new Aluno();

            aluno.setId(c.getString(c.getColumnIndex("id")));
            aluno.setNome(c.getString(c.getColumnIndex("nome")));
            aluno.setEndereco(c.getString(c.getColumnIndex("endereco")));
            aluno.setTelefone(c.getString(c.getColumnIndex("telefone")));
            aluno.setSite(c.getString(c.getColumnIndex("site")));
            aluno.setNota(c.getDouble(c.getColumnIndex("nota")));
            aluno.setCaminhoFoto(c.getString(c.getColumnIndex("caminhoFoto")));
            aluno.setSincronizado(c.getInt(c.getColumnIndex("sincronizado")));
            aluno.setDesativado(c.getInt(c.getColumnIndex("desativado")));
            alunos.add(aluno);
        }
        return alunos;
    }

    public void deletaAluno(Aluno aluno) {

        SQLiteDatabase db = getWritableDatabase();

        String[] params = {aluno.getId().toString()};

        if(aluno.estaDesativado()) {
            db.delete("Alunos", "id = ?", params);
        }else{
            aluno.desativaAluno();
            alteraAluno(aluno);
        }

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

    public boolean existe(Aluno aluno){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Alunos WHERE id = ? LIMIT 1", new String[]{aluno.getId()});

        int result = c.getCount();

        c.close();

        return result > 0; // true or false

    }

    public List<Aluno> listaNaoSincronizados(){
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM Alunos WHERE sincronizado = 0 ";
        Cursor cursor = db.rawQuery(sql, null);

        return populaAlunos(cursor);
    }
}
