package br.com.alura.agenda.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import br.com.alura.agenda.retrofit.RetrofitInicializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgendaInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh(){
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("token firebase", "Refreshed token: " + refreshedToken);

        enviaTokenParaservidor(refreshedToken);
    }

    private void enviaTokenParaservidor(final String refreshedToken) {

        Call<Void> call = new RetrofitInicializador().getDispositivoService().enviaToken(refreshedToken);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i("Token Enviado", "Sucesso " + refreshedToken );
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.i("Token Enviado", "Falha " + refreshedToken);
            }
        });
    }
}
