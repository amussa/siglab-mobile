#!/bin/bash

# =============================================================================
# SIGLAB Mobile - Lançamento Rápido
# =============================================================================
# Script simples para instalar e executar o APK mais recente
# =============================================================================

set -e

# Cores
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

print_info() { echo -e "${BLUE}ℹ️  $1${NC}"; }
print_success() { echo -e "${GREEN}✅ $1${NC}"; }
print_error() { echo -e "${RED}❌ $1${NC}"; }

echo -e "${BLUE}🚀 SIGLAB Mobile - Lançamento Rápido${NC}"
echo "========================================"
echo
if [[ -n "${1:-}" ]]; then
    echo "📱 Modo: APK Específico"
    echo "📂 Arquivo: ${1}"
else
    echo "📱 Modo: APK Mais Recente (Automático)"
fi
echo

# Verificar ADB
if ! command -v adb &> /dev/null; then
    print_error "ADB não encontrado!"
    exit 1
fi

# Verificar device
if [[ $(adb devices | grep -v "List of devices" | grep -v "^$" | wc -l) -eq 0 ]]; then
    print_error "Nenhum device conectado!"
    exit 1
fi

print_success "Device conectado!"

# Configurar APK a ser usado
# Pode ser especificado como argumento ou usar o mais recente
LATEST_APK="${1:-}"

# Se não foi especificado um APK, encontrar o mais recente
if [[ -z "$LATEST_APK" ]]; then
    print_info "Procurando APK mais recente..."
    
    # Procurar em releases/ primeiro
    if [[ -d "releases" ]]; then
        LATEST_APK=$(ls -1t releases/*.apk 2>/dev/null | head -1 || echo "")
    fi
    
    # Se não encontrar, procurar em old-releases/
    if [[ -z "$LATEST_APK" && -d "old-releases" ]]; then
        LATEST_APK=$(ls -1t old-releases/*.apk 2>/dev/null | head -1 || echo "")
    fi
    
    if [[ -z "$LATEST_APK" ]]; then
        print_error "Nenhum APK encontrado!"
        exit 1
    fi
else
    # Verificar se o arquivo especificado existe
    if [[ ! -f "$LATEST_APK" ]]; then
        # Tentar procurar nas diretorias releases/ e old-releases/
        if [[ -f "releases/$LATEST_APK" ]]; then
            LATEST_APK="releases/$LATEST_APK"
        elif [[ -f "old-releases/$LATEST_APK" ]]; then
            LATEST_APK="old-releases/$LATEST_APK"
        else
            print_error "APK não encontrado: $LATEST_APK"
            print_info "Verifique se o arquivo existe em releases/ ou old-releases/"
            exit 1
        fi
    fi
    print_info "Usando APK especificado: $LATEST_APK"
fi

APK_NAME=$(basename "$LATEST_APK")
if [[ -n "${1:-}" ]]; then
    print_info "APK selecionado: $APK_NAME"
else
    print_info "APK mais recente: $APK_NAME"
fi

# Instalar APK
print_info "Instalando APK..."
if adb install "$LATEST_APK"; then
    print_success "APK instalado!"
else
    print_error "Falha na instalação!"
    exit 1
fi

# Executar aplicativo
print_info "Executando SIGLAB Mobile..."
adb shell am start -n org.openlmis.core/.view.activity.LoginActivity

print_success "SIGLAB Mobile executado com sucesso! 🎉"

# Mostrar informações
echo
print_info "📱 APK: $APK_NAME"
print_info "📦 Tamanho: $(ls -lh "$LATEST_APK" | awk '{print $5}')"
print_info "🤖 Device: $(adb shell getprop ro.product.model | tr -d '\r')"
