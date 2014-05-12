package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public enum Configuracoes {

    app_name,
    base_intranet_url,
    tela_cheia,
    bloqueio_tela;

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
            String path = new File(".").getCanonicalPath();
            InputStream is =   new FileInputStream(path+"/build/classes/resources/config.properties");
            props.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
