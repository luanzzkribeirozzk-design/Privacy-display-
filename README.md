# 🔒 Privacy Display — LN Display

App Android que replica a função **Privacy Display** do Samsung Galaxy S25 Ultra.

## ✨ Funcionalidades

- **Filtro de privacidade** — escurece bordas da tela, impedindo visualização de lados, cima e baixo
- **Barra de ajuste** — controle o nível de privacidade de 0% a 100%
- **Sempre ativo** — fica rodando em segundo plano mesmo fechando o app
- **Central de Controle** — atalho Quick Settings para ativar/desativar rapidamente
- **Interface roxa neon** com neve animada ❄️
- **Nome interno**: LN Display | **Nome externo**: PRIVACY DISPLAY

## 📱 Como usar

1. Instale o APK
2. Abra o app **PRIVACY DISPLAY**
3. Conceda permissão de **"Exibir sobre outros apps"** quando solicitado
4. Ative o toggle **Privacy Display**
5. Ajuste o nível pela barra deslizante
6. Adicione o tile na **Central de Controle** para acesso rápido

## 🔨 Build via GitHub Actions

### Passos:

1. **Fork ou suba este projeto** para seu repositório GitHub

2. Vá em **Actions** → o workflow `Build APK` roda automaticamente a cada push

3. Após o build, vá em **Actions → workflow run → Artifacts** e baixe o APK

### Build local:
```bash
./gradlew assembleDebug
# APK gerado em: app/build/outputs/apk/debug/
```

## 🔑 Permissões necessárias

- `SYSTEM_ALERT_WINDOW` — para exibir o overlay sobre outros apps
- `FOREGROUND_SERVICE` — para manter o serviço ativo
- `RECEIVE_BOOT_COMPLETED` — para iniciar com o dispositivo

## ⚙️ Requisitos

- Android 8.0+ (API 26)
- Permissão de overlay ("Exibir sobre outros apps")

---

**LN Display** © 2025
