package br.com.alura.agenda.sync;

import android.content.Context;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.dto.AlunoDTO;
import br.com.alura.agenda.events.AtualizarListaAlunoEvent;
import br.com.alura.agenda.retrofit.RetrofitInicializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlunoSincronizador {

    private final Context context;
    private EventBus eventBus = EventBus.getDefault();

    public AlunoSincronizador(Context context) {
        this.context = context;
    }

    public void buscaAlunos() {
        Call<AlunoDTO> lista = new RetrofitInicializador().getAlunoService().lista();

        lista.enqueue(new Callback<AlunoDTO>() {
            @Override
            public void onResponse(Call<AlunoDTO> call, Response<AlunoDTO> response) {
                AlunoDTO body = response.body();
                AlunoDAO alunoDAO = new AlunoDAO(context);
                alunoDAO.sincronizaAluno(body.getAlunos());
                alunoDAO.close();

                eventBus.post(new AtualizarListaAlunoEvent());
            }
            @Override
            public void onFailure(Call<AlunoDTO> call, Throwable t) {
                Log.e("onFailure", "buscaAlunos " + t.getMessage());
                eventBus.post(new AtualizarListaAlunoEvent());
            }
        });
    }
}