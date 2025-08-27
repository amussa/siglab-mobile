# Documentação Técnica - SIGLAB Mobile

## 1. Visão Geral do Sistema

### 1.1 Introdução
O **SIGLAB Mobile** (Sistema de Informação de Gestão Logística de Laboratório) é a componente móvel do sistema SIGLAB, desenvolvido para dispositivos Android. É uma adaptação do OpenLMIS (Open Logistics Management Information System) especificamente configurado para gestão logística de laboratórios em Moçambique.

### 1.2 Objetivo
O aplicativo móvel permite aos laboratórios de saúde gerir:
- Inventário e stock de materiais laboratoriais
- Requisições e formulários específicos para diferentes equipamentos laboratoriais
- Testes rápidos e programas de saúde
- Sincronização de dados com servidor central
- Relatórios e análises de consumo

### 1.3 Arquitetura de Alto Nível

```mermaid
graph TB
    subgraph "SIGLAB Mobile App"
        A[UI Layer<br/>Activities & Fragments]
        B[Presenter Layer<br/>MVP Pattern]
        C[Service Layer<br/>Business Logic]
        D[Repository Layer<br/>Data Access]
        E[Database<br/>SQLite + OrmLite]
    end
    
    subgraph "Backend"
        F[SIGLAB Server<br/>REST APIs]
        G[Central Database]
    end
    
    A --> B
    B --> C
    C --> D
    D --> E
    D <--> F
    F --> G
    
    style A fill:#e1f5fe
    style B fill:#b3e5fc
    style C fill:#81d4fa
    style D fill:#4fc3f7
    style E fill:#29b6f6
    style F fill:#ffecb3
    style G fill:#ffe082
```

## 2. Tecnologias e Frameworks

### 2.1 Stack Tecnológico Principal

| Componente | Tecnologia | Versão | Propósito |
|------------|------------|---------|-----------|
| **Linguagem** | Java | 8 | Desenvolvimento principal |
| **Plataforma** | Android | SDK 27, Min SDK 17 | Sistema operacional móvel |
| **Build Tool** | Gradle | - | Compilação e gestão de dependências |
| **DI Framework** | RoboGuice | 3.0.1 | Injeção de dependências |
| **ORM** | OrmLite | 4.45 | Mapeamento objeto-relacional |
| **Networking** | Retrofit | 1.9.0 | Comunicação REST API |
| **Reactive** | RxJava/RxAndroid | 1.0.14/1.0.1 | Programação reativa |
| **Testing** | JUnit, Robolectric, Mockito | - | Testes unitários |
| **Testing UI** | Calabash | 0.9.0 | Testes funcionais |
| **Analytics** | Google Analytics, Crashlytics | - | Monitoramento e crash reports |

### 2.2 Padrão Arquitetural - MVP (Model-View-Presenter)

```mermaid
classDiagram
    class View {
        <<interface>>
        +showLoading()
        +showData()
        +showError()
    }
    
    class Activity {
        +onCreate()
        +initView()
        +bindEvents()
    }
    
    class Presenter {
        -View view
        -Repository repository
        +attachView(View)
        +loadData()
        +processBusinessLogic()
    }
    
    class Model {
        +id: long
        +createdAt: Date
        +updatedAt: Date
    }
    
    class Repository {
        -DbUtil dbUtil
        +create(Model)
        +update(Model)
        +queryAll()
        +delete(Model)
    }
    
    Activity --|> View : implements
    Activity --> Presenter : uses
    Presenter --> View : updates
    Presenter --> Repository : data operations
    Repository --> Model : manages
```

## 3. Estrutura do Projeto

### 3.1 Organização de Diretórios

```
siglab-mobile/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/org/openlmis/core/
│   │   │   │   ├── model/           # Modelos de dados
│   │   │   │   ├── view/            # Activities e Adapters
│   │   │   │   ├── presenter/       # Presenters (MVP)
│   │   │   │   ├── service/         # Serviços e sincronização
│   │   │   │   ├── persistence/     # Camada de persistência
│   │   │   │   ├── network/         # Comunicação de rede
│   │   │   │   ├── manager/         # Gerenciadores
│   │   │   │   └── utils/           # Utilitários
│   │   │   ├── res/                 # Recursos (layouts, strings, etc)
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                    # Testes unitários
│   │   └── [dev|qa|uat|prd|training|showcase]/ # Ambientes
│   └── build.gradle
├── functionalTests/                  # Testes funcionais Calabash
├── contractTests/                    # Testes de contrato
└── docker/                          # Configurações Docker
```

## 4. Principais Funcionalidades

### 4.1 Programas de Laboratório Suportados

O sistema suporta múltiplos programas específicos para diferentes equipamentos e protocolos laboratoriais:

