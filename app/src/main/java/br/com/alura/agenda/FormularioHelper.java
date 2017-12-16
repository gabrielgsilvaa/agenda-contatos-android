package br.com.alura.agenda;

import android.app.Activity;
import android.widget.EditText;
import android.widget.RatingBar;

import br.com.alura.agenda.modelo.Aluno;

/**
 * Created by glga on 09/12/2017.
 */

public class FormularioHelper {

    private final EditText txtNome;
    private final EditText txtEndereco;
    private final EditText txtTelefone;
    private final EditText txtSite;
    private final RatingBar barNota;

    private Aluno aluno;

    public  FormularioHelper(FormularioActivity activity){

        this.txtNome = (EditText) activity.findViewById(R.id.formulario_nome);
        this.txtEndereco = (EditText) activity.findViewById(R.id.formulario_endereco);
        this.txtTelefone = (EditText) activity.findViewById(R.id.formulario_telefone);
        this.txtSite = (EditText) activity.findViewById(R.id.formulario_site);
        this.barNota = (RatingBar) activity.findViewById(R.id.formulario_nota);

        aluno = new Aluno();

    }

    public Aluno pegaAluno() {

        aluno.setNome(txtNome.getText().toString());
        aluno.setEndereco(txtEndereco.getText().toString());
        aluno.setTelefone(txtTelefone.getText().toString());
        aluno.setSite(txtSite.getText().toString());
        aluno.setNota(Double.valueOf(barNota.getProgress()));

        return aluno;
    }

    public void preencheFormulario(Aluno aluno) {
        txtNome.setText(aluno.getNome());
        txtEndereco.setText(aluno.getEndereco());
        txtTelefone.setText(aluno.getTelefone());
        txtSite.setText(aluno.getSite());
        barNota.setProgress(aluno.getNota().intValue());

        this.aluno = aluno;
    }
}
