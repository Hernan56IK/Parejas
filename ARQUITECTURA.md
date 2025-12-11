# Arquitectura del Proyecto - Juego de Parejas

## 📋 Resumen
Este proyecto implementa un juego de memoria (parejas) siguiendo los patrones de diseño **Modelo-Vista-Controlador (MVC)** y **Singleton**.

## 🏗️ Estructura del Proyecto

### 📁 Modelo (Model)
**Ubicación:** `src/main/java/com/example/minigamerecu/model/`

- **Card.java**: Representa una carta del juego
  - Atributos: `id`, `symbol`, `matched`, `flipped`
  - Métodos: Getters y Setters para acceder a los atributos
  - Responsabilidad: Almacenar el estado de una carta individual

### 🎨 Vista (View)
**Ubicación:** `src/main/resources/com/example/minigamerecu/view/`

- **start.fxml**: Pantalla de inicio del juego
  - Selección de dificultad
  - Botones de inicio y salida
  - Controlador: `StartController`

- **game.fxml**: Pantalla principal del juego
  - Tablero de cartas
  - Contadores de movimientos y parejas
  - Botón de pistas e instrucciones
  - Controlador: `GameController`

### 🎮 Controlador (Controller)
**Ubicación:** `src/main/java/com/example/minigamerecu/controller/`

- **StartController.java**: Controla la pantalla de inicio
  - Maneja la selección de dificultad
  - Navegación entre pantallas
  - Usa `GameManager.getInstance()` para acceder al singleton

- **GameController.java**: Controla la lógica del juego
  - Maneja los clics en las cartas
  - Gestiona las animaciones
  - Actualiza la vista según el estado del juego
  - Usa `GameManager.getInstance()` para acceder al singleton

### 🎯 Manager (Patrón Singleton)
**Ubicación:** `src/main/java/com/example/minigamerecu/manager/`

- **GameManager.java**: Gestiona el estado global del juego
  - **Patrón Singleton**: Implementado con doble verificación (thread-safe)
  - Constructor privado para prevenir instanciación externa
  - Método estático `getInstance()` para obtener la única instancia
  - Responsabilidades:
    - Estado del juego (movimientos, parejas encontradas, etc.)
    - Configuración (dificultades, símbolos, límites)
    - Estadísticas globales
    - Sistema de pistas

## ✅ Verificación de Patrones

### Patrón Singleton ✓
- ✅ Constructor privado
- ✅ Variable estática `instance`
- ✅ Método `getInstance()` con doble verificación
- ✅ Thread-safe (synchronized)
- ✅ Todos los controladores usan `GameManager.getInstance()`
- ✅ No hay instanciaciones directas con `new GameManager()`

### Patrón MVC ✓
- ✅ **Modelo**: `Card.java` - Entidad de datos sin lógica de presentación
- ✅ **Vista**: Archivos FXML - Interfaz de usuario sin lógica de negocio
- ✅ **Controlador**: `StartController`, `GameController` - Lógica de negocio y coordinación
- ✅ Separación clara de responsabilidades
- ✅ Los controladores no contienen datos del modelo directamente
- ✅ El modelo no conoce la vista ni el controlador

## 📊 Flujo de Datos

```
Usuario → Vista (FXML) → Controlador → GameManager (Singleton) → Modelo (Card)
                ↑                                              ↓
                └────────── Actualización de Vista ←──────────┘
```

## 🔍 Puntos Clave de la Arquitectura

1. **Singleton Correcto**: 
   - Una sola instancia de `GameManager` en toda la aplicación
   - Acceso centralizado al estado del juego
   - Thread-safe para posibles futuras extensiones multihilo

2. **Separación MVC**:
   - Modelo: Solo datos (`Card`)
   - Vista: Solo presentación (FXML)
   - Controlador: Solo lógica y coordinación

3. **Manejo de Estado**:
   - El estado del juego se mantiene en `GameManager` (Singleton)
   - Los controladores acceden al estado a través del singleton
   - No hay duplicación de estado

4. **Configuración Centralizada**:
   - Todas las constantes y configuraciones en `GameManager`
   - Fácil modificación de reglas del juego
   - Enum `Difficulty` para diferentes niveles

## 🚀 Mejoras Implementadas

- Sistema de dificultades (Fácil, Medio, Difícil, Experto)
- Sistema de pistas (3 pistas por partida)
- Estadísticas globales
- Animaciones y efectos visuales
- Ajuste automático de tamaño de ventana según dificultad

## 📝 Notas Técnicas

- **Thread Safety**: El Singleton usa doble verificación para ser thread-safe
- **Inmutabilidad**: Los métodos del GameManager que retornan listas crean copias defensivas
- **Encapsulación**: Todos los atributos del modelo son privados con acceso a través de métodos

