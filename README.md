# AgenPet 🐾

**AgenPet** é um aplicativo mobile Android para o sistema de agendamento de consultas veterinárias. O projeto foi desenvolvido como parte do Projeto Integrador de Sistemas para Dispositivos Móveis.

O objetivo principal é oferecer uma ferramenta intuitiva e eficiente para que tutores de animais possam gerenciar a saúde de seus pets, agendando consultas e acompanhando seu histórico, enquanto clínicas veterinárias organizam seus horários.

## ✨ Funcionalidades

O aplicativo foi desenvolvido com base nos seguintes requisitos funcionais:

* **Autenticação de Usuário:** Sistema completo de Login e Cadastro de usuário.
* **Gerenciamento de Pets:**
    * Cadastro de múltiplos pets por usuário.
    * Visualização da lista de pets cadastrados com sistema de busca.
    * Edição e visualização dos detalhes de cada pet.
* **Sistema de Agendamento:**
    * Fluxo completo para efetuar novos agendamentos.
    * Visualização do histórico de consultas agendadas e canceladas.
* **Gerenciamento de Perfil:** Visualização e edição dos dados do usuário.
* **Design Responsivo e Acessível:** Interface desenvolvida com foco em usabilidade, consistência e acessibilidade, seguindo as heurísticas de Jakob Nielsen.

## 📱 Telas (Wireframes)

O design do aplicativo foi planejado para ser minimalista e intuitivo, seguindo um guia de estilo consistente em todas as telas.

## 🎨 Guia de Estilo

### Paleta de Cores
A paleta de cores é baseada em um esquema monocromático de azul, escolhida para garantir bom contraste, visibilidade e acessibilidade, especialmente para usuários com daltonismo.

| Cor | Hexadecimal | Utilização |
| :--- | :--- | :--- |
| Cor Primária | `#003366` | Headers, botões principais e ações. |
| Cor de Detalhe | `#6487A7` | Botões não selecionados e notificações.|
| Cor de Fundo | `#E0E0E0` | Fundo principal, inputs de texto. |
| Branco | `#FFFFFF` | Textos, ícones e componentes sobre fundos escuros. |
| Preto | `#000000` | Textos sobre fundos claros. |

### Tipografia
A fonte principal utilizada no projeto é a **Inter**, escolhida por sua excelente legibilidade e design limpo.

## 🛠️ Arquitetura e Tecnologias Utilizadas

O projeto foi desenvolvido seguindo as melhores práticas e arquiteturas modernas para Android:

* **Linguagem:** 100% **Kotlin**
* **Arquitetura:** **MVVM (Model-View-ViewModel)**, separando a lógica de negócio da UI.
* **Android Jetpack:**
    * **ViewModel:** Para gerenciar o estado da UI de forma consciente ao ciclo de vida. Utilizamos um **SharedViewModel** para comunicação entre a Activity e os Fragments.
    * **LiveData:** Para notificar a UI sobre mudanças nos dados de forma reativa.
    * **Navigation Component:** Para gerenciar todo o fluxo de navegação do aplicativo, incluindo a passagem de dados com **Safe Args**.
    * **ViewBinding:** Para referenciar as views de forma segura.
* **UI (Interface do Usuário):**
    * **Material Design 3:** Para componentes de UI modernos e consistentes.
    * **RecyclerView:** Para exibição eficiente de listas (pets e agendamentos).
    * `SwipeRefreshLayout` para a funcionalidade "Puxar para Atualizar".
* **Rede (Networking):**
    * **Retrofit:** Para realizar chamadas à API REST de forma declarativa.
    * **OkHttp:** Como cliente HTTP subjacente do Retrofit.
* **Assincronismo:** **Kotlin Coroutines** para gerenciar operações em segundo plano (como chamadas de rede) sem bloquear a thread principal.

## 🚀 Como Executar o Projeto

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/caiorodri/android-agenpet.git
    ```
2.  **Abra no Android Studio:**
    * Abra o Android Studio.
    * Selecione "Open an existing Project" e navegue até a pasta do projeto clonado.
    * Aguarde o Gradle sincronizar todas as dependências.

3.  **Configure as Variáveis de Ambiente:**
    * Por segurança, as chaves da API não são armazenadas no repositório. Você precisa criá-las localmente.
    * Na pasta raiz do projeto, crie um arquivo chamado `local.properties` (se ele ainda não existir).
    * Adicione as seguintes duas variáveis a este arquivo, substituindo com os valores corretos da sua API:
        ```properties
        API_URL="API_URL"
        API_NAME="API_NAME"
        ```

4.  **Execute o aplicativo:**
    * Selecione um emulador ou conecte um dispositivo físico.
    * Clique no botão "Run" (▶️) no Android Studio.
