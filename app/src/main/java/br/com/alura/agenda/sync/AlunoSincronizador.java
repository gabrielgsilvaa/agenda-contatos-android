package br.com.alura.agenda.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.dto.AlunoDTO;
import br.com.alura.agenda.events.AtualizarListaAlunoEvent;
import br.com.alura.agenda.preferences.AlunoPreferences;
import br.com.alura.agenda.retrofit.RetrofitInicializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlunoSincronizador {

    private final Context context;
    private EventBus eventBus = EventBus.getDefault();
    private AlunoPreferences preferences;

    public AlunoSincronizador(Context context) {
        this.context = context;
        preferences = new AlunoPreferences(context);
    }

    public void buscaTodosAlunos(){
        if(preferences.temVersao()){
            buscaNovos();
        }else{
            buscaAlunos();
        }
    }

    private void buscaNovos() {
        String versao = preferences.getVersao();
        Call<AlunoDTO> call = new RetrofitInicializador().getAlunoService().novos(versao);
        call.enqueue(buscaAlunosCallback());
    }

    private void buscaAlunos() {
        Call<AlunoDTO> lista = new RetrofitInicializador().getAlunoService().lista();
        lista.enqueue(buscaAlunosCallback());
    }

    @NonNull
    private Callback<AlunoDTO> buscaAlunosCallback() {
        return new Callback<AlunoDTO>() {
            @Override
            public void onResponse(Call<AlunoDTO> call, Response<AlunoDTO> response) {
                AlunoDTO body = response.body();
                String versao = body.getMomentoDaUltimaModificacao();

                preferences.salvaVersao(versao);

                AlunoDAO alunoDAO = new AlunoDAO(context);
                alunoDAO.sincronizaAluno(body.getAlunos());
                alunoDAO.close();

                Log.i("versao", preferences.getVersao());

                eventBus.post(new AtualizarListaAlunoEvent());
            }
            @Override
            public void onFailure(Call<AlunoDTO> call, Throwable t) {
                Log.e("onFailure", "buscaAlunos " + t.getMessage());
                eventBus.post(new AtualizarListaAlunoEvent());
            }
        };
    }
}