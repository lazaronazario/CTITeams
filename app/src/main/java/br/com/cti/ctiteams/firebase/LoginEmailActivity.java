package br.com.cti.ctiteams.firebase;

import android.content.Intent;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import br.com.cti.ctiteams.R;

public class LoginEmailActivity  extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmail,EditTextSenha;
    private Button btn_login, btn_recuperarSenha;
    private FirebaseAuth auth;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        editTextEmail = (EditText) findViewById(R.id.editText_EmailLogin);
        EditTextSenha = (EditText) findViewById(R.id.editText_SenhaLogin);
        btn_login =     (Button) findViewById(R.id.button_Oklogin);
        btn_recuperarSenha = (Button) findViewById(R.id.button_RecuperarSenha);

        btn_login.setOnClickListener(this);
        btn_recuperarSenha.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.button_Oklogin:
                loginEmail();
                break;

            case R.id.button_RecuperarSenha:

                recuperaSenha();
                break;

        }
    }

    private void recuperaSenha(){
        String email = editTextEmail.getText().toString().trim();

        if (email.isEmpty()) {

            Toast.makeText(getBaseContext(), "Insira seu email para recuperar senha", Toast.LENGTH_LONG).show();

        }else{
            enviarEmail(email);
        }

    }
    private void enviarEmail(String email){
        auth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getBaseContext(),"Enviamos um mensagem com um link para redefinição de senha",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String erro = e.toString();
                util.opcoesErro(getBaseContext(),erro);
            }
        });
    }

    private void loginEmail(){
        String email = editTextEmail.getText().toString().trim();
        String senha = EditTextSenha.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty()){

            Toast.makeText(getBaseContext(), "Insira os campos obrigatórios", Toast.LENGTH_LONG).show();
        }else{
            if(util.verificarInternet(this)) {

                confirmaLoginEmail(email, senha);
            }else{
                Toast.makeText(getBaseContext(), "Erro - Verifique se a conexão está disponível", Toast.LENGTH_LONG).show();
            }

        }

    }

    private void confirmaLoginEmail(String email, String senha){

        auth.signInWithEmailAndPassword(email,senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(getBaseContext(), PrincipalActivity.class));
                    Toast.makeText(getBaseContext(), "Usuário logado com sucesso", Toast.LENGTH_LONG).show();
                    finish();
                } else{

                    String resposta = task.getException().toString();
                    util.opcoesErro(getBaseContext(), resposta);
                }
            }
        });
    }

}
