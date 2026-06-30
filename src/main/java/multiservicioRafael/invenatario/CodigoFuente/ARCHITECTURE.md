# Documentación de Arquitectura y Patrones de Diseño

## Resumen de la Refactorización

Se ha reorganizado el código para eliminar el anti-patrón "God Class" en la clase `Sistema`, dividiéndola en fachadas especializadas y mejorando la gestión del contexto de usuario. Los controladores de Spring Boot ahora inyectan directamente las fachadas correspondientes.

## Patrones de Diseño Utilizados

### 1. Patrón Facade (Fachada)

**Ubicación:** Todas las clases en el paquete `facade/`

**Descripción:** Proporciona una interfaz simplificada para un subsistema complejo. Cada fachada encapsula la lógica de un dominio específico.

**Fachadas creadas:**
- `AuthFacade`: Gestión de autenticación y recuperación de contraseña
- `TrabajadorFacade`: Gestión de trabajadores
- `ClienteFacade`: Gestión de clientes
- `ProveedorFacade`: Gestión de proveedores
- `ConfiguracionFacade`: Gestión de configuración (roles, menús, categorías)
- `ExportacionFacade`: Generación de reportes (PDF, Excel)
- `ConsultaExternaFacade`: Consultas a APIs externas (DNI, RUC)

**Beneficios:**
- Reduce la complejidad del código cliente
- Desacopla el código cliente de los subsistemas
- Facilita el mantenimiento y testing
- Cada fachada tiene una responsabilidad única

### 2. Patrón Singleton

**Ubicación:** Clase `Sistema`

**Descripción:** Garantiza que solo exista una instancia de la clase y proporciona un punto de acceso global.

**Implementación:**
```java
private static Sistema instancia;

public static Sistema getInstancia() {
    if (instancia == null) {
        instancia = new Sistema();
    }
    return instancia;
}
```

**Nota:** La clase `Sistema` ahora actúa como un fachada principal que delega a las fachadas especializadas. Se mantiene por compatibilidad con código existente.

### 3. Patrón ThreadLocal Storage (Contexto por Hilo)

**Ubicación:** Clase `UserContext`

**Descripción:** Almacena datos específicos de cada hilo de ejecución. Soluciona el problema de concurrencia cuando múltiples usuarios acceden simultáneamente.

**Implementación:**
```java
private static final ThreadLocal<String> usuarioLogueado = new ThreadLocal<>();

public static void setUsuario(String username) {
    usuarioLogueado.set(username);
}

public static String getUsuario() {
    return usuarioLogueado.get();
}

public static void clear() {
    usuarioLogueado.remove();
}
```

**Beneficios:**
- Thread-safe: Cada petición HTTP tiene su propio contexto
- Evita conflictos entre usuarios concurrentes
- Más eficiente que variables estáticas globales

### 4. Patrón Filter Chain (Cadena de Filtros)

**Ubicación:** Clase `UserContextCleanupFilter`

**Descripción:** Intercepta cada petición HTTP para limpiar el contexto del usuario al finalizar.

**Implementación:**
```java
@Component
@Order(1)
public class UserContextCleanupFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {
            chain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }
}
```

**Beneficios:**
- Previene memory leaks en ThreadLocal
- Limpieza automática del contexto
- Centralizado y no intrusivo

### 5. Patrón Dependency Injection (Inyección de Dependencias)

**Ubicación:** Todos los controladores Spring Boot

**Descripción:** Spring Boot inyecta las fachadas en los controladores en lugar de usar el singleton global.

**Beneficios:**
- Testabilidad: Las fachadas pueden ser mockeadas fácilmente
- Desacoplamiento: Los controladores no dependen de una clase singleton global
- Flexibilidad: Spring Boot gestiona el ciclo de vida de las fachadas
- Mantenibilidad: Código más limpio y sigue principios SOLID

## Estructura de Paquetes

```
CodigoFuente/
├── context/
│   └── UserContext.java              # Contexto de usuario thread-safe
├── facade/
│   ├── AuthFacade.java               # Fachada de autenticación
│   ├── TrabajadorFacade.java         # Fachada de trabajadores
│   ├── ClienteFacade.java            # Fachada de clientes
│   ├── ProveedorFacade.java          # Fachada de proveedores
│   ├── ConfiguracionFacade.java      # Fachada de configuración
│   ├── ExportacionFacade.java        # Fachada de exportación
│   └── ConsultaExternaFacade.java    # Fachada de consultas externas
├── ConfigreConect/
│   └── UserContextCleanupFilter.java # Filtro para limpiar contexto
├── Sistema.java                       # Fachada principal (refactorizada)
└── ... (resto del código existente)
```

