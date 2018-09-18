package br.com.alura.agenda.converter;

import org.json.JSONException;
import org.json.JSONStringer;

import java.util.List;

import br.com.alura.agenda.modelo.Aluno;

/**
 * Created by glga on 01/01/2018.
 */

public class AlunoConverter {

    public String converteParaJson(List<Aluno> alunos) {
        JSONStringer js = new JSONStringer();
        try {

            js.object().key("list").array().object().key("aluno").array();
            for (Aluno aluno:alunos){
                js.object().key("nome").value(aluno.getNome()).key("nota").value(aluno.getNota());
                js.endObject();
            }
            js.endArray().endObject().endArray().endObject();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return js.toString();
    }
}
