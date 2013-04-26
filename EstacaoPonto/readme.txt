=====Projeto
O projeto EstacaoPonto foi desenvolvido utilizando a Tecnologia JavaFX a qual permite
imbutir dentro de uma aplicação Desktop Standalone um browser.
Com isso, podemos integrar um dispositivo de leitura biometrica ao sistema Intranet do TJPI de forma ágil
utilizando a linguagem de Programação Java.

Dúvidas: Anderson Soares < aersandersonsoares@gmail.com >


=====
Estrutura

src
	async			-> Contém Serviços que não utilizam a thread principal do JavaFX, e rodam de forma assincrona a aplicação
	controllers		-> Contém toda a gerência da aplicação
	core			-> Contém classes que fazem acesso a recursos do sistema operacional/outros dispositivos
	listeners		-> Contém classes que manipulam eventos do JavaFX, como quando o usuário acessou uma url diferente
	resources		-> Contém arquivos de 'recurso', como imagens, estilos css e configuracao xml do JavaFX
	utils			-> Contém classes que contém métodos auxiliares que são utilizados na aplicação
	EstacaoPonto.java
	
lib
	bcprov			-> Criptografia
	Registry 		-> Manipulacao Registro Windows
	NBioBSPJNI 		-> Interface portada para Java para manipulacao do dispositivo biometrico
	