```mermaid
graph LR
    A[SIGLAB Mobile] --> B[Programas Gerais]
    A --> C[Equipamentos Abbott]
    A --> D[Equipamentos Roche]
    A --> E[Outros Equipamentos]
    
    B --> B1[VIA - Via Clássica]
    B --> B2[MMIA - MMIA]
    B --> B3[AL - Malária]
    B --> B4[PTV - Prevenção Transmissão Vertical]
    B --> B5[TEST_KIT - Testes Rápidos]
    
    C --> C1[Abbott M2000]
    C --> C2[Abbott Alinity M]
    
    D --> D1[Roche Cobas 6800]
    D --> D2[Roche Cobas 5800]
    D --> D3[Roche CAPCTM 96]
    
    E --> E1[Hologic Panter]
    E --> E2[Biosecurity Material]
    E --> E3[mPIMA]
    
    style A fill:#f9f
    style B fill:#bbf
    style C fill:#bfb
    style D fill:#fbf
    style E fill:#ffb
```

### 4.2 Fluxo de Dados Principal

```mermaid
sequenceDiagram
    participant U as Usuário
    participant A as Activity
    participant P as Presenter
    participant R as Repository
    participant D as Database
    participant S as Server
    
    U->>A: Interage com UI
    A->>P: Chama método do presenter
    P->>R: Solicita dados
    R->>D: Query local
    D-->>R: Retorna dados
    
    alt Necessita sincronização
        R->>S: Sync request
        S-->>R: Sync response
        R->>D: Atualiza dados locais
    end
    
    R-->>P: Retorna dados processados
    P-->>A: Atualiza View
    A-->>U: Mostra resultado
```

## 5. Modelos de Dados Principais

### 5.1 Hierarquia de Modelos

```mermaid
classDiagram
    BaseModel <|-- Product
    BaseModel <|-- StockCard
    BaseModel <|-- RnRForm
    BaseModel <|-- Program
    BaseModel <|-- User
    BaseModel <|-- StockMovementItem
    BaseModel <|-- Regimen
    BaseModel <|-- ProgramDataForm
    
    class BaseModel {
        -long id
        -Date createdAt
        -Date updatedAt
    }
    
    class Product {
        -String code
        -String primaryName
        -String strength
        -boolean isKit
        -boolean isBasic
        -boolean isActive
    }
    
    class StockCard {
        -Product product
        -long stockOnHand
        -float avgMonthlyConsumption
        +getStockMovementItems()
    }
    
    class RnRForm {
        -Program program
        -STATUS status
        -Date periodBegin
        -Date periodEnd
        -boolean synced
        +getRnrFormItems()
    }
    
    class Program {
        -String programCode
        -String programName
        -boolean isSupportEmergency
    }
    
    StockCard "1" --> "1" Product : has
    StockCard "1" --> "*" StockMovementItem : contains
    RnRForm "*" --> "1" Program : belongs to
    Product "*" --> "1" Program : belongs to
```

### 5.2 Estados de Requisição (RnR Form)

```mermaid
stateDiagram-v2
    [*] --> DRAFT: Criar nova
    DRAFT --> SUBMITTED: Submeter
    SUBMITTED --> AUTHORIZED: Autorizar
    SUBMITTED --> DRAFT: Rejeitar
    AUTHORIZED --> SYNCED: Sincronizar
    SYNCED --> [*]
    
    DRAFT --> DRAFT_MISSED: Período perdido
    DRAFT_MISSED --> SUBMITTED_MISSED: Submeter atrasado
    SUBMITTED_MISSED --> AUTHORIZED: Autorizar
```

## 6. Sistema de Sincronização

### 6.1 Arquitetura de Sincronização

```mermaid
graph TB
    subgraph "Mobile Device"
        A[SyncService] --> B[SyncAdapter]
        B --> C[SyncUpManager]
        B --> D[SyncDownManager]
        C --> E[Local Database]
        D --> E
    end
    
    subgraph "Server"
        F[REST API Endpoints]
        G[Server Database]
    end
    
    C -.-> F
    D -.-> F
    F --> G
    
    H[NetworkChangeReceiver] --> A
    I[BootCompletedReceiver] --> A
    J[Periodic Sync Timer] --> A
    
    style A fill:#e8f5e9
    style B fill:#c8e6c9
    style C fill:#a5d6a7
    style D fill:#81c784
    style F fill:#fff3e0
    style G fill:#ffe0b2
```

### 6.2 Fluxo de Sincronização

