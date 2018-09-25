package br.com.alura.agenda.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.alura.agenda.modelo.Aluno;
import br.com.alura.agenda.services.AlunoService;
import br.com.alura.agenda.services.DispositivoService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInicializador {

    private final Retrofit retrofit;

    public RetrofitInicializador(){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.addInterceptor(interceptor);

        retrofit = new Retrofit.Builder()
                        .baseUrl("http://192.168.0.27:8080/api/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client.build())
                        .build();
    }

    public AlunoService getAlunoService() {
        return retrofit.create(AlunoService.class);
    }

    public DispositivoService getDispositivoService() {
        return retrofit.create(DispositivoService.class);
    }
}
