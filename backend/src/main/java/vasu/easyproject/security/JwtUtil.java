package vasu.easyproject.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    @Value("${jwt.secret.key}")
    private String secretKey;

    // Générer un JWT
    public String generateToken(String username, Long userId) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)  // Ajouter l'ID de l'utilisateur dans le token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))  // 1 heure d'expiration
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // Extraire le nom d'utilisateur à partir du token
    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Long extractUserIdFromToken(String token) {
        // Vérifier le contenu du token
        var claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        System.out.println("Claims: " + claims);  // Affiche les claims pour vérifier que "userId" est bien présent
        return claims.get("userId", Long.class);  // Extraire "userId"
    }

    // Vérifier si le token est expiré
    public boolean isTokenExpired(String token) {
        return extractExpirationDate(token).before(new Date());
    }

    // Extraire la date d'expiration du token
    private Date extractExpirationDate(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    // Valider un token
    public boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }
}
