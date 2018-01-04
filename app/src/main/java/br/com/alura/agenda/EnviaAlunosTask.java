package br.com.alura.agenda;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.List;

import br.com.alura.agenda.converter.AlunoConverter;
import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.modelo.Aluno;

/**
 * Created by glga on 01/01/2018.
 */

public class EnviaAlunosTask extends AsyncTask<Void, Void, String> {

    private Context context;
    private ProgressDialog dialog;

    public EnviaAlunosTask(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... objects) {

        AlunoDAO dao = new AlunoDAO(context);
        List<Aluno> alunos = dao.buscaAlunos();
        dao.close();

        AlunoConverter conversor = new AlunoConverter();
        String json = conversor.converteParaJson(alunos);

        System.out.println("1" + json);

        WebClient client = new WebClient();
        String resposta = client.post(json);

        System.out.println("2" + resposta);


        return resposta;
    }

    @Override
    protected void onPreExecute() {
        dialog = ProgressDialog.show(context, "Aguarde", "Enviando alunos...", true, true);
    }

    @Override
    protected void onPostExecute(String resposta) {
        dialog.dismiss();
        Toast.makeText(context, "Enviando notas..." + resposta , Toast.LENGTH_LONG).show();
    }
}
