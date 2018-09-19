package br.com.alura.agenda.tasks;

import android.os.AsyncTask;

import br.com.alura.agenda.WebClient;
import br.com.alura.agenda.converter.AlunoConverter;
import br.com.alura.agenda.modelo.Aluno;

public class InsereAlunoTask extends AsyncTask{

    private Aluno aluno;

    public InsereAlunoTask(Aluno aluno) {
        this.aluno = aluno;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        String alunoJson = new AlunoConverter().converteParaJsonCompleto(aluno);
        new WebClient().insere(alunoJson);
        return null;
    }
}
