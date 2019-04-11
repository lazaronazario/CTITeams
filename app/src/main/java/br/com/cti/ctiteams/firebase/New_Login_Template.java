package br.com.cti.ctiteams.firebase;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.ornach.nobobutton.NoboButton;

import java.util.Arrays;

import br.com.cti.ctiteams.R;
import util.Permissoes;
import util.Util;

public class New_Login_Template extends AppCompatActivity implements View.OnClickListener {

    NoboButton tabSignin, tabSignup;
    View layoutSignin, layoutSignup;
    EditText edtxtemail,edttxtsenha,edtxtrepetesenha,edittextemailcadastro,edittextsenhacadastro,edittextnomecadastro;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private GoogleSignInClient googleSignInClient;
    private CallbackManager callbackManager;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Button button_Deslogar;//não implementado
    private String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new__login__template);

        tabSignin = (NoboButton) findViewById(R.id.tab_signin);
        tabSignup = (NoboButton) findViewById(R.id.tab_signup);
        layoutSignin = findViewById(R.id.content_signin);
        layoutSignup = findViewById(R.id.content_signup);

        //--para login
        edttxtsenha = findViewById(R.id.input_password);
        edtxtemail = findViewById(R.id.input_email);

        //Para cadastro
        edittextemailcadastro = findViewById(R.id.input_email2);
        edittextnomecadastro = findViewById(R.id.input_name);
        edittextsenhacadastro = findViewById(R.id.input_password2);
        edtxtrepetesenha = findViewById(R.id.input_password_repeat);


        auth = FirebaseAuth.getInstance();
        servicosAutenticacao();
        servicosGoogle();
        servicosFacebook();

        //ValidarPermissão
        Permissoes.validarPermissoes(permissoes, this, 1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults){
            if(permissaoResultado == PackageManager.PERMISSION_DENIED){
               // alertaValidacaoPermissao();

            }
        }
    }

    private void alertaValidacaoPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.tab_signup:
                showRegisterView();
                break;
            case R.id.tab_signin:
                showLoginView();
                break;
            }
    }
    public void btn_fb(View View) {
        //signInFacebook();
         startActivity(new Intent(this, NavigationMapasActivity.class));
    }

    public void btn_google(View View) {
        signInGoogle();
    }
    public void btn_entra (View View){
        loginEmail();
    }
    public void btn_forgot_password(View View){
        recuperaSenha();

    }

    public void btn_cadastro(View View){
        //cadastrar();

        FirebaseAuth.getInstance().signOut();

        LoginManager.getInstance().logOut();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInClient.signOut();



        finish();

        startActivity(new Intent(getBaseContext(),New_Login_Template.class));
    }
    private void showRegisterView(){
        tabSignin.setBackgroundColor(ContextCompat.getColor(this,R.color.tabColorNormal));
        tabSignin.setTextColor(ContextCompat.getColor(this,R.color.tabTextColorNormal));
        tabSignup.setBackgroundColor(ContextCompat.getColor(this,R.color.tabColorActive));
        tabSignup.setTextColor(ContextCompat.getColor(this,R.color.tabTextColorActive));

        layoutSignin.setVisibility(View.GONE);
        layoutSignup.setVisibility(View.VISIBLE);
    }
    private void showLoginView(){
        tabSignup.setBackgroundColor(ContextCompat.getColor(this,R.color.tabColorNormal));
        tabSignup.setTextColor(ContextCompat.getColor(this,R.color.tabTextColorNormal));
        tabSignin.setBackgroundColor(ContextCompat.getColor(this,R.color.tabColorActive));
        tabSignin.setTextColor(ContextCompat.getColor(this,R.color.tabTextColorActive));

        layoutSignup.setVisibility(View.GONE);
        layoutSignin.setVisibility(View.VISIBLE);
    }


    //-------------------------------------------------SERVICOS LOGIN--------------------------------------------------

    private void servicosFacebook(){

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                adicionarContaFacebookaoFirebase(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getBaseContext(),"Cancelado" , Toast.LENGTH_LONG).show();

            }

            @Override
            public void onError(FacebookException error) {

                String resultado = error.getMessage();

                Util.opcoesErro(getBaseContext(),resultado);

            }

        });

    }

    private void servicosGoogle(){

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        googleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void servicosAutenticacao(){


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user!=null){

                    Toast.makeText(getBaseContext(),"Usuario "+ user.getEmail() + " está logado" , Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getBaseContext(),MapsActivity.class));
                }else{

                  // startActivity(new Intent(getBaseContext(),New_Login_Template.class));
                  //  finish();

                }


            }
        };



    }

    //------------------------------------------METODOS DE LOGIN---------------------------------------------------------------

    private void signInFacebook(){

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile","email"));


    }

    private void signInGoogle(){

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if(account == null){

            Intent intent = googleSignInClient.getSignInIntent();

            startActivityForResult(intent,555);
        }else{

            //já existe alem conectado pelo google
            Toast.makeText(getBaseContext(),"Já logado",Toast.LENGTH_LONG).show();
            startActivity(new Intent(getBaseContext(),MapsActivity.class));

            //  googleSignInClient.signOut();


        }

    }


    private void signInEmail(){


        user = auth.getCurrentUser();

        if (user == null){


            startActivity(new Intent(this, New_Login_Template.class));

        }else{

            finish();
            startActivity(new Intent(this, MapsActivity.class));

        }

    }

    private void signInAnonimo(){

        acessarContaAnonimaaoFirebase();

    }



    //---------------------------------------AUTENTICACAO NO FIREBASE---------------------------------------------------------------


    private void adicionarContaFacebookaoFirebase(AccessToken token) {


        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            finish();
                            startActivity(new Intent(getBaseContext(),MapsActivity.class));


                        } else {

                            String resultado = task.getException().toString();

                            Util.opcoesErro(getBaseContext(),resultado);
                        }

                        // ...
                    }
                });
    }

    private void adicionarContaGoogleaoFirebase(GoogleSignInAccount acct) {


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            finish();
                            startActivity(new Intent(getBaseContext(),MapsActivity.class));

                        } else {

                            String resultado = task.getException().toString();

                            Util.opcoesErro(getBaseContext(),resultado);

                        }

                        // ...
                    }
                });
    }


    private void acessarContaAnonimaaoFirebase(){

        auth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            finish();
                            startActivity(new Intent(getBaseContext(),MapsActivity.class));

                        } else {

                            String resultado = task.getException().toString();

                            Util.opcoesErro(getBaseContext(),resultado);


                        }

                        // ...
                    }
                });


    }





    //-------------------------------METODOS DA ACTIVITY--------------------------------------------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        callbackManager.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 555){

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);


            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);

                adicionarContaGoogleaoFirebase(account);


            }catch (ApiException e){

                String resultado = e.getMessage();

                Util.opcoesErro(getBaseContext(),resultado);
            }



        }

    }

    @Override
    protected void onStart() {
        super.onStart();

       auth.addAuthStateListener(authStateListener);

    }

    //-------Métodos de login com email e recuperação de senha


    private void recuperaSenha(){
        String email = edtxtemail.getText().toString().trim();

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
                Toast.makeText(getBaseContext(),"Enviamos uma mensagem com um link para redefinição de senha",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String erro = e.toString();
                Util.opcoesErro(getBaseContext(),erro);
            }
        });
    }

    private void loginEmail(){
        String email = edtxtemail.getText().toString().trim();
        String senha = edttxtsenha.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty()){

            Toast.makeText(getBaseContext(), "Insira os campos obrigatórios", Toast.LENGTH_LONG).show();
        }else{
            if(Util.verificarInternet(this)) {

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
                    startActivity(new Intent(getBaseContext(), MapsActivity.class));
                    Toast.makeText(getBaseContext(), "Usuário logado com sucesso", Toast.LENGTH_LONG).show();
                    finish();
                } else{

                    String resposta = task.getException().toString();
                    Util.opcoesErro(getBaseContext(), resposta);
                }
            }
        });
    }

    //------- Fim Métodos de login com email e recuperação de senha

    //-------- métodos cadastrar
    private void cadastrar() {
        String email = edittextemailcadastro.getText().toString().trim();
        String senha = edittextsenhacadastro.getText().toString().trim();
        String confirmaSenha = edtxtrepetesenha.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty() || confirmaSenha.isEmpty()){
            Toast.makeText(getBaseContext(), "Favor preencher os campos", Toast.LENGTH_LONG).show();
        }else {

            if (senha.contentEquals(confirmaSenha)) {
                if (Util.verificarInternet(this)){

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
                    Util.opcoesErro(getBaseContext(), resposta);
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
    //--------- fim métodos cadastrar

    @Override
    protected void onStop() {
        super.onStop();

        if (authStateListener != null){

            auth.removeAuthStateListener(authStateListener);
        }

    }

}