```mermaid
sequenceDiagram
    participant T as Timer/Trigger
    participant SA as SyncAdapter
    participant SDM as SyncDownManager
    participant SUM as SyncUpManager
    participant API as Server API
    participant DB as Local DB
    
    T->>SA: onPerformSync()
    SA->>SDM: syncDownServerData()
    
    SDM->>API: Fetch Programs
    API-->>SDM: Programs data
    SDM->>DB: Save Programs
    
    SDM->>API: Fetch Products
    API-->>SDM: Products data
    SDM->>DB: Save Products
    
    SDM->>API: Fetch Stock Cards
    API-->>SDM: Stock Cards data
    SDM->>DB: Save Stock Cards
    
    SA->>SUM: syncRnr()
    SUM->>DB: Query unsync forms
    DB-->>SUM: Unsync forms
    SUM->>API: Submit forms
    API-->>SUM: Success
    SUM->>DB: Mark as synced
    
    SA->>SUM: syncStockCards()
    SUM->>API: Submit stock data
    API-->>SUM: Success
```

## 7. Gestão de Stock

### 7.1 Movimentações de Stock

```mermaid
graph LR
    subgraph "Tipos de Movimento"
        A[RECEIVE<br/>Recebimentos]
        B[ISSUE<br/>Saídas]
        C[POSITIVE_ADJUST<br/>Ajuste Positivo]
        D[NEGATIVE_ADJUST<br/>Ajuste Negativo]
        E[PHYSICAL_INVENTORY<br/>Inventário Físico]
        F[LOSSES<br/>Perdas]
    end
    
    subgraph "Stock Card"
        G[Stock On Hand<br/>Quantidade em Mão]
    end
    
    A --> |Aumenta| G
    B --> |Diminui| G
    C --> |Aumenta| G
    D --> |Diminui| G
    E --> |Define| G
    F --> |Diminui| G
```

### 7.2 Razões de Movimento Específicas

**Recebimentos (RECEIVE):**
- QUIMOFAR - Fornecedor Quimofar
- HOSPITEC - Fornecedor Hospitec
- THL - Fornecedor THL
- DISTRICT_DDM - Distrito (DDM)
- OTHERS - Outros

**Saídas (ISSUE):**
- UNPACK_KIT - Desempacotar kit
- Equipamentos específicos (Abbott, Roche, etc.)

**Ajustes e Perdas:**
- Produtos expirados
- Produtos danificados
- Correções de inventário
- Empréstimos
- Devoluções

## 8. Testes Rápidos (Rapid Tests)

### 8.1 Estrutura de Dados

```mermaid
classDiagram
    class RapidTestForm {
        -Period period
        -STATUS status
        -List~RapidTestFormItem~ items
        +validate()
        +submit()
    }
    
    class RapidTestFormItem {
        -String testType
        -Long consumptionValue
        -Long positiveValue
        -Long unjustifiedValue
        +validateValues()
    }
    
    class TestType {
        <<enumeration>>
        HIVDetermine
        HIVUnigold
        Syphillis
        Malaria
    }
    
    RapidTestForm "1" --> "*" RapidTestFormItem : contains
    RapidTestFormItem --> TestType : type
```

## 9. Sistema de Testes

### 9.1 Estrutura de Testes

```mermaid
graph TD
    A[Testes SIGLAB Mobile] --> B[Testes Unitários<br/>JUnit + Robolectric]
    A --> C[Testes Funcionais<br/>Calabash]
    A --> D[Testes de Contrato<br/>RSpec]
    
    B --> B1[Repository Tests]
    B --> B2[Presenter Tests]
    B --> B3[Service Tests]
    B --> B4[Model Tests]
    
    C --> C1[Feature Tests]
    C --> C2[Regression Tests]
    C --> C3[Stress Tests]
    C --> C4[Upgrade Tests]
    
    style A fill:#e8eaf6
    style B fill:#c5cae9
    style C fill:#9fa8da
    style D fill:#7986cb
```

### 9.2 Cobertura de Testes

- **Testes Unitários**: Cobrem lógica de negócio, repositórios, presenters e serviços
- **Testes Funcionais**: Validam fluxos completos de usuário usando Calabash
- **Testes de Regressão**: Garantem que funcionalidades existentes não quebrem
- **Testes de Stress**: Validam performance com grandes volumes de dados

## 10. Persistência de Dados

### 10.1 Migrações de Database

O sistema usa um sistema de migrações incrementais para evoluir o schema do banco de dados:

```mermaid
graph LR
    A[CreateTables] --> B[AddProducts]
    B --> C[AddStockCards]
    C --> D[AddRnRForms]
    D --> E[AddRapidTests]
    E --> F[AddNewPrograms]
    F --> G[UpdateColumns]
    G --> H[Current Schema]
    
    style A fill:#fff3e0
    style H fill:#c8e6c9
```

### 10.2 Repositórios Principais

| Repositório | Responsabilidade |
|-------------|------------------|
| **ProductRepository** | Gestão de produtos |
| **StockRepository** | Controle de stock cards |
| **RnrFormRepository** | Formulários de requisição |
| **UserRepository** | Dados de usuários |
| **ProgramRepository** | Programas disponíveis |
| **InventoryRepository** | Inventários físicos |
| **ProgramDataFormRepository** | Formulários de dados de programa |

