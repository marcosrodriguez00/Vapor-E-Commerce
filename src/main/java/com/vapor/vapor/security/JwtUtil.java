package com.vapor.vapor.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Set;

//tiene utilidades necesarias para implementar jwt
//Instancia Única: Spring creará una sola instancia de la clase JwtUtil cuando se inicie la aplicación. 
//Inyección de Dependencias: Permite que esta instancia sea inyectada automáticamente en otras clases que la necesiten
//sin la necesidad  de  crearla manualmente.
@Component
public class JwtUtil {
    /**
     * Declarado en application.properties
     * El "secret" es una cadena de texto confidencial que solo el servidor conoce.
     * Se utiliza para firmar digitalmente cada token JWT.
     * La firma garantiza que el token es auténtico y que no ha sido modificado por un tercero.
     * El valor se inyecta desde el archivo `application.properties` a través de la clave `jwt.secret`.
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Declarado en application.properties
     * Define el tiempo de vida de un token en milisegundos.
     * Después de este tiempo, el token expira y ya no es válido, obligando al usuario a autenticarse de nuevo.
     * Es una medida de seguridad crucial para limitar la ventana de oportunidad en caso de que un token sea robado.
     * El valor se inyecta desde el archivo `application.properties` a través de la clave `jwt.expiration`.
     */
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Este método genera una clave secreta para firmar el token JWT.
     * La clave se crea a partir de un 'secret' definido en las propiedades de la aplicación.
     * se llama en generateToken y getClaims
     * @return la clave secreta para firmar el token.
     */
    private SecretKey getSigningKey() {
        // La clave debe tener al menos 256 bits para el algoritmo HS256
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Genera un token JWT para un usuario.
     * El token incluye el nombre de usuario (subject), sus roles, la fecha de emisión y una fecha de expiración.
     * Finalmente, se firma el token con la clave secreta.
     * se llamada desde AuthenticationService al hacer login
     * @param username el nombre de usuario.
     * @param roles los roles del usuario.
     * @return el token JWT como una cadena de texto.
     */
    public String generateToken(String username, Set<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", String.join(",", roles))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrae el nombre de usuario del token JWT.
     * Lo hace parseando el token y obteniendo el "subject" (sujeto), que en este caso es el username.
     * se llama desde JwtFilter para obtener el username y cargar los detalles del usuario
     * @param token el token JWT.
     * @return el nombre de usuario.
     */
    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Extrae los roles del usuario del token JWT.
     * Parsea el token para acceder a los "claims" (datos) y recupera los roles.
     * se llama desde JwtFilter para obtener los roles y asignar las authorities
     * @param token el token JWT.
     * @return un conjunto de roles del usuario.
     */
    public Set<String> getRoles(String token) {
        String roles = (String) getClaims(token).get("roles");
        return Set.of(roles.split(","));
    }

    /**
     * Valida un token JWT.
     * Comprueba que el token no haya expirado y que la firma sea correcta.
     * Si el token está manipulado o ha expirado, el método lanzará una excepción y devolverá false.
     * se llama desde JwtFilter para validar el token antes de autenticar al usuario
     * @param token el token JWT a validar.
     * @return true si el token es válido, false en caso contrario.
     */
    public boolean validateToken(String token) {
        //TODO: ssanchez- crear exception personalizada y manerjar con GlobalExceptionHandler   
        try {
            //este método es el que valida el token y extra la info del token: usarname, expiration, roles, etc.
            Claims claims = getClaims(token);
            //validacion expiración redundante, ya lo hace getClaims al parsear el token,
            //se hace para que quede explicito e independiente de jwt
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Valida el token y también extrae los "claims" (datos) del token JWT.
       iss (Issuer)	El emisor del token (quién lo generó).	mi-ecommerce.com
       sub (Subject)	El sujeto o principal, que en su caso es el nombre de usuario (username).	juan.perez
       exp (Expiration)	El momento exacto en que el token expira. (Necesario para la seguridad).	1666352400 (timestamp)
       iat (Issued At)	El momento en que el token fue emitido (creado).	1666316400 (timestamp)
      
     * Utiliza la clave de firma para verificar la integridad del token antes de extraer los datos.
     * @param token el token JWT.
     * @return los claims (datos) del token.
     */
    private Claims getClaims(String token) {
  
        return Jwts.parserBuilder()
                // establece la clave secreta para verificar la firma del token
                .setSigningKey(getSigningKey())
                .build()
                // valida el token y parsea sus claims 
                // La firma del token: Asegura que el token no haya sido modificado. Si la firma es inválida, lanza una excepción. SignatureException
                // El formato del token: Confirma que sea un JWT bien estructurado. MalformedJwtException
                // La fecha de expiración: Si el token ya expiró, también lanza una excepción. ExpiredJwtException
                .parseClaimsJws(token)
                // estrae los datos si todo es correcto
                .getBody();
    }
}