package com.vapor.vapor.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vapor.vapor.dto.LoginRequest;
import com.vapor.vapor.dto.RegisterRequest;
import com.vapor.vapor.model.Role;
import com.vapor.vapor.model.Usuario;
import com.vapor.vapor.repository.UsuarioRepository;
import com.vapor.vapor.security.JwtUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * Método que realiza el registro de un nuevo usuario en el sistema.
     * 
     * Este método es responsable de realizar el procedimiento completo de registro,
     * validando unicidad del email, creando el usuario con datos encriptados, 
     * asignando permisos por defecto y persistiendo en la base de datos.
     * 
     * FLUJO DE EJECUCIÓN:
     * 1. Valida que el email no esté duplicado en el sistema
     * 2. Construye una nueva entidad Usuario con el patrón Builder
     * 3. Encripta la contraseña usando BCrypt (algoritmo seguro resistente a ataques de fuerza bruta)
     * 4. Asigna automáticamente el rol de usuario básico (USER)
     * 5. Persiste el usuario en la base de datos dentro de una transacción
     * 6. Retorna un mensaje de confirmación
     * 
     * @param request objeto RegisterRequest que contiene:
     *                - nombre: nombre del usuario a registrar
     *                - apellido: apellido del usuario a registrar
     *                - email: email único del usuario (validado en PASO 1)
     *                - password: contraseña en texto plano que será encriptada
     * @return "User registered successfully" - mensaje de confirmación del registro exitoso
     * @throws RuntimeException si el email ya existe en el sistema 
     *         (TODO: implementar excepción personalizada EmailException y manejar con @ControllerAdvice)
     */
    public String register(RegisterRequest request) {

        // ==================== PASO 1: VALIDACIÓN DE EMAIL ÚNICO ====================
        // Verifica que el email proporcionado no esté ya registrado en la base de datos.
        // Esto evita duplicados y garantiza que cada usuario tenga un identificador único.
        // Se utiliza el método existsByEmail() del repositorio para una consulta eficiente.
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            //TODO: ssanchez - crear exception personalizada EmailException y manejar con @ControllerAdvice
            // Si el email ya existe, se lanza una excepción. En futuras actualizaciones se debe
            // crear una excepción personalizada (EmailException) y capturarla en un @ControllerAdvice
            // para devolver respuestas HTTP consistentes y mensajes de error profesionales
            throw new RuntimeException("El email ya existe en la base de datos");
        }

        // ==================== PASO 2: CONSTRUCCIÓN DEL OBJETO USUARIO ====================
        // Se utiliza el patrón BUILDER (diseño de software) proporcionado por Lombok.
        // La anotación @Builder en la clase Usuario permite construir instancias de forma fluida.
        // 
        // VENTAJAS del patrón Builder:
        // - Código más legible y autoexplicativo (cada línea muestra qué atributo se está asignando)
        // - Evita tener constructores con demasiados parámetros
        // - Evita el repetitivo boilerplate code de setters
        // - Facilita el mantenimiento si se agregan más campos en el futuro
        // 
        // Ejemplo de alternativa sin Builder (código repetitivo no recomendado):
        // Usuario usuario = new Usuario();
        // usuario.setNombre(request.getNombre());
        // usuario.setApellido(request.getApellido());
        // usuario.setEmail(request.getEmail());
        // usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        // usuario.setRole(Role.USER);

        //con el builder, se construye el usuario de forma fluida y clara, asignando cada campo de manera explícita
        Usuario usuario = Usuario.builder()
                // 2.1) Asigna el nombre completo del usuario desde el request
                .nombre(request.getNombre())
                // 2.2) Asigna el apellido del usuario desde el request
                .apellido(request.getApellido())
                // 2.3) Asigna el email del usuario (ya validado como único en PASO 1)
                .email(request.getEmail())
                // 2.4) ENCRIPTACIÓN DE LA CONTRASEÑA (PASO CRÍTICO DE SEGURIDAD)
                //      Se utiliza passwordEncoder (PasswordEncoder de Spring Security) 
                //      para codificar la contraseña usando el algoritmo BCrypt
                //      IMPORTANTE: La contraseña NUNCA se almacena en texto plano en la BD
                //      Características de seguridad de BCrypt:
                //      - Resistente a ataques de fuerza bruta (computacionalmente costoso)
                //      - El mismo password se codifica diferente en cada llamada
                //      - Al verificar en login, compara el hash almacenado con el nuevo hash generado
                .password(passwordEncoder.encode(request.getPassword()))
                // 2.5) Asigna el rol de usuario a todos los registros nuevos
                //      Todos los usuarios nuevos tienen rol USER (permisos limitados)
                //      Solo administradores pueden asignar roles especiales (ADMIN, MODERATOR, etc.)
                //      Esto sigue el principio de "least privilege" (menor nivel de privilegios)
                .role(Role.USER)
                // 2.6) Finaliza la construcción y retorna la instancia Usuario 
                //      con todos los campos configurados y listos para usar
                .build();

        // ==================== PASO 3: PERSISTENCIA EN LA BASE DE DATOS ====================
        // Guarda el usuario en la base de datos a través del UsuarioRepository.
        // 
        // CONTEXTO TRANSACCIONAL:
        // - La clase AuthenticationService está anotada con @Transactional
        // - Esto indica que todos los métodos de la clase se ejecutan dentro de una transacción
        // - La transacción se abre automáticamente al inicio del método y se confirma
        //   (commit) automáticamente al finalizar sin excepciones
        // - Si ocurre una excepción, la transacción se revierte automáticamente (rollback)
        //   garantizando la consistencia de datos
        usuarioRepository.save(usuario);
        
        // ==================== PASO 4: RETORNO DE RESPUESTA ====================
        // Retorna un mensaje de confirmación al cliente informando que el registro fue exitoso.
        // En el futuro, se podría mejorar esta respuesta para incluir:
        // - El ID del usuario creado
        // - Un objeto JSON con los datos del usuario registrado
        // - Un token de autenticación automático
        return "User registered successfully";
    }

    /**
     * Método que autentica un usuario existente y genera un token JWT.
     * 
     * Este método implementa el flujo completo de autenticación:
     * 1. Valida las credenciales (email y contraseña) contra la base de datos
     * 2. Verifica que el usuario existe y que la contraseña es correcta
     * 3. Extrae los roles/permisos del usuario autenticado
     * 4. Genera un token JWT con la información de autenticación
     * 5. Retorna el token al cliente para futuras solicitudes autenticadas
     * 
     * FLUJO DE SEGURIDAD:
     * - Se utiliza Spring Security AuthenticationManager para validar credenciales
     * - Las contraseñas se comparan de manera segura usando BCrypt
     * - El token JWT se genera con email y roles del usuario
     * - El cliente debe incluir este token en el header Authorization de futuras solicitudes
     * 
     * @param request objeto LoginRequest que contiene:
     *                - email: identificador único del usuario
     *                - password: contraseña en texto plano (será encriptada internamente para validación)
     * @return token JWT (JSON Web Token) que el cliente debe usar para autenticarse en solicitudes futuras
     * @throws UsernameNotFoundException si el usuario (email) no existe en la base de datos
     * @throws BadCredentialsException si la contraseña proporcionada es incorrecta
     * @throws NoSuchElementException si no se encuentra el usuario después de la autenticación exitosa
     */
    public String authenticate(LoginRequest request) {
        
        // ==================== PASO 1: VALIDACIÓN DE CREDENCIALES ====================
        // Utiliza el AuthenticationManager de Spring Security para validar las credenciales
        // 
        // AuthenticationManager:
        // - Se configura automáticamente por Spring Boot en base a la configuración de SecurityConfig
        // - Obtiene el PasswordEncoder para comparar contraseñas de manera segura
        // - Usa UserDetailsService para cargar los detalles del usuario desde la BD
        // - Implementa el protocolo de validación de Spring Security
        //
        // UsernamePasswordAuthenticationToken:
        // - Representa las credenciales NO autenticadas del usuario
        // - Constructor: (principal, credentials)
        //   * principal: email del usuario (actúa como username)
        //   * credentials: contraseña en texto plano que fue enviada por el cliente
        //
        // Proceso de validación:
        // 1. Spring busca el usuario por email en la BD (a través de UserDetailsService)
        // 2. Si no existe, lanza UsernameNotFoundException
        // 3. Si existe, obtiene la contraseña encriptada almacenada en BD
        // 4. Compara la contraseña enviada (encriptada con el mismo salt) con la almacenada
        // 5. Si coinciden, retorna el token autenticado con los roles del usuario
        // 6. Si no coinciden, lanza BadCredentialsException
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        // Email del usuario (actúa como username)
                        // Ejemplo: ssanchez@gmail.com
                        request.getEmail(),
                        // Contraseña en texto plano enviada por el cliente
                        // Ejemplo: 1234
                        // El AuthenticationManager la encriptará internamente y la comparará con la BD
                        request.getPassword()));

        // ==================== PASO 2: OBTENER INFORMACIÓN DEL USUARIO AUTENTICADO ====================
        // Una vez que authenticationManager.authenticate() pasa sin excepciones,
        // significa que las credenciales son válidas. Ahora se obtienen los detalles del usuario.
        //
        // findByEmail() retorna un Optional<Usuario> (puede o no existir el usuario)
        // orElseThrow() lanza NoSuchElementException si el usuario no existe
        // (esto es una precaución adicional, aunque en teoría ya fue validado en PASO 1)
        Usuario user = usuarioRepository.findByEmail(request.getEmail()).orElseThrow();
        
        // ==================== PASO 3: EXTRACCIÓN Y EXTRACCIÓN DE ROLES ====================
        // Obtiene la lista de roles/permisos del usuario autenticado
        //
        // user.getAuthorities():
        // - Retorna una Collection<? extends GrantedAuthority>
        // - GrantedAuthority representa un permiso o rol del usuario
        // - Ejemplo: ROLE_USER, ROLE_ADMIN, ROLE_MODERATOR
        //
        // Flujo de transformación (Stream API):
        // 1. user.getAuthorities().stream() - convierte la colección en un stream
        // 2. .map(grantedAuthority -> grantedAuthority.getAuthority()) - extrae el nombre del rol (String)
        //    * Transforma cada GrantedAuthority en su representación como String
        //    * Ejemplo: [GrantedAuthority("ROLE_USER")] -> ["ROLE_USER"]
        // 3. .collect(Collectors.toSet()) - recolecta los resultados en un Set<String>
        //    * Set evita duplicados (aunque normalmente un usuario no tiene roles duplicados)
        //    * Ejemplo salida: {"ROLE_USER", "ROLE_ADMIN"}
        Set<String> roles = user.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toSet());

        // ==================== PASO 4: GENERACIÓN DEL TOKEN JWT ====================
        // Genera un token JWT (JSON Web Token) que será enviado al cliente
        //
        // JWT (JSON Web Token):
        // - Formato estándar para tokens de autenticación
        // - Estructura: [header].[payload].[signature]
        // - Es un token autofirmado (contiene su propia validación mediante firma)
        // - No se requiere consultar BD en cada solicitud para validarlo (validación local)
        // - Contiene información encriptada sobre el usuario (email, roles)
        // - Tiene expiration time (vencimiento después de cierto tiempo)
        // - El cliente lo debe incluir en el header Authorization para futuras solicitudes
        //
        // jwtUtil.generateToken(email, roles):
        // - Crea un token JWT con el email y los roles del usuario
        // - Firma el token con una clave secreta definida en la aplicación
        // - El servidor podrá verificar este token en futuras solicitudes sin consultar BD
        // - Solo tokens con firma válida serán aceptados (previene manipulación)
        return jwtUtil.generateToken(user.getEmail(), roles);
    }
}