## Solución al Problema del Usuario Logueado

### Problema Original
La clase `Sistema` tenía una variable de instancia `private String usuario` que no era thread-safe. Cuando múltiples usuarios accedían simultáneamente, el contexto se mezclaba.

### Solución Implementada
1. **UserContext con ThreadLocal:** Cada hilo de ejecución (petición HTTP) tiene su propio contexto de usuario.
2. **Filtro de limpieza:** `UserContextCleanupFilter` limpia el contexto automáticamente al final de cada petición.
3. **Uso en fachadas:** Las fachadas obtienen el usuario del contexto con `UserContext.getUsuario()`.

### Flujo de una Petición

```
1. Usuario hace login → AuthFacade.procesarLogin()
2. Se establece el contexto → UserContext.setUsuario(username)
3. Usuario realiza operaciones → Fachadas usan UserContext.getUsuario()
4. Petición finaliza → UserContextCleanupFilter limpia el contexto
```

## Controladores Actualizados

Todos los controladores principales han sido actualizados para inyectar sus fachadas correspondientes:

- **ControladorLogin** → Inyecta `AuthFacade`
- **ControladorCliente** → Inyecta `ClienteFacade`, `ConsultaExternaFacade`, `AuthFacade`, `ExportacionFacade`
- **ControladorTrabajador** → Inyecta `TrabajadorFacade`, `ConsultaExternaFacade`, `AuthFacade`, `ExportacionFacade`
- **ControladorConfiguracion** → Inyecta `ConfiguracionFacade`, `AuthFacade`
- **ControladorProveedor** → Inyecta `ProveedorFacade`, `ConsultaExternaFacade`, `AuthFacade`, `ExportacionFacade`
- **ControladorProducto** → Usa `Sistema` temporalmente (métodos legados en memoria)

## Ejemplo de Uso en Controladores Spring Boot

### Forma Anterior (usando Sistema - DEPRECATED)
```java
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    Usuario user = Sistema.getInstancia().procesarLogin(request.getUsuario(), request.getContrasena());
    // ...
}
```

### Forma Actual (inyectando fachadas - IMPLEMENTADO)
```java
@RestController
@RequestMapping("/api/auth")
public class ControladorLogin {
    
    private final AuthFacade authFacade;
    
    public ControladorLogin(AuthFacade authFacade) {
        this.authFacade = authFacade;
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Usuario user = authFacade.procesarLogin(request.getUsuario(), request.getContrasena());
        // ...
    }
}
```

## Migración del Código Existente

### Compatibilidad Mantenida
La clase `Sistema` mantiene todos sus métodos públicos originales para no romper el código existente. Ahora delega a las fachadas:

```java
// Antes (código en Sistema)
public String nuevoTrabajador(Map<String, Object> datos) {
    // Lógica directa...
}

// Después (delegación)
public String nuevoTrabajador(Map<String, Object> datos) {
    return trabajadorFacade.nuevoTrabajador(datos);
}
```

### Próximos Pasos Recomendados
1. **ProductoFacade:** Crear fachada especializada para productos y migrar ControladorProducto
2. **Testing:** Crear tests unitarios para cada fachada
3. **Eliminación gradual:** Una vez que todos los controladores usen las fachadas directamente, se puede eliminar la clase `Sistema`

## Ventajas de la Nueva Arquitectura

1. **Separación de responsabilidades:** Cada fachada maneja un dominio específico.
2. **Thread-safe:** El contexto de usuario es seguro para concurrencia.
3. **Mantenibilidad:** Código más fácil de entender y modificar.
4. **Testabilidad:** Las fachadas pueden ser testeadas independientemente.
5. **Escalabilidad:** Fácil agregar nuevas funcionalidades sin modificar código existente.
6. **No memory leaks:** El filtro garantiza limpieza del ThreadLocal.
7. **Inyección de dependencias:** Los controladores son más testables y desacoplados.

## Conclusión

La refactorización ha transformado una "God Class" en una arquitectura modular basada en patrones de diseño probados. El código ahora es más mantenible, testable y seguro para concurrencia. Los controladores de Spring Boot siguen las mejores prácticas de inyección de dependencias.
