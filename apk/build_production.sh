#!/bin/bash

# =============================================================================
# SIGLAB Mobile - Script de Build de Produção
# =============================================================================
# Este script automatiza o build de produção do SIGLAB Mobile
# Suporta 2 keystores diferentes para assinatura
# =============================================================================

set -e  # Parar execução em caso de erro

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para imprimir mensagens coloridas
print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_header() {
    echo -e "${BLUE}"
    echo "=============================================="
    echo "🚀 SIGLAB Mobile - Build de Produção"
    echo "=============================================="
    echo -e "${NC}"
}

# Verificar se estamos no diretório correto
check_directory() {
    if [[ ! -f "../app/build.gradle" ]]; then
        print_error "Erro: Execute este script a partir da diretoria 'apk'"
        print_error "Uso: cd apk && ./build_production.sh"
        exit 1
    fi
}

# Função para mostrar menu de keystores
show_keystore_menu() {
    echo
    print_info "Escolha o keystore para assinatura:"
    echo "1) lmis_moz.jks (DEFAULT - Recomendado)"
    echo "   📍 Localização: ../scripts/lmis_moz.jks"
    echo "   🏢 Organização: Clinton Health Access Initiative"
    echo "   🔑 Senha: !EBus&tre46A"
    echo
    echo "2) appstore.jks (Alternativo)"
    echo "   📍 Localização: ../appstore/appstore.jks"
    echo "   🏢 Organização: Unknown"
    echo "   🔑 Senha: password"
    echo
    echo "3) Sair"
    echo
}

# Função para configurar keystore no build.gradle
configure_keystore() {
    local keystore_choice=$1
    local build_gradle="../app/build.gradle"
    
    print_info "Configurando keystore no build.gradle..."
    
    if [[ $keystore_choice == "1" ]]; then
        # Configurar lmis_moz.jks
        sed -i.bak 's|storeFile file("../appstore/appstore.jks")|storeFile file("../scripts/lmis_moz.jks")|g' "$build_gradle"
        sed -i.bak 's|storePassword "password"|storePassword "!EBus&tre46A"|g' "$build_gradle"
        sed -i.bak 's|keyPassword "password"|keyPassword "!EBus&tre46A"|g' "$build_gradle"
        print_success "Configurado para usar lmis_moz.jks"
        KEYSTORE_NAME="lmis_moz"
        KEYSTORE_PATH="../scripts/lmis_moz.jks"
        KEYSTORE_PASS="!EBus&tre46A"
    elif [[ $keystore_choice == "2" ]]; then
        # Configurar appstore.jks
        sed -i.bak 's|storeFile file("../scripts/lmis_moz.jks")|storeFile file("../appstore/appstore.jks")|g' "$build_gradle"
        sed -i.bak 's|storePassword "!EBus&tre46A"|storePassword "password"|g' "$build_gradle"
        sed -i.bak 's|keyPassword "!EBus&tre46A"|keyPassword "password"|g' "$build_gradle"
        print_success "Configurado para usar appstore.jks"
        KEYSTORE_NAME="appstore"
        KEYSTORE_PATH="../appstore/appstore.jks"
        KEYSTORE_PASS="password"
    fi
    
    # Remover arquivo de backup
    rm -f "$build_gradle.bak"
}

# Função para verificar se keystore existe
check_keystore() {
    local keystore_path=$1
    if [[ ! -f "$keystore_path" ]]; then
        print_error "Keystore não encontrado: $keystore_path"
        exit 1
    fi
    print_success "Keystore encontrado: $keystore_path"
}

# Função para fazer build
do_build() {
    print_info "Iniciando build de produção..."
    
    cd ..
    
    print_info "Limpando build anterior..."
    ./gradlew clean
    
    print_info "Fazendo build de produção (prd release)..."
    ./gradlew assemblePrdRelease
    
    cd apk
    
    local apk_path="../app/build/outputs/apk/prd/release/org.openlmis.core-86-release.apk"
    
    if [[ -f "$apk_path" ]]; then
        print_success "Build concluído com sucesso!"
        print_info "APK gerado: $apk_path"
        
        # Verificar assinatura
        print_info "Verificando assinatura..."
        if jarsigner -verify "$apk_path" &>/dev/null; then
            print_success "APK está corretamente assinado!"
        else
            print_warning "APK não está assinado. Fazendo assinatura manual..."
            manual_sign "$apk_path"
        fi
        
        # Obter informações de versão do gradle.properties
        local version_name=$(grep "semanticVersion" ../gradle.properties | cut -d'=' -f2)
        local version_code=$(grep "androidVersionCode" ../gradle.properties | cut -d'=' -f2)
        local build_variant="prd"
        
        # Copiar APK para diretoria releases com prefixo de data/hora e versão completa
        local timestamp=$(date +%Y%m%d_%H%M)
        local final_apk="releases/${timestamp}_SIGLAB-Mobile-${version_name}-${version_code}-${build_variant}-${KEYSTORE_NAME}-signed.apk"
        
        # Criar diretoria releases se não existir
        mkdir -p releases
        
        cp "$apk_path" "$final_apk"
        print_success "APK copiado para: $final_apk"
        
        # Mostrar informações do APK
        show_apk_info "$final_apk"
        
    else
        print_error "Falha no build! APK não foi gerado."
        exit 1
    fi
}

# Função para assinatura manual
manual_sign() {
    local apk_path=$1
    print_info "Assinando APK manualmente..."
    
    jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
        -keystore "$KEYSTORE_PATH" \
        -storepass "$KEYSTORE_PASS" \
        -keypass "$KEYSTORE_PASS" \
        "$apk_path" \
        clintonhealthaccess
    
    if jarsigner -verify "$apk_path" &>/dev/null; then
        print_success "APK assinado com sucesso!"
    else
        print_error "Falha na assinatura do APK!"
        exit 1
    fi
}

# Função para mostrar informações do APK
show_apk_info() {
    local apk_file=$1
    local size=$(ls -lh "$apk_file" | awk '{print $5}')
    
    echo
    print_success "=== INFORMAÇÕES DO APK ==="
    echo "📱 Arquivo: $apk_file"
    echo "📏 Tamanho: $size"
    echo "🔐 Keystore: $KEYSTORE_NAME"
    echo "✅ Status: Assinado e Verificado"
    echo "🎯 Compatibilidade: Android 9-14"
    echo
}

# Função principal
main() {
    print_header
    check_directory
    
    # Menu interativo
    while true; do
        show_keystore_menu
        read -p "Escolha uma opção (1-3): " choice
        
        case $choice in
            1|2)
                configure_keystore "$choice"
                check_keystore "$KEYSTORE_PATH"
                do_build
                break
                ;;
            3)
                print_info "Saindo..."
                exit 0
                ;;
            *)
                print_error "Opção inválida! Escolha 1, 2 ou 3."
                ;;
        esac
    done
    
    print_success "Build de produção concluído com sucesso! 🎉"
}

# Executar função principal
main "$@"
