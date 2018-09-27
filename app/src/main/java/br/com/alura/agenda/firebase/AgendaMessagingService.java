package br.com.alura.agenda.firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.dto.AlunoDTO;
import br.com.alura.agenda.events.AtualizarListaAlunoEvent;
import br.com.alura.agenda.modelo.Aluno;
import br.com.alura.agenda.sync.AlunoSincronizador;

public class AgendaMessagingService extends FirebaseMessagingService{

    private Gson gson = new Gson();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> mensagem = remoteMessage.getData();
        Log.i("mensagem FCM", String.valueOf(mensagem));

        converteParaAluno(mensagem);
    }

    private void converteParaAluno(Map<String, String> mensagem) {

        String chaveAcesso = "alunoSync";

        if(mensagem.containsKey(chaveAcesso)){
            String json = mensagem.get(chaveAcesso);
            AlunoDTO alunoDTO = gson.fromJson(json, AlunoDTO.class);

            new AlunoSincronizador(AgendaMessagingService.this).sincroniza(alunoDTO);

            EventBus eventBus = EventBus.getDefault();
            eventBus.post(new AtualizarListaAlunoEvent());

        }


    }

}