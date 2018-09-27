package br.com.alura.agenda.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.com.alura.agenda.ListaAlunosActivity;
import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.dto.AlunoDTO;
import br.com.alura.agenda.events.AtualizarListaAlunoEvent;
import br.com.alura.agenda.modelo.Aluno;
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
                AlunoDTO alunoDTO = response.body();
                sincroniza(alunoDTO);

//              Log.i("versao", preferences.getVersao());

                eventBus.post(new AtualizarListaAlunoEvent());
                sincronizaAlunosInternos();
            }
            @Override
            public void onFailure(Call<AlunoDTO> call, Throwable t) {
                Log.e("onFailure", "buscaAlunos " + t.getMessage());
                eventBus.post(new AtualizarListaAlunoEvent());
            }
        };
    }

    public void sincroniza(AlunoDTO alunoDTO) {
        String versao = alunoDTO.getMomentoDaUltimaModificacao();

        Log.i("Versao Externa", versao);

        if(temVersaoNova(versao)) {

            preferences.salvaVersao(versao);

            Log.i("Versao atual", preferences.getVersao());

            AlunoDAO alunoDAO = new AlunoDAO(context);
            alunoDAO.sincronizaAluno(alunoDTO.getAlunos());
            alunoDAO.close();
        }
    }

    private boolean temVersaoNova(String versaoExterna) {

        if(!preferences.temVersao())
            return true;

        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss.SSS");

        try {
            String versaoInterna = preferences.getVersao();

            Log.i("Versao Interna", versaoInterna);

            Date dataExterna = formatDate.parse(versaoExterna);
            Date dataInterna = formatDate.parse(versaoInterna);

            return dataExterna.after(dataInterna);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void sincronizaAlunosInternos(){
        final AlunoDAO alunoDAO = new AlunoDAO(context);

        List<Aluno> alunos = alunoDAO.listaNaoSincronizados();
        alunoDAO.close();

        Call<AlunoDTO> call = new RetrofitInicializador().getAlunoService().atualiza(alunos);

        call.enqueue(new Callback<AlunoDTO>() {
            @Override
            public void onResponse(Call<AlunoDTO> call, Response<AlunoDTO> response) {
                AlunoDTO alunoDTO = response.body();
                sincroniza(alunoDTO);
            }

            @Override
            public void onFailure(Call<AlunoDTO> call, Throwable t) {

            }
        });
    }

    public void deletaAluno(final Aluno aluno) {
        Call<Void> deleta = new RetrofitInicializador().getAlunoService().deleta(aluno.getId());

        deleta.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                AlunoDAO alunoDAO = new AlunoDAO(context);
                alunoDAO.deletaAluno(aluno);
                alunoDAO.close();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
}