# Как собрать Aurora Client

## Требования
- **Java 21 JDK** (рекомендуется [Eclipse Temurin](https://adoptium.net/) или Zulu)
- Git
- Интернет для первой загрузки зависимостей

## Шаги сборки

### Linux / macOS
```bash
git clone https://github.com/vvdd7232-lang/aurora-client.git
cd aurora-client
chmod +x gradlew
./gradlew build
```

### Windows (PowerShell / cmd)
```powershell
git clone https://github.com/vvdd7232-lang/aurora-client.git
cd aurora-client
gradlew.bat build
```

## Результат
Готовый мод появится по пути:
```
build/libs/aurora-client-1.0.0.jar
```

## Установка
1. Установите [Fabric Loader](https://fabricmc.net/use/installer/) для Minecraft **1.21.11**
2. Скачайте и положите в папку `mods` мод [Fabric API](https://modrinth.com/mod/fabric-api)
3. Положите `aurora-client-1.0.0.jar` в `.minecraft/mods/`
4. Запустите профиль `fabric-loader-1.21.11`
5. Нажмите **Right Shift (Правый Shift)** в игре — откроется меню Aurora
