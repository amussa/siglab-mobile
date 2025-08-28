# 🔐 Documentação de Assinatura - SIGLAB Mobile

## 📋 Keystores Disponíveis

O projeto SIGLAB Mobile possui **2 keystores** disponíveis para assinatura de APKs de produção:

### 1. **lmis_moz.jks** (DEFAULT - Recomendado)
- **Localização**: `../scripts/lmis_moz.jks`
- **Organização**: Clinton Health Access Initiative
- **Alias**: `clintonhealthaccess`
- **Senha do Keystore**: `!EBus&tre46A`
- **Senha da Chave**: `!EBus&tre46A`
- **Algoritmo**: SHA256withRSA (2048-bit)
- **Validade**: Até 2040-09-03
- **Status**: ✅ **CONFIGURAÇÃO PADRÃO**

### 2. **appstore.jks** (Alternativo)
- **Localização**: `../appstore/appstore.jks`
- **Organização**: Unknown
- **Alias**: `clintonhealthaccess`
- **Senha do Keystore**: `password`
- **Senha da Chave**: `password`
- **Algoritmo**: SHA256withRSA (2048-bit)
- **Validade**: Até 2043-01-31
- **Status**: ⚠️ **Alternativo**

---

## 🛠️ Comandos de Build

### Build com lmis_moz.jks (DEFAULT)

```bash
# 1. Configurar o build.gradle (já está configurado por padrão)
# Configuração atual em app/build.gradle:
#   storeFile file("../scripts/lmis_moz.jks")
#   storePassword "!EBus&tre46A"
#   keyAlias "clintonhealthaccess"
#   keyPassword "!EBus&tre46A"

# 2. Fazer build limpo
./gradlew clean assemblePrdRelease

# 3. APK gerado em:
# app/build/outputs/apk/prd/release/org.openlmis.core-86-release.apk
```

### Build com appstore.jks (Alternativo)

```bash
# 1. Alterar configuração no app/build.gradle
# Substituir a seção signingConfigs.release por:
#   storeFile file("../appstore/appstore.jks")
#   storePassword "password"
#   keyAlias "clintonhealthaccess"
#   keyPassword "password"

# 2. Fazer build limpo
./gradlew clean assemblePrdRelease

# 3. APK gerado em:
# app/build/outputs/apk/prd/release/org.openlmis.core-86-release.apk
```

---

## 🔧 Assinatura Manual (se necessário)

### Com lmis_moz.jks
```bash
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
  -keystore ../scripts/lmis_moz.jks \
  -storepass '!EBus&tre46A' \
  -keypass '!EBus&tre46A' \
  app/build/outputs/apk/prd/release/org.openlmis.core-86-release.apk \
  clintonhealthaccess
```

### Com appstore.jks
```bash
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
  -keystore ../appstore/appstore.jks \
  -storepass 'password' \
  -keypass 'password' \
  app/build/outputs/apk/prd/release/org.openlmis.core-86-release.apk \
  clintonhealthaccess
```

---

## ✅ Verificação de Assinatura

```bash
# Verificar se APK está assinado
jarsigner -verify caminho/para/o/arquivo.apk

# Verificar detalhes da assinatura
jarsigner -verify -verbose -certs caminho/para/o/arquivo.apk
```

---

## 📁 Estrutura de Arquivos

```
siglab-mobile/
├── scripts/
│   └── lmis_moz.jks          # Keystore principal (DEFAULT)
├── appstore/
│   └── appstore.jks          # Keystore alternativo
└── apk/
    ├── README_ASSINATURA.md  # Esta documentação
    ├── build_production.sh   # Script de build
    ├── launch_on_device.sh   # Script de lançamento (interativo)
    ├── quick_launch.sh       # Script de lançamento rápido
    ├── KEYSTORES_INFO.txt    # Resumo dos keystores
    ├── releases/             # 📁 Diretoria para APKs gerados
    │   ├── README.md         # Documentação dos releases
    │   └── YYYYMMDD_HHMM_SIGLAB-Mobile-v86-production-[keystore]-signed.apk
    └── old-releases/         # APKs antigos (histórico)
```

---

## ⚠️ Notas Importantes

1. **Keystore Padrão**: O projeto está configurado para usar `lmis_moz.jks` por padrão
2. **Senhas Especiais**: A senha `!EBus&tre46A` contém caracteres especiais - use aspas simples
3. **Algoritmo Seguro**: Ambos keystores usam SHA256withRSA (recomendado)
4. **Backup**: Mantenha backup seguro dos keystores - são únicos e irreversíveis
5. **Segurança**: Nunca compartilhe senhas em repositórios públicos

---

## 🚀 Build Rápido

Para build rápido de produção, use o script:
```bash
cd apk
./build_production.sh
```

## 📱 Lançar no Device

### Lançamento Rápido (Automático)
```bash
cd apk
./quick_launch.sh                           # APK mais recente
./quick_launch.sh nome_do_arquivo.apk       # APK específico
```
- **Modo automático**: Instala o APK mais recente automaticamente
- **Modo específico**: Instala o APK especificado
- Procura automaticamente nas diretorias `releases/` e `old-releases/`
- Executa o aplicativo após instalação

### Lançamento com Seleção (Interativo)
```bash
cd apk
./launch_on_device.sh
```
- Menu interativo para escolher APK
- Opções para instalar ou apenas executar
- Informações detalhadas do device e APK

📦 APKs Gerados

Todos os APKs de produção são salvos em `apk/releases/` com informações completas de versão:
- Formato: `YYYYMMDD_HHMM_SIGLAB-Mobile-[versionName]-[versionCode]-[buildVariant]-[keystore]-signed.apk`
- Exemplo: `20250829_0038_SIGLAB-Mobile-1.12.86.240216-86-prd-lmis_moz-signed.apk`
- Componentes:
  - `20250829_0038`: Data e hora do build
  - `1.12.86.240216`: Version name (semanticVersion)
  - `86`: Version code (androidVersionCode)
  - `prd`: Build variant
  - `lmis_moz`: Keystore usado

---

**Última atualização**: Agosto 2024  
**Versão do APK**: v86  
**Compatibilidade**: Android 9-14
