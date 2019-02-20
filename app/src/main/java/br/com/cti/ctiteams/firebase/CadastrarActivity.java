package br.com.cti.ctiteams.firebase;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import br.com.cti.ctiteams.R;

public class CadastrarActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editText_email, editText_senha, editTextRepetirSenha;
    private Button btn_cadastrousuario, btn_cancelar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        editText_email = (EditText) findViewById(R.id.editTextEmailCadastro);
        editText_senha = (EditText) findViewById(R.id.editText_SenhaCadastro);
        editTextRepetirSenha = (EditText) findViewById(R.id.editText_RepetirSenhaCadastro);

        btn_cadastrousuario = (Button) findViewById(R.id.button_cadastrarUsuario);
        btn_cancelar = (Button) findViewById(R.id.button_cancelar);

        btn_cadastrousuario.setOnClickListener(this);
        auth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_cadastrarUsuario:
                cadastrar();
                break;

        }


    }

    private void cadastrar() {
        String email = editText_email.getText().toString().trim();
        String senha = editText_senha.getText().toString().trim();
        String confirmaSenha = editTextRepetirSenha.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty() || confirmaSenha.isEmpty()){
            Toast.makeText(getBaseContext(), "Favor preencher os campos", Toast.LENGTH_LONG).show();
        }else {

            if (senha.contentEquals(confirmaSenha)) {
                if (util.verificarInternet(this)){

                    criarUsuario(email,senha);
                }else{
                    Toast.makeText(getBaseContext(), "Erro - Verifique se a conexão está disponível", Toast.LENGTH_LONG).show();
                }

            } else {

                Toast.makeText(getBaseContext(), "Senhas divergentes", Toast.LENGTH_LONG).show();
            }
        }
    }
        private void criarUsuario(String email, String senha){
        //auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(getBaseContext(), "Cadastro Efetuado com Sucesso", Toast.LENGTH_LONG).show();
                } else {
                    String resposta = task.getException().toString();
                    util.opcoesErro(getBaseContext(), resposta);
                }
            }
        });
    }

    private boolean verificaInternet(){
        ConnectivityManager conexao = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo informacao = (conexao.getActiveNetworkInfo());

        if (informacao != null && informacao.isConnected()){
            return true;

        } else{
            return false;
        }

    }
}

