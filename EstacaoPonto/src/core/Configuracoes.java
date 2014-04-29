package core;

import java.io.File;
import java.io.FileInputStream;
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
            String path = new File(".").getCanonicalPath();
            InputStream is =   new FileInputStream(path+"/build/clsses/resources/config.properties");
            props.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
