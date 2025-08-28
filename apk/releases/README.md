# 📦 SIGLAB Mobile - Releases

Esta diretoria contém todos os APKs de produção gerados do SIGLAB Mobile.

## 📋 Convenção de Nomenclatura

Todos os APKs seguem o padrão:
```
YYYYMMDD_HHMM_SIGLAB-Mobile-v86-production-[keystore]-signed.apk
```

### Componentes do Nome:
- **YYYYMMDD_HHMM**: Data e hora do build (formato 24h)
- **SIGLAB-Mobile**: Nome do aplicativo
- **v86**: Versão do APK
- **production**: Flavor de produção
- **[keystore]**: Nome do keystore usado (`lmis_moz` ou `appstore`)
- **signed**: Indica que o APK está assinado

### Exemplos:
- `20240828_2315_SIGLAB-Mobile-v86-production-lmis_moz-signed.apk`
- `20240828_1430_SIGLAB-Mobile-v86-production-appstore-signed.apk`

## 🔍 Identificação de Versões

### Por Data/Hora (Mais Recente Primeiro):
- Os arquivos são ordenados cronologicamente pelo prefixo
- Formato YYYYMMDD_HHMM permite ordenação natural
- Mais fácil identificar a versão mais recente

### Por Keystore:
- **lmis_moz**: Keystore principal (Clinton Health Access Initiative)
- **appstore**: Keystore alternativo

## 📊 Informações dos APKs

### Especificações Técnicas:
- **Tamanho**: ~12-13 MB
- **Compatibilidade**: Android 9-14 (API 28-34)
- **Assinatura**: SHA256withRSA (2048-bit)
- **Flavor**: Production (prd)

### Status de Assinatura:
✅ Todos os APKs nesta diretoria estão **assinados e verificados**

## 🛠️ Como Gerar Novos APKs

Execute o script de build na diretoria pai:
```bash
cd ../
./build_production.sh
```

O script automaticamente:
1. Gera o APK
2. Assina com o keystore escolhido
3. Verifica a assinatura
4. Salva nesta diretoria com timestamp

## 📱 Instalação

Para instalar um APK:
```bash
adb install nome_do_arquivo.apk
```

## 🗂️ Organização

### Manter Apenas Versões Necessárias:
- Versões de desenvolvimento: manter últimas 3-5
- Versões de produção: manter todas as releases importantes
- Versões antigas: arquivar ou remover conforme necessário

### Backup:
- Fazer backup das versões de produção importantes
- Manter keystores em local seguro
- Documentar mudanças significativas entre versões

---

**📅 Última atualização**: Agosto 2024  
**🔧 Gerado por**: build_production.sh  
**📍 Localização**: apk/releases/
