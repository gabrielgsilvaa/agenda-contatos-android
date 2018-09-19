package br.com.alura.agenda;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.modelo.Aluno;
import br.com.alura.agenda.retrofit.RetrofitInicializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormularioActivity extends AppCompatActivity {

    public static final int CODIGO_CAMERA = 567;
    private FormularioHelper helper;
    private String caminhoFoto;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        helper = new FormularioHelper(this);

        final Intent intent = getIntent();

        Aluno aluno = (Aluno) intent.getSerializableExtra("aluno");

        if (aluno != null){
            helper.preencheFormulario(aluno);
        }

        Button botaoFoto = (Button) findViewById(R.id.formulario_botao_foto);
        botaoFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                caminhoFoto = getExternalFilesDir(null) +    "/"+ System.currentTimeMillis() +".jpg";
                File arquivoFoto = new File(caminhoFoto);

                // ANDROID 6
                //intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(arquivoFoto));

                // Android 7
                intentCamera.putExtra(
                        MediaStore.EXTRA_OUTPUT,
                        FileProvider.getUriForFile(context,BuildConfig.APPLICATION_ID + ".provider", arquivoFoto));

                startActivityForResult(intentCamera, CODIGO_CAMERA); // aguardando o resultado da activity
            }
        });

    }

    // aguardando o resultado da activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode== CODIGO_CAMERA && resultCode == Activity.RESULT_OK){ // request da camera
            helper.carregaImagem(caminhoFoto);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_formulario, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.menu_formulario_ok:

                Aluno aluno = helper.pegaAluno();
                AlunoDAO dao = new AlunoDAO(this);

                if(aluno.getId() == null){
                    dao.insereAluno(aluno);
                }else{
                    dao.alteraAluno(aluno);
                }

                dao.close();

                Call call = new RetrofitInicializador().getAlunoService().insere(aluno);
                call.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        Log.i("onResponse", "sucesso");

                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        Log.e("onFailure", "falha");
                    }
                });


                Toast.makeText(FormularioActivity.this, "Aluno '"+aluno.getNome()+"', salvo com sucesso!", Toast.LENGTH_SHORT).show();

                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
