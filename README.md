# PI_III_GrupoH

Projeto do Aplicativo de autenticação, referente ao Projeto integrador III do curso de Engenharia de Software da PUC(Pontifícia Universidade Católica)

Grupo H:
Gabriel adorno Simoso - 24012009
Hugo Silva Pinheiro - 24012527
Paulo Cesar Whitehead Junior - 24018776
Talitha Barbosa Gentil - 24013755
Thiago Volponi Cosmo - 24020763





**SuperID** é um aplicativo inovador que permite que você faça login em sites e serviços **sem usar senhas**. Além disso, ele funciona como um **gerenciador seguro de senhas tradicionais**, organizando suas credenciais em categorias personalizadas.

---

 1. Criação de Conta
Ao abrir o app pela primeira vez:
- Você verá uma breve explicação sobre o funcionamento do SuperID.
- Será necessário aceitar os **termos de uso**.
- Em seguida, crie sua conta informando:
  - Nome
  - E-mail
  - Senha mestre (única para acessar o app)

> **Atenção**: A validação do e-mail é obrigatória para usar o recurso de login sem senha.

---

Gerenciamento de Senhas

Depois de logado:
- Cadastre, edite ou exclua senhas de acesso.
- Organize-as em categorias como:
  - Sites Web (categoria obrigatória)
  - Aplicativos
  - Teclados de Acesso Físico (ex: painéis numéricos)
- Você pode criar novas categorias conforme desejar.

Cada senha pode conter:
- Login (opcional)
- Senha
- Descrição (opcional)

---

Login Sem Senha

Em sites parceiros:
1. Escolha a opção **Entrar com SuperID** (sem preencher login/senha).
2. O site exibirá um **QR Code**.
3. No app, selecione a opção **Login Sem Senha** e escaneie o QR Code com a câmera.
4. Após a leitura, o app confirmará sua identidade e enviará os dados ao site, que poderá completar o login.

---

Integração com Sites Parceiros

- Sites parceiros utilizam uma função Firebase chamada `performAuth`.
- Eles recebem um QR Code gerado com um `loginToken` exclusivo.
- O app lê o QR Code e envia a confirmação do login para o Firebase.
- O site consulta a função `getLoginStatus` para obter o usuário autenticado.

---

Segurança

- A senha mestre é exigida sempre que o app for aberto (a menos que já esteja em execução).
- O conteúdo sensível é armazenado com criptografia.
- Os tokens de login têm validade limitada e são protegidos contra múltiplas tentativas de uso.

---

Site

Como Rodar Localmente (Firebase)

Este projeto utiliza **Firebase Authentication**, **Cloud Functions** e **Firestore**.

Pré-requisitos

1. [Node.js](https://nodejs.org/)
2. Firebase CLI:
   ```bash
   npm install -g firebase-tools
   firebase login
   firebase init
   firebase emulators:start
   



Tecnologias Utilizadas

- **Kotlin** para desenvolvimento Android
- **Firebase Authentication** para autenticação de usuários
- **Firebase Firestore** como banco de dados em nuvem
- **Firebase Functions** para integração com sites parceiros
- **Node, JS e Firebase serve** para site parceiro de teste
