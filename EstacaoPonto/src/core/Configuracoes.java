package core;

import java.io.InputStream;
import java.util.Properties;

public enum Configuracoes {

    app_name,
    base_intranet_url;

    private static Properties props;
    private boolean inicialized = false;
    public String get(){
        if(!inicialized){
            init();
        }
        return props.get(this.name()).toString();
    }

    private void init() {
        props = new Properties();
        try {
            InputStream is = getClass().getResourceAsStream("../resources/config.properties");
            props.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
