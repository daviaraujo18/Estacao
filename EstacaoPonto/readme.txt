=====Projeto
O projeto core.EstacaoPonto foi desenvolvido utilizando a Tecnologia JavaFX a qual permite
imbutir dentro de uma aplica��o Desktop Standalone um browser.
Com isso, podemos integrar um dispositivo de leitura biometrica ao sistema Intranet do TJPI de forma �gil
utilizando a linguagem de Programa��o Java.

D�vidas: Anderson Soares < aersandersonsoares@gmail.com >


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
	