## 11. Segurança e Autenticação

### 11.1 Fluxo de Autenticação

```mermaid
sequenceDiagram
    participant U as Usuário
    participant LA as LoginActivity
    participant LP as LoginPresenter
    participant API as Server API
    participant AM as AccountManager
    participant DB as Local DB
    
    U->>LA: Enter credentials
    LA->>LP: login(username, password)
    LP->>API: authenticate
    API-->>LP: User data + token
    LP->>AM: Create sync account
    LP->>DB: Save user data
    LP-->>LA: Login success
    LA->>U: Navigate to Home
```

## 12. Funcionalidades por Ambiente

### 12.1 Ambientes Disponíveis

| Ambiente | Package ID | Uso |
|----------|------------|-----|
| **Local** | org.openlmis.core.local | Desenvolvimento local |
| **Dev** | org.openlmis.core.dev | Desenvolvimento |
| **QA** | org.openlmis.core.qa | Testes QA |
| **UAT** | org.openlmis.core.uat | Testes de aceitação |
| **Training** | org.openlmis.core.training | Treinamento |
| **Production** | org.openlmis.core | Produção |
| **Showcase** | org.openlmis.core.showcase | Demonstração |

### 12.2 Feature Toggles

```xml
<bool name="feature_archive_old_data">true</bool>
<bool name="feature_training">false</bool>
<bool name="feature_rapid_test">true</bool>
<bool name="feature_all_drugs_movements_history">true</bool>
<bool name="feature_basic_products_in_inventory">true</bool>
<bool name="feature_patient_data">false</bool>
```

## 13. Integração com Equipamentos Laboratoriais

### 13.1 Equipamentos Suportados

```mermaid
graph TB
    subgraph "Abbott"
        A1[M2000]
        A2[Alinity M]
    end
    
    subgraph "Roche"
        R1[Cobas 6800]
        R2[Cobas 5800]
        R3[CAPCTM 96]
    end
    
    subgraph "Outros"
        O1[Hologic Panter]
        O2[POC mPIMA]
    end
    
    subgraph "Materiais"
        M1[Biosecurity Material]
    end
```

Cada equipamento tem seu próprio programa e formulário de requisição específico, adaptado às necessidades e consumíveis particulares de cada máquina.

## 14. Monitoramento e Analytics

### 14.1 Google Analytics

O sistema rastreia:
- Navegação entre telas
- Ações importantes (submissão de formulários, sincronização)
- Erros e exceções
- Tempo de uso

### 14.2 Crashlytics

- Monitoramento de crashes em produção
- Stack traces detalhados
- Métricas de estabilidade
- Alertas automáticos

## 15. Considerações de Performance

### 15.1 Otimizações Implementadas

1. **Cache de Dados**: Dados frequentemente acessados são mantidos em cache
2. **Lazy Loading**: Carregamento sob demanda de listas grandes
3. **Batch Operations**: Operações em lote para reduzir transações de DB
4. **Sincronização Incremental**: Apenas dados alterados são sincronizados
5. **Compressão**: Dados são comprimidos antes do envio ao servidor

### 15.2 Gestão de Memória

```mermaid
graph LR
    A[MultiDex Enabled] --> B[Suporte a APKs grandes]
    C[ProGuard Rules] --> D[Otimização de código]
    E[Memory Leaks Detection] --> F[Stetho Integration]
```

## 16. Processo de Build e Deploy

### 16.1 Pipeline de Build

```mermaid
graph LR
    A[Source Code] --> B[Gradle Build]
    B --> C[Run Tests]
    C --> D[Static Analysis<br/>Checkstyle/FindBugs]
    D --> E[Generate APK]
    E --> F[Sign APK]
    F --> G[Deploy]
    
    style A fill:#e8f5e9
    style G fill:#c8e6c9
```

### 16.2 Comandos Principais

```bash
# Testes unitários
./gradlew testLocalDebug

# Testes funcionais
./gradlew functionalTests

# Build de produção
./gradlew assemblePrdRelease

# Análise estática
./gradlew checkstyle findbugs
```

## 17. Conclusão

O SIGLAB Mobile é um sistema robusto e completo para gestão logística laboratorial, com arquitetura bem definida, testes abrangentes e suporte para múltiplos programas e equipamentos laboratoriais. A aplicação segue boas práticas de desenvolvimento Android, incluindo:

- Padrão MVP para separação de responsabilidades
- Injeção de dependências com RoboGuice
- Programação reativa com RxJava
- Testes automatizados em múltiplos níveis
- Sincronização robusta offline-first
- Suporte para múltiplos ambientes e configurações

O sistema está preparado para escalar e adicionar novos programas e funcionalidades conforme necessário para atender às necessidades do sistema de saúde de Moçambique.
