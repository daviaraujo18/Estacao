package core;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import utils.LogAplicacao;

public enum Configuracoes {

    app_name,
    base_intranet_url,
    tela_cheia,
    bloqueio_tela,
	baixa_foto,
    nivel_seguranca_leitor;

    private static Properties props;
    private boolean inicializado = false;
    public String get(){
        if(!inicializado){
            init();
        }
        Object prop = props.get(this.name());
        if (prop ==  null)
            prop = "";
        return prop.toString();
    }

    public boolean getBooleanValue(){
        return this.get().toUpperCase().equals("TRUE");
    }

    public int getIntValue(){
        return Integer.parseInt(this.get());
    }

    private void init() {
        props = new Properties();
        try {
            InputStream is;
//            if (EstacaoPonto.ambiente.equals("desenvolvimento"))
//            {
//                is =   new FileInputStream("C:/Downloads/docs/NetBeans/EstacaoPonto/dist/config.properties");//desenvolvimento
//            }
//            else
//            {
                is = new FileInputStream("./config.properties");//teste //produ��o
//            }
            
            props.load(is);
        } catch (Exception e) {
			LogAplicacao.e(e);
//            e.printStackTrace();
        }
    }

}
