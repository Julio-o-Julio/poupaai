# Poupa ai

## Aplicativo Mobile de Controle de Gastos Pessoais: Poupa ai

Aluno: Julio Augusto Monteiro de Souza Filho  
Link Github: [https://github.com/Julio-o-Julio/poupaai](https://github.com/Julio-o-Julio/poupaai)

### Introdução

#### Propósito
O objetivo deste documento é especificar os requisitos funcionais e não-funcionais para o desenvolvimento de um aplicativo mobile de Controle de Gastos Pessoais chamado “Poupa ai”, utilizando a linguagem de programação Java através do Android Studio. Este aplicativo permitirá que os usuários acompanhem seus gastos pessoais – despesas, armazenando os dados localmente usando o Room para persistência dos dados.

#### Escopo
O aplicativo será desenvolvido para dispositivos móveis Android e oferecerá funcionalidades para gerenciamento de gastos pessoais, com recursos adicionais de usabilidade, acessibilidade e segurança.

### Requisitos Funcionais

#### Tela de Login e Cadastro de Usuários
1. O aplicativo deve ter uma opção de cadastro de novos usuários, permitindo que os novos usuários criem suas contas utilizando: nome, email, senha e uma foto ou imagem de usuário opcional.
2. O aplicativo deve ter uma tela inicial de login onde os usuários possam entrar com seu identificador (email) e senha.

#### Gerenciamento de Despesas
1. O aplicativo deve permitir aos usuários registrar, editar e excluir despesas.
2. Cada entrada de despesa deve incluir um valor, categoria, data e descrição.
3. O aplicativo deve permitir que os usuários mandem pedidos de amizade a outros usuários.
4. O aplicativo deve permitir que os usuários aceitem pedidos de amizade recebidos de outros usuários.
5. O aplicativo deve permitir que os usuários vejam as despesas lançadas de seus amigos.
6. As entradas devem ser associadas ao usuário logado, garantindo que o usuário que não tenha amigos veja apenas suas próprias transações.

### Requisitos Obrigatórios

#### Testes de Caixa Preta e Tratamento de Erros
1. O sistema deve realizar testes de caixa preta para garantir que:
   - Um usuário não cadastrado não consiga acessar o sistema.
   - Campos obrigatórios não podem ser deixados em branco.
   - Erros como divisão por zero sejam tratados adequadamente.
   - Entradas inválidas, como textos em campos numéricos, sejam rejeitadas com mensagens de erro apropriadas.

#### Associação de Atividades aos Usuários
1. O aplicativo deve ser capaz de associar as atividades: lançar despesas, mandar pedido de amizade e aceitar pedido de amizade ao usuário logado.
2. O aplicativo deve permitir que os usuários vejam as despesas dos seus amigos somente se eles forem amigos.

#### Utilização de Recursos
1. O aplicativo deve utilizar os seguintes recursos de forma eficiente:
   - Fragmentos para organizar a interface do usuário.
   - Strings, cores, e imagens para uma interface esteticamente agradável.
   - Sons e notificações para alertas de pedidos de amizade.
   - Menus para navegação.

### Requisitos de Segurança

1. O aplicativo deve restringir o acesso aos recursos com base no perfil do usuário.
2. Informações sensíveis, como senhas, devem ser criptografadas e armazenadas de forma segura (por exemplo, usando hash de senha).
3. Deve haver medidas de segurança para proteger os dados dos usuários contra acessos não autorizados.

### Requisitos de Acessibilidade

1. O aplicativo deve ser acessível a usuários com necessidades especiais, incluindo:
   - Configurações de alto contraste e cores para usuários com visão prejudicada ou daltônicos.
   - Navegação fácil para usuários com restrições motoras.

### Requisitos de Usabilidade

#### Interface Intuitiva
1. O aplicativo deve possuir uma interface de usuário intuitiva que permita aos usuários navegar e realizar operações comuns (como registrar despesas, ver amigos, aceitar pedidos de amizade) de forma rápida e sem a necessidade de treinamento ou suporte técnico.

#### Consistência Visual e Funcional
1. A interface do aplicativo deve ser consistente em todas as telas, utilizando padrões de design comuns e ícones padronizados para ações semelhantes. Isso ajuda os usuários a aprenderem e preverem como interagir com o aplicativo.

#### Mensagens de Erro Claras
1. Mensagens de erro devem ser claras e informativas, ajudando o usuário a entender o problema e a forma de corrigi-lo. Por exemplo, se um campo obrigatório estiver vazio, a mensagem de erro deve especificar qual campo precisa ser preenchido.
