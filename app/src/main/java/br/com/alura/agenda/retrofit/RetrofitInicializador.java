package br.com.alura.agenda.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.alura.agenda.modelo.Aluno;
import br.com.alura.agenda.services.AlunoService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInicializador {

    private final Retrofit retrofit;

    public RetrofitInicializador(){

        retrofit = new Retrofit.Builder()
                        .baseUrl("http://192.168.0.27:8080/api/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
    }

    public AlunoService getAlunoService() {
        return retrofit.create(AlunoService.class);
    }
}
