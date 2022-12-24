# GerenteDeMemoria
O gerente de memória controla a alocação de um conjunto de quadros de memória para que sejam
utilizados por um conjunto de programas virtuais. Estes programas virtuais utilizam um esquema
de endereçamento lógico de 10 bits, dos quais 5 são utilizados para a identificação da página e 5
são utilizados para o deslocamento. Assim, cada programa possui no máximo 32 páginas de
memória, cada qual com 32 bytes.

O gerente de memória é parametrizado com a quantidade de quadros a serem gerenciados. Esta
quantidade é definida por meio do construtor desta classe, sendo múltiplo de 32 (32, 64 ou 128
quadros).

Cada programa virtual possui três diferentes segmentos: texto, dados e pilha. O tamanho do
segmento de texto é definido por um número natural maior do que 1 e menor ou igual a 960 (em
bytes). O tamanho do segmento de dados é também definido por um número natural maior ou igual
a 0 e menor ou igual a 928 (em bytes). O segmento de pilha possui tamanho fixo de 64 bytes. O
tamanho máximo de um programa é 1024 bytes.

Uma vez definido o tamanho do segmento de texto, este não muda. Porém, o segmento de dados
possui uma parte de tamanho fixo (definida na inicialização do programa na memória) e uma parte
de tamanho variável (heap), a qual possui inicialmente tamanho 0, podendo aumentar ou diminuir
dependendo da alocação/liberação dinâmica de memória enquanto o programa permanece em
execução. 
