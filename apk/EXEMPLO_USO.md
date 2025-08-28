# 🚀 Exemplos de Uso - SIGLAB Mobile

## 📱 Scripts de Lançamento

### 1. **quick_launch.sh** - Lançamento Rápido

#### Modo Automático (APK Mais Recente)
```bash
cd apk
./quick_launch.sh
```
**Output:**
```
🚀 SIGLAB Mobile - Lançamento Rápido
========================================

📱 Modo: APK Mais Recente (Automático)

✅ Device conectado!
ℹ️  Procurando APK mais recente...
ℹ️  APK mais recente: 20250828_2317_SIGLAB-Mobile-v86-production-appstore-signed.apk
ℹ️  Instalando APK...
✅ APK instalado!
ℹ️  Executando SIGLAB Mobile...
✅ SIGLAB Mobile executado com sucesso! 🎉
```

#### Modo Específico (Nome do Arquivo)
```bash
cd apk
./quick_launch.sh 20250828_2316_SIGLAB-Mobile-v86-production-lmis_moz-signed.apk
```
**Output:**
```
🚀 SIGLAB Mobile - Lançamento Rápido
========================================

📱 Modo: APK Específico
📂 Arquivo: 20250828_2316_SIGLAB-Mobile-v86-production-lmis_moz-signed.apk

✅ Device conectado!
ℹ️  Usando APK especificado: releases/20250828_2316_SIGLAB-Mobile-v86-production-lmis_moz-signed.apk
ℹ️  APK selecionado: 20250828_2316_SIGLAB-Mobile-v86-production-lmis_moz-signed.apk
✅ SIGLAB Mobile executado com sucesso! 🎉
```

#### Modo Específico (Caminho Completo)
```bash
cd apk
./quick_launch.sh releases/20250828_2316_SIGLAB-Mobile-v86-production-lmis_moz-signed.apk
```

### 2. **build_production.sh** - Build de Produção

```bash
cd apk
./build_production.sh
```
**Menu Interativo:**
```
🚀 SIGLAB Mobile - Build de Produção

ℹ️  Escolha o keystore para assinatura:
1) lmis_moz.jks (DEFAULT - Recomendado)
2) appstore.jks (Alternativo)
3) Sair

Escolha uma opção (1-3): 1
```

## 🔄 Fluxo Completo de Desenvolvimento

### Cenário 1: Build e Lançamento Rápido
```bash
# 1. Fazer build de produção
cd apk
./build_production.sh
# Escolher opção 1 (lmis_moz.jks)

# 2. Lançar APK mais recente no device
./quick_launch.sh

# APK automaticamente instalado e executado!
```

### Cenário 2: Testar APK Específico
```bash
# Listar APKs disponíveis
ls releases/

# Lançar APK específico
./quick_launch.sh 20250828_1430_SIGLAB-Mobile-v86-production-lmis_moz-signed.apk
```

### Cenário 3: Comparar Diferentes Keystores
```bash
# Instalar APK com keystore lmis_moz
./quick_launch.sh 20250828_2316_SIGLAB-Mobile-v86-production-lmis_moz-signed.apk

# Desinstalar app atual (se necessário devido a conflito de assinatura)
adb uninstall org.openlmis.core.local

# Instalar APK com keystore appstore
./quick_launch.sh 20250828_2317_SIGLAB-Mobile-v86-production-appstore-signed.apk
```

## 📊 Estrutura de Arquivos Gerados

```
apk/releases/
├── 20250828_1430_SIGLAB-Mobile-v86-production-lmis_moz-signed.apk    # Manhã
├── 20250828_2316_SIGLAB-Mobile-v86-production-lmis_moz-signed.apk    # Tarde
└── 20250828_2317_SIGLAB-Mobile-v86-production-appstore-signed.apk    # Tarde (diferente keystore)
```

### Como Identificar:
- **Data/Hora**: `20250828_2316` = 28/08/2024 às 23:16
- **Keystore**: `lmis_moz` ou `appstore`
- **Ordem**: Arquivos ordenados cronologicamente

## ⚠️ Resolução de Problemas

### Problema: Conflito de Assinatura
```
adb: failed to install: Failure [INSTALL_FAILED_UPDATE_INCOMPATIBLE: 
Existing package signatures do not match newer version]
```

**Solução:**
```bash
# Desinstalar versão atual
adb uninstall org.openlmis.core.local

# Instalar nova versão
./quick_launch.sh nome_do_apk.apk
```

### Problema: Device Não Conectado
```
❌ Nenhum device conectado!
```

**Solução:**
```bash
# Verificar devices
adb devices

# Conectar device via USB e habilitar depuração USB
# Ou iniciar emulador Android
```

### Problema: APK Não Encontrado
```
❌ APK não encontrado: arquivo.apk
```

**Solução:**
```bash
# Listar APKs disponíveis
ls releases/
ls old-releases/

# Usar nome exato do arquivo
./quick_launch.sh nome_correto.apk
```

## 🎯 Dicas de Uso

### 1. **Desenvolvimento Diário**
```bash
# Workflow rápido para testes
cd apk
./build_production.sh  # Build novo APK
./quick_launch.sh       # Instalar e testar
```

### 2. **Testes de Regressão**
```bash
# Testar versão específica
./quick_launch.sh 20250828_1000_SIGLAB-Mobile-v86-production-lmis_moz-signed.apk

# Comparar com versão mais recente
./quick_launch.sh
```

### 3. **Gestão de Versões**
```bash
# Ver APKs por data (mais recente primeiro)
ls -lt releases/

# Ver apenas APKs de um keystore específico
ls releases/*lmis_moz*
ls releases/*appstore*
```

---

**💡 Dica**: Use sempre o mesmo keystore para evitar conflitos de assinatura durante desenvolvimento!

**📅 Última atualização**: Agosto 2024
