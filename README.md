# AgenPet 🐾

**AgenPet** é uma solução completa para gestão de clínicas veterinárias e saúde animal. O projeto consiste em um aplicativo mobile nativo (Android) integrado a um backend robusto (Spring Boot).

O objetivo é conectar tutores, veterinários e recepcionistas em um ecossistema eficiente, permitindo desde o agendamento de consultas até a emissão de receitas médicas digitais.

---

## ✨ Funcionalidades Principais

O sistema opera com perfis de acesso distintos (Cliente, Veterinário, Recepcionista e Administrador), oferecendo recursos específicos para cada um:

### 👤 Para o Tutor (Cliente)
* **Autenticação:** Cadastro e Login seguros.
* **Meus Pets:** Gerenciamento completo (CRUD) dos animais, incluindo foto e dados físicos.
* **Agendamento Fácil:** Busca por tipo de serviço, veterinário e horários disponíveis.
* **Histórico e Receitas:** Visualização de consultas passadas e **download de receitas médicas em PDF** diretamente pelo app. (EM BREVE)
* **Perfil:** Gerenciamento de dados pessoais e preferências.

### 🩺 Para o Veterinário
* **Gestão de Agenda:** Definição dos horários de atendimento (dias da semana e intervalos de horas).
* **Fluxo de Atendimento:** Visualização da agenda diária e realização de consultas.
* **Prontuário Digital (Pós-Consulta):**
    * Registro de diagnóstico.
    * Observações clínicas.
    * Prescrição de medicamentos dinâmica.
* **Dados Clínicos:** Visualização e edição de dados sensíveis do animal durante a consulta.

### 💼 Para a Administração (Recepcionista/Admin)
* **Gestão de Usuários:** Cadastro e edição de funcionários (Veterinários e Recepcionistas).
* **Controle de Horários:** Configuração da grade de horários dos veterinários.

---

## 🎨 Interface e Experiência (UI/UX)

O aplicativo segue as diretrizes do **Material Design 3**, focando em acessibilidade e usabilidade.

* **Design Responsivo:** Layouts adaptáveis (ConstraintLayout, NestedScrollView).
* **Feedback Visual:** Telas de *Loading*, *Empty States* (telas vazias ilustrativas) e tratamento de erros amigável.
* **Internacionalização (i18n):** Suporte completo para **Português (Brasil), Inglês e Espanhol**.
* **Tema:** Suporte a Tema Claro e Escuro (Dark Mode).

### Paleta de Cores
* Baseada em tons monocromáticos de azul para transmitir confiança e saúde. 
* A paleta foi escolhida a dedo para ter suporte ao daltonismo

| Cor | Hexadecimal | Utilização |
| :--- | :--- | :--- |
| **Primary** | `#003366` | Ações principais, headers e botões de destaque. |
| **Secondary** | `#6487A7` | Elementos de apoio e ícones. |
| **Surface** | `#FFFFFF` | Fundos de cards e áreas de conteúdo. |
| **Background** | `#E0E0E0` | Fundo geral da aplicação. |

---

## 🛠️ Arquitetura e Tecnologias

O projeto é dividido em dois grandes módulos: Mobile e Backend.

### 📱 Android (Mobile)
* **Linguagem:** 100% **Kotlin**.
* **Arquitetura:** **MVVM** (Model-View-ViewModel).
* **Jetpack Components:**
    * **Navigation Component:** Fluxo de telas e passagem de argumentos (Safe Args).
    * **Fragment Result API:** Comunicação eficiente entre fragments (ex: atualizar lista após cadastro).
    * **LiveData & ViewModel:** Gestão de estado reativa.
* **Rede:** **Retrofit + OkHttp** para comunicação REST.
* **Imagens:** **Glide** para carregamento e cache de fotos dos pets.
* **PDF:** Integração com `FileProvider` e Intents para visualização de receitas.

### ☕ Backend (API REST) 
* **Linguagem:** Java 17+.
* **Framework:** **Spring Boot 3**.
* **Banco de Dados:** MySQL (com Flyway ou Hibernate DDL Auto).
* **ORM:** JPA / Hibernate.
* **Funcionalidades Extras:**
    * **Scheduler:** Tarefas agendadas para marcar consultas não realizadas como "Perdidas".
    * **OpenPDF:** Geração dinâmica de arquivos PDF para receitas médicas.
    * **Swagger/OpenAPI:** Documentação automática dos endpoints.
 
Obs: a API é encontrada também nesse github (agendamento-veterinario)

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
* Android Studio (versão recente).
* JDK 17 ou superior.
* Banco de Dados MySQL rodando localmente ou em container.

### Passos

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/caiorodri/android-agenpet.git
    ```

2.  **Configuração do Mobile (Android):**
    * Abra a pasta do projeto no Android Studio.
    * Crie o arquivo `local.properties` na raiz (se não existir) e configure a URL da sua API local (ex: IP da sua máquina na rede):
        ```properties
        API_URL="http://192.168.X.X:8080/"
        ```
    * Sincronize o Gradle e execute em um emulador/dispositivo.

3.  **Configuração do Backend:**
    * Certifique-se de que o MySQL está rodando e o schema `agendamento_veterinario` foi criado.
    * Configure o `application.properties` do Spring com suas credenciais de banco.
    * Execute a aplicação Spring Boot.

---

## 👥 Equipe e Créditos

Projeto desenvolvido com foco acadêmico e prático em desenvolvimento mobile full-stack.

* **Desenvolvedores:** Consulte a tela "Sobre > Desenvolvedores" no aplicativo para ver a equipe e links para LinkedIn.
