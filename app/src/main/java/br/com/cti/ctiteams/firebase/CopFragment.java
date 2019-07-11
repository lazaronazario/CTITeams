package br.com.cti.ctiteams.firebase;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import br.com.cti.ctiteams.R;

public class CopFragment extends Fragment {
    String recebe;
    EditText ip;
    private ProgressDialog progress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_cop, container, false);

        ip = (EditText) myView.findViewById(R.id.editIp);
        return myView;
    }
    public void sendGetRequest(View View) {
        recebe = ip.getText().toString().trim() + "/atuador/fluxo/lento";
        new GetClass(getContext()).execute();
    }

    public void sendGetRequest2(View View) {
        recebe = ip.getText().toString().trim()+ "/atuador/fluxo/normal";
        new GetClass(getContext()).execute();
    }

    public void sendGetRequest3(View View) {
        recebe = ip.getText().toString().trim() + "/atuador/fluxo/rapido";
        new GetClass(getContext()).execute();
    }

    private class GetClass extends AsyncTask<String, Void, Void> {
        private final Context context;
        public GetClass(Context c){
            this.context = c;
        }
        protected void onPreExecute(){
            progress= new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();
        }
        @Override
        protected Void doInBackground(String... params) {
            try {
                final TextView outputView = (TextView) getView().findViewById(R.id.showOutput);
                URL url = new URL(recebe);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                String urlParameters = "fizz=buzz";
                connection.setRequestMethod("GET");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                int responseCode = connection.getResponseCode();
                final StringBuilder output = new StringBuilder("Request URL " + url );
                output.append(System.getProperty("line.separator") + "Response Code " + responseCode);
                output.append(System.getProperty("line.separator") + "Type " + "GET");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();
                System.out.println("output===============" + br);
                while((line = br.readLine()) != null ) {
                    responseOutput.append(line);
                }
                br.close();
                output.append(System.getProperty("line.separator") + "Response " + System.getProperty("line.separator") + System.getProperty("line.separator") + responseOutput.toString());
                CopFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        outputView.setText(output);
                        progress.dismiss();
                    }
                });
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }
}
