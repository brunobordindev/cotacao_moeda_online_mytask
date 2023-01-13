package br.com.cotacaomoedaonline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import br.com.cotacaomoedaonline.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Moeda moeda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.btnDollar.setOnClickListener(view -> {

            binding.linearCampos.setVisibility(View.VISIBLE);
            MyTask task = new MyTask();
            String urlDollar = "https://economia.awesomeapi.com.br/json/last/USD-BRL";
            task.execute(urlDollar);

        });

        binding.btnCompra.setOnClickListener(view -> {

            if (!binding.editCompra.getText().toString().isEmpty()){

                double pegarValor = Double.parseDouble(binding.editCompra.getText().toString());
                double passarValorInteiro = Double.parseDouble(moeda.getValorDolarCompra());
                double total = pegarValor / passarValorInteiro;

                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                String resultado = decimalFormat.format(total);

                binding.textResultadoCompra.setVisibility(View.VISIBLE);
                binding.textResultadoCompra.setText("U$ " + resultado);

            }else{
                Toast.makeText(getApplicationContext(), "Preencha o valor primeiro!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnVenda.setOnClickListener(view -> {

            if (!binding.editVenda.getText().toString().isEmpty()){

                double pegarValor = Double.parseDouble(binding.editVenda.getText().toString());
                double passarValorInteiro = Double.parseDouble(moeda.getValorDolarVenda());
                double total = pegarValor * passarValorInteiro;

                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                String resultado = decimalFormat.format(total);

                binding.textResultadoVenda.setVisibility(View.VISIBLE);
                binding.textResultadoVenda.setText("R$ " + resultado);

            }else{
                Toast.makeText(getApplicationContext(), "Preencha o valor primeiro!", Toast.LENGTH_SHORT).show();
            }

        });
    }

    class MyTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {

            String stringUrl = strings[0];
            InputStream inputStream = null;
            InputStreamReader inputStreamReader = null;
            StringBuffer buffer = null;

            try {

                URL url = new URL(stringUrl);
                HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                inputStream = conexao.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);

                BufferedReader reader = new BufferedReader(inputStreamReader);

                buffer = new StringBuffer();
                String linha = "";

                while ((linha = reader.readLine()) != null){
                    buffer.append(linha);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return buffer.toString();
        }

        @Override
        protected void onPostExecute(String resultado) {
            super.onPostExecute(resultado);

            String objetoValorReal = null;
            String dollarCompra = null;
            String dollarVenda = null;

            try {
                JSONObject jsonObject = new JSONObject(resultado);
                objetoValorReal = jsonObject.getString("USDBRL");

                JSONObject jsonObjectValor = new JSONObject(objetoValorReal);
                dollarCompra = jsonObjectValor.getString("bid");
                dollarVenda = jsonObjectValor.getString("ask");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            moeda = new Moeda();
            moeda.setValorDolarCompra(dollarCompra);
            moeda.setValorDolarVenda(dollarVenda);

            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            String valorCompra = decimalFormat.format(Double.parseDouble(moeda.getValorDolarCompra()));
            String valorVenda = decimalFormat.format(Double.parseDouble(moeda.getValorDolarVenda()));

            binding.textDollarCompra.setText("Valor de compra R$ " + valorCompra);
            binding.textDollarVenda.setText("Valor de venda R$ " + valorVenda);
        }
    }
}