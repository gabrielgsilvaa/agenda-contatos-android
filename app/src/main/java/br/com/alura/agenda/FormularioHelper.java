package br.com.alura.agenda;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.EditText;
import android.widget.ImageView;
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
    private final ImageView imageFoto;

    private Aluno aluno;

    public  FormularioHelper(FormularioActivity activity){

        this.txtNome = (EditText) activity.findViewById(R.id.formulario_nome);
        this.txtEndereco = (EditText) activity.findViewById(R.id.formulario_endereco);
        this.txtTelefone = (EditText) activity.findViewById(R.id.formulario_telefone);
        this.txtSite = (EditText) activity.findViewById(R.id.formulario_site);
        this.barNota = (RatingBar) activity.findViewById(R.id.formulario_nota);
        this.imageFoto = (ImageView) activity.findViewById(R.id.formulario_foto);

        aluno = new Aluno();

    }

    public Aluno pegaAluno() {

        aluno.setNome(txtNome.getText().toString());
        aluno.setEndereco(txtEndereco.getText().toString());
        aluno.setTelefone(txtTelefone.getText().toString());
        aluno.setSite(txtSite.getText().toString());
        aluno.setNota(Double.valueOf(barNota.getProgress()));
        aluno.setCaminhoFoto((String) imageFoto.getTag());

        return aluno;
    }

    public void preencheFormulario(Aluno aluno) {
        txtNome.setText(aluno.getNome());
        txtEndereco.setText(aluno.getEndereco());
        txtTelefone.setText(aluno.getTelefone());
        txtSite.setText(aluno.getSite());
        barNota.setProgress(aluno.getNota().intValue());
        carregaImagem(aluno.getCaminhoFoto());
        this.aluno = aluno;
    }

    public void carregaImagem( String caminhoFoto) {

        if(caminhoFoto!=null){

            Bitmap bitmap = BitmapFactory.decodeFile(caminhoFoto);
            Bitmap bitmapReduzido = bitmap.createScaledBitmap(bitmap, 300, 300, true);
            imageFoto.setImageBitmap(bitmapReduzido);
            imageFoto.setScaleType(ImageView.ScaleType.FIT_XY);
            imageFoto.setTag(caminhoFoto);
        }

    }
}
