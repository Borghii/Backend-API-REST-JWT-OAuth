package project.api.rest.security.JWT;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix = "rsa")
public record RsaKetProperties(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
}
