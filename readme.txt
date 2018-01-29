=====Projeto
O projeto core.EstacaoPonto foi desenvolvido utilizando a Tecnologia JavaFX a qual permite
imbutir dentro de uma aplica��o Desktop Standalone um browser.
Com isso, podemos integrar um dispositivo de leitura biometrica ao sistema Intranet do TJPI de forma �gil
utilizando a linguagem de Programa��o Java.


=====
Estrutura

src
	async			-> Cont�m Servi�os que n�o utilizam a thread principal do JavaFX, e rodam de forma assincrona a aplica��o
	controllers		-> Cont�m toda a ger�ncia da aplica��o
	core			-> Cont�m classes que fazem acesso a recursos do sistema operacional/outros dispositivos
	listeners		-> Cont�m classes que manipulam eventos do JavaFX, como quando o usu�rio acessou uma url diferente
	resources		-> Cont�m arquivos de 'recurso', como imagens, estilos css e configuracao xml do JavaFX
	utils			-> Cont�m classes que cont�m m�todos auxiliares que s�o utilizados na aplica��o
	core.EstacaoPonto.java
	
lib
	bcprov			-> Criptografia
	Registry 		-> Manipulacao Registro Windows
	NBioBSPJNI 		-> Interface portada para Java para manipulacao do dispositivo biometrico

#----------------------------------------------------------------------


Plugue o leitor apenas quando solicitado

1.	Baixar o conteúdo em http://svn.tjpi.jus.br:81/svn/pontodigital

2.	No caminho \res\downloads\registro windows\drivers\registro-windows,
        escolha x32 ou x64 e copie o arquivo ICE_UNIRegistry.dll para a pasta C:\Windows\system32

3.	No caminho \res\downloads\leitor fingkey\enbsp java sdk\Modules\Window\ (x64, se for o caso)
        copie NBioBSPJNI.dll e NBioBSPISO4JNI.dll para Windows/system32

4.	Em \res\downloads\leitor fingkey\(extraia os arquivos de "Nitgen SDK for 64")NITGEN\Driver Hamster I, II, DX e III - x86 e x64
        clique em Setup.exe e escolha USB Fingkey Hamster(HFDUO1/04/06)

5.	Em res\downloads\leitor fingkey\NITGEN\eNBSP SDK v4.841 x64
        clique em setup.exe. Quando solicitado, utilize o serial em SERIAL.txt.
        Instale o NetFrameWork se solicitado.

6.	Na pasta de arquivos baixados, renomeie a pasta core.EstacaoPonto para um nome qualquer.

7.	Crie um projeto JAVA FX com o nome Estacao Ponto
	Deixe marcado ”Usar pasta dedicada para Armazenar bibliotecas”
	Deixar marcado “Criar classe de Aplicação”

8.	No Windows Explorer, apague a pasta src do novo projeto

9.	Copie/recorte a pasta src e lib que você baixou para a pasta do novo projeto (mescle a pasta lib)

10.	Na pasta nbproject abra o arquivo project.properties

11.	Procure a linha "javafx.main.class=estacaoponto.core.EstacaoPonto" e substitua por "javafx.main.class=core.EstacaoPonto"

12.	Abra o projeto no netBeans e adicione os “.jar” existentes na lib que você copiou (NBioBSPJNI.jar, bcprov-jdk14-132.jar e registry.jar)

13. 	Encoding do projeto: ISO-8859-1

14. 	Criar config.properties na raiz do projeto com o seguintes parametros:

		app_name=ESTACAOPONTO
		base_intranet_url=http://localhost:8080/intranet
		#base_intranet_url=http://www.tjpi.jus.br/intranet
		tela_cheia=true
		bloqueio_tela=false
		# 1-9
		nivel_seguranca_leitor=8
		baixa_foto=false

14. 	Se ao executar a aplicação o sistema informar erro: "Sem permissão de escrita", deve-se criar as pastas
		c:\Estacao\img
		c:\Estacao\logs
	manualmente...

    PS:
    SO, NetBeans, JDK e JavaFX precisam ter a mesma versão de bits.
    Em alguns casos a pasta system32 não é reconhecida como path,
    utilize System.getProperty("java.library.path") para saber os diretórios reconhecidos como path
    e coloque os arquivos copiados para system32 (indicado nos passos) em algum deles